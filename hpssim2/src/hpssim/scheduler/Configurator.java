package hpssim.scheduler;

import hpssim.hardware.Hardware;

public class Configurator {
	
	public Configurator(Hardware hw, int njobs, int qt, double classificationRate, double realTimeJobsProb, double percentOpenCLjob, int avgta, int simTime, Class sched, Class queue, boolean endJob) throws Exception {
		
		if(hw.numcpus()+hw.getNumGPU() == 0)
			throw new Exception("Inserire almeno in device");
		
		this.hw = hw;
		this.njobs = njobs;
		if(njobs==0)
			throw new Exception("Inserire almeno un job");
		
		this.classificationRate = classificationRate;
		this.realTimeJobsProb = realTimeJobsProb;
		this.percentOpenCLjob = percentOpenCLjob;
		
		if(avgta<=0)
			throw new Exception("La media di interarrivo deve essere maggiore di zero");
		this.avgta = avgta;
		
		if(simTime<=0)
			throw new Exception("Il tempo di simulazione deve essere maggiore di zero");
		this.simTime = simTime;
		
		this.qt = qt;
		if(qt >= (simTime / 100))
			throw new Exception("Time slice deve essere minore di " + (simTime / 100));
		
		this.scheduler = sched;
		this.queue = queue;
		this.endJob = endJob;
	}
	public Hardware hw;
	public int njobs, qt, simTime, avgta;
	public double classificationRate, realTimeJobsProb, percentOpenCLjob;
	public Class scheduler, queue;
	public boolean endJob;
}
