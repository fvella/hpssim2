package hpssim.simulator;

import hpssim.scheduler.Configurator;

import java.util.ArrayList;
import java.util.List;

public class Statistiche {
	
	private hpssim.scheduler.Configurator configuration;
	
	
	private int lastExeTime;
	private int processiNelSistema;
	private int processiInElaborazione;
	private int processiInCoda;
	
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

	public int getCpuLoad() {
		
		if(configuration.hw.numcpus() == 0)
			return 0;
		
		if(configuration.hw.getCPUfree() == 0)
			return 100;

		else return (configuration.hw.getCPUfree() * 100 / configuration.hw.numcpus() );
	}

	public int getGpuLoad() {
		if( configuration.hw.numgpus() == 0)
			return 0;
		
		if(configuration.hw.getGPUfree() == 0)
			return 100;

		else return (configuration.hw.getGPUfree() * 100 / configuration.hw.numgpus() );
	}

	public int getProcessiNelSistema() {
		return processiNelSistema;
	}

	public void setProcessiNelSistema(int processiNelSistema) {
		this.processiNelSistema = processiNelSistema;
	}

	public int getProcessiInElaborazione() {
		
		processiInElaborazione = processiNelSistema 
				- (configuration.hw.numcpus() - configuration.hw.getCPUfree())
				- (configuration.hw.numgpus() - configuration.hw.getGPUfree());
		
		return processiInElaborazione;
	}


	public int getProcessiInCoda() {
		processiInCoda = processiNelSistema - getProcessiInElaborazione() ;
		return processiInCoda;
	}


	public List<Job> getListaJob() {
		if(listaJob==null)
			listaJob = new ArrayList<>();
		return listaJob;
	}

	public int getLastExeTime() {
		return lastExeTime;
	}

	public void setLastExeTime(int lastExeTime) {
		this.lastExeTime = lastExeTime;
	}

	
}
