package hpssim.scheduler.policy.scheduling;

import java.lang.reflect.Array;

import hpssim.hardware.Hardware;
import hpssim.scheduler.policy.queue.RedBlackTree;
import hpssim.simulator.Event;
import hpssim.simulator.EventList;
import hpssim.simulator.Job;

public class CompletelyFairScheduler implements SchedulingPolicy {
	// QUEUE CFS
	private RedBlackTree cfs_rq;
	private int cfs_rq_index = 0;

	private RedBlackTree[] cpu_rq;
	private RedBlackTree[] gpu_rq;

	/**
	 * 
	 * Completely Fair Scheduler calculates time slice based on the number of
	 * processes running and its weight compared to the load of the system. It
	 * first calculates a "period" to determine the length of time it needs for
	 * each task with a policy of SCHED_OTHER/SCHED_BATCH to run. This is done
	 * using the __sched_period() function defined in sched_fair.c. The idea is
	 * that if there are 5 or less processes needing the CPU, 20ms should be
	 * enough time for each of them to run. However, if the system becomes busy
	 * the period is extended by adding 4ms for every process that is on the
	 * system. The next two pieces of information that are needed to determine
	 * the time slice are the weight of that process, and the weight of the run
	 * queue. The processes weight is determined by its static priority, and a
	 * static array of available weight values from 15 for a process with a nice
	 * value of 19, to 88761 for a process with a nice value of -20. Together
	 * with the sum of all the processes weights on the run queue an appropriate
	 * amount of CPU time can be given to each process. the calculation of a
	 * process time slice is:
	 * 
	 * TimeSlice = lenght_of_period * (task's weight/total weight of tasks in
	 * the run queue)
	 * 
	 * Timeslices become smaller as the load increases. When the number of
	 * runnable tasks exceeds sched_latency_ns/sched_min_granularity_ns, the
	 * slice becomes number_of_running_tasks * sched_min_granularity_ns. Prior
	 * to that, the slice is equal to sched_latency_ns. This value also
	 * specifies the maximum amount of time during which a sleeping task is
	 * considered to be running for entitlement calculations. Increasing this
	 * variable increases the amount of time a waking task may consume before
	 * being preempted, thus increasing scheduler latency for CPU bound tasks.
	 * The default value is 20000000ns (20ms).
	 * 
	 * 
	 */

	public static int prio_to_weight[] = {
	/* -20 */88761, 71755, 56483, 46273, 36291,
	/* -15 */29154, 23254, 18705, 14949, 11916,
	/* -10 */9548, 7620, 6100, 4904, 3906,
	/* -5 */3121, 2501, 1991, 1586, 1277,
	/* 0 */1024, 820, 655, 526, 423,
	/* 5 */335, 272, 215, 172, 137,
	/* 10 */110, 87, 70, 56, 45,
	/* 15 */36, 29, 23, 18, 15, };

	public static int NICE_0_LOAD = prio_to_weight[20];

	/**
	 * kernel/sched_fair.c row 25 to 48
	 */
	/**
	 * Targeted preemption latency for CPU-bound tasks: (default: 20ms * (1 +
	 * ilog(ncpus)), units: nanoseconds)
	 * 
	 * NOTE: this latency value is not the same as the concept of 'timeslice
	 * length' - timeslices in CFS are of variable length and have no persistent
	 * notion like in traditional, time-slice based scheduling concepts.
	 * 
	 * (to see the precise effective timeslice length of your workload, run
	 * vmstat and monitor the context-switches (cs) field)
	 */
	public long sysctl_sched_latency = 20L;

	/*
	 * Minimal preemption granularity for CPU-bound tasks: (default: 4 msec * (1
	 * + ilog(ncpus)), units: nanoseconds)
	 */
	public long sysctl_sched_min_granularity = 4L;

	/*
	 * is kept at sysctl_sched_latency / sysctl_sched_min_granularity
	 */
	public long sched_nr_latency = 5L;

	public CompletelyFairScheduler(Hardware hw) {

		int ncpu = hw.numcpus() + hw.numgpus();

		sysctl_sched_latency = 20 * (1 + ncpu);
		sysctl_sched_min_granularity = 4 * (1 + ncpu);
		sched_nr_latency = sysctl_sched_latency / sysctl_sched_min_granularity;

		cpu_rq = new RedBlackTree[hw.numcpus()];
		gpu_rq = new RedBlackTree[hw.numgpus()];

		for (int i = 0; i < cpu_rq.length; i++) {
			cpu_rq[i] = new RedBlackTree();
		}

		for (int i = 0; i < gpu_rq.length; i++) {
			gpu_rq[i] = new RedBlackTree();
		}

		// default cfs_rq contents current queue
		cfs_rq = cpu_rq[0];
	}

	private long getTimeSlice(Job j) {
		long lenght_of_period = __sched_period(cfs_rq.size());
		long weight = j.getWeight();

		long timeSlice = (lenght_of_period * weight) / (cfs_rq.renqueue_weight);

		return timeSlice == 0 ? 1 : timeSlice;
	}

	/**
	 * Priorities
	 * 
	 * Selecting a process that should be the next to run is a critical aspect
	 * of the scheduler. Though in the 2.4 Kernel there is very little
	 * information available to determine this, it uses a simple calculation in
	 * the goodness() function to determine the best candidate. It takes the sum
	 * of the time remaining in the time slice, value of 20 less the nice value,
	 * and one addition credit if the memory for the current process is mapped.
	 * A final bonus of 1000 is given to real-time processes with a policy of
	 * SCHED_FIFO and SCHED_RR to ensure that they run before the other
	 * processes. This calculation unfortunately depends largely of the programs
	 * nice value which is not the best value to basing the scheduling of
	 * processes.
	 * 
	 * Having 100 real time priority levels and 40 normal priority levels for
	 * processes allows the scheduler in the 2.6 Linux kernel to easily
	 * determine the next process to run. Normal processes are placed in their
	 * run queue based on their nice value, -20 to 19, giving the 40 priority
	 * run queues. Real time processes hold their initial priority through the
	 * life of the system while, unless changed through system calls, but normal
	 * processes have their priority altered throughout their execution
	 * lifetime. Processes can have their priority level increased by up to five
	 * levels, and decreased by up to five levels. The system does this by
	 * determining the processes interactive rating. The introduction of process
	 * interactivity credits into the scheduler was one of the most significant
	 * developments. Using two complex calculations to determine if the process
	 * is interactive, the process can be placed back in its queue and begin
	 * executing once the other processes in that priority level have finished
	 * execution. This allows for a more responsive feel to the system for the
	 * user, and at the same time increases resource utilization by constantly
	 * keeping tasks ready while they are waiting for IO.
	 * 
	 * For real time processes in the CFS, the scheduler first checks if there
	 * are any on the run queue to be executed. Stepping through the array
	 * starting at the highest priority and working its way down through
	 * executing the processes the same as it does in the original 2.6 kernel
	 * scheduler. The major difference is seen when the scheduler attempts to
	 * execute a normal process, known in the scheduler as a "fair" process. As
	 * mentioned earlier, each process is given a percentage of runtime based on
	 * the number of other processes, and the weight of all the processes on the
	 * run queue. The CFS prioritizes its task by calculating the amount of time
	 * it ran based its weight, not the actual amount of time it spent on the
	 * CPU. the calculation of virtual runtime the Completely Fair Scheduler is:
	 * 
	 * VirtualRuntime = (ActualRuntime * 1024) / Renqueue_weight
	 * 
	 */

	/*
	 * Update the current task's runtime statistics. Skip current tasks that are
	 * not in our scheduling class.
	 */
	/**
	 * Get the amount of time the current task was running since the last time
	 * we changed load
	 */
	private void update_curr(Job curr, Integer now) { // now = timeEvent
		Integer delta_exec = now - curr.getLastExecutionTime();

		__update_curr(curr, delta_exec);

		curr.setLastExecutionTime(now);
	}

	private void __update_curr(Job curr, Integer delta_exec) {

		Integer delta_exec_weighted = 0;

		// curr->sum_exec_runtime += delta_exec;
		curr.setExecutionTime(curr.getExecutionTime() + delta_exec);

		delta_exec_weighted = calc_delta_fair(delta_exec, curr);

		// curr->vruntime += delta_exec_weighted;
		curr.setVruntime(curr.getVruntime() + delta_exec_weighted);

		// di fare l'update del min vrtime non c'è bisogno poiche la struttura
		// dati già salva il valore del min una volta inserito
		// update_min_vruntime(cfs_rq);
	}

	/*
	 * delta /= w
	 */
	private Integer calc_delta_fair(Integer delta_exec, Job se) {
		if ((se.getWeight() != NICE_0_LOAD))
			delta_exec = calc_delta_mine(delta_exec, se.getWeight());

		return delta_exec;
	}

	/**
	 * calc_delta_fair does is to compute the value given by the following
	 * formula:
	 * 
	 * delta_exec_weighted = delta_exec × NICE_0_LOAD / curr->load
	 * 
	 * */

	private Integer calc_delta_mine(Integer delta_exec, Integer weight) {
		return delta_exec * (NICE_0_LOAD / weight);
	}

	/*
	 * kernel/sched_fair.c row 406
	 * 
	 * The idea is to set a period in which each task runs once.
	 * 
	 * When there are too many tasks (sysctl_sched_nr_latency) we have to
	 * stretch this period because otherwise the slices get too small.
	 * 
	 * p = (nr <= nl) ? l : l*nr/nl
	 */
	private long __sched_period(int nr_running) {
		long period = sysctl_sched_latency;
		long nr_latency = sched_nr_latency;

		if (!(nr_running > nr_latency)) {
			period = sysctl_sched_min_granularity;
			period *= nr_running;
		}
		return period;
	}

	// SCHEDULER
	@Override
	public void execute(Event ev, Hardware hw, int timeEv, EventList evl) {

		int nextime = timeEv + (int) sysctl_sched_latency;

		if (ev.job == null) {
			// si tratta di un evento run chiamato dallo scheduler

			// GPU
			if (hw.getCPUfree() > 0) {
				// per eseguire un processo sulla gpu
				// c'è bisogno di almeno una gpu
				for (int i = 0; i < gpu_rq.length; i++) {
					// scorro la lista delle gpu
					cfs_rq = gpu_rq[i];
					// chiamo il metodo che esegue il job
					if (cfs_rq.size() > 0)
						executeJob(hw, timeEv++, evl, true,
								hw.getGPUfree() > 0, i);
				}
			}

			// CPU
			for (int i = 0; i < cpu_rq.length; i++) {
				// scorro la lista delle cpu
				cfs_rq = cpu_rq[i];
				if (cfs_rq.size() > 0)
					executeJob(hw, timeEv++, evl, false, hw.getCPUfree() > 0, i);
			}
		} else {
			// si tratta di un evento run chiamato dall'esecuzione di un job
			if (ev.job.classification == 0) {
				cfs_rq = cpu_rq[ev.indexQueue];

				if (cfs_rq.getCurr() == null) {
					executeJob(hw, timeEv++, evl, false, false, ev.indexQueue);
				} else
					executeJob(hw, timeEv++, evl, false, true, ev.indexQueue);
			} else {
				cfs_rq = cpu_rq[ev.indexQueue];

				if (cfs_rq.getCurr() == null) {
					executeJob(hw, timeEv++, evl, true, false, ev.indexQueue);
				} else
					executeJob(hw, timeEv++, evl, true, true, ev.indexQueue);
			}
		}
		//
		// if ((j.remainingTime > Simulator.tq) && (!gpurun)) {
		// // REQUEUE //SETTA LA NUOVA PRIORITA' //SETTA IL TEMPO
		// // RIMANENTE
		// // j.rescheduled++;
		// evl.insertEvent(new Event(j, Event.REQUEUE, timeEv
		// + Simulator.tq));
		// System.out.print("REQUEUE " + (timeEv + Simulator.tq) + " "
		// + hw.getCPUfree() + " " + hw.getGPUfree() + " "
		// + queue.size() + " ");
		// // evl.insertEvent(ev1);
		// } else { // FINALIZE
		//
		// }
	}

	private void updateJob(Job j, int timeEv, boolean gpurun) {
		if (j.rescheduled == 0) {
			// if first execution set start run
			// time, remaning time
			j.setExecutionTime(timeEv);
			j.setQueueTime(timeEv);

			if (gpurun) {
				j.setRemainingTime(j.executionTimeGPU);
			} else {
				j.setRemainingTime(j.executionTimeCPU);
			}
		} else {
			// non è la prima esecuzione
			j.setRemainingTime(j.getRemainingTime() - (int) getTimeSlice(j));
		}
	}

	private void executeJob(Hardware hw, int timeEv, EventList evl,
			boolean gpu, boolean devicefree, int indexQueue) {
		int nextime = timeEv + (int) sysctl_sched_latency;
		Job j = null;

		if (devicefree) {
			// nel caso ci sono gpu\cpu libere
			// eseguo il primo della lista
			j = cfs_rq.extract();

			if (gpu)
				hw.assignJobtoGPU(j);
			else
				hw.assignJobtoCPU(j);

			update_curr(j, timeEv);
			updateJob(j, timeEv, true);

			int timeslice = (int) getTimeSlice(j);
			evl.insertEvent(new Event(j, Event.RUN, timeEv + timeslice,
					indexQueue));
			System.out.print("RUN " + timeslice + " " + hw.getCPUfree() + " "
					+ hw.getGPUfree() + " " + size() + " ");

		} else {
			// va in loop xke devicefree == false
			// nel caso l'evento arriva da un ENQUEUE
			// e quindi con job == null
			// curr == null xke non sono stati eseguiti job nella coda corrente
			j = cfs_rq.getCurr();

			if (j == null) {
				evl.insertEvent(new Event(null, Event.RUN, nextime));
				System.out.print("RUN " + nextime + " " + hw.getCPUfree() + " "
						+ hw.getGPUfree() + " " + size() + " ");
				return;
			}

			// controllare se il curr ha finito
			// il tempo di esecuzione a lui dato
			if (j.getRemainingTime() <= 0) {
				// ha finito la sua esecuzione
				// deve andare in stato di finalize

				evl.insertEvent(new Event(j, Event.FINALIZE, nextime));

				System.out.print("FINALIZE " + (nextime) + " "
						+ hw.getCPUfree() + " " + hw.getGPUfree() + " "
						+ size() + " ");

				cfs_rq.setCurr(null);

				evl.insertEvent(new Event(null, Event.RUN, nextime));
				System.out.print("RUN " + nextime + " " + hw.getCPUfree() + " "
						+ hw.getGPUfree() + " " + size() + " ");

			} else {
				// non ha ancora finito l'esecuzione
				// se non ci sono job nella coda con priorità maggiore
				// riesegue lo stesso

				// controllo se il primo della lista ha
				// vtime minore del curr
				Job jLookup = cfs_rq.lookup();

				if (jLookup != null
						&& cfs_rq.getKey(jLookup) < cfs_rq.getKey(j)) {
					// devo effettuare lo switch tra i processi
					// e reinserire in coda il processo curr
					j = jLookup;
					Job curr = cfs_rq.getCurr();

					evl.insertEvent(new Event(curr, Event.ENQUEUE, nextime));

					System.out.print("REQUEUE " + (nextime) + " "
							+ hw.getCPUfree() + " " + hw.getGPUfree() + " "
							+ size() + " ");

					j = cfs_rq.extract();

					if (gpu)
						hw.assignJobtoGPU(j);
					else
						hw.assignJobtoCPU(j);

					update_curr(j, timeEv);
					updateJob(j, timeEv, true);
				} else {
					// rieseguo lo stesso

					update_curr(j, timeEv);
					updateJob(j, timeEv, true);

					int timeslice = (int) getTimeSlice(j);
					evl.insertEvent(new Event(j, Event.RUN, timeEv + timeslice,
							indexQueue));
					System.out.print("RUN " + timeslice + " " + hw.getCPUfree()
							+ " " + hw.getGPUfree() + " " + size() + " ");
				}
			}
		}

	}

	@Override
	public int enqueue(Job se, int time) {
		int j = loadBalancing(se.classification, true);
		/*
		 * Update run-time statistics of the 'current'.
		 */
		update_curr(se, time);

		cfs_rq.insert(se);
		// renqueue_weight += se.getWeight();
		return j;
	}

	/**
	 * return the number of best cpu\gpu queue end set cfs_rq
	 * 
	 * */
	private int loadBalancing(int classification, boolean setcfs) {
		int j = 0;
		long w = 0;
		if (classification == 0) {
			// CPU
			w = cpu_rq[0].renqueue_weight;
			for (int i = 1; i < cpu_rq.length; i++) {
				if (w > cpu_rq[i].renqueue_weight) {
					w = cpu_rq[i].renqueue_weight;
					j = i;
				}
			}
			if (setcfs)
				cfs_rq = cpu_rq[j];
		} else {
			// GPU
			w = gpu_rq[0].renqueue_weight;
			for (int i = 1; i < gpu_rq.length; i++) {
				if (w > gpu_rq[i].renqueue_weight) {
					w = gpu_rq[i].renqueue_weight;
					j = i;
				}
			}
			if (setcfs)
				cfs_rq = gpu_rq[j];
		}
		return j;
	}

	@Override
	public Job extract() {
		Job job = cfs_rq.extract();
		cfs_rq.renqueue_weight -= job.getWeight();
		return job;
	}

	@Override
	public void printjob() {
		// TODO Auto-generated method stub

	}

	@Override
	public int size() {
		int size = 0;
		for (int i = 0; i < cpu_rq.length; i++) {
			size += cpu_rq[i].size();
		}
		for (int i = 0; i < gpu_rq.length; i++) {
			size += gpu_rq[i].size();
		}
		return size;
	}

	@Override
	public void newpriority() {
		// notNecessary
	}

}
