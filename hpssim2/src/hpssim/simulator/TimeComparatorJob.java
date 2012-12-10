package hpssim.simulator;

import java.util.Comparator;

/**
 * 
 * @author Luigi Giorgio Claudio Mancini
 */
public class TimeComparatorJob implements Comparator<Job> {

	@Override
	public int compare(Job o1, Job o2) {
		return (getTime(o1) > getTime(o2) ? 1 : (getTime(o1) == getTime(o2) ? 0 : -1));
}
	
	public static int getTime(Job request) {
		return request.classification == 0 ? request.getExecutionTimeCPU() : request.getExecutionTimeGPU();
	}
}
