package hpssim.simulator;

import hpssim.scheduler.Configurator;

import java.util.ArrayList;
import java.util.List;

public class Statistiche {
	
	private hpssim.scheduler.Configurator configuration;
	
	
	private double lastExeTime;
	private int processiNelSistema;
	private int processiInElaborazione;
	private int processiInCoda;
	
	private int processiInCodaCPU;
	public int getProcessiInCodaCPU() {
		return processiInCodaCPU;
	}

	public void setProcessiInCodaCPU(int processiInCodaCPU) {
		this.processiInCodaCPU = processiInCodaCPU;
	}

	public int getProcessiInCodaGPU() {
		return processiInCodaGPU;
	}

	public void setProcessiInCodaGPU(int processiInCodaGPU) {
		this.processiInCodaGPU = processiInCodaGPU;
	}

	private int processiInCodaGPU;
	
	private List<Job> listaJob;

	public Statistiche(Configurator conf) {
		configuration = conf;
	}

	public hpssim.scheduler.Configurator getConfiguration() {
		return configuration;
	}

	public void setConfiguration(hpssim.scheduler.Configurator configuration) {
		this.configuration = configuration;
	}
	
	private int cpuFree,gpuFree;
	public void setCPUFree(int cpu){
		cpuFree = cpu;
	}
	public void setGPUFree(int gpu ){
		gpuFree=gpu;
	}
	
	public int getCpuLoad() {
		
		if(configuration.hw.numcpus() == 0)
			return 0;
		
		else return ((configuration.hw.numcpus() - cpuFree));
	}

	public int getGpuLoad() {
		if( configuration.hw.numgpus() == 0)
			return 0;
		
		else return ((configuration.hw.numgpus() - gpuFree));
	}
	
	public int getCpuLoadPerc() {
		
		if(configuration.hw.numcpus() == 0)
			return 0;
		
		else return (getCpuLoad() * 100 / configuration.hw.numcpus() );
	}

	public int getGpuLoadPerc() {
		if( configuration.hw.numgpus() == 0)
			return 0;
		
		else return (getGpuLoad() * 100 / configuration.hw.numgpus() );
	}

	public int getProcessiNelSistema() {
		processiNelSistema = getProcessiInElaborazione() + getProcessiInCoda();
		return processiNelSistema ;
	}

	public int getProcessiInElaborazione() {
		return processiInElaborazione;
	}

	public int getProcessiInCoda() {
		return processiInCoda;
	}

	public void setProcessiInElaborazione(int processiInElaborazione) {
		this.processiInElaborazione = processiInElaborazione;
	}

	public void setProcessiInCoda(int processiInCoda) {
		this.processiInCoda = processiInCoda;
	}

	public List<Job> getListaJob() {
		if(listaJob==null)
			listaJob = new ArrayList<>();
		return listaJob;
	}

	public double getLastExeTime() {
		return lastExeTime;
	}

	public void setLastExeTime(double lastExeTime) {
		this.lastExeTime = lastExeTime;
	}

	
}
