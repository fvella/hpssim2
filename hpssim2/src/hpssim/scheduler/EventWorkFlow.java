/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hpssim.scheduler;

import hpssim.hardware.Hardware;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.scheduler.policy.scheduling.SchedulingPolicy;
import hpssim.simulator.Event;
import hpssim.simulator.EventList;
import hpssim.simulator.Job;
import hpssim.simulator.Simulator;

/**
 * 
 * @author fla
 */
public class EventWorkFlow {


//	public HPSSimScheduler(int _type) {
//		type = _type;
//	}

//	public HPSSimScheduler(String _type) {
//		if (_type != null) {
//			switch (_type) {
//			case "FIFO":
//				type = 0;
//				break;
//			case "RoundRobin":
//				type = 1;
//				break;
//			case "PR":
//				type = 2;
//				break;
//
//			default:
//				type = 0;
//				break;
//			}
//		} else
//			type = 0;
//	}

	/*
	 * Other methods such as recalculate priority of all job
	 */
	public static void enqueue(SchedulingPolicy scheduler,EventList evl, Event ev, Hardware hw) {
		scheduler.enqueue(ev.job, ev.time);
		Event ev1 = new Event(null, Event.RUN, ev.time);
		evl.insertEvent(ev1);
		System.out.print("RUN " + ev.time + " " + hw.getCPUfree() + " "
				+ hw.getGPUfree() + " " + scheduler.size() + " ");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#run(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue)
	 */
	public static void run(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw) throws Exception{
		scheduler.execute(hw, ev.time, evl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#requeue(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue)
	 */
	public static Event requeue(SchedulingPolicy scheduler,EventList evl, Event ev, Hardware hw) {
		// System.out.println("REQUEUE");
		System.out.print("ENEQUEUE " + (ev.time) + " " + hw.getCPUfree() + " "
				+ hw.getGPUfree() + " " + scheduler.size() + " ");
		Event ev1;
		Job j = hw.teminate(ev.job.getId());
		j.setPs(j.getPriority() - 5);
		j.remainingTime -= Simulator.tq;
		j.executionTime += Simulator.tq;
		j.rescheduled++;
		ev1 = new Event(j, Event.ENQUEUE, ev.time);
		evl.insertEvent(ev1);
		return ev1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#reschedule(hpssim.EventList,
	 * hpssim.Event, hpssim.DeviceList, hpssim.Queue)
	 */
	public static void reschedule(SchedulingPolicy scheduler,EventList evl, Event ev, Hardware hw) {
		Event ev1;
		scheduler.newpriority();
		ev.time += 0;
		ev1 = new Event(null, Event.RUN, ev.time);
		evl.insertEvent(ev1);
		System.out.print("RUN " + ev.time + " " + hw.getCPUfree() + " "
				+ hw.getGPUfree() + " " + scheduler.size() + " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#finalize(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue, hpssim.Queue)
	 */
	public static void finalize(SchedulingPolicy scheduler,EventList evl, Event ev, Hardware hw, IQueue result) {
		Job j;
		Event ev1;
		j = hw.teminate(ev.job.id);
		j.executionTime += j.remainingTime;
		j.remainingTime = 0;
		j.setTfinalize(ev.time);
		// add job to terminated job for stats
		result.insert(j);
		ev1 = new Event(null, Event.RESCHEDULE, ev.time + 1);
		evl.insertEvent(ev1);
		System.out.print("null " + "n/a" + " " + hw.getCPUfree() + " "
				+ hw.getGPUfree() + " " + scheduler.size() + " ");
		System.out.print("Queue | ");
		scheduler.printjob();
	}

}
