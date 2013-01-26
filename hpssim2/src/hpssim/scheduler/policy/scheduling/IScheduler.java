package hpssim.scheduler.policy.scheduling;

import hpssim.hardware.Hardware;
import hpssim.simulator.EventList;
import hpssim.simulator.Job;

import java.io.Serializable;


public interface IScheduler extends SchedulingPolicy  {

	

	public abstract void printjob();

	public abstract int size();

	public abstract void newpriority();

   

	public abstract int getProcessiInElaborazione(Hardware hw);
	
	public abstract int getProcessiInCodaCPU();
	
	public abstract int getProcessiInCodaGPU();
	
	public abstract void disableLog();
}