package hpssim.scheduler.policy.queue;

import hpssim.simulator.Job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * @author Luigi Giorgio Claudio Mancini
 */
public class RedBlackTree implements IQueue {
	private static final long serialVersionUID = 7019036045460362139L;
	private final String nome;

	private TreeMap<Integer, ArrayList<Job>> requestsQueue;
	private Job curr;
	public long renqueue_weight = 0;
	
	public boolean lockDevice = false;

	@Override
	public String toString() {
		return nome;
	}

	public RedBlackTree(String string) {
		requestsQueue = new TreeMap<>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return (o1.intValue() < o2.intValue() ? -1 : (o1.intValue() == o2.intValue() ? 0 : 1));
			}
		});
		nome = string;
		size = 0;
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
//		int size = 0;
//		for (List<Job> jobQueue : requestsQueue.values()) {
//			size += jobQueue.size();
//		}
//		if (curr != null)
//			return 1 + size;
//		else
			return size;
	}

	@Override
	public void printpriority() {
		for (List<Job> jobQueue : requestsQueue.values()) {
			for (Job job : jobQueue) {
				System.out.print(job.getNice());
			}
		}
	}

	@Override
	public void printjob() {
		try {
			for (List<Job> jobQueue : requestsQueue.values()) {
				for (Job job : jobQueue) {
					System.out.print(" " + job.id + " ");
				}
			}
			if (requestsQueue.isEmpty()) {
				System.out.print("EMPITY");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printJobsStat() {
		for (List<Job> jobQueue : requestsQueue.values()) {
			for (Job job : jobQueue) {
				job.printjobstat();
			}
		}
	}

	@Override
	public Job getJob(int key) {
		List<Job> f = requestsQueue.get(key);
		if (f.size() > 1)
			return f.remove(0);
		else
			return requestsQueue.remove(key).get(0);
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
	
	private int size ;
	@Override
	public void insert(Job j) {
		if (!requestsQueue.containsKey(getKey(j))) {
			ArrayList<Job> list = new ArrayList<>();
			list.add(j);
			requestsQueue.put(getKey(j), list);
		} else {
			// COLLISIONI
			// CFS:
			/**
			 * We dont care about collisions. Nodes with the same key stay
			 * together.
			 */
			requestsQueue.get(getKey(j)).add(j);
		}
		size++;
	}

	@Override
	public Job extract() {
		if (requestsQueue.isEmpty())
			return null;
		
		size--;
		
		int fkey = requestsQueue.firstKey();
		
		List<Job> f = requestsQueue.get(fkey);
		if (f.size() > 1)
			setCurr(f.remove(0));
		else
			setCurr(requestsQueue.remove(fkey).get(0));
			
		renqueue_weight -= getCurr().getWeight();
		
		if (requestsQueue.isEmpty())
			renqueue_weight=1;
		
		return getCurr();
	}

	public Job lookup() {
		if (requestsQueue.size() > 0) {
			List<Job> f = requestsQueue.get(requestsQueue.firstKey());
			return f.get(0);
		} else
			return null;
	}

	/**
	 * Then the tasks are ordered according to the key equal to
	 * 
	 * current_vruntime - min_vruntime,
	 * 
	 * where current runtime is the virtual runtime of the current task and
	 * min_vruntime the smallest virtual runtime within the nodes on the tree
	 * 
	 * */

	public int getKey(Job job) {
		return job.getVruntime() - getMin_Vrtime();
		// return job.getVruntime();
		// return (job.getFair_clock() - job.getWait_runtime());
	}

	public Integer getMin_Vrtime() {
		return requestsQueue.size() != 0 ? requestsQueue.firstKey() : 0;
	}

	public Job getCurr() {
		return curr;
	}

	public void setCurr(Job curr) {
		this.curr = curr;
	}

}
