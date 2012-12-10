package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;
import java.util.TreeMap;

/**
 * @author Luigi Giorgio Claudio Mancini
 */
public class RedBlackTree implements IQueue{

	
	private static final long serialVersionUID = 7019036045460362139L;
	private TreeMap<Integer, Job> requestsQueue;
	
	private Job curr;
	
	public long renqueue_weight = 0;
	
	private String nome;
	
	@Override
	public String toString() {
		return nome;
	}

	public RedBlackTree(String string) {
		requestsQueue = new TreeMap<>();
		nome = string;
	}

	@Override
	public Job getFirstCPUJob() {
		// not necessary
		return null;
	}

	@Override
	public Job getFirstGPUJob() {
		// not necessary
		return null;
	}

	@Override
	public void sort() {
		// not necessary
	}

	@Override
	public int size() {
			return requestsQueue.size() ;
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
				System.out.print(" " + job.id + " ");
			}
			if(requestsQueue.isEmpty()){
				System.out.print("EMPITY");
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
		if(requestsQueue.isEmpty())
			return null;
		
		setCurr(requestsQueue.remove(requestsQueue.firstKey()));
		return getCurr();
	}
	
	public Job lookup(){
		if(requestsQueue.size()>0)
		return requestsQueue.get(requestsQueue.firstKey());
		else return null;
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
	
	public int getKey(Job job){
		return job.getVruntime() - getMin_Vrtime();
//		return job.getVruntime();
//		return (job.getFair_clock() - job.getWait_runtime());
	}
	
	public Integer getMin_Vrtime(){
		return requestsQueue.size() != 0 ? requestsQueue.firstKey() : 0;
	}

	public Job getCurr() {
		return curr;
	}

	public void setCurr(Job curr) {
		this.curr = curr;
	}
	
}
