package hpssim.scheduler.policy.queue;

import java.io.Serializable;

import hpssim.simulator.Job;

public interface IQueue extends Serializable{

	public abstract Job getFirstCPUJob();

	public abstract Job getFirstGPUJob();

	public abstract void sort();

	public abstract int size();

	public abstract void printpriority();

	public abstract void printjob();

	public abstract void printJobsStat();

	public abstract Job getJob(int index);

	public abstract int getIndexFirstGPUjob();

	public abstract int getIndexFirstCPUjob();

	public abstract void newpriority();

	public abstract void insert(Job request);

	public abstract Job extract();

}