package hpssim.simulator;

import hpssim.scheduler.Configurator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistiche {

	public Statistiche(Configurator conf) {
		
		configuration = conf;
		try {
			PrintWriter outputnew = new PrintWriter(new FileWriter("c:/out.csv",false));
			outputnew.printf("%s\r\n", "TIME;CPULOAD;QueueSize;ldavg_1;ldavg_5;ldavg_15;");
			outputnew.close();
			
			output = new PrintWriter(new FileWriter("c:/out.csv",true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private PrintWriter output ;
	
	private hpssim.scheduler.Configurator configuration;

	private int cpuFree, gpuFree;
	private double lastExeTime;
	private List<Job> listaJob;
	private int processiInCoda;

	private int processiInCodaCPU;
	private int processiInCodaGPU;

	private int processiInElaborazione;

	private int processiNelSistema;
	
	public long mediaCoda = 0;
	
//	#define FSHIFT 11 /* nr of bits of precision */
//	#define FIXED_1 (1<<FSHIFT) /* 1.0 as fixed-point */
//	#define LOAD_FREQ (5*HZ) /* 5 sec intervals  = 5000 millisecond*/
//	#define EXP_1 1884 /* 1/exp(5sec/1min) as fixed-point */
//	#define EXP_5 2014 /* 1/exp(5sec/5min) */
//	#define EXP_15 2037 /* 1/exp(5sec/15min) */
//	
//	#define CALC_LOAD(load,exp,n) \
//	load *= exp; \
//	load += n*(FIXED_1-exp);\
//	load >>= FSHIFT;
	
//	 static inline void calc_load(unsigned long ticks)
//	 {
//	   unsigned long active_tasks; /* fixed-point */
//	   static int count = LOAD_FREQ;
//	
//	 count -= ticks;
//	 if (count > 0) {
//	    count += LOAD_FREQ;
//	 active_tasks = count_active_tasks( );
//	 CALC_LOAD(avenrun[0], EXP_1, active_tasks);
//	 CALC_LOAD(avenrun[1], EXP_5, active_tasks);
//	 CALC_LOAD(avenrun[2], EXP_15, active_tasks);
//	   }
//	 }
		
	private long runq_sz = 0l, plist_sz = 0l, ldavg_1 = 0l, ldavg_5 = 0l, ldavg_15 = 0l, LOAD_FREQ = 5000l, FIXED_1 = 1l << 11l;
	
	private double count = LOAD_FREQ;
	public void calc_load(int eventtime){
		
		if((count-eventtime) <= 0){
			count = eventtime + LOAD_FREQ;
			calc_load();
		}
		
	}
	
	public void calc_load(){
		
         double active_tasks = getProcessiNelSistema();
         
         ldavg_1 *= 1884l; 
         ldavg_1 += active_tasks*(FIXED_1-1884l);
         long l = ldavg_1 >> 11l;
         ldavg_1 = l;
         
         ldavg_5 *= 2014l; 
         ldavg_5 += active_tasks*(FIXED_1-2014l);
         l = ldavg_5 >> 11l;
         ldavg_5 = l;
         
         ldavg_15 *= 2037l; 
         ldavg_15 += active_tasks*(FIXED_1-2037l);
         l = ldavg_15 >> 11l;
         ldavg_15 = l;
	}
	
	private int TIME_RWFILE = 1000;
	private int countRW = TIME_RWFILE;
	public void elaboraStatistiche(int eventtime) {
		
		calc_load(eventtime);
		
		if((countRW-eventtime) <= 0){
			countRW = eventtime + TIME_RWFILE;
			
			try {
				scriviFile((eventtime)/1000 + ";" + getCpuLoad() + ";" + getRunq_sz() +";" + ldavg_1 + ";" + ldavg_5 + ";" + ldavg_15 + ";");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void scriviFile(String content) throws IOException{
		try {
		    output.printf("%s\r\n", content);
		} 
		catch (Exception e) {}
	}
	
	public long getRunq_sz() {
		runq_sz = new Long(getProcessiInCoda());
		return runq_sz;
	}

	public double getPlist_sz() {
		return plist_sz;
	}

	public void setPlist_sz(long plist_sz) {
		this.plist_sz = plist_sz;
	}

	public long getLdavg_1() {
		return ldavg_1;
	}

	public void setLdavg_1(long ldavg_1) {
		this.ldavg_1 = ldavg_1;
	}

	public long getLdavg_5() {
		return ldavg_5;
	}

	public void setLdavg_5(long ldavg_5) {
		this.ldavg_5 = ldavg_5;
	}

	public double getLdavg_15() {
		return ldavg_15;
	}

	public void setLdavg_15(long ldavg_15) {
		this.ldavg_15 = ldavg_15;
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
	
	public long mediaUsoCPU = 0;
	public void setCPUFree(int cpu) {
		cpuFree = cpu;
		mediaUsoCPU += getCpuLoadPerc();
	}
	
	public long mediaUsoGPU = 0;
	public void setGPUFree(int gpu) {
		gpuFree = gpu;
		mediaUsoGPU+=getGpuLoadPerc();
	}

	public void setLastExeTime(double lastExeTime) {
		this.lastExeTime = lastExeTime;
	}

	public void setProcessiInCoda(int processiInCoda) {
		this.processiInCoda = processiInCoda;
		mediaCoda+=processiInCoda;
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

	public void endStatistiche() {
		output.close();
	}

}
