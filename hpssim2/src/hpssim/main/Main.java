package hpssim.main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import hpssim.grafica.HPSsim;
import hpssim.hardware.Hardware;
import hpssim.scheduler.Configurator;
import hpssim.simulator.Simulator;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

					
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
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
//		Configurator conf = null;
//		try {
//			conf = new Configurator(hw, njobs, qt, classificationRate, realTimeJobsProb, percentOpenCLjob, avgta, 1000000);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Simulator sim = new Simulator(conf, new HPSsim());
//		
//		sim.init();
//		sim.run();
//
//		System.out.println(System.currentTimeMillis() - test);
	
}

