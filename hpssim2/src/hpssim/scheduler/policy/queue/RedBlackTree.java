package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;
import java.util.TreeMap;

// Class Definitions
public class RedBlackTree implements IQueue{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7019036045460362139L;
	private TreeMap<Integer, Job> requestsQueue;
	
	private Job curr;
	
	public long renqueue_weight = 1;

	public RedBlackTree() {
		requestsQueue = new TreeMap<>();
	}

	@Override
	public Job getFirstCPUJob() {
		for (Job job : requestsQueue.values()) {
			if (job.getClassification() == 0) {
				return job;
			}
		}
		return null;
	}

	@Override
	public Job getFirstGPUJob() {
		for (Job job : requestsQueue.values()) {
			if (job.getClassification() == 1) {
				return job;
			}
		}
		return null;
	}

	@Override
	public void sort() {
		// not necessary
	}

	@Override
	public int size() {
		return requestsQueue.size();
	}

	@Override
	public void printpriority() {
		for (Job job : requestsQueue.values()) {
			System.out.print(job.getPriority());
		}
	}

	@Override
	public void printjob() {
		try {
			for (Job job : requestsQueue.values()) {
				System.out.print(job.getId() + " ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printJobsStat() {
		for (Job job : requestsQueue.values()) {
			job.printjobstat();
		}
	}

	@Override
	public Job getJob(int key) {
		return requestsQueue.remove(key);
	}

	@Override
	public int getIndexFirstGPUjob() {
		return getKey(getFirstGPUJob());
	}

	@Override
	public int getIndexFirstCPUjob() {
		return getKey(getFirstCPUJob());
	}

	@Override
	public void newpriority() {
		//
		// TODO Auto-generated method stub
	}

	@Override
	public void insert(Job j) {
		if (!requestsQueue.containsKey(getKey(j))) {
			requestsQueue.put(getKey(j), j);
		} else {
			//COLLISIONI
			//CFS:
			/**
			We dont care about collisions. Nodes with
			the same key stay together.
			*/
			//TODO
		}
	}

	@Override
	public Job extract() {
		setCurr(requestsQueue.remove(requestsQueue.firstKey()));
		return getCurr();
	}
	
	
	/**
	 * Then the tasks are ordered
	 *	according to the key equal to 
	 *	
	 *	current_vruntime - min_vruntime, 
	 *	
	 *	where current runtime is the virtual runtime 
	 *	of the current task and min_vruntime the
	 *	smallest virtual runtime within the nodes on the tree
	 * 
	 * */
	
	private int getKey(Job job){
		return job.getVruntime() - getMin_Vrtime();
//		return job.getVruntime();
//		return (job.getFair_clock() - job.getWait_runtime());
	}
	
	public Integer getMin_Vrtime(){
		return requestsQueue.firstKey() != null ? requestsQueue.firstKey() : 0;
	}

	public Job getCurr() {
		return curr;
	}

	public void setCurr(Job curr) {
		this.curr = curr;
	}
	
}
