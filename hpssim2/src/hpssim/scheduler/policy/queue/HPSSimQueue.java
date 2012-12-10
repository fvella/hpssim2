/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;
import hpssim.simulator.PriorityComparatorJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 
 * @author Igor Neri <igor@cimice.net>
 */
public class HPSSimQueue implements IQueue {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5299958343199082163L;

	/**
	 * The queue of requests sorted using priority policy.
	 */
	protected ArrayList<Job> requestsQueue;

	private int currentMaxPriority;
	private int currentPriority;
	private int strategy;

	public HPSSimQueue() {
		strategy = 0;
		requestsQueue = new ArrayList<>();
		this.currentMaxPriority = -10000;
	}

	public HPSSimQueue(int _strategy) {
		strategy = _strategy;
		requestsQueue = new ArrayList<>();
		this.currentMaxPriority = -10000;
	}

	/*
	 * Add methods: add, remove, search element for features
	 */
	@Deprecated
	final void push(Job j) {
		if (requestsQueue.isEmpty()) {
			requestsQueue.add(j);
			this.currentMaxPriority = j.schedulingPriority;
		} else {
			switch (strategy) {
			case 0: /* FIFO */
				requestsQueue.add(j);
				currentMaxPriority = j.schedulingPriority;
				break;
			case 1: /* PR */
				currentPriority = j.schedulingPriority;
				if (currentPriority > currentMaxPriority) {
					requestsQueue.add(0, j);
					currentMaxPriority = currentPriority;
				} else if (currentPriority <= requestsQueue.get(requestsQueue
						.size() - 1).schedulingPriority) {
					requestsQueue.add(j);
				} else {
					for (int i = 0; i < requestsQueue.size(); i++) {
						if (currentPriority > requestsQueue.get(i).schedulingPriority) {
							requestsQueue.add(i, j);
							break;
						}
					}
				}

			}
		}
		// this.printpriority();
	}
	@Deprecated
	final Job pop() {
		if (requestsQueue.isEmpty()) {
			System.out.println("ERROR QUEUE is Empty non si fanno dei pop inutili");
			return null;
		} else {
			return requestsQueue.remove(0);
		}
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#getFirstCPUJob()
	 */
	@Override
	public Job getFirstCPUJob() {
		if (requestsQueue.isEmpty()) {
			return null;
		} else {
			for (int i = 0; i < requestsQueue.size(); i++) {
				if (requestsQueue.get(i).classification == 0) {
					return requestsQueue.remove(i);
				}
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#getFirstGPUJob()
	 */
	@Override
	public Job getFirstGPUJob() {
		if (requestsQueue.isEmpty()) {
			return null;
		} else {
			for (int i = 0; i < requestsQueue.size(); i++) {
				if (requestsQueue.get(i).classification == 1) {
					return requestsQueue.remove(i);
				}
			}
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#sort()
	 */
	@Override
	public void sort() {
		Collections.sort(requestsQueue,new PriorityComparatorJob());
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#size()
	 */
	@Override
	public int size() {
		return requestsQueue.size();
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#printpriority()
	 */
	@Override
	public void printpriority() {
		for (Job job : requestsQueue) {
			System.out.print(job.getPriority());
		}

	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#printjob()
	 */
	@Override
	public void printjob() {
		try {
			for (int i = 0; i < requestsQueue.size(); i++) {
				System.out.print(requestsQueue.get(i).id + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#printJobsStat()
	 */
	@Override
	public void printJobsStat() {
		for (int i = 0; i < requestsQueue.size(); i++) {
			requestsQueue.get(i).printjobstat();
			// System.out.println();
		}

	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#getJob(int)
	 */
	@Override
	public Job getJob(int index) {
		return requestsQueue.remove(index);
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#getIndexFirstGPUjob()
	 */
	@Override
	public int getIndexFirstGPUjob() {
		int index = -1;
		for (int i = 0; i < requestsQueue.size(); i++) {
			if (requestsQueue.get(i).getClassification() == 1) {
				index = i;
				break;
			}
		}
		return index;
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#getIndexFirstCPUjob()
	 */
	@Override
	public int getIndexFirstCPUjob() {
		int index = -1;
		for (int i = 0; i < requestsQueue.size(); i++) {
			if (requestsQueue.get(i).getClassification() == 0) {
				index = i;
				break;
			}
		}
		return index;
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#newpriority()
	 */
	@Override
	public final void newpriority() {
		int oldprio = 0;
		int newprio = 0;
		for (Job job : requestsQueue) {
			oldprio = job.getPp();
			newprio = oldprio + job.getPs();
			job.setPs(newprio);
			oldprio = 0;
			newprio = 0;
		}
		sort();
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#insert(hpssim.simulator.Job)
	 */
	@Override
	public void insert(Job request) {
		push(request);
	}

	/* (non-Javadoc)
	 * @see hpssim.scheduler.policy.assignment.IQueue#extract()
	 */
	@Override
	public Job extract() {
		return pop();
	}

}
