package hpssim.simulator;

import hpssim.scheduler.policy.scheduling.CompletelyFairScheduler;

public class Job /* implements Comparable<Job> */{
	/*
	 * JOB RECORD T_a Classification PriorityProcess PrioritySched DeviceSched
	 * RunCPU RunGPU TimeRemaining TqTeTr
	 */

	public int timeArrival; /* arrival time */
	public int classification; /* classification: 0 cpu 1 gpu */
	public int processPriority; /* process priority */
	public int schedulingPriority; /* scheduling priority */
	public int deviceScheduled; /* scheduled device */
	public int executionTimeCPU;
	public int executionTimeGPU; /* execution time (cpu or gpu) */
	public int remainingTime; /* remaining time */
	public int queueTime; /* queue time */
	public int executionTime; /* execution time */
	public int tfinalize; /**/
	public int rescheduled;
	public int type; /* O realttime 1 user */
	public final int id;

	public Integer vruntime; /* valore per CFS */
	public int nice;
	public Integer lastExecutionTime;
	public int queue;
	
	/*
	 * Costructor
	 */
	
	public Job(int ta, int rcpu, int rgpu, int type, int nice, int vruntime, int current_id) {
		this.timeArrival = ta;
		this.executionTimeCPU = rcpu;
		this.executionTimeGPU = rgpu;
		this.queueTime = 0;
		this.executionTime = 0;
		this.tfinalize = 0;
		this.rescheduled = 0;
		this.processPriority = 0;
		this.schedulingPriority = 0;
		this.id = current_id;
		
		this.nice = nice;
		this.vruntime = vruntime;
	}
	
	public Job(int ta, int rcpu, int rgpu, int type, int current_id) {
		this.timeArrival = ta;
		this.executionTimeCPU = rcpu;
		this.executionTimeGPU = rgpu;
		this.queueTime = 0;
		this.executionTime = 0;
		this.tfinalize = 0;
		this.rescheduled = 0;
		this.processPriority = 0;
		this.schedulingPriority = 0;
		this.id = current_id;
		
		this.nice = 0;
		this.vruntime = 0;
	}

	public Job(int ta, int current_id) {
		this.timeArrival = ta;
		this.queueTime = 0;
		this.executionTime = 0;
		this.tfinalize = 0;
		this.rescheduled = 0;
		this.id = current_id;
		this.processPriority = 0;
		this.schedulingPriority = 0;
		
		this.nice = 0;
		this.vruntime = 0;
	}

//	public Job(int current_id) {
//		this.id = current_id;
//		
//		this.nice = 0;
//		this.vruntime = 0;
//	}

	/*
	 * Set methods
	 */
	public void printJob() {
		System.out.println(toString());
	}

//	public void printjobstat() {
//		System.out.println(
//				  " id " + this.id  
//				+ " Nice " + nice 
//				+ " TimeArrival " + this.timeArrival
//				+ " Type " + getType() 
//				+ " Classification " + (classification==0?"CPU":"GPU")
//				+ " Rescheduled " + this.rescheduled 
//				+ " QueueTime " + this.getTq()
//				+ " ExecutionTime " + this.getTe() 
//				+ " FinalizeTime " + this.getTfinalize());
//	}
	
	public void printjobstat() {
		System.out.println(
				  " id " + this.id  
				+ " - Nice " + nice 
				+ " - Ta " + this.timeArrival
				+ " - Type " + getType() 
				+ " - Cl " + (classification==0?"CPU":"GPU")
				+ " - Resch " + this.rescheduled 
				+ " - Qt " + this.getQueueTime()
				+ " - Et " + this.getExecutionTime() 
				+ " - Ft " + this.getTfinalize());
	}
	@Override
	public String toString() {
		return "ID " + this.id + " TA " + this.timeArrival + " "
				+ getType() + " | " + "class " + this.classification
				+ " CPU and GPU timeservice " + this.getRcpu() + " "
				+ this.getRgpu();
	}
	public int getWeight() {
		return CompletelyFairScheduler.prio_to_weight[nice];
	}
	
	/*
	 * Get methods
	 */
	
	
	public String getType(){
		if(type==0) return "RT";
		return "US";
	}
	
	public int getTa() {
		return timeArrival;
	}

	public void setTa(int ta) {
		this.timeArrival = ta;
	}

	public int getClassification() {
		return classification;
	}

	public void setClassification(int classification) {
		this.classification = classification;
	}

	public int getPp() {
		return processPriority;
	}

	public void setPp(int pp) {
		this.processPriority = pp;
	}

	public int getPs() {
		return schedulingPriority;
	}

	public void setPs(int ps) {
		this.schedulingPriority = ps;
	}

	public int getDevicesched() {
		return deviceScheduled;
	}

	public void setDevicesched(int devicesched) {
		this.deviceScheduled = devicesched;
	}

	public int getRcpu() {
		return executionTimeCPU;
	}

	public void setRcpu(int rcpu) {
		this.executionTimeCPU = rcpu;
	}

	public int getRgpu() {
		return executionTimeGPU;
	}

	public void setRgpu(int rgpu) {
		this.executionTimeGPU = rgpu;
	}

	public int getTrem() {
		return remainingTime;
	}

	public void setTrem(int trem) {
		this.remainingTime = trem;
	}

	public int getTq() {
		return queueTime;
	}

	public void setTq(int tq) {
		this.queueTime = tq;
	}

	public int getTe() {
		return executionTime;
	}

	public void setTe(int te) {
		this.executionTime = te;
	}

	public int getTfinalize() {
		return tfinalize;
	}

	public void setTfinalize(int tfinalize) {
		this.tfinalize = tfinalize;
	}

	public int getRescheduled() {
		return rescheduled;
	}

	public void setRescheduled(int rescheduled) {
		this.rescheduled = rescheduled;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	// public int compareTo(Job o2) {
	// if (this.ps > ((Job)o2).ps) return 1;
	// return -1;
	// }
	//
	// ASC priority compare
	// public int compareTo(Job o2) {
	// return (schedulingPriority > o2.schedulingPriority ? 1 :
	// (schedulingPriority == o2.schedulingPriority ? 0 : -1));
	// }

	public int getPriority() {
		return schedulingPriority;
	}

	public int getTimeArrival() {
		return timeArrival;
	}

	public void setTimeArrival(int timeArrival) {
		this.timeArrival = timeArrival;
	}

	public int getProcessPriority() {
		return processPriority;
	}

	public void setProcessPriority(int processPriority) {
		this.processPriority = processPriority;
	}

	public int getSchedulingPriority() {
		return schedulingPriority;
	}

	public void setSchedulingPriority(int schedulingPriority) {
		this.schedulingPriority = schedulingPriority;
	}

	public int getDeviceScheduled() {
		return deviceScheduled;
	}

	public void setDeviceScheduled(int deviceScheduled) {
		this.deviceScheduled = deviceScheduled;
	}

	public int getExecutionTimeCPU() {
		return executionTimeCPU;
	}

	public void setExecutionTimeCPU(int executionTimeCPU) {
		this.executionTimeCPU = executionTimeCPU;
	}

	public int getExecutionTimeGPU() {
		return executionTimeGPU;
	}

	public void setExecutionTimeGPU(int executionTimeGPU) {
		this.executionTimeGPU = executionTimeGPU;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public int getQueueTime() {
		return queueTime;
	}

	public void setQueueTime(int queueTime) {
		this.queueTime = queueTime;
	}

	public int getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public Integer getVruntime() {
		return vruntime;
	}

	public void setVruntime(Integer vruntime) {
		this.vruntime = vruntime;
	}

	public int getNice() {
		return nice;
	}

	public void setNice(int nice) {
		this.nice = nice;
	}
	
	public Integer getLastExecutionTime() {
		
		if(lastExecutionTime==null)
			return new Integer(executionTime);
		
		return lastExecutionTime;
	}

	public void setLastExecutionTime(int lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
	}
}
