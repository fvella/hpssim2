/*
 * Created by JFormDesigner on Tue Dec 11 16:38:43 CET 2012
 */

package hpssim.grafica;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import hpssim.hardware.Hardware;
import hpssim.scheduler.Configurator;
import hpssim.scheduler.policy.queue.FIFO;
import hpssim.scheduler.policy.queue.HPSSimQueue;
import hpssim.scheduler.policy.queue.HighestPriorityFirst;
import hpssim.scheduler.policy.queue.IQueue;
import hpssim.scheduler.policy.queue.RandomQueue;
import hpssim.scheduler.policy.queue.RedBlackTree;
import hpssim.scheduler.policy.queue.ShortestJobFirst;
import hpssim.scheduler.policy.scheduling.CompletelyFairScheduler;
import hpssim.scheduler.policy.scheduling.HPSSimScheduler;
import hpssim.scheduler.policy.scheduling.IScheduler;
import hpssim.simulator.Simulator;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.jgoodies.forms.factories.DefaultComponentFactory;

/**
 * @author 
 */
public class HPSsim {

	public Thread sim;
	
	private boolean endJobs = false;
	public boolean realtimeStat = true;
	
	public HPSsim() {
		initComponents();
		setMeter();
		hpssimWindow.setVisible(true);
		sim = null;
	}

	private void resetButtonActionPerformed(ActionEvent e) {
		resetPanelConfig();
	}

	private void resetPanelConfig() {
		textFieldNjob.setText(String.valueOf(0));
		textFieldSimTime.setText(String.valueOf(0) + " ms");
		labelRT.setText(String.valueOf(50) + "%");
		labelOPENCL.setText(String.valueOf(50) + "%");
		textFieldQVGA.setText("");
		textFieldTimeSlice.setText("");
		labelclassRate.setText("90%");
		ncpu.setText("0");
		ngpu.setText("0");
		comboBoxScheduler.setSelectedItem("Priority Round Robin");
		comboBoxQueue.setSelectedItem("FIFO");

		sliderSimulationTime.setValue(0);
		sliderJob.setValue(0);
		sliderclassRate.setValue(90);
		sliderOpenCl.setValue(0);
		sliderRTJob.setValue(0);
	}

	private Configurator getConf() throws Exception{
		int njobs = Integer.parseInt((new String(textFieldNjob.getText())).trim());
		int simTime = Integer.parseInt((new String(textFieldSimTime.getText())).replace("ms", "").trim());
		// il tempo cpu che può spendere il processo
		Integer qt = Integer.parseInt((new String(textFieldTimeSlice.getText())).trim());
		double classificationRate = (Double.valueOf(sliderclassRate.getValue())) / 100;
		double realTimeJobsProb = (Double.valueOf(sliderRTJob.getValue())) / 100;
		double percentOpenCLjob = (Double.valueOf(sliderOpenCl.getValue())) / 100;
		int avgta = Integer.parseInt(textFieldQVGA.getText().trim());
		boolean endJob= checkBoxEndJob.isSelected();
		int exeTime = Integer.parseInt((new String(tex_mediaexe.getText())).trim());
		if(endJob)
			progressBar.setIndeterminate(true);
		
		Class queue = null;
		switch (comboBoxQueue.getSelectedIndex()) {
		case 0:
			queue = FIFO.class;
			break;
		case 1:
			queue = HighestPriorityFirst.class;
			break;
		case 2:
			queue = ShortestJobFirst.class;
			break;
		case 3:
			queue= HPSSimQueue.class;
			break;
		case 4:
			queue = RandomQueue.class;
			break;
		default:
			break;
		}
		
		Class scheduler = null;
		if(comboBoxScheduler.getSelectedIndex()==0)
			scheduler = HPSSimScheduler.class;
		else {
			scheduler = CompletelyFairScheduler.class;
			queue = RedBlackTree.class;
		}
		
		Hardware hw = new Hardware(Integer.parseInt(ncpu.getText()), Integer.parseInt(ngpu.getText()));
		
		return new Configurator(hw, njobs, qt.intValue(), exeTime,
				classificationRate, realTimeJobsProb, percentOpenCLjob, avgta, simTime, 
				scheduler ,queue, endJob, false, false);
		
	}
	
	private void startSimulation(Configurator conf) throws Exception{
		sim = new Simulator(conf, this);
		((Simulator) sim).setLogLevel(checkBox_enableLog.isSelected());
		
		synchronized (sim) {
			((Simulator) sim).run = true;
			sim.notify();
		}
		
	}
	
	private void startSimulationNT(Configurator conf) throws Exception{
		sim = new Simulator(conf, this);
		((Simulator) sim).setLogLevel(checkBox_enableLog.isSelected());
		
		((Simulator) sim).run = true;
		((Simulator) sim).simulate();
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		realtimeStat=true;
		try {
			 startSimulation(getConf());
		} catch (Exception e2) {
			e2.printStackTrace();
			erroreLabel.setText(e2.getMessage());
			dialog1.setVisible(true);
		}

	}

	private void sliderJobStateChanged(ChangeEvent e) {
		textFieldNjob.setText(String.valueOf(sliderJob.getValue()));
	}

	private void sliderSimulationTimeStateChanged(ChangeEvent e) {
		textFieldSimTime.setText(String.valueOf(sliderSimulationTime.getValue()) + " ms");
	}

	private void sliderRTJobStateChanged(ChangeEvent e) {
		labelRT.setText(String.valueOf(sliderRTJob.getValue()) + "%");
	}

	private void sliderOpenClStateChanged(ChangeEvent e) {
		labelOPENCL.setText(String.valueOf(sliderOpenCl.getValue()) + "%");
	}

	private void sliderclassRateStateChanged(ChangeEvent e) {
		labelclassRate.setText(String.valueOf(sliderclassRate.getValue()) + "%");
	}

	private void comboBoxSchedulerActionPerformed(ActionEvent e) {
		if (comboBoxScheduler.getSelectedIndex() > 0) {
			comboBoxQueue.setModel(new DefaultComboBoxModel<>(new String[] { "Red Black Tree", }));
			textFieldTimeSlice.setEnabled(false);
		} else {
			comboBoxQueue.setModel(new DefaultComboBoxModel<>(new String[] { "FIFO", "Highest Priority First", "Shortest Job First", "Round Robin", "Random Queue" }));
			textFieldTimeSlice.setEnabled(true);
		}
	}

	private void button2ActionPerformed(ActionEvent e) {
		dialog1.setVisible(false);
	}

	private void button1ActionPerformed(ActionEvent e) {
		if (sim != null) {
			synchronized (sim) {
				((Simulator) sim).run = false;
				sim.notify();
			}
//			button3.setVisible(true);
		}
	}

	private void pauseActionPerformed(ActionEvent e) {
		if (sim != null && !((Simulator) sim).run) {
			synchronized (sim) {
				((Simulator) sim).run = true;
				sim.notify();
			}
			button3.setVisible(false);
		}
	}
	
	class EsecuzioneJobs extends Thread {

		public EsecuzioneJobs(String hw) {
			this.hw = hw;
			start();
		}
		private String hw;
		public void run() {
			try {
				while (endJobs == false) {
					Thread.sleep(1000);
				}
				
				Thread.sleep(1000);
				setQueueXY(hw);
				Grafici.setVisible(true);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//COSTANTE
	private void button_CostanteCodaSuMediaActionPerformed(ActionEvent e) {
		realtimeStat=false;
		datasetQueue = new XYSeries("Queue size");
		
		try {
			Hardware hw = new Hardware(4, 0);
			 Configurator conf = new Configurator(hw, //Hardware
											/*NJOB*/			1000,    
											/*QT*/				210, 
											/*mediaEsecJob*/	997.9d,
											/*classRate*/		1d, 
											/*rtJobProb*/		0.17d, 
											/*clPerc*/			0.15d, 
											/*mediaArrivo*/		1000, 
											/*simTime*/			362011, 
											CompletelyFairScheduler.class ,
											RedBlackTree.class,
											/*endJob*/			false,
											/*costante*/		true, false);
			 
			 startSimulation(conf);
			 
			 synchronized(this){	 
				 new EsecuzioneJobs(hw.numcpus()+"CPU " + hw.numgpus() + "GPU");
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
		
	}

	private void button_CostanteTempoMedioArrivoActionPerformed(ActionEvent e) {
		realtimeStat=false;
		try {
			Hardware hw = new Hardware(1, 0);
     		 Configurator conf = new Configurator(hw, //Hardware
											/*NJOB*/			1000,    
											/*QT*/				210, 
											/*mediaEsecJob*/	200d,
											/*classRate*/		1d, 
											/*rtJobProb*/		0.17d, 
											/*clPerc*/			0.20d, 
											/*mediaArrivo*/		200, 
											/*simTime*/			362011, 
											CompletelyFairScheduler.class ,
											RedBlackTree.class,
											/*endJob*/			false,
											/*costante*/		true, false);
			 
			 
			 conf.hw = new Hardware(1, 1);
			 conf.hw = new Hardware(2, 0);
			 conf.hw = new Hardware(2, 1);
			 conf.hw = new Hardware(2, 2);
			 conf.hw = new Hardware(3, 0);
			 conf.hw = new Hardware(3, 1);
			 conf.hw = new Hardware(3, 2);
			 conf.hw = new Hardware(3, 3);
			 conf.hw = new Hardware(4, 0);
			 conf.hw = new Hardware(4, 1);
			 conf.hw = new Hardware(4, 2);
			 conf.hw = new Hardware(4, 3);
			 conf.hw = new Hardware(4, 4);
			 conf.hw = new Hardware(5, 0);
			 conf.hw = new Hardware(5, 1);
			 conf.hw = new Hardware(5, 2);
			 conf.hw = new Hardware(5, 3);
			 conf.hw = new Hardware(5, 4);
			 conf.hw = new Hardware(5, 5);
			 conf.hw = new Hardware(10, 256);
			 
			 startSimulation(conf);
			 
//			 for(int i = 2; i<5 ; i++){
//				 for(int j = 0; j<=i ; j++){
//					 conf.hw = new Hardware(i, j);
//					 startSimulationNT(conf);
//					 Thread.sleep(1000);
//				 }
//			 }
		
		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}
//CRESCENTE
	private void button_CrescenteCaricoActionPerformed(ActionEvent e) {
		realtimeStat=false;
		try {
			Hardware hw = new Hardware(1, 0);
			 Configurator conf = new Configurator(hw, //Hardware
						/*NJOB*/			1000,    
						/*QT*/				210, 
						/*mediaEsecJob*/	1000d,
						/*classRate*/		1d, 
						/*rtJobProb*/		0.17d, 
						/*clPerc*/			0.15d, 
						/*mediaArrivo*/		4000, 
						/*simTime*/			362011, 
						CompletelyFairScheduler.class ,
						RedBlackTree.class,
						/*endJob*/			false,
						/*costante*/		false,
						/*crescente*/ true);
			 
			 
			 conf.hw = new Hardware(1, 1);
			 conf.hw = new Hardware(2, 0);
			 conf.hw = new Hardware(2, 1);
			 conf.hw = new Hardware(2, 2);
			 conf.hw = new Hardware(3, 0);
			 conf.hw = new Hardware(3, 1);
			 conf.hw = new Hardware(3, 2);
			 conf.hw = new Hardware(3, 3);
			 conf.hw = new Hardware(4, 0);
			 conf.hw = new Hardware(4, 1);
			 conf.hw = new Hardware(4, 2);
			 conf.hw = new Hardware(4, 3);
			 conf.hw = new Hardware(4, 4);
			 conf.hw = new Hardware(5, 0);
			 conf.hw = new Hardware(5, 1);
			 conf.hw = new Hardware(5, 2);
			 conf.hw = new Hardware(5, 3);
			 conf.hw = new Hardware(5, 4);
			 conf.hw = new Hardware(5, 5);
			 conf.hw = new Hardware(10, 256);
			 
			 startSimulation(conf);

		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}

	private void button_CrescenteCodaSuMediaActionPerformed(ActionEvent e) {
		realtimeStat=false;
		datasetQueue = new XYSeries("Queue size");
		
		try {
			Hardware hw = new Hardware(4, 0);
			 Configurator conf = new Configurator(hw, //Hardware
											/*NJOB*/			1000,    
											/*QT*/				210, 
											/*mediaEsecJob*/	1000d,
											/*classRate*/		1d, 
											/*rtJobProb*/		0.17d, 
											/*clPerc*/			0.15d, 
											/*mediaArrivo*/		4000, 
											/*simTime*/			362011, 
											CompletelyFairScheduler.class ,
											RedBlackTree.class,
											/*endJob*/			false,
											/*costante*/		false,
											/*crescente*/ true);
			 
			 startSimulation(conf);
			 
			 synchronized(this){	 
				 new EsecuzioneJobs(hw.numcpus()+"CPU " + hw.numgpus() + "GPU");
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}
//BURST
	private void button_BurstCodaSuMediaActionPerformed(ActionEvent e) {
		realtimeStat=false;
		datasetQueue = new XYSeries("Queue size");
		
		try {
			Hardware hw = new Hardware(4, 0);
			 Configurator conf = new Configurator(hw, //Hardware
											/*NJOB*/			1000,    
											/*QT*/				210, 
											/*mediaEsecJob*/	1000d,
											/*classRate*/		1d, 
											/*rtJobProb*/		0.17d, 
											/*clPerc*/			0.15d, 
											/*mediaArrivo*/		200, 
											/*simTime*/			362011, 
											CompletelyFairScheduler.class ,
											RedBlackTree.class,
											/*endJob*/			false,
											/*costante*/		false, false);
			 
			 startSimulation(conf);
			 
			 synchronized(this){	 
				 new EsecuzioneJobs(hw.numcpus()+"CPU " + hw.numgpus() + "GPU");
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}

	private void button_BurstTempoMedioArrivoActionPerformed(ActionEvent e) {
		realtimeStat=false;
		try {
			Hardware hw = new Hardware(1, 0);
			Configurator conf = new Configurator(hw, //Hardware
					/*NJOB*/			1000,    
					/*QT*/				210, 
					/*mediaEsecJob*/	1000d,
					/*classRate*/		1d, 
					/*rtJobProb*/		0.17d, 
					/*clPerc*/			0.15d, 
					/*mediaArrivo*/		200, 
					/*simTime*/			362011, 
					CompletelyFairScheduler.class ,
					RedBlackTree.class,
					/*endJob*/			false,
					/*costante*/		false, false);
			 
			 
//			 conf.hw = new Hardware(1, 1);
			 conf.hw = new Hardware(2, 0);
			 conf.hw = new Hardware(2, 1);
			 conf.hw = new Hardware(2, 2);
			 conf.hw = new Hardware(3, 0);
			 conf.hw = new Hardware(3, 1);
			 conf.hw = new Hardware(3, 2);
 		     conf.hw = new Hardware(3, 3);
			 conf.hw = new Hardware(4, 0);
			 conf.hw = new Hardware(4, 1);
			 conf.hw = new Hardware(4, 2);
			 conf.hw = new Hardware(4, 3);
			 conf.hw = new Hardware(4, 4);
			 conf.hw = new Hardware(5, 0);
			 conf.hw = new Hardware(5, 1);
			 conf.hw = new Hardware(5, 2);
			 conf.hw = new Hardware(5, 3);
			 conf.hw = new Hardware(5, 4);
			 conf.hw = new Hardware(5, 5);
			 conf.hw = new Hardware(10, 256);
			 
			 startSimulation(conf);

		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}
	//CLASS RATE
	private void button_ClassRateCodaSuMediaActionPerformed(ActionEvent e) {
		realtimeStat=false;
		datasetQueue = new XYSeries("Queue size");
		
		try {
			Hardware hw = new Hardware(4, 0);
			 Configurator conf = new Configurator(hw, //Hardware
											/*NJOB*/			1000,    
											/*QT*/				210, 
											/*mediaEsecJob*/	1000d,
											/*classRate*/		Double.parseDouble(text_ClassRate.getText()), 
											/*rtJobProb*/		0.25d, 
											/*clPerc*/			0.40d, 
											/*mediaArrivo*/		700, 
											/*simTime*/			362011, 
											CompletelyFairScheduler.class ,
											RedBlackTree.class,
											/*endJob*/			false,
											/*costante*/		false, false);
			 
			 startSimulation(conf);
			 
			 synchronized(this){	 
				 new EsecuzioneJobs(hw.numcpus()+"CPU " + hw.numgpus() + "GPU");
			}
		
		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}

	private void button_ClassRateTempoMedioArrivoActionPerformed(ActionEvent e) {
		realtimeStat=false;
		try {
			Hardware hw = new Hardware(1, 0);
			 Configurator conf = new Configurator(hw, //Hardware
						/*NJOB*/			1000,    
						/*QT*/				210, 
						/*mediaEsecJob*/	1000d,
						/*classRate*/		0.9d, 
						/*rtJobProb*/		0.25d, 
						/*clPerc*/			0.40d, 
						/*mediaArrivo*/		700, 
						/*simTime*/			362011, 
						CompletelyFairScheduler.class ,
						RedBlackTree.class,
						/*endJob*/			false,
						/*costante*/		false, false);
			 
			 
//			 conf.hw = new Hardware(1, 1);
//			 conf.hw = new Hardware(2, 0);
//			 conf.hw = new Hardware(2, 1);
//			 conf.hw = new Hardware(2, 2);
//			 conf.hw = new Hardware(3, 0);
//			 conf.hw = new Hardware(3, 1);
//			 conf.hw = new Hardware(3, 2);
// 		     conf.hw = new Hardware(3, 3);
//			 conf.hw = new Hardware(4, 0);
//			 conf.hw = new Hardware(4, 1);
//			 conf.hw = new Hardware(4, 2);
//			 conf.hw = new Hardware(4, 3);
//			 conf.hw = new Hardware(4, 4);
//			 conf.hw = new Hardware(5, 0);
//			 conf.hw = new Hardware(5, 1);
//			 conf.hw = new Hardware(5, 2);
//			 conf.hw = new Hardware(5, 3);
//			 conf.hw = new Hardware(5, 4);
//			 conf.hw = new Hardware(5, 5);
//			 conf.hw = new Hardware(10, 256);
//			 
			 startSimulation(conf);

		} catch (Exception e1) {
			e1.printStackTrace();
			erroreLabel.setText(e1.getMessage());
			dialog1.setVisible(true);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Lgc M
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		HPSsimWindow = new JFrame();
		hpssimWindow = new JPanel();
		hpssimTab = new JTabbedPane();
		panelConfiguration = new JPanel();
		label10 = new JLabel();
		label3 = new JLabel();
		ncpu = new JTextField();
		label4 = new JLabel();
		ngpu = new JTextField();
		vSpacer1 = new JPanel(null);
		label11 = new JLabel();
		label2 = new JLabel();
		sliderSimulationTime = new JSlider();
		textFieldSimTime = new JLabel();
		label1 = new JLabel();
		sliderJob = new JSlider();
		textFieldNjob = new JLabel();
		label9 = new JLabel();
		textFieldQVGA = new JTextField();
		label43 = new JLabel();
		tex_mediaexe = new JTextField();
		checkBoxEndJob = new JCheckBox();
		label6 = new JLabel();
		comboBoxScheduler = new JComboBox<>();
		label8 = new JLabel();
		textFieldTimeSlice = new JTextField();
		label7 = new JLabel();
		comboBoxQueue = new JComboBox<>();
		vSpacer2 = new JPanel(null);
		label21 = new JLabel();
		sliderclassRate = new JSlider();
		labelclassRate = new JLabel();
		label12 = new JLabel();
		sliderRTJob = new JSlider();
		labelRT = new JLabel();
		label14 = new JLabel();
		sliderOpenCl = new JSlider();
		labelOPENCL = new JLabel();
		checkBox_enableLog = new JCheckBox();
		panelPerformance = new JPanel();
		separator1 = new JSeparator();
		tabbedPane1 = new JTabbedPane();
		panelCPU = new JPanel();
		labelCPUUsage = new JLabel();
		panelCPUQueue = new JPanel();
		tabbedPane2 = new JTabbedPane();
		panelGPU = new JPanel();
		labelGPUUsage = new JLabel();
		panelGPUQueue = new JPanel();
		panel2 = new JPanel();
		label18 = new JLabel();
		virtualTime = new JTextField();
		label5 = new JLabel();
		processiNelSistema = new JTextField();
		label17 = new JLabel();
		processiElaborazione = new JTextField();
		label16 = new JLabel();
		processiInCoda = new JTextField();
		label15 = new JLabel();
		ldavg_1 = new JTextField();
		label19 = new JLabel();
		ldavg_5 = new JTextField();
		label20 = new JLabel();
		ldavg_15 = new JTextField();
		panel3 = new JPanel();
		progressBar = new JProgressBar();
		panelGraph = new JPanel();
		graphPanel = new JPanel();
		label23 = new JLabel();
		label38 = new JLabel();
		text_ClassRate = new JTextField();
		label24 = new JLabel();
		button_CostanteCodaSuMedia = new JButton();
		label39 = new JLabel();
		button_ClassRateCodaSuMedia = new JButton();
		label25 = new JLabel();
		button_CostanteTempoMedioArrivo = new JButton();
		hSpacer1 = new JPanel(null);
		label40 = new JLabel();
		button_ClassRateTempoMedioArrivo = new JButton();
		label26 = new JLabel();
		label41 = new JLabel();
		label27 = new JLabel();
		label42 = new JLabel();
		label28 = new JLabel();
		label33 = new JLabel();
		label29 = new JLabel();
		label30 = new JLabel();
		label31 = new JLabel();
		label32 = new JLabel();
		button_CrescenteCodaSuMedia = new JButton();
		label34 = new JLabel();
		button_BurstCodaSuMedia = new JButton();
		button_CrescenteCarico = new JButton();
		label35 = new JLabel();
		button_BurstTempoMedioArrivo = new JButton();
		label36 = new JLabel();
		label37 = new JLabel();
		label22 = new JLabel();
		title1 = compFactory.createTitle("HPSsim 2.0 ");
		button1 = new JButton();
		okButton = new JButton();
		button3 = new JButton();
		dialog1 = new JDialog();
		button2 = new JButton();
		label13 = new JLabel();
		erroreLabel = new JLabel();
		Grafici = new JFrame();
		panelGraficoFinestra = new JPanel();

		//======== HPSsimWindow ========
		{
			HPSsimWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			HPSsimWindow.setResizable(false);
			Container HPSsimWindowContentPane = HPSsimWindow.getContentPane();

			//======== hpssimWindow ========
			{
				hpssimWindow.setForeground(Color.blue);

				// JFormDesigner evaluation mark
				hpssimWindow.setBorder(new javax.swing.border.CompoundBorder(
					new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
						"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), hpssimWindow.getBorder())); hpssimWindow.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

				hpssimWindow.setLayout(null);

				//======== hpssimTab ========
				{

					//======== panelConfiguration ========
					{
						panelConfiguration.setLayout(new TableLayout(new double[][] {
							{1, 70, 70, 70, 68, 70, 70, 74},
							{0.01, 27, 21, 26, 27, 25, 25, 25, 21, 21, TableLayout.PREFERRED, 12, TableLayout.PREFERRED, 22, 23}}));
						((TableLayout)panelConfiguration.getLayout()).setHGap(5);
						((TableLayout)panelConfiguration.getLayout()).setVGap(5);

						//---- label10 ----
						label10.setText("Hardware");
						label10.setFont(new Font("Segoe UI", Font.ITALIC, 16));
						panelConfiguration.add(label10, new TableLayoutConstraints(1, 1, 7, 1, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

						//---- label3 ----
						label3.setText("CPU");
						label3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
						label3.setLabelFor(ncpu);
						panelConfiguration.add(label3, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- ncpu ----
						ncpu.setText("4");
						panelConfiguration.add(ncpu, new TableLayoutConstraints(2, 2, 3, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label4 ----
						label4.setText("GPU");
						label4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
						label4.setLabelFor(ngpu);
						panelConfiguration.add(label4, new TableLayoutConstraints(4, 2, 4, 2, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- ngpu ----
						ngpu.setText("0");
						panelConfiguration.add(ngpu, new TableLayoutConstraints(5, 2, 6, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						panelConfiguration.add(vSpacer1, new TableLayoutConstraints(1, 3, 7, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label11 ----
						label11.setText("Simulation");
						label11.setFont(new Font("Segoe UI", Font.ITALIC, 16));
						panelConfiguration.add(label11, new TableLayoutConstraints(1, 4, 7, 4, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

						//---- label2 ----
						label2.setText("Sim Time");
						panelConfiguration.add(label2, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderSimulationTime ----
						sliderSimulationTime.setValue(100000);
						sliderSimulationTime.setMaximum(1200000);
						sliderSimulationTime.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderSimulationTimeStateChanged(e);
							}
						});
						panelConfiguration.add(sliderSimulationTime, new TableLayoutConstraints(2, 5, 6, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- textFieldSimTime ----
						textFieldSimTime.setText("100000 ms");
						panelConfiguration.add(textFieldSimTime, new TableLayoutConstraints(7, 5, 7, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label1 ----
						label1.setText("Numero di job");
						panelConfiguration.add(label1, new TableLayoutConstraints(1, 6, 1, 6, TableLayoutConstraints.RIGHT, TableLayoutConstraints.CENTER));

						//---- sliderJob ----
						sliderJob.setMaximum(20000);
						sliderJob.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderJobStateChanged(e);
							}
						});
						panelConfiguration.add(sliderJob, new TableLayoutConstraints(2, 6, 6, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- textFieldNjob ----
						textFieldNjob.setText("50");
						panelConfiguration.add(textFieldNjob, new TableLayoutConstraints(7, 6, 7, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label9 ----
						label9.setText("Media arrivo");
						panelConfiguration.add(label9, new TableLayoutConstraints(1, 7, 1, 7, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- textFieldQVGA ----
						textFieldQVGA.setText("230");
						panelConfiguration.add(textFieldQVGA, new TableLayoutConstraints(2, 7, 2, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label43 ----
						label43.setText("Media exe");
						panelConfiguration.add(label43, new TableLayoutConstraints(3, 7, 3, 7, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- tex_mediaexe ----
						tex_mediaexe.setText("1000");
						panelConfiguration.add(tex_mediaexe, new TableLayoutConstraints(4, 7, 4, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- checkBoxEndJob ----
						checkBoxEndJob.setText("End Job");
						panelConfiguration.add(checkBoxEndJob, new TableLayoutConstraints(6, 7, 6, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label6 ----
						label6.setText("Scheduler");
						panelConfiguration.add(label6, new TableLayoutConstraints(1, 9, 1, 9, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- comboBoxScheduler ----
						comboBoxScheduler.setModel(new DefaultComboBoxModel<>(new String[] {
							"Priority Round Robin",
							"Completely Fair Scheduler"
						}));
						comboBoxScheduler.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								comboBoxSchedulerActionPerformed(e);
							}
						});
						panelConfiguration.add(comboBoxScheduler, new TableLayoutConstraints(2, 9, 5, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label8 ----
						label8.setText("Time Slice");
						panelConfiguration.add(label8, new TableLayoutConstraints(6, 9, 6, 9, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- textFieldTimeSlice ----
						textFieldTimeSlice.setText("210");
						panelConfiguration.add(textFieldTimeSlice, new TableLayoutConstraints(7, 9, 7, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label7 ----
						label7.setText("Queue");
						panelConfiguration.add(label7, new TableLayoutConstraints(1, 10, 1, 10, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- comboBoxQueue ----
						comboBoxQueue.setModel(new DefaultComboBoxModel<>(new String[] {
							"FIFO",
							"Highest Priority First",
							"Shortest Job First",
							"Round Robin",
							"Random Queue"
						}));
						panelConfiguration.add(comboBoxQueue, new TableLayoutConstraints(2, 10, 5, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						panelConfiguration.add(vSpacer2, new TableLayoutConstraints(1, 11, 7, 11, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label21 ----
						label21.setText("Class Rate");
						panelConfiguration.add(label21, new TableLayoutConstraints(1, 12, 1, 12, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderclassRate ----
						sliderclassRate.setValue(99);
						sliderclassRate.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderclassRateStateChanged(e);
							}
						});
						panelConfiguration.add(sliderclassRate, new TableLayoutConstraints(2, 12, 4, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelclassRate ----
						labelclassRate.setText("99%");
						panelConfiguration.add(labelclassRate, new TableLayoutConstraints(5, 12, 5, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label12 ----
						label12.setText("RT Job Prob");
						panelConfiguration.add(label12, new TableLayoutConstraints(1, 13, 1, 13, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderRTJob ----
						sliderRTJob.setValue(45);
						sliderRTJob.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderRTJobStateChanged(e);
							}
						});
						panelConfiguration.add(sliderRTJob, new TableLayoutConstraints(2, 13, 4, 13, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelRT ----
						labelRT.setText("45%");
						panelConfiguration.add(labelRT, new TableLayoutConstraints(5, 13, 5, 13, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label14 ----
						label14.setText("OpenCL Job ");
						panelConfiguration.add(label14, new TableLayoutConstraints(1, 14, 1, 14, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderOpenCl ----
						sliderOpenCl.setValue(20);
						sliderOpenCl.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderOpenClStateChanged(e);
							}
						});
						panelConfiguration.add(sliderOpenCl, new TableLayoutConstraints(2, 14, 4, 14, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelOPENCL ----
						labelOPENCL.setText("20%");
						panelConfiguration.add(labelOPENCL, new TableLayoutConstraints(5, 14, 5, 14, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- checkBox_enableLog ----
						checkBox_enableLog.setText("log");
						checkBox_enableLog.setSelected(true);
						panelConfiguration.add(checkBox_enableLog, new TableLayoutConstraints(7, 14, 7, 14, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					}
					hpssimTab.addTab("Configuration", panelConfiguration);


					//======== panelPerformance ========
					{

						//======== tabbedPane1 ========
						{

							//======== panelCPU ========
							{
								panelCPU.setLayout(new BorderLayout());

								//---- labelCPUUsage ----
								labelCPUUsage.setText("0\\0");
								labelCPUUsage.setHorizontalAlignment(SwingConstants.CENTER);
								panelCPU.add(labelCPUUsage, BorderLayout.SOUTH);
							}
							tabbedPane1.addTab("Usage", panelCPU);


							//======== panelCPUQueue ========
							{
								panelCPUQueue.setLayout(new BorderLayout());
							}
							tabbedPane1.addTab("Queue", panelCPUQueue);

						}

						//======== tabbedPane2 ========
						{

							//======== panelGPU ========
							{
								panelGPU.setLayout(new BorderLayout());

								//---- labelGPUUsage ----
								labelGPUUsage.setText("0\\0");
								labelGPUUsage.setHorizontalAlignment(SwingConstants.CENTER);
								panelGPU.add(labelGPUUsage, BorderLayout.SOUTH);
							}
							tabbedPane2.addTab("Usage", panelGPU);


							//======== panelGPUQueue ========
							{
								panelGPUQueue.setLayout(new BorderLayout());
							}
							tabbedPane2.addTab("Queue", panelGPUQueue);

						}

						//======== panel2 ========
						{
							panel2.setLayout(new TableLayout(new double[][] {
								{TableLayout.PREFERRED, TableLayout.FILL},
								{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

							//---- label18 ----
							label18.setText("Virtual Time");
							panel2.add(label18, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
							panel2.add(virtualTime, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label5 ----
							label5.setText("Processi nel sistema");
							panel2.add(label5, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

							//---- processiNelSistema ----
							processiNelSistema.setText("0");
							panel2.add(processiNelSistema, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label17 ----
							label17.setText("Processi in elaborazione");
							panel2.add(label17, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

							//---- processiElaborazione ----
							processiElaborazione.setText("0");
							panel2.add(processiElaborazione, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label16 ----
							label16.setText("Processi in coda");
							panel2.add(label16, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

							//---- processiInCoda ----
							processiInCoda.setText("0");
							panel2.add(processiInCoda, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label15 ----
							label15.setText("ldavg_1");
							panel2.add(label15, new TableLayoutConstraints(0, 5, 0, 5, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
							panel2.add(ldavg_1, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label19 ----
							label19.setText("ldavg_5");
							panel2.add(label19, new TableLayoutConstraints(0, 6, 0, 6, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
							panel2.add(ldavg_5, new TableLayoutConstraints(1, 6, 1, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label20 ----
							label20.setText("ldavg_15");
							panel2.add(label20, new TableLayoutConstraints(0, 7, 0, 7, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
							panel2.add(ldavg_15, new TableLayoutConstraints(1, 7, 1, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						}

						//======== panel3 ========
						{
							panel3.setLayout(new TableLayout(new double[][] {
								{TableLayout.PREFERRED, TableLayout.PREFERRED},
								{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
						}

						GroupLayout panelPerformanceLayout = new GroupLayout(panelPerformance);
						panelPerformance.setLayout(panelPerformanceLayout);
						panelPerformanceLayout.setHorizontalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGroup(panelPerformanceLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(panelPerformanceLayout.createParallelGroup()
										.addComponent(separator1)
										.addGroup(panelPerformanceLayout.createSequentialGroup()
											.addGroup(panelPerformanceLayout.createParallelGroup()
												.addGroup(panelPerformanceLayout.createSequentialGroup()
													.addComponent(panel2, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addGroup(panelPerformanceLayout.createParallelGroup()
														.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addGroup(panelPerformanceLayout.createSequentialGroup()
															.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)
															.addGap(0, 0, Short.MAX_VALUE))))
												.addGroup(panelPerformanceLayout.createSequentialGroup()
													.addComponent(tabbedPane1, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
													.addComponent(tabbedPane2, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)))
											.addContainerGap(8, Short.MAX_VALUE))))
						);
						panelPerformanceLayout.setVerticalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGroup(panelPerformanceLayout.createSequentialGroup()
									.addContainerGap(15, Short.MAX_VALUE)
									.addGroup(panelPerformanceLayout.createParallelGroup()
										.addComponent(tabbedPane2, GroupLayout.DEFAULT_SIZE, 218, GroupLayout.PREFERRED_SIZE)
										.addComponent(tabbedPane1, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
									.addGroup(panelPerformanceLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addComponent(panel2, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
										.addGroup(panelPerformanceLayout.createSequentialGroup()
											.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addGap(18, 18, 18)
											.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)))
									.addContainerGap())
						);
					}
					hpssimTab.addTab("Performance", panelPerformance);


					//======== panelGraph ========
					{

						//======== graphPanel ========
						{
							graphPanel.setLayout(new TableLayout(new double[][] {
								{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED},
								{27, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 25, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

							//---- label23 ----
							label23.setText("Carico costante");
							label23.setFont(label23.getFont().deriveFont(Font.BOLD|Font.ITALIC));
							graphPanel.add(label23, new TableLayoutConstraints(0, 0, 1, 0, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

							//---- label38 ----
							label38.setText("Classification Rate");
							label38.setFont(label38.getFont().deriveFont(Font.BOLD|Font.ITALIC));
							graphPanel.add(label38, new TableLayoutConstraints(5, 0, 6, 0, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));
							graphPanel.add(text_ClassRate, new TableLayoutConstraints(7, 0, 7, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label24 ----
							label24.setText("Coda\\Media");
							graphPanel.add(label24, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_CostanteCodaSuMedia ----
							button_CostanteCodaSuMedia.setText("Esegui");
							button_CostanteCodaSuMedia.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_CostanteCodaSuMediaActionPerformed(e);
								}
							});
							graphPanel.add(button_CostanteCodaSuMedia, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label39 ----
							label39.setText("Coda\\Media");
							graphPanel.add(label39, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_ClassRateCodaSuMedia ----
							button_ClassRateCodaSuMedia.setText("Esegui");
							button_ClassRateCodaSuMedia.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_ClassRateCodaSuMediaActionPerformed(e);
								}
							});
							graphPanel.add(button_ClassRateCodaSuMedia, new TableLayoutConstraints(7, 1, 7, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label25 ----
							label25.setText("Tempo Medio Arrivo");
							graphPanel.add(label25, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_CostanteTempoMedioArrivo ----
							button_CostanteTempoMedioArrivo.setText("Esegui");
							button_CostanteTempoMedioArrivo.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_CostanteTempoMedioArrivoActionPerformed(e);
								}
							});
							graphPanel.add(button_CostanteTempoMedioArrivo, new TableLayoutConstraints(2, 2, 2, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
							graphPanel.add(hSpacer1, new TableLayoutConstraints(3, 0, 3, 15, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label40 ----
							label40.setText("Tempo Medio Arrivo");
							graphPanel.add(label40, new TableLayoutConstraints(5, 2, 5, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_ClassRateTempoMedioArrivo ----
							button_ClassRateTempoMedioArrivo.setText("Esegui");
							button_ClassRateTempoMedioArrivo.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_ClassRateTempoMedioArrivoActionPerformed(e);
								}
							});
							graphPanel.add(button_ClassRateTempoMedioArrivo, new TableLayoutConstraints(7, 2, 7, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label26 ----
							label26.setText("Troughput");
							graphPanel.add(label26, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label41 ----
							label41.setText("Troughput");
							graphPanel.add(label41, new TableLayoutConstraints(5, 3, 5, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label27 ----
							label27.setText("Carico");
							graphPanel.add(label27, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label42 ----
							label42.setText("Carico");
							graphPanel.add(label42, new TableLayoutConstraints(5, 4, 5, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label28 ----
							label28.setText("Carico Crescente");
							label28.setFont(label28.getFont().deriveFont(Font.BOLD|Font.ITALIC));
							graphPanel.add(label28, new TableLayoutConstraints(0, 5, 1, 5, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

							//---- label33 ----
							label33.setText("Carico Burst");
							label33.setFont(label33.getFont().deriveFont(Font.BOLD|Font.ITALIC));
							graphPanel.add(label33, new TableLayoutConstraints(5, 5, 6, 5, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

							//---- label29 ----
							label29.setText("Coda\\Media");
							graphPanel.add(label29, new TableLayoutConstraints(0, 6, 0, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label30 ----
							label30.setText("Tempo Medio Arrivo");
							graphPanel.add(label30, new TableLayoutConstraints(0, 7, 0, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label31 ----
							label31.setText("Troughput");
							graphPanel.add(label31, new TableLayoutConstraints(0, 8, 0, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label32 ----
							label32.setText("Carico");
							graphPanel.add(label32, new TableLayoutConstraints(0, 9, 0, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_CrescenteCodaSuMedia ----
							button_CrescenteCodaSuMedia.setText("Esegui");
							button_CrescenteCodaSuMedia.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_CrescenteCodaSuMediaActionPerformed(e);
								}
							});
							graphPanel.add(button_CrescenteCodaSuMedia, new TableLayoutConstraints(2, 6, 2, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label34 ----
							label34.setText("Coda\\Media");
							graphPanel.add(label34, new TableLayoutConstraints(5, 6, 5, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_BurstCodaSuMedia ----
							button_BurstCodaSuMedia.setText("Esegui");
							button_BurstCodaSuMedia.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_BurstCodaSuMediaActionPerformed(e);
								}
							});
							graphPanel.add(button_BurstCodaSuMedia, new TableLayoutConstraints(7, 6, 7, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_CrescenteCarico ----
							button_CrescenteCarico.setText("Esegui");
							button_CrescenteCarico.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_CrescenteCaricoActionPerformed(e);
								}
							});
							graphPanel.add(button_CrescenteCarico, new TableLayoutConstraints(2, 7, 2, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label35 ----
							label35.setText("Tempo Medio Arrivo");
							graphPanel.add(label35, new TableLayoutConstraints(5, 7, 5, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- button_BurstTempoMedioArrivo ----
							button_BurstTempoMedioArrivo.setText("Esegui");
							button_BurstTempoMedioArrivo.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									button_BurstTempoMedioArrivoActionPerformed(e);
								}
							});
							graphPanel.add(button_BurstTempoMedioArrivo, new TableLayoutConstraints(7, 7, 7, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label36 ----
							label36.setText("Troughput");
							graphPanel.add(label36, new TableLayoutConstraints(5, 8, 5, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label37 ----
							label37.setText("Carico");
							graphPanel.add(label37, new TableLayoutConstraints(5, 9, 5, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						}

						//---- label22 ----
						label22.setText("Grafici");
						label22.setFont(label22.getFont().deriveFont(label22.getFont().getStyle() | Font.BOLD, label22.getFont().getSize() + 4f));

						GroupLayout panelGraphLayout = new GroupLayout(panelGraph);
						panelGraph.setLayout(panelGraphLayout);
						panelGraphLayout.setHorizontalGroup(
							panelGraphLayout.createParallelGroup()
								.addGroup(panelGraphLayout.createSequentialGroup()
									.addGroup(panelGraphLayout.createParallelGroup()
										.addGroup(panelGraphLayout.createSequentialGroup()
											.addGap(243, 243, 243)
											.addComponent(label22)
											.addGap(0, 246, Short.MAX_VALUE))
										.addGroup(GroupLayout.Alignment.TRAILING, panelGraphLayout.createSequentialGroup()
											.addContainerGap()
											.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)))
									.addContainerGap())
						);
						panelGraphLayout.setVerticalGroup(
							panelGraphLayout.createParallelGroup()
								.addGroup(GroupLayout.Alignment.TRAILING, panelGraphLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(label22)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
									.addContainerGap())
						);
					}
					hpssimTab.addTab("Graph", panelGraph);

				}
				hpssimWindow.add(hpssimTab);
				hpssimTab.setBounds(10, 40, 555, 450);

				//---- title1 ----
				title1.setFont(title1.getFont().deriveFont(title1.getFont().getSize() + 8f));
				hpssimWindow.add(title1);
				title1.setBounds(10, 11, 132, title1.getPreferredSize().height);

				//---- button1 ----
				button1.setText("Stop");
				button1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						button1ActionPerformed(e);
					}
				});
				hpssimWindow.add(button1);
				button1.setBounds(385, 495, 74, button1.getPreferredSize().height);

				//---- okButton ----
				okButton.setText("Start");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				hpssimWindow.add(okButton);
				okButton.setBounds(470, 495, 74, okButton.getPreferredSize().height);

				//---- button3 ----
				button3.setText("Resume");
				button3.setVisible(false);
				button3.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						pauseActionPerformed(e);
					}
				});
				hpssimWindow.add(button3);
				button3.setBounds(300, 495, 74, button3.getPreferredSize().height);

				{ // compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < hpssimWindow.getComponentCount(); i++) {
						Rectangle bounds = hpssimWindow.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = hpssimWindow.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					hpssimWindow.setMinimumSize(preferredSize);
					hpssimWindow.setPreferredSize(preferredSize);
				}
			}

			GroupLayout HPSsimWindowContentPaneLayout = new GroupLayout(HPSsimWindowContentPane);
			HPSsimWindowContentPane.setLayout(HPSsimWindowContentPaneLayout);
			HPSsimWindowContentPaneLayout.setHorizontalGroup(
				HPSsimWindowContentPaneLayout.createParallelGroup()
					.addGroup(HPSsimWindowContentPaneLayout.createSequentialGroup()
						.addComponent(hpssimWindow, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE)
						.addGap(0, 4, Short.MAX_VALUE))
			);
			HPSsimWindowContentPaneLayout.setVerticalGroup(
				HPSsimWindowContentPaneLayout.createParallelGroup()
					.addGroup(HPSsimWindowContentPaneLayout.createSequentialGroup()
						.addComponent(hpssimWindow, GroupLayout.PREFERRED_SIZE, 528, GroupLayout.PREFERRED_SIZE)
						.addGap(0, 1, Short.MAX_VALUE))
			);
			HPSsimWindow.pack();
			HPSsimWindow.setLocationRelativeTo(HPSsimWindow.getOwner());
		}

		//======== dialog1 ========
		{
			dialog1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			Container dialog1ContentPane = dialog1.getContentPane();

			//---- button2 ----
			button2.setText("ok");
			button2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					button2ActionPerformed(e);
				}
			});

			//---- label13 ----
			label13.setText("Attenzione!");

			GroupLayout dialog1ContentPaneLayout = new GroupLayout(dialog1ContentPane);
			dialog1ContentPane.setLayout(dialog1ContentPaneLayout);
			dialog1ContentPaneLayout.setHorizontalGroup(
				dialog1ContentPaneLayout.createParallelGroup()
					.addGroup(dialog1ContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(dialog1ContentPaneLayout.createParallelGroup()
							.addComponent(label13, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
							.addGroup(GroupLayout.Alignment.TRAILING, dialog1ContentPaneLayout.createSequentialGroup()
								.addGap(0, 281, Short.MAX_VALUE)
								.addComponent(button2))
							.addComponent(erroreLabel, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
						.addContainerGap())
			);
			dialog1ContentPaneLayout.setVerticalGroup(
				dialog1ContentPaneLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, dialog1ContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(label13, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
						.addComponent(erroreLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button2)
						.addContainerGap())
			);
			dialog1.pack();
			dialog1.setLocationRelativeTo(dialog1.getOwner());
		}

		//======== Grafici ========
		{
			Container GraficiContentPane = Grafici.getContentPane();

			//======== panelGraficoFinestra ========
			{

				// JFormDesigner evaluation mark
				panelGraficoFinestra.setBorder(new javax.swing.border.CompoundBorder(
					new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
						"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), panelGraficoFinestra.getBorder())); panelGraficoFinestra.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

				panelGraficoFinestra.setLayout(new BorderLayout());
			}

			GroupLayout GraficiContentPaneLayout = new GroupLayout(GraficiContentPane);
			GraficiContentPane.setLayout(GraficiContentPaneLayout);
			GraficiContentPaneLayout.setHorizontalGroup(
				GraficiContentPaneLayout.createParallelGroup()
					.addGroup(GraficiContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(panelGraficoFinestra, GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
						.addContainerGap())
			);
			GraficiContentPaneLayout.setVerticalGroup(
				GraficiContentPaneLayout.createParallelGroup()
					.addGroup(GraficiContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(panelGraficoFinestra, GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
						.addContainerGap())
			);
			Grafici.pack();
			Grafici.setLocationRelativeTo(Grafici.getOwner());
		}
		// //GEN-END:initComponents
	}

	public org.jfree.data.xy.XYSeries datasetQueue = new XYSeries("Queue size");
	
	public void setQueueXY(String hw){
		XYSeriesCollection xyseriescollection = new XYSeriesCollection();
		xyseriescollection.addSeries(datasetQueue);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Queue Size - " + hw,
				"Tempo", "runq_sz", xyseriescollection,
				PlotOrientation.VERTICAL, true, true, false);
			XYPlot xyplot = (XYPlot) chart.getPlot();
			xyplot.setBackgroundPaint(Color.white);
			xyplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
			
			panelGraficoFinestra.add("Center", new ChartPanel(chart));
			panelGraficoFinestra.add("South", new Label(""));
	}
	
	public DefaultValueDataset datasetCPU;
	public DefaultValueDataset datasetGPU;
	
	public DefaultValueDataset datasetQueueCPU;
	public DefaultValueDataset datasetQueueGPU;
	
	private void setMeter() {
		datasetCPU = new DefaultValueDataset(0D);
		JFreeChart jfreechartCPU = createChart(datasetCPU, "CPU Usage");

		datasetGPU = new DefaultValueDataset(0D);
		JFreeChart jfreechartGPU = createChart(datasetGPU, "GPU Usage");

		panelCPU.add("Center", new ChartPanel(jfreechartCPU));
		panelCPU.add("South", new Label(""));

		panelGPU.add("Center", new ChartPanel(jfreechartGPU));
		panelGPU.add("South", new Label(""));
		
		datasetQueueCPU = new DefaultValueDataset(0D);
		datasetQueueGPU = new DefaultValueDataset(0D);
		
		ThermometerPlot gpuThermometerQueue = new ThermometerPlot(datasetQueueGPU);
		gpuThermometerQueue.setRange(0, 50);

		gpuThermometerQueue.setSubrange(ThermometerPlot.NORMAL, 0.0, 10.0);
		gpuThermometerQueue.setSubrange(ThermometerPlot.WARNING, 10.0, 25.0);
		gpuThermometerQueue.setSubrange(ThermometerPlot.CRITICAL, 25.0, 5000.0);
		gpuThermometerQueue.setUnits(ThermometerPlot.UNITS_NONE);
		gpuThermometerQueue.setBulbRadius(22);
		gpuThermometerQueue.setColumnRadius(20);
		
		ThermometerPlot cpuThermometerQueue = new ThermometerPlot(datasetQueueCPU);
		cpuThermometerQueue.setRange(0, 50);

		cpuThermometerQueue.setSubrange(ThermometerPlot.NORMAL, 0.0, 10.0);
		cpuThermometerQueue.setSubrange(ThermometerPlot.WARNING, 10.0, 25.0);
		cpuThermometerQueue.setSubrange(ThermometerPlot.CRITICAL, 25.0, 5000.0);
		cpuThermometerQueue.setUnits(ThermometerPlot.UNITS_NONE);
		cpuThermometerQueue.setBulbRadius(22);
		cpuThermometerQueue.setColumnRadius(20);
		
		panelCPUQueue.add("Center", new ChartPanel(new JFreeChart("CPU Queue", JFreeChart.DEFAULT_TITLE_FONT, cpuThermometerQueue, false)));
		panelCPUQueue.add("South", new Label(""));
		
		panelGPUQueue.add("Center", new ChartPanel(new JFreeChart("GPU Queue", JFreeChart.DEFAULT_TITLE_FONT, gpuThermometerQueue, false)));
		panelGPUQueue.add("South", new Label(""));
	}

	private JFreeChart createChart(ValueDataset valuedataset, String name) {
		MeterPlot meterplot = new MeterPlot(valuedataset);
		meterplot.addInterval(new MeterInterval("High", new Range(80D, 100D)));
		meterplot.setDialOutlinePaint(Color.white);

		JFreeChart jfreechart = new JFreeChart(name, JFreeChart.DEFAULT_TITLE_FONT, meterplot, false);
		return jfreechart;
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Lgc M
	public JFrame HPSsimWindow;
	private JPanel hpssimWindow;
	private JTabbedPane hpssimTab;
	private JPanel panelConfiguration;
	private JLabel label10;
	private JLabel label3;
	private JTextField ncpu;
	private JLabel label4;
	private JTextField ngpu;
	private JPanel vSpacer1;
	private JLabel label11;
	private JLabel label2;
	private JSlider sliderSimulationTime;
	private JLabel textFieldSimTime;
	private JLabel label1;
	private JSlider sliderJob;
	private JLabel textFieldNjob;
	private JLabel label9;
	private JTextField textFieldQVGA;
	private JLabel label43;
	private JTextField tex_mediaexe;
	private JCheckBox checkBoxEndJob;
	private JLabel label6;
	private JComboBox<String> comboBoxScheduler;
	private JLabel label8;
	private JTextField textFieldTimeSlice;
	private JLabel label7;
	private JComboBox<String> comboBoxQueue;
	private JPanel vSpacer2;
	private JLabel label21;
	private JSlider sliderclassRate;
	private JLabel labelclassRate;
	private JLabel label12;
	private JSlider sliderRTJob;
	private JLabel labelRT;
	private JLabel label14;
	private JSlider sliderOpenCl;
	private JLabel labelOPENCL;
	private JCheckBox checkBox_enableLog;
	private JPanel panelPerformance;
	private JSeparator separator1;
	private JTabbedPane tabbedPane1;
	private JPanel panelCPU;
	public JLabel labelCPUUsage;
	private JPanel panelCPUQueue;
	private JTabbedPane tabbedPane2;
	private JPanel panelGPU;
	public JLabel labelGPUUsage;
	private JPanel panelGPUQueue;
	private JPanel panel2;
	private JLabel label18;
	public JTextField virtualTime;
	private JLabel label5;
	public JTextField processiNelSistema;
	private JLabel label17;
	public JTextField processiElaborazione;
	private JLabel label16;
	public JTextField processiInCoda;
	private JLabel label15;
	public JTextField ldavg_1;
	private JLabel label19;
	public JTextField ldavg_5;
	private JLabel label20;
	public JTextField ldavg_15;
	private JPanel panel3;
	public JProgressBar progressBar;
	private JPanel panelGraph;
	private JPanel graphPanel;
	private JLabel label23;
	private JLabel label38;
	private JTextField text_ClassRate;
	private JLabel label24;
	private JButton button_CostanteCodaSuMedia;
	private JLabel label39;
	private JButton button_ClassRateCodaSuMedia;
	private JLabel label25;
	private JButton button_CostanteTempoMedioArrivo;
	private JPanel hSpacer1;
	private JLabel label40;
	private JButton button_ClassRateTempoMedioArrivo;
	private JLabel label26;
	private JLabel label41;
	private JLabel label27;
	private JLabel label42;
	private JLabel label28;
	private JLabel label33;
	private JLabel label29;
	private JLabel label30;
	private JLabel label31;
	private JLabel label32;
	private JButton button_CrescenteCodaSuMedia;
	private JLabel label34;
	private JButton button_BurstCodaSuMedia;
	private JButton button_CrescenteCarico;
	private JLabel label35;
	private JButton button_BurstTempoMedioArrivo;
	private JLabel label36;
	private JLabel label37;
	private JLabel label22;
	private JLabel title1;
	private JButton button1;
	private JButton okButton;
	private JButton button3;
	private JDialog dialog1;
	private JButton button2;
	private JLabel label13;
	private JLabel erroreLabel;
	private JFrame Grafici;
	private JPanel panelGraficoFinestra;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setEndJobs(boolean b) {
		endJobs = b;
	}
}
