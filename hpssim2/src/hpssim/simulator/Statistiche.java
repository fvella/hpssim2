package hpssim.simulator;

import hpssim.scheduler.Configurator;

import java.util.ArrayList;
import java.util.List;

public class Statistiche {

	private hpssim.scheduler.Configurator configuration;

	private int cpuFree, gpuFree;
	private double lastExeTime;
	private List<Job> listaJob;
	private int processiInCoda;

	private int processiInCodaCPU;
	private int processiInCodaGPU;

	private int processiInElaborazione;

	private int processiNelSistema;

	private Double runq_sz = 0d, plist_sz = 0d, ldavg_1 = 0d, ldavg_5 = 0d, ldavg_15 = 0d;
	
	public void calc_load(){
         double active_tasks = getProcessiInCoda();
         
         ldavg_1 *= 0.1884d; 
         ldavg_1 += active_tasks*0.1884d;
         ldavg_1 = Math.round(ldavg_1 * 100) / 100.0;
         
         ldavg_5 *= 0.2014d; 
         ldavg_5 += active_tasks*0.2014d;
         ldavg_5 = Math.round(ldavg_5 * 100) / 100.0;
         
         ldavg_15 *= 0.2037d; 
         ldavg_15 += active_tasks*0.2037d; 
         ldavg_15 = Math.round(ldavg_15 * 100) / 100.0;
	}
	
	public Double getRunq_sz() {
		runq_sz = new Double(getProcessiInCoda());
		return runq_sz;
	}

	public double getPlist_sz() {
		return plist_sz;
	}

	public void setPlist_sz(double plist_sz) {
		this.plist_sz = plist_sz;
	}

	public double getLdavg_1() {
		return ldavg_1;
	}

	public void setLdavg_1(double ldavg_1) {
		this.ldavg_1 = ldavg_1;
	}

	public double getLdavg_5() {
		return ldavg_5;
	}

	public void setLdavg_5(double ldavg_5) {
		this.ldavg_5 = ldavg_5;
	}

	public double getLdavg_15() {
		return ldavg_15;
	}

	public void setLdavg_15(double ldavg_15) {
		this.ldavg_15 = ldavg_15;
	}

	public Statistiche(Configurator conf) {
		configuration = conf;
	}

	public hpssim.scheduler.Configurator getConfiguration() {
		return configuration;
	}

	public int getCpuLoad() {

		if (configuration.hw.numcpus() == 0)
			return 0;

		else
			return ((configuration.hw.numcpus() - cpuFree));
	}

	public int getCpuLoadPerc() {

		if (configuration.hw.numcpus() == 0)
			return 0;

		else
			return (getCpuLoad() * 100 / configuration.hw.numcpus());
	}

	public int getGpuLoad() {
		if (configuration.hw.numgpus() == 0)
			return 0;

		else
			return ((configuration.hw.numgpus() - gpuFree));
	}

	public int getGpuLoadPerc() {
		if (configuration.hw.numgpus() == 0)
			return 0;

		else
			return (getGpuLoad() * 100 / configuration.hw.numgpus());
	}

	public double getLastExeTime() {
		return lastExeTime;
	}

	public List<Job> getListaJob() {
		if (listaJob == null)
			listaJob = new ArrayList<>();
		return listaJob;
	}

	public int getProcessiInCoda() {
		return processiInCoda;
	}

	public int getProcessiInCodaCPU() {
		return processiInCodaCPU;
	}

	public int getProcessiInCodaGPU() {
		return processiInCodaGPU;
	}

	public int getProcessiInElaborazione() {
		return processiInElaborazione;
	}

	public int getProcessiNelSistema() {
		processiNelSistema = getProcessiInElaborazione() + getProcessiInCoda();
		return processiNelSistema;
	}

	public void setConfiguration(hpssim.scheduler.Configurator configuration) {
		this.configuration = configuration;
	}

	public void setCPUFree(int cpu) {
		cpuFree = cpu;
	}

	public void setGPUFree(int gpu) {
		gpuFree = gpu;
	}

	public void setLastExeTime(double lastExeTime) {
		this.lastExeTime = lastExeTime;
	}

	public void setProcessiInCoda(int processiInCoda) {
		this.processiInCoda = processiInCoda;
	}

	public void setProcessiInCodaCPU(int processiInCodaCPU) {
		this.processiInCodaCPU = processiInCodaCPU;
	}

	public void setProcessiInCodaGPU(int processiInCodaGPU) {
		this.processiInCodaGPU = processiInCodaGPU;
	}

	public void setProcessiInElaborazione(int processiInElaborazione) {
		this.processiInElaborazione = processiInElaborazione;
	}

}
