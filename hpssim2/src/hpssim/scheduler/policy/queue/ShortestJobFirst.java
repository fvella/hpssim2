package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;
import hpssim.simulator.TimeComparatorJob;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Luigi Giorgio Claudio Mancini
 */

public class ShortestJobFirst extends HPSSimQueue {

	public ShortestJobFirst() {
		requestsQueue = new ArrayList<>();
	}

	/**
	 * It inserts the request of the process in the queue. The order depends on
	 * the time of the request.
	 * 
	 * @param request
	 *            the request to add.
	 */
	public void insert(Job request) {
		boolean added = false;
		for (int i = 0; i < requestsQueue.size() && !added; i++) {
			if (TimeComparatorJob.getTime(request) < TimeComparatorJob
					.getTime(requestsQueue.get(i))) {
				added = true;
				requestsQueue.add(i, request);
			}
		}
		if (!added) {
			requestsQueue.add(request);
		}
	}

	/**
	 * It extracts the request from the head of the queue.
	 * 
	 * @return the request.
	 */
	public Job extract() {
		if (requestsQueue.size() > 0) {
			return requestsQueue.remove(0);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return requestsQueue.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<Job> getRequestsQueue() {
		ArrayList<Job> sp = new ArrayList<>(requestsQueue.size());
		for (Job job : requestsQueue) {
			sp.add(job);
		}
		return sp;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Shortest Job First";
	}

	@Override
	public void sort() {
		Collections.sort(requestsQueue, new TimeComparatorJob());
	}
}
