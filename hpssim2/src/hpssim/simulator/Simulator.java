package hpssim.simulator;

import hpssim.grafica.HPSsim;
import hpssim.hardware.Hardware;
import hpssim.scheduler.Configurator;
import hpssim.scheduler.EventWorkFlow;
import hpssim.scheduler.policy.queue.FIFO;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.scheduler.policy.scheduling.CompletelyFairScheduler;
import hpssim.scheduler.policy.scheduling.HPSSimScheduler;
import hpssim.scheduler.policy.scheduling.IScheduler;
import hpssim.scheduler.policy.scheduling.SchedulingPolicy;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
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
	private double percentOpenCLjob;
	private Hardware hw;
	private int njobs;
	private double realTimeJobsProb, mediaEsecuzioneJob;
	private IScheduler scheduler;
	public double time_sim = 1000000d;
	private boolean costant=false;
	private Configurator conf;
	// private IQueue q = new Queue(1);
	// public IQueue jobended = new Queue(0);

	public IQueue jobended = new FIFO();

	public Statistiche stat;
	private HPSsim owner;
	private boolean endJob;

//	public Simulator(Hardware _hw, int _njobs, int _quantum, double mediaEsecuzioneJob, double _classificationRate, double _realTimeJobsProb, double _hjobrate, int _avgta, double _simTime) {
//		logger.setLevel(Level.INFO);
//
//		tq = _quantum;
//		this.njobs = _njobs;
//		this.classificationRate = _classificationRate;
//		this.realTimeJobsProb = _realTimeJobsProb;
//		this.percentOpenCLjob = _hjobrate;
//		this.hw = _hw;
//		this.avgta = _avgta;
//		this.mediaEsecuzioneJob = mediaEsecuzioneJob;
//		
//		time_sim = _simTime;
//
//	}
	
	public void setLogLevel(boolean l){
		infoLog = l;
		if(!l)
			scheduler.disableLog();
	}
	
	public Simulator(hpssim.scheduler.Configurator conf, HPSsim hpSsim) {

		this.events = new EventList();
		this.tq = conf.qt;
		this.njobs = conf.njobs;
		this.classificationRate = conf.classificationRate;
		this.realTimeJobsProb = conf.realTimeJobsProb;
		this.percentOpenCLjob = conf.percentOpenCLjob;
		this.hw = conf.hw;
		this.avgta = conf.avgta;
		this.endJob = conf.endJob;
		this.mediaEsecuzioneJob = conf.mediaEsecuzioneJob;
		
		this.time_sim = conf.simTime;
		if(conf.cost)
			this.costant = true;

		this.owner = hpSsim;

		if (conf.scheduler.equals(HPSSimScheduler.class))
			scheduler = new HPSSimScheduler(conf.queue, conf.qt);
		else {
			scheduler = new CompletelyFairScheduler(hw, events);
			tq = (int) ((CompletelyFairScheduler) scheduler).sysctl_sched_latency;
		}
		
		this.conf = conf;
		
		init();

		start();
	}

	public IQueue getJobsEnded() {
		return this.jobended;
	}

	public void init() {
		System.out.println("Start initialization...");
		
		stat = new Statistiche(conf);
		
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
			
			int interarrivo = (int) randomJob.nextExponential(this.avgta);
			
			while(interarrivo<0){
				interarrivo = (int) randomJob.nextExponential(this.avgta);
			}
			
			if(costant)
				interarrivo = avgta;
			if(conf.crescente)
				this.avgta -=  0.011D * avgta;
			if(avgta<tq)
				avgta=tq;
			
			geninterarrivo += interarrivo;
			Job job = new Job(geninterarrivo, i);

			/* Random job type */
			if (randomJob.nextUniform(0, 1) > realTimeJobsProb) {
				/* user job */
				job.setType(1);
				/*
				 * uniform to selection gpu job according to gpujobrat to
				 * implementhere
//				 */
//				mediaEsecuzioneJob = time_sim / ( njobs);
				if(costant)
					job.setExecutionTimeCPU((int) mediaEsecuzioneJob);
				else
					job.setExecutionTimeCPU((int) randomJob.nextGaussian(mediaEsecuzioneJob, mediaEsecuzioneJob/2d));
//				job.setRcpu((int) randomJob.nextUniform(0, time_sim / (10 * njobs)));
				
				while(job.getExecutionTimeCPU()<0){
					job.setExecutionTimeCPU((int) randomJob.nextGaussian(mediaEsecuzioneJob, mediaEsecuzioneJob/2d));
				}
				
				if (randomJob.nextUniform(0, 1) > percentOpenCLjob) {
					/* CPU JOB */
					/* GPU time is long */
					job.setExecutionTimeGPU((int) (job.getExecutionTimeCPU() * (int)randomJob.nextUniform(1, 300)));

				} else {
					/* GPU */
					/* using avg speedup between 2 e 40x */
					job.setExecutionTimeGPU((int) (job.getExecutionTimeCPU() / (int)randomJob.nextUniform(2, 40)));
				}

				if (randomJob.nextUniform(0, 1) > classificationRate) {
					/* sbaglio classificazione invece che metterlo in gpu lo mette in cpu*/
//					if (job.getExecutionTimeCPU() > job.getExecutionTimeGPU()) {
						job.setClassification(0);
//					} else {
//						job.setClassification(1);
//					}
				} else {
					/* azzecco classificazione */
					if (job.getExecutionTimeCPU() > job.getExecutionTimeGPU()) {
						job.setClassification(1);
					} else {
						job.setClassification(0);
					}

				}
				ujobs++;
			} else {
				/* rt job */
				job.setType(0);
				
				if(costant)
					job.setExecutionTimeCPU((int) mediaEsecuzioneJob);
				else
					job.setExecutionTimeCPU((int) randomJob.nextUniform(tq, mediaEsecuzioneJob));
				
				job.setExecutionTimeGPU(-1);
				job.setClassification(0);
				rtjobs++;
			}
			/*
			 * Settare priorità di default se RT o user rt 1..99 user 100 ..140
			 * 
			 * valore di nice se rt -20>=nice>0 se user 0>=nice<20
			 */
			if (job.getProcessType() == 0) {
				// REAL TIME
				job.setProcessPriority(3);
				job.setSchedulingPriority(90);
				job.setNice(randomJob.nextInt(0, 20));
			} else {
				// USER
				job.setProcessPriority(1);
				job.setSchedulingPriority(20);
				job.setNice(randomJob.nextInt(21, 39));
			}

			job.printJob();

			if (hw.getNumGPU() == 0)
				job.setClassification(0);

			if (job.getClassification() == 0){
				cpujobs++;
			}
			else {
				gpujobs++;
			}
			Event ev = new Event(job, Event.ENQUEUE, job.getTimeArrival());
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
					System.out.print("Queue (size=" + scheduler.size() + ") | ");
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
	int i = 1;
	
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
			
			/************************************************/
			
			if(owner.realtimeStat){
			
			owner.datasetCPU.setValue(stat.getCpuLoadPerc());
			owner.datasetGPU.setValue(stat.getGpuLoadPerc());
			owner.datasetQueueCPU.setValue(stat.getProcessiInCodaCPU());
			owner.datasetQueueGPU.setValue(stat.getProcessiInCodaGPU());
			
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
			
			} else {
				owner.datasetQueue.add(lastEvTime, stat.getRunq_sz());
			}
			
			i++;
			
			stat.elaboraStatistiche(lastEvTime);
			
		}

			System.out.println("");
			if (infoLog)
				jobended.printJobsStat();
			if (infoLog)
				System.out.println("");
			if (infoLog)
				System.out.println("" + i);
			if (infoLog)
				System.out.println("Tempo di esecuzione :" + (System.currentTimeMillis() - test));
			
			System.out.println("Simulazione terminata");
			
			owner.progressBar.setValue(100);
			
			stat.endStatistiche();
			

			if(!owner.realtimeStat){
				PrintWriter output = new PrintWriter(new FileWriter("c:/stat.csv",true));
				
				double tempoMedioInterarrivo = 0 , troughput = 0 , mediaUsoCPU, mediaUsoGPU, mediaCoda;
				List<Job> listajob = ((FIFO)jobended).getList();
				for(Job j : listajob ){
					tempoMedioInterarrivo += j.getExecutionTime()-j.getTimeArrival();
				}
				
				tempoMedioInterarrivo /= (double)listajob.size() ;
				troughput = ((double)listajob.size()) / ((double)lastEvTime/1000l);
				mediaUsoCPU = (double)stat.mediaUsoCPU / (double)i;
				mediaUsoGPU = (double)stat.mediaUsoGPU / (double)i;
				mediaCoda = (double)stat.mediaCoda / (double)i;
				
				String stringa = "" 
						+tempoMedioInterarrivo + ";" + troughput  +";" +
						stat.getLdavg_1() + ";" + stat.getLdavg_5() + ";" + stat.getLdavg_15() + ";"
						+ mediaUsoCPU + ";" + mediaUsoGPU + ";" + hw.numcpus()+ ";" + hw.numgpus() + ";"+ mediaCoda + ";";
				stringa = stringa.replace(".", ",");
				
				output.printf("%s\r\n", stringa);
				output.close();
			}
			
			owner.setEndJobs(false);
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
