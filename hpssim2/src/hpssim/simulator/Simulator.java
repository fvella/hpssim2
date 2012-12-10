package hpssim.simulator;

import hpssim.hardware.Hardware;
import hpssim.scheduler.EventWorkFlow;
import hpssim.scheduler.policy.queue.FIFO;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.scheduler.policy.scheduling.CompletelyFairScheduler;
import hpssim.scheduler.policy.scheduling.HPSSimScheduler;
import hpssim.scheduler.policy.scheduling.SchedulingPolicy;

import org.apache.commons.math3.random.RandomDataImpl;

/**
 * 
 * @author Igor Neri <igor@cimice.net>
 */
public class Simulator implements Runnable {

	public double classificationRate;
	public static int tq; // quantum base time

	private int avgta;
	private EventList events = new EventList();
	private double hjobrate;
	private Hardware hw;
	private int njobs;
	private double realTimeJobsProb;
	private SchedulingPolicy scheduler;
	public double time_sim = 1000000d;

	// private IQueue q = new Queue(1);
	// public IQueue jobended = new Queue(0);

	public IQueue jobended = new FIFO();

	public Simulator(Hardware _hw, int _njobs, int _quantum, double _classificationRate, double _realTimeJobsProb, double _hjobrate, int _avgta) {

		Simulator.tq = _quantum;
		this.njobs = _njobs;
		this.classificationRate = _classificationRate;
		this.realTimeJobsProb = _realTimeJobsProb;
		this.hjobrate = _hjobrate;
		this.hw = _hw;
		this.avgta = _avgta;

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
				 */
				job.setRcpu((int) randomJob.nextUniform(tq, time_sim / 100));
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
			if (job.classification == 0)
				cpujobs++;
			else
				gpujobs++;
			Event ev = new Event(job, Event.ENQUEUE, job.timeArrival);
			events.insertEvent(ev);
		}
		System.out.println("RT " + rtjobs + " - User " + ujobs);
		System.out.println("CPU " + cpujobs + " - GPU " + gpujobs);
		// System.out.println(realTimeJobsProb);
		hw.infosystem();
		System.out.println("done");

//		scheduler = new HPSSimScheduler(FIFO.class);
		scheduler = new CompletelyFairScheduler(hw, events);
	}

	private Event clock() throws Exception {
		Event ev = null;
		try {

			ev = events.pop();

			switch (ev.type) {
			case Event.ENQUEUE:
				System.out.print("ENQUEUE " + ev.time + " ");
				// call scheduler to rearrange start time
				// Insert a Job in Queue
				// scheduler.schedule(events, ev, dev, q);
				EventWorkFlow.enqueue(scheduler, events, ev, hw);
				System.out.print("Queue | ");
				scheduler.printjob();
				break;
			case Event.RUN:
				System.out.print("RUN " + ev.time + " ");
				EventWorkFlow.run(scheduler, events, ev, hw);
				System.out.print("Queue | ");
				scheduler.printjob();
				break;
			case Event.RESCHEDULE: // each RESCHEDULE events has job = null
				// rearrange priority on queued jobs and set next run job
				// call scheduler
				System.out.print("RESCHEDULE " + ev.time + " ");
				EventWorkFlow.reschedule(scheduler, events, ev, hw);
				System.out.print("Queue | ");
				scheduler.printjob();
				break;
			case Event.FINALIZE:
				// deallocate device and recall scheduling procedure
				// free device
				System.out.print("FINALIZE " + ev.time + " ");
				EventWorkFlow.finalize(scheduler, events, ev, hw, jobended);
				break;
			case Event.REQUEUE:
				// Add old Job in queue set reschelude attrib for job ++
				System.out.print("REQUEUE " + ev.time + " ");
				EventWorkFlow.requeue(scheduler, events, ev, hw);
				System.out.print("Queue | ");
				scheduler.printjob();
				break;
			case Event.NULL:
				/* NOT used */
				break;
			}

			System.out.println();
			System.out.println("----------------------------------------");

			return ev;
		} catch (Exception e) {
			throw e;
		}

	}

	public void setNewQtime(int nqt) {
		this.tq = nqt;
	}

	@Override
	public void run() {
		try {
			simulate();
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(e);
		}
	}

	public synchronized void simulate() throws Exception {
		System.out.println("Start simulation...");
		System.out.println("Current Event - Ta - Next Event Generate - Ta - Device Status - Queue Size | Queue");
		int lastEvTime = 0;
		int i = 0;
		synchronized (events) {
			while (lastEvTime < (time_sim) && (!events.list.isEmpty())) {

				Event ev = clock();
				lastEvTime = ev.time;
				// printProgBar((int) (lastEvTime / time_sim * 100));

				// this.wait(1);

				i++;
			}
		}

		// q.printpriority();
		// jobended.printjob();
		System.out.println("");
		jobended.printJobsStat();
		System.out.println("");
		System.out.println(i);
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
