package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements a Random assignment policy.
 * 
 * @author Luigi Giorgio Claudio Mancini 
 * @version 1.1
 */
public class RandomQueue extends HPSSimQueue {

	/**
	 * To serializable
	 */
	private static final long serialVersionUID = -5014L;

	/**
	 * The random generator.
	 */
	protected Random random;

	/**
	 * It creates the random queue.
	 */
	public RandomQueue() {
		requestsQueue = new ArrayList<>();
		random = new Random();
	}

	/**
	 * It inserts the request of the process in tail of the queue.
	 * 
	 * @param request
	 *            The request of the process.
	 */
	public void insert(Job request) {
		requestsQueue.add(request);
	}

	/**
	 * It extracts in a random way a request of resource.
	 * 
	 * @return a process.
	 */
	public Job extract() {
		if (size() > 0) {
			return requestsQueue.remove(random.nextInt(size()));
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
	public ArrayList<Job> getJobsQueue() {
		ArrayList<Job> sp = new ArrayList<Job>(requestsQueue.size());
		for (Job job : requestsQueue) {
			sp.add(job);
		}
		return sp;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "Random";
	}
}