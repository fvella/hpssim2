package hpssim.scheduler.policy.scheduling;

import hpssim.hardware.Hardware;
import hpssim.simulator.EventList;

/**
 * This is a generic scheduling policy.
 * 
 * @author
 * @version 1.1
 */
public interface SchedulingPolicy extends IScheduler {
	public void execute(Hardware hw, int timeEv, EventList evl)	throws Exception;
}
