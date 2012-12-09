package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;

import java.util.ArrayList;

/**
 * This class implements a First In First Out assignment policy.
 * 
 * @author 
 */
public class FIFO extends HPSSimQueue {

	private static final long serialVersionUID = -5001L;

	/**
	 * It creates a FCFS queue.
	 */
	public FIFO() {
		requestsQueue = new ArrayList<Job>();
	}

	/**
	 * It inserts the request of the process in tail of the queue.
	 * 
	 * @param request
	 *            the request to add.
	 */
	public void insert(Job request) {
		requestsQueue.add(request);
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

	public int size() {
		return requestsQueue.size();
	}

	public ArrayList<Job> getRequestsQueue() {
		ArrayList<Job> sp = new ArrayList<Job>(requestsQueue.size());
		for (Job job : requestsQueue) {
			sp.add(job);
		}
		return sp;
	}

	 
	public String toString() {
		return "First In First Out";
	}

}
