package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;

import java.util.ArrayList;

/**
 * This class implements the Highest Priority First assignment policy. This
 * policy always extracts the request with the highest priority.
 * 
 * @author Luigi Giorgio Claudio Mancini 
 */
public class HighestPriorityFirst extends HPSSimQueue {

	/**
	 * To serializable
	 */
	private static final long serialVersionUID = -5003L;


	/**
	 * It creates the Highest Priority First assignment policy.
	 */
	public HighestPriorityFirst() {
		requestsQueue = new ArrayList<>();
	}

	/**
	 * It inserts the request of the process in the queue. The order depends on
	 * the priority of the request.
	 * 
	 * @param request
	 *            the request to add.
	 */
	public void insert(Job request) {
		boolean added = false;
		for (int i = 0; i < requestsQueue.size() && !added; i++) {
			if (request.getPriority() < requestsQueue.get(i).getPriority()) {
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
		return "Highest Priority First";
	}

}
