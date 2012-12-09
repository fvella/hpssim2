package hpssim.hardware;

import hpssim.simulator.Job;

public class Device {

	private int cpulock; // id if run gpu job controlled by gpu id; else 0
	private Job job;
	private int processed;
	private int status; // status 0 free 1 busy for job. if CPU device is locked from gpu
	private int tin; // start execution time (used only for GPU)
	private DeviceType type; // 0 CPU 1 GPU

	public Device(DeviceType type) {
		this.type = type;
		this.status = 0;
		this.processed = 0;
		this.cpulock = 0;

	}

	public int getgpuidlock() {
		return this.cpulock;
	}

	public int getstatus() {
		return this.status;
	}

	public int gettin() {
		return this.tin;
	}

	public DeviceType gettype() {
		return this.type;
	}

	public void runjob(Job j) {
		this.job = j;
		this.processed++;
		this.setstatus(1);
	}

	public void setcpulock(int gpuid) {
		this.cpulock = gpuid;
	}

	public void setstatus(int status) {
		this.status = status;
	}

	public void settin(int tin) {
		this.tin = tin;
	}

	public Job terminatejob() {
		Job j;
		this.setstatus(0);
		j = this.job;
		this.job = null;
		return j;
	}

	public int whorun() {
		if (this.status == 1)
			return job.id;
		else
			return -100;
	}
}
