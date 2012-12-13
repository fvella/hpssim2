/*
 * Created by JFormDesigner on Tue Dec 11 16:38:43 CET 2012
 */

package hpssim.grafica;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import hpssim.hardware.Hardware;
import hpssim.scheduler.Configurator;
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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;

import com.jgoodies.forms.factories.DefaultComponentFactory;

/**
 * @author 
 */
public class HPSsim {

	public Thread sim;

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

	private void okButtonActionPerformed(ActionEvent e) {
		try {

			int njobs = Integer.parseInt((new String(textFieldNjob.getText())).trim());
			int simTime = Integer.parseInt((new String(textFieldSimTime.getText())).replace("ms", "").trim());
			// il tempo cpu che pu� spendere il processo
			Integer qt = Integer.parseInt((new String(textFieldTimeSlice.getText())).trim());
			double classificationRate = sliderclassRate.getValue() / 100;
			double realTimeJobsProb = (Double.valueOf(sliderRTJob.getValue())) / 100;
			double percentOpenCLjob = (Double.valueOf(sliderOpenCl.getValue())) / 100;
			int avgta = Integer.parseInt(textFieldQVGA.getText().trim());

			Hardware hw = new Hardware(Integer.parseInt(ncpu.getText()), Integer.parseInt(ngpu.getText()));
			Configurator conf = new Configurator(hw, njobs, qt.intValue(), classificationRate, realTimeJobsProb, percentOpenCLjob, avgta, simTime);
			sim = new Simulator(conf, this);

			synchronized (sim) {
				((Simulator) sim).run = true;
				sim.notify();
			}

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
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - 
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
		label6 = new JLabel();
		comboBoxScheduler = new JComboBox<>();
		label8 = new JLabel();
		textFieldTimeSlice = new JTextField();
		label7 = new JLabel();
		comboBoxQueue = new JComboBox<>();
		label12 = new JLabel();
		sliderRTJob = new JSlider();
		labelRT = new JLabel();
		label14 = new JLabel();
		sliderOpenCl = new JSlider();
		labelOPENCL = new JLabel();
		label21 = new JLabel();
		sliderclassRate = new JSlider();
		labelclassRate = new JLabel();
		vSpacer2 = new JPanel(null);
		resetButton = new JButton();
		panelPerformance = new JPanel();
		panelCPU = new JPanel();
		panelGPU = new JPanel();
		separator1 = new JSeparator();
		panel1 = new JPanel();
		label18 = new JLabel();
		virtualTime = new JTextField();
		label5 = new JLabel();
		processiNelSistema = new JTextField();
		label19 = new JLabel();
		mediaUsoCPU = new JTextField();
		label17 = new JLabel();
		processiElaborazione = new JTextField();
		label20 = new JLabel();
		mediaUsoGPU = new JTextField();
		label16 = new JLabel();
		processiInCoda = new JTextField();
		panelGraph = new JPanel();
		graphPanel = new JPanel();
		title1 = compFactory.createTitle("HPSsim 2.0 ");
		button1 = new JButton();
		okButton = new JButton();
		spinner1 = new JSpinner();
		dialog1 = new JDialog();
		button2 = new JButton();
		label13 = new JLabel();
		erroreLabel = new JLabel();

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
						"", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), hpssimWindow.getBorder())); hpssimWindow.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

				hpssimWindow.setLayout(null);

				//======== hpssimTab ========
				{

					//======== panelConfiguration ========
					{
						panelConfiguration.setLayout(new TableLayout(new double[][] {
							{1, 70, 70, 70, 68, 70, 70, 74},
							{0.01, 27, 21, 26, 27, 25, 25, 25, 21, 21, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 22, TableLayout.PREFERRED}}));
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
						ncpu.setText("0");
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
						sliderSimulationTime.setValue(0);
						sliderSimulationTime.setMaximum(100000000);
						sliderSimulationTime.setMinimum(10000);
						sliderSimulationTime.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderSimulationTimeStateChanged(e);
							}
						});
						panelConfiguration.add(sliderSimulationTime, new TableLayoutConstraints(2, 5, 6, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- textFieldSimTime ----
						textFieldSimTime.setText("10000 ms");
						panelConfiguration.add(textFieldSimTime, new TableLayoutConstraints(7, 5, 7, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label1 ----
						label1.setText("Numero di job");
						panelConfiguration.add(label1, new TableLayoutConstraints(1, 6, 1, 6, TableLayoutConstraints.RIGHT, TableLayoutConstraints.CENTER));

						//---- sliderJob ----
						sliderJob.setValue(0);
						sliderJob.setMaximum(1000);
						sliderJob.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderJobStateChanged(e);
							}
						});
						panelConfiguration.add(sliderJob, new TableLayoutConstraints(2, 6, 4, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- textFieldNjob ----
						textFieldNjob.setText("0");
						panelConfiguration.add(textFieldNjob, new TableLayoutConstraints(5, 6, 5, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label9 ----
						label9.setText("Media arrivo");
						panelConfiguration.add(label9, new TableLayoutConstraints(6, 6, 6, 6, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- textFieldQVGA ----
						textFieldQVGA.setText("0");
						panelConfiguration.add(textFieldQVGA, new TableLayoutConstraints(7, 6, 7, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label6 ----
						label6.setText("Scheduler");
						panelConfiguration.add(label6, new TableLayoutConstraints(1, 8, 1, 8, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

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
						panelConfiguration.add(comboBoxScheduler, new TableLayoutConstraints(2, 8, 5, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label8 ----
						label8.setText("Time Slice");
						panelConfiguration.add(label8, new TableLayoutConstraints(6, 8, 6, 8, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- textFieldTimeSlice ----
						textFieldTimeSlice.setText("0");
						panelConfiguration.add(textFieldTimeSlice, new TableLayoutConstraints(7, 8, 7, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label7 ----
						label7.setText("Queue");
						panelConfiguration.add(label7, new TableLayoutConstraints(1, 9, 1, 9, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- comboBoxQueue ----
						comboBoxQueue.setModel(new DefaultComboBoxModel<>(new String[] {
							"FIFO",
							"Highest Priority First",
							"Shortest Job First",
							"Round Robin",
							"Random Queue"
						}));
						panelConfiguration.add(comboBoxQueue, new TableLayoutConstraints(2, 9, 5, 9, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label12 ----
						label12.setText("RT Job Prob");
						panelConfiguration.add(label12, new TableLayoutConstraints(1, 11, 1, 11, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderRTJob ----
						sliderRTJob.setValue(0);
						sliderRTJob.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderRTJobStateChanged(e);
							}
						});
						panelConfiguration.add(sliderRTJob, new TableLayoutConstraints(2, 11, 2, 11, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelRT ----
						labelRT.setText("0%");
						panelConfiguration.add(labelRT, new TableLayoutConstraints(3, 11, 3, 11, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label14 ----
						label14.setText("OpenCL Job ");
						panelConfiguration.add(label14, new TableLayoutConstraints(5, 11, 5, 11, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderOpenCl ----
						sliderOpenCl.setValue(0);
						sliderOpenCl.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderOpenClStateChanged(e);
							}
						});
						panelConfiguration.add(sliderOpenCl, new TableLayoutConstraints(6, 11, 6, 11, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelOPENCL ----
						labelOPENCL.setText("0%");
						panelConfiguration.add(labelOPENCL, new TableLayoutConstraints(7, 11, 7, 11, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label21 ----
						label21.setText("Class Rate");
						panelConfiguration.add(label21, new TableLayoutConstraints(1, 12, 1, 12, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderclassRate ----
						sliderclassRate.setValue(90);
						sliderclassRate.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderclassRateStateChanged(e);
							}
						});
						panelConfiguration.add(sliderclassRate, new TableLayoutConstraints(2, 12, 4, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- labelclassRate ----
						labelclassRate.setText("90%");
						panelConfiguration.add(labelclassRate, new TableLayoutConstraints(5, 12, 5, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						panelConfiguration.add(vSpacer2, new TableLayoutConstraints(1, 13, 7, 13, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- resetButton ----
						resetButton.setText("Reset");
						resetButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								resetButtonActionPerformed(e);
								resetButtonActionPerformed(e);
								resetButtonActionPerformed(e);
							}
						});
						panelConfiguration.add(resetButton, new TableLayoutConstraints(7, 14, 7, 14, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					}
					hpssimTab.addTab("Configuration", panelConfiguration);


					//======== panelPerformance ========
					{

						//======== panelCPU ========
						{
							panelCPU.setLayout(new BorderLayout());
						}

						//======== panelGPU ========
						{
							panelGPU.setLayout(new BorderLayout());
						}

						//======== panel1 ========
						{
							panel1.setLayout(new TableLayout(new double[][] {
								{TableLayout.PREFERRED, 114, 72, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, 114, 72},
								{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
							((TableLayout)panel1.getLayout()).setHGap(5);
							((TableLayout)panel1.getLayout()).setVGap(5);

							//---- label18 ----
							label18.setText("Virtual Time");
							panel1.add(label18, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
							panel1.add(virtualTime, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label5 ----
							label5.setText("Processi nel sistema");
							panel1.add(label5, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- processiNelSistema ----
							processiNelSistema.setText("0");
							panel1.add(processiNelSistema, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label19 ----
							label19.setText("Media Utilizzo CPU");
							panel1.add(label19, new TableLayoutConstraints(7, 2, 7, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- mediaUsoCPU ----
							mediaUsoCPU.setText("0");
							panel1.add(mediaUsoCPU, new TableLayoutConstraints(8, 2, 8, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label17 ----
							label17.setText("Processi in elaborazione");
							panel1.add(label17, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- processiElaborazione ----
							processiElaborazione.setText("0");
							panel1.add(processiElaborazione, new TableLayoutConstraints(2, 3, 2, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label20 ----
							label20.setText("Media Utilizzo GPU");
							panel1.add(label20, new TableLayoutConstraints(7, 3, 7, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- mediaUsoGPU ----
							mediaUsoGPU.setText("0");
							panel1.add(mediaUsoGPU, new TableLayoutConstraints(8, 3, 8, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- label16 ----
							label16.setText("Processi in coda");
							panel1.add(label16, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

							//---- processiInCoda ----
							processiInCoda.setText("0");
							panel1.add(processiInCoda, new TableLayoutConstraints(2, 4, 2, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						}

						GroupLayout panelPerformanceLayout = new GroupLayout(panelPerformance);
						panelPerformance.setLayout(panelPerformanceLayout);
						panelPerformanceLayout.setHorizontalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGroup(panelPerformanceLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(panelPerformanceLayout.createParallelGroup()
										.addGroup(panelPerformanceLayout.createSequentialGroup()
											.addComponent(panelCPU, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(panelGPU, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE))
										.addComponent(panel1, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
										.addComponent(separator1, GroupLayout.Alignment.TRAILING))
									.addContainerGap())
						);
						panelPerformanceLayout.setVerticalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGroup(panelPerformanceLayout.createSequentialGroup()
									.addGap(38, 38, 38)
									.addGroup(panelPerformanceLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(panelCPU, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE)
										.addComponent(panelGPU, GroupLayout.PREFERRED_SIZE, 205, GroupLayout.PREFERRED_SIZE))
									.addGap(18, 18, 18)
									.addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(panel1, GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
									.addContainerGap())
						);
					}
					hpssimTab.addTab("Performance", panelPerformance);


					//======== panelGraph ========
					{

						//======== graphPanel ========
						{
							graphPanel.setLayout(new BorderLayout());
						}

						GroupLayout panelGraphLayout = new GroupLayout(panelGraph);
						panelGraph.setLayout(panelGraphLayout);
						panelGraphLayout.setHorizontalGroup(
							panelGraphLayout.createParallelGroup()
								.addGroup(panelGraphLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
									.addContainerGap())
						);
						panelGraphLayout.setVerticalGroup(
							panelGraphLayout.createParallelGroup()
								.addGroup(panelGraphLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
									.addContainerGap())
						);
					}
					hpssimTab.addTab("Graph", panelGraph);

				}
				hpssimWindow.add(hpssimTab);
				hpssimTab.setBounds(10, 40, 545, 450);

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
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
							.addComponent(label13, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
							.addGroup(GroupLayout.Alignment.TRAILING, dialog1ContentPaneLayout.createSequentialGroup()
								.addGap(0, 206, Short.MAX_VALUE)
								.addComponent(button2))
							.addComponent(erroreLabel, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))
						.addContainerGap())
			);
			dialog1ContentPaneLayout.setVerticalGroup(
				dialog1ContentPaneLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, dialog1ContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(label13, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(erroreLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button2)
						.addContainerGap())
			);
			dialog1.pack();
			dialog1.setLocationRelativeTo(dialog1.getOwner());
		}
		// //GEN-END:initComponents
	}

	public DefaultValueDataset datasetCPU;
	public DefaultValueDataset datasetGPU;

	private void setMeter() {
		datasetCPU = new DefaultValueDataset(0D);
		JFreeChart jfreechartCPU = createChart(datasetCPU, "CPU Usage");

		datasetGPU = new DefaultValueDataset(0D);
		JFreeChart jfreechartGPU = createChart(datasetGPU, "GPU Usage");

		panelCPU.add("Center", new ChartPanel(jfreechartCPU));
		panelCPU.add("South", new Label(""));

		panelGPU.add("Center", new ChartPanel(jfreechartGPU));
		panelGPU.add("South", new Label(""));
		// panelCPU.add("South", jslider);

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
	// Generated using JFormDesigner Evaluation license - 
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
	private JLabel label6;
	private JComboBox<String> comboBoxScheduler;
	private JLabel label8;
	private JTextField textFieldTimeSlice;
	private JLabel label7;
	private JComboBox<String> comboBoxQueue;
	private JLabel label12;
	private JSlider sliderRTJob;
	private JLabel labelRT;
	private JLabel label14;
	private JSlider sliderOpenCl;
	private JLabel labelOPENCL;
	private JLabel label21;
	private JSlider sliderclassRate;
	private JLabel labelclassRate;
	private JPanel vSpacer2;
	private JButton resetButton;
	private JPanel panelPerformance;
	private JPanel panelCPU;
	private JPanel panelGPU;
	private JSeparator separator1;
	private JPanel panel1;
	private JLabel label18;
	public JTextField virtualTime;
	private JLabel label5;
	public JTextField processiNelSistema;
	private JLabel label19;
	public JTextField mediaUsoCPU;
	private JLabel label17;
	public JTextField processiElaborazione;
	private JLabel label20;
	public JTextField mediaUsoGPU;
	private JLabel label16;
	public JTextField processiInCoda;
	private JPanel panelGraph;
	private JPanel graphPanel;
	private JLabel title1;
	private JButton button1;
	private JButton okButton;
	private JSpinner spinner1;
	private JDialog dialog1;
	private JButton button2;
	private JLabel label13;
	private JLabel erroreLabel;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
