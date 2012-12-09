package hpssim.main;

import hpssim.hardware.Hardware;
import hpssim.simulator.Simulator;

public class HPSsim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long test = System.currentTimeMillis();

		int njobs = 3;

		// il tempo cpu che può spendere il processo
		int qt = 210;
		double classificationRate = 0.9D;
		double realTimeJobsProb = 0.4D;
		double percentOpenCLjob = 0.25D;
		int avgta = 230;

		Hardware hw = new Hardware(4, 1);

		Simulator sim = new Simulator(hw, njobs, qt, classificationRate,
				realTimeJobsProb, percentOpenCLjob, avgta);
		sim.init();
		sim.run();

		System.out.println(System.currentTimeMillis() - test);
	}
}
