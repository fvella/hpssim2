package hpssim.scheduler.policy.scheduling;

import hpssim.hardware.Hardware;
import hpssim.scheduler.policy.queue.HPSSimQueue;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.simulator.Event;
import hpssim.simulator.EventList;
import hpssim.simulator.Job;
import hpssim.simulator.Simulator;

public class HPSSimScheduler implements SchedulingPolicy {

	private IQueue queue;

	public HPSSimScheduler() {
		queue = new HPSSimQueue();
	}

	@Override
	public void execute(Hardware hw, int timeEv, EventList evl)
			throws Exception {
		// System.out.println("RUN");

		int indexGPUjob;
		boolean gpurun = false;
		Job j = null;
		if (hw.getCPUfree() == 0) {
			try {
				evl.insertEvent(new Event(null, Event.RESCHEDULE, timeEv + 1));
				System.out.print("RESCHEDULE " + (timeEv + 1) + " "
						+ queue.size() + " ");
				// evl.insertEvent(ev1);
			} catch (Exception e) {
				throw e;
			}

		} else {
			while ((hw.getCPUfree() > 0) && (queue.size() > 0)) {
				indexGPUjob = queue.getIndexFirstGPUjob();
				// System.out.print(" q.size " + q.size()+ " ");
				if (indexGPUjob >= 0 && hw.getGPUfree() > 0) {
					j = queue.getJob(indexGPUjob);
					// System.out.print(" GPU cado qui gpurun jid "
					// +j.getid()+" ");
					gpurun = true;
				} else {
					gpurun = false;
					j = queue.extract();
				}
				if (j.rescheduled == 0) { // if first execution set start run
											// time, remaning time
					j.setTe(timeEv);
					j.setTq(timeEv);

					if (gpurun) {
						// System.out.print(" GPU ");
						j.setTrem(j.executionTimeGPU);
					} else {
						j.setTrem(j.executionTimeCPU);
					}
				}
				if (gpurun) {
					// System.out.println("GPUJOB" + gpurun);
					hw.assignJobtoGPU(j);
				} else {
					hw.assignJobtoCPU(j);
				}
				if ((j.remainingTime > Simulator.tq) && (!gpurun)) {
					// REQUEUE //SETTA LA NUOVA PRIORITA' //SETTA IL TEMPO
					// RIMANENTE
					// j.rescheduled++;
					evl.insertEvent(new Event(j, Event.REQUEUE, timeEv
							+ Simulator.tq));
					System.out.print("REQUEUE " + (timeEv + Simulator.tq) + " "
							+ hw.getCPUfree() + " " + hw.getGPUfree() + " "
							+ queue.size() + " ");
					// evl.insertEvent(ev1);
				} else { // FINALIZE
					evl.insertEvent(new Event(j, Event.FINALIZE, timeEv
							+ j.remainingTime));
					System.out.print("FINALIZE " + (timeEv + j.remainingTime)
							+ " " + hw.getCPUfree() + " " + hw.getGPUfree()
							+ " " + queue.size() + " ");
					// evl.insertEvent(ev1);
				}
			}
		}
	}

	@Override
	public void enqueue(Job job, int time) {
		queue.insert(job);
	}

	@Override
	public Job extract() {
		return queue.extract();
	}

	@Override
	public void printjob() {
		queue.printjob();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public void newpriority() {
		queue.newpriority();
	}

}
