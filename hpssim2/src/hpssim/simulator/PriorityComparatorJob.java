package hpssim.simulator;

import java.util.Comparator;

/**
 * @author Luigi Giorgio Claudio Mancini
 */
public class PriorityComparatorJob implements Comparator<Job> {

	@Override
	public int compare(Job o1, Job o2) {
		return (o1.getSchedulingPriority() > o2.getSchedulingPriority() ? 1 : (o1.getSchedulingPriority() == o2.getSchedulingPriority() ? 0 : -1));
}

}
