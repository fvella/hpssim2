package hpssim.scheduler.policy.scheduling;

import hpssim.hardware.Hardware;
import hpssim.scheduler.policy.queue.HPSSimQueue;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.simulator.Event;
import hpssim.simulator.EventList;
import hpssim.simulator.Job;
import hpssim.simulator.Simulator;

public class HPSSimScheduler implements IScheduler {

	private HPSSimQueue queue;
	private int timeslice;

	public HPSSimScheduler(Class<? extends HPSSimQueue> queueType, int timeslice) {
		try {
			queue = queueType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		this.timeslice = timeslice;
	}

	@Override
	public void execute(Event ev, Hardware hw, int timeEv, EventList evl) throws Exception {
		// System.out.println("RUN");

		int indexGPUjob;
		boolean gpurun = false;
		Job j = null;
		if (hw.getCPUfree() == 0) {
			try {
				evl.insertEvent(new Event(null, Event.RESCHEDULE, timeEv + 1));
				System.out.print("RESCHEDULE " + (timeEv + 1) + " " + queue.size() + " ");
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
				if (j.getRescheduled() == 0) { // if first execution set start run
											// time, remaning time
					j.setExecutionTime(timeEv);
					j.setQueueTime(timeEv);

					if (gpurun) {
						// System.out.print(" GPU ");
						j.setRemainingTime(j.getExecutionTimeGPU());
					} else {
						j.setRemainingTime(j.getExecutionTimeCPU());
					}
				}
				if (gpurun) {
					// System.out.println("GPUJOB" + gpurun);
					hw.assignJobtoGPU(j);
				} else {
					hw.assignJobtoCPU(j);
				}
				if ((j.getRemainingTime() > timeslice) && (!gpurun)) {
					// REQUEUE //SETTA LA NUOVA PRIORITA' //SETTA IL TEMPO
					// RIMANENTE
					// j.rescheduled++;
					evl.insertEvent(new Event(j, Event.REQUEUE, timeEv + timeslice));
					System.out.print("REQUEUE " + (timeEv + timeslice) + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + queue.size() + " ");
					// evl.insertEvent(ev1);
				} else { // FINALIZE
					evl.insertEvent(new Event(j, Event.FINALIZE, timeEv + j.getRemainingTime()));
					System.out.print("FINALIZE " + (timeEv + j.getRemainingTime()) + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + queue.size() + " ");
					// evl.insertEvent(ev1);
				}
			}
		}
	}

	@Override
	public void enqueue(EventList evl, Job job, int time) {
		queue.insert(job);
		Event ev1 = new Event(job, Event.RUN, time);
		evl.insertEvent(ev1);
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

	@Override
	public void finalize(EventList evl, Event ev, Hardware hw, IQueue result) {
		Job j;
		Event ev1;
		j = hw.teminate(ev.job.id);
		j.setExecutionTime(j.getExecutionTime() + j.getRemainingTime());
		j.setRemainingTime(0);
		j.setTfinalize(ev.time);
		// add job to terminated job for stats
		result.insert(j);
		ev1 = new Event(null, Event.RESCHEDULE, ev.time + 1);
		evl.insertEvent(ev1);
		System.out.print("null " + "n/a" + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + size() + " ");
		System.out.print("Queue | ");
		printjob();
	}

	@Override
	public int getProcessiInElaborazione(Hardware hw) {
		int elcpu = hw.numcpus() - hw.getCPUfree();
		int elgpu = hw.numgpus() - hw.getGPUfree();
		return elcpu + elgpu;
	}

	@Override
	public int getProcessiInCodaCPU() {
		return queue.getLenghtQueueCPU();
	}

	@Override
	public int getProcessiInCodaGPU() {
		return queue.getLenghtQueueGPU();
	}

	@Override
	public void disableLog() {
		// TODO Auto-generated method stub
		
	}

}
