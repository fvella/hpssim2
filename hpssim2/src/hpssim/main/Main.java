package hpssim.main;

import hpssim.grafica.HPSsim;

import javax.swing.SwingUtilities;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

					
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					HPSsim window = new HPSsim();
					window.HPSsimWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
					
//		long test = System.currentTimeMillis();
//
//		int njobs = 50;
//
//		// il tempo cpu che può spendere il processo
//		int qt = 210;
//		double classificationRate = 0.95D;
//		double realTimeJobsProb = 0.45D;
//		double percentOpenCLjob = 0.35D;
//		int avgta = 230;
//
//		Hardware hw = new Hardware(4, 2);
//
//		Simulator sim = new Simulator(hw, njobs, qt, classificationRate, realTimeJobsProb, percentOpenCLjob, avgta);
//		sim.init();
//		sim.run();
//
//		System.out.println(System.currentTimeMillis() - test);
	}

