package hpssim.scheduler.policy.scheduling;

import hpssim.hardware.Hardware;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.simulator.Event;
import hpssim.simulator.EventList;

/**
 * This is a generic scheduling policy.
 * 
 * @author
 * @version 1.1
 */
public interface SchedulingPolicy extends IScheduler {
	public void execute(Event ev,Hardware hw, int timeEv, EventList evl)	throws Exception;
	public void finalize(EventList evl, Event ev, Hardware hw, IQueue result);
}
