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

	// public HPSSimScheduler(int _type) {
	// type = _type;
	// }

	// public HPSSimScheduler(String _type) {
	// if (_type != null) {
	// switch (_type) {
	// case "FIFO":
	// type = 0;
	// break;
	// case "RoundRobin":
	// type = 1;
	// break;
	// case "PR":
	// type = 2;
	// break;
	//
	// default:
	// type = 0;
	// break;
	// }
	// } else
	// type = 0;
	// }

	/*
	 * Other methods such as recalculate priority of all job
	 */
	public static void enqueue(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw) {
		scheduler.enqueue(evl, ev.job, ev.time);
		System.out.print("RUN " + ev.time + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + scheduler.size() + " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#run(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue)
	 */
	public static void run(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw) throws Exception {
		scheduler.execute(ev, hw, ev.time, evl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#requeue(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue)
	 */
	public static Event requeue(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw, int timeslice) {
		// System.out.println("REQUEUE");
		System.out.print("ENEQUEUE " + (ev.time) + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + scheduler.size() + " ");
		Event ev1;
		Job j = hw.teminate(ev.job.getId());
		j.setSchedulingPriority(j.getProcessPriority() - 5);
		j.setExecutionTime(j.getExecutionTime() + timeslice);
		j.setRemainingTime(j.getRemainingTime() - timeslice);
		j.setRescheduled(j.getRescheduled() + 1);
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
	public static void reschedule(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw) {
		Event ev1;
		scheduler.newpriority();
		ev.time += 0;
		ev1 = new Event(null, Event.RUN, ev.time);
		evl.insertEvent(ev1);
		System.out.print("RUN " + ev.time + " " + hw.getCPUfree() + " " + hw.getGPUfree() + " " + scheduler.size() + " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hpssim.scheduler.IScheduler#finalize(hpssim.EventList, hpssim.Event,
	 * hpssim.DeviceList, hpssim.Queue, hpssim.Queue)
	 */
	public static void finalize(SchedulingPolicy scheduler, EventList evl, Event ev, Hardware hw, IQueue result) {
		scheduler.finalize(evl, ev, hw, result);
	}

}
