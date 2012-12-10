package hpssim.scheduler.policy.scheduling;

import hpssim.simulator.Job;

import java.io.Serializable;


public interface IScheduler {
	public abstract int enqueue(Job job, int time);

	public abstract Job extract();

	public abstract void printjob();

	public abstract int size();

	public abstract void newpriority();

}