package hpssim.simulator;

import java.util.Comparator;

public class PriorityComparatorJob implements Comparator<Job> {

	@Override
	public int compare(Job o1, Job o2) {
		return (o1.schedulingPriority > o2.schedulingPriority ? 1 : (o1.schedulingPriority == o2.schedulingPriority ? 0 : -1));
}

}
