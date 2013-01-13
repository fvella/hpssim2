package hpssim.simulator;

import hpssim.grafica.HPSsim;
import hpssim.hardware.Hardware;
import hpssim.scheduler.Configurator;
import hpssim.scheduler.EventWorkFlow;
import hpssim.scheduler.policy.queue.FIFO;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.scheduler.policy.scheduling.CompletelyFairScheduler;
import hpssim.scheduler.policy.scheduling.HPSSimScheduler;
import hpssim.scheduler.policy.scheduling.SchedulingPolicy;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.random.RandomDataImpl;

/**
 * 
 * @author Igor Neri <igor@cimice.net>
 */
public class Simulator extends Thread {

	private static Logger logger = Logger.getLogger("hpssim.simulator.Simulator");
	private boolean infoLog = true;

	public double classificationRate;
	public int tq; // quantum base time

	public boolean run = false;

	private int avgta;
	private EventList events;
	private double hjobrate;
	private Hardware hw;
	private int njobs;
	private double realTimeJobsProb;
	private SchedulingPolicy scheduler;
	public double time_sim = 1000000d;

	// private IQueue q = new Queue(1);
	// public IQueue jobended = new Queue(0);

	public IQueue jobended = new FIFO();

	public Statistiche stat;
	private HPSsim owner;
	private boolean endJob;

	public Simulator(Hardware _hw, int _njobs, int _quantum, double _classificationRate, double _realTimeJobsProb, double _hjobrate, int _avgta, double _simTime) {
		logger.setLevel(Level.INFO);

		tq = _quantum;
		this.njobs = _njobs;
		this.classificationRate = _classificationRate;
		this.realTimeJobsProb = _realTimeJobsProb;
		this.hjobrate = _hjobrate;
		this.hw = _hw;
		this.avgta = _avgta;

		time_sim = _simTime;

	}

	public Simulator(hpssim.scheduler.Configurator conf, HPSsim hpSsim) {

		this.events = new EventList();
		this.tq = conf.qt;
		this.njobs = conf.njobs;
		this.classificationRate = conf.classificationRate;
		this.realTimeJobsProb = conf.realTimeJobsProb;
		this.hjobrate = conf.realTimeJobsProb;
		this.hw = conf.hw;
		this.avgta = conf.avgta;
		this.endJob = conf.endJob;

		this.time_sim = conf.simTime;

		this.stat = new Statistiche(conf);
		this.owner = hpSsim;

		if (conf.scheduler.equals(HPSSimScheduler.class))
			scheduler = new HPSSimScheduler(conf.queue, conf.qt);
		else {
			scheduler = new CompletelyFairScheduler(hw, events);
			tq = (int) ((CompletelyFairScheduler) scheduler).sysctl_sched_latency;
		}

		init();

		start();
	}

	public IQueue getJobsEnded() {
		return this.jobended;
	}

	public void init() {
		System.out.println("Start initialization...");

		// inizializzazione scheduler
		// scheduler = _scheduler;

		RandomDataImpl randomJob = new RandomDataImpl();
		// randomJob.reSeed(Simulator.tq);
		// SOLO I JOB UTENTE POSSONO ESSERE CPUoGPU

		int rtjobs = 0;
		int ujobs = 0;
		int cpujobs = 0;
		int gpujobs = 0;

		// per ogni njob che viene in input..
		int geninterarrivo = 0;
		for (int i = 1; i <= njobs; i++) {
			// Create new job
			/* ta,rcpu,rgpu,type */
			geninterarrivo += (int) randomJob.nextExponential(this.avgta);
			Job job = new Job(geninterarrivo, i);

			/* Random job type */
			if (randomJob.nextUniform(0, 1) > realTimeJobsProb) {
				/* user job */
				job.setType(1);
				/*
				 * uniform to selection gpu job according to gpujobrat to
				 * implementhere
//				 */
				job.setRcpu((int) randomJob.nextExponential( time_sim / ( njobs)));
//				job.setRcpu((int) randomJob.nextUniform(0, time_sim / (10 * njobs)));
				if (randomJob.nextUniform(0, 1) > hjobrate) {
					/* CPU JOB */
					/* GPU time is long */
					job.setRgpu((int) (job.getRcpu() * randomJob.nextUniform(1, 300)));

				} else {
					/* GPU */
					/* using avg speedup between 2 e 40x */
					job.setRgpu((int) (job.getRcpu() / randomJob.nextUniform(2, 40)));
				}

				if (randomJob.nextUniform(0, 1) > classificationRate) {
					/* sbaglio classificazione */
					if (job.getRcpu() > job.getRgpu()) {
						job.setClassification(0);
					} else {
						job.setClassification(1);
					}
				} else {
					/* azzecco classificazione */
					if (job.getRcpu() > job.getRgpu()) {
						job.setClassification(1);
					} else {
						job.setClassification(0);
					}

				}
				ujobs++;
			} else {
				/* rt job */
				job.setType(0);
				job.setRcpu((int) randomJob.nextUniform(tq / 2, tq * 3));
				job.setRgpu(-1);
				job.setClassification(0);
				rtjobs++;
			}
			/*
			 * Settare priorità di default se RT o user rt 1..99 user 100 ..140
			 * 
			 * valore di nice se rt -20>=nice>0 se user 0>=nice<20
			 */
			if (job.type == 0) {
				// REAL TIME
				job.setPp(3);
				job.setPs(90);
				job.setNice(randomJob.nextInt(0, 20));
			} else {
				// USER
				job.setPp(1);
				job.setPs(20);
				job.setNice(randomJob.nextInt(21, 39));
			}

			job.printJob();

			if (hw.getNumGPU() == 0)
				job.setClassification(0);

			if (job.classification == 0)
				cpujobs++;
			else
				gpujobs++;
			Event ev = new Event(job, Event.ENQUEUE, job.timeArrival);
			events.insertEvent(ev);

			stat.getListaJob().add(job);
		}
		if (infoLog)
			System.out.println("RT " + rtjobs + " - User " + ujobs);
		if (infoLog)
			System.out.println("CPU " + cpujobs + " - GPU " + gpujobs);
		// if(infoLog) System.out.println(realTimeJobsProb);
		hw.infosystem();
		System.out.println("done");
	}

	private Event clock() throws Exception {
		Event ev = null;
		try {

			ev = events.pop();

			switch (ev.type) {
			case Event.ENQUEUE:
				if (infoLog)
					System.out.print("ENQUEUE " + ev.time + " ");
				// call scheduler to rearrange start time
				// Insert a Job in Queue
				// scheduler.schedule(events, ev, dev, q);
				EventWorkFlow.enqueue(scheduler, events, ev, hw);
				if (infoLog)
					System.out.print("Queue | ");
				if (infoLog)
					scheduler.printjob();
				break;
			case Event.RUN:
				if (infoLog)
					System.out.print("RUN " + ev.time + " ");
				EventWorkFlow.run(scheduler, events, ev, hw);
				if (infoLog)
					System.out.print("Queue | ");
				if (infoLog)
					scheduler.printjob();
				break;
			case Event.RESCHEDULE: // each RESCHEDULE events has job = null
				// rearrange priority on queued jobs and set next run job
				// call scheduler
				if (infoLog)
					System.out.print("RESCHEDULE " + ev.time + " ");
				EventWorkFlow.reschedule(scheduler, events, ev, hw);
				if (infoLog)
					System.out.print("Queue | ");
				if (infoLog)
					scheduler.printjob();
				break;
			case Event.FINALIZE:
				// deallocate device and recall scheduling procedure
				// free device
				if (infoLog)
					System.out.print("FINALIZE " + ev.time + " ");
				EventWorkFlow.finalize(scheduler, events, ev, hw, jobended);
				break;
			case Event.REQUEUE:
				// Add old Job in queue set reschelude attrib for job ++
				if (infoLog)
					System.out.print("REQUEUE " + ev.time + " ");
				EventWorkFlow.requeue(scheduler, events, ev, hw, tq);
				if (infoLog)
					System.out.print("Queue | ");
				if (infoLog)
					scheduler.printjob();
				break;
			case Event.NULL:
				/* NOT used */
				break;
			}

			if (infoLog)
				System.out.println("");
			if (infoLog)
				System.out.println("----------------------------------------");

			return ev;
		} catch (Exception e) {
			throw e;
		}

	}

	public void setNewQtime(int nqt) {
		this.tq = nqt;
	}

	public void run() {

		try {
			if (run)
//				wait();
//			else
				simulate();
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e);
		}
	}
	
	int lastEvTime = 0;
	int i = 0;
	
	public void simulate() throws Exception {
		if (infoLog)
			System.out.println("Start simulation...");

		long test = System.currentTimeMillis();

		if (infoLog)
			System.out.println("Current Event - Ta - Next Event Generate - Ta - Device Status - Queue Size | Queue");
		
		while (run) {
			
			if (!endJob) {
				if (lastEvTime > (time_sim))
					run = false;
			}
			if (events.list.isEmpty()){
				run = false;
				break;
			}
			Event ev = clock();
			lastEvTime = ev.time;

			stat.setLastExeTime(new Double(lastEvTime)/1000d);
			
			if(scheduler instanceof CompletelyFairScheduler){
				stat.setCPUFree(((CompletelyFairScheduler) scheduler).getCPUfree());
				stat.setGPUFree(((CompletelyFairScheduler) scheduler).getGPUfree());
			} else {
				stat.setCPUFree(hw.getCPUfree());
				stat.setGPUFree(hw.getGPUfree());
			}
			
			stat.setProcessiInCoda(scheduler.size());
			stat.setProcessiInElaborazione(scheduler.getProcessiInElaborazione(hw));
			stat.setProcessiInCodaCPU(scheduler.getProcessiInCodaCPU());
			stat.setProcessiInCodaGPU(scheduler.getProcessiInCodaGPU());
			stat.calc_load();
			
			
			/************************************************/
			
			
			owner.datasetCPU.setValue(stat.getCpuLoadPerc());
			owner.datasetGPU.setValue(stat.getGpuLoadPerc());
			owner.datasetQueueCPU.setValue(stat.getProcessiInCodaCPU());
			owner.datasetQueueGPU.setValue(stat.getProcessiInCodaGPU());
			
			owner.datasetQueue.add(time_sim, stat.getRunq_sz());
			
			owner.virtualTime.setText("" + stat.getLastExeTime());

			owner.processiNelSistema.setText("" + stat.getProcessiNelSistema());
			owner.processiInCoda.setText("" + stat.getProcessiInCoda());
			owner.processiElaborazione.setText("" + stat.getProcessiInElaborazione());
			
			owner.labelCPUUsage.setText(stat.getCpuLoad() + "/" + hw.numcpus() );
			owner.labelGPUUsage.setText(stat.getGpuLoad() + "/" + hw.numgpus() );
			
			owner.ldavg_1.setText(""+stat.getLdavg_1());
			owner.ldavg_5.setText(""+stat.getLdavg_5());
			owner.ldavg_15.setText(""+stat.getLdavg_15());
			
			if (!endJob) 
				owner.progressBar.setValue((int) (lastEvTime * 100 / time_sim));
			
			i++;
		}

			if (infoLog)
				System.out.println("");
			if (infoLog)
				jobended.printJobsStat();
			if (infoLog)
				System.out.println("");
			if (infoLog)
				System.out.println("" + i);
			if (infoLog)
				System.out.println("Tempo di esecuzione :" + (System.currentTimeMillis() - test));
			
			owner.progressBar.setValue(100);
			owner.setQueueXY();
	}

	public void printProgBar(int percent) {
		StringBuilder bar = new StringBuilder("[");

		for (int i = 0; i < 50; i++) {
			if (i < (percent / 2)) {
				bar.append("=");
			} else if (i == (percent / 2)) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}

		bar.append("]   ").append(percent).append("%     ");
		System.out.print("\n" + bar.toString());
	}
}
