/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hpssim.hardware;

import hpssim.simulator.Job;

import java.util.ArrayList;

/**
 *
 * @author fla
 */
public class Hardware {
	
    private int ngpu = 0;
    private int ncpu = 0;
    private int cpusfree = 0;
    private int gpusfree = 0;
    private int totaldevice = 0;
    private ArrayList<Device> mySystem;
    
    /*Create device*/
    public Hardware(int ncpu, int ngpu){
        mySystem = new ArrayList<Device>();        
        
        this.ncpu = ncpu;
        this.ngpu = ngpu;
        
        //per ogni cpu crea un device di tipo 0 (cpu)
        for (int i = 0; i < ncpu; i++){
            mySystem.add(new Device(DeviceType.CPU));
            this.cpusfree++;
        }
        
        //per ogni gpu crea un device di tipo 0 (cpu)
        for (int i = 0; i < ngpu; i++){
            mySystem.add(new Device(DeviceType.GPU));
            this.gpusfree++;
        }
        
        totaldevice = this.ncpu+this.ngpu;
    }
    
    public void infosystem(){
        for (int i = 0; i < totaldevice; i++){
        System.out.print(mySystem.get(i).gettype() == DeviceType.CPU ? " CPU " : " GPU " +  " ");
        }
        System.out.println();
    }
   
   
    public int getfirstCPUfree(){        
        int index = -1;
        //System.out.print(" cpusfree ? " + this.cpusfree + " <- INDICA CPU");
        if (this.cpusfree < 1){
           // System.out.print(" cpusfree < 1 ");
            return index;
        }
        else{
            //System.out.print(" faccio il for |");
            for (int i = 0; i < this.ncpu; i++){
                //System.out.print(" funzioni ? ");
                if (mySystem.get(i).getstatus()== 0){
                    //System.out.print(" cpu index free is " + i +" ");
                    index = i;
                    break;
                }
            }
            return index;
        }
    }
   
    public int getfirstGPUfree(){        
        int index = -1;
        if (this.gpusfree == 0) return index;
        else{
            for (int i = ncpu; i < totaldevice; i++){
                if (mySystem.get(i).getstatus()== 0){
                    index = i;
                    break;
                }                
            }
            return index;
        }
    }
    public int assignJobtoCPU(Job j){
        int indexCPUfree = 0;
        if (this.cpusfree > 0){            
            indexCPUfree = this.getfirstCPUfree();
            //System.out.println(" ASSIGN TO CPU " + indexCPUfree + " " +this.cpusfree);
            mySystem.get(indexCPUfree).runjob(j);
            this.cpusfree--;
            return 1;
        }
        else
            return 0;        
    }
    public int assignJobtoGPU(Job j){        
        if (this.cpusfree > 0 && this.gpusfree > 0){
            int indexCPUfree;
            int indexGPUfree;
            /*to run in gpu 1 cpu must be free*/
            indexCPUfree = this.getfirstCPUfree();
            indexGPUfree = this.getfirstGPUfree();
            mySystem.get(indexCPUfree).runjob(j);
            mySystem.get(indexCPUfree).setcpulock(indexGPUfree);
            mySystem.get(indexGPUfree).runjob(j);
            this.cpusfree--;
            this.gpusfree--;
            return 1;
        }
        else return 0;
                       
    }
    public Job teminate(int jobid){
        Job j = null;
        int gpuid;
        for (int i = 0; i < ncpu; i++){
            if (mySystem.get(i).whorun() == jobid){                
                /*if cpu lock is == 0 job runs only cpu*/
                if (mySystem.get(i).getgpuidlock() == 0){
                    j = mySystem.get(i).terminatejob();
                    this.cpusfree++;                    
                }
                else{
                    /*job gpu free gpuid */
                    gpuid = mySystem.get(i).getgpuidlock();
                    j = mySystem.get(gpuid).terminatejob();                    
                    mySystem.get(i).terminatejob();
                    this.cpusfree++;
                    mySystem.get(i).setcpulock(0);
                    this.gpusfree++;                    
                }
                break;
            }            
        }
//       if(j==null)
//    	   throw new NullPointerException();
        return j;
    }
    public int numcpus(){
        return this.ncpu;
    }
    public int numgpus(){
        return this.ngpu;
    }
    public int getCPUfree(){
        return this.cpusfree;
    }
    public int getGPUfree(){
        return this.gpusfree;
    }

	public int getNumGPU() {
		return ngpu;
	}
}

