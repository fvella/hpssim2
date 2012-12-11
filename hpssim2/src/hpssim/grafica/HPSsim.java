/*
 * Created by JFormDesigner on Tue Dec 11 16:38:43 CET 2012
 */

package hpssim.grafica;

import javax.swing.*;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
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
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.DefaultComponentFactory;

/**
 * @author Marco delle lande
 */
public class HPSsim {
	
	public HPSsim() {
		initComponents();
		hpssimWindow.setVisible(true);
	}

	private void resetButtonActionPerformed(ActionEvent e) {
		resetPanelConfig();
	}

	private void okButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void sliderJobStateChanged(ChangeEvent e) {
		textFieldNjob.setText(String.valueOf(sliderJob.getValue()));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Marco delle lande
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
		label1 = new JLabel();
		sliderJob = new JSlider();
		textFieldNjob = new JTextField();
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
		label13 = new JLabel();
		label14 = new JLabel();
		sliderOpenCl = new JSlider();
		label15 = new JLabel();
		resetButton = new JButton();
		okButton = new JButton();
		panelProcesses = new JPanel();
		panelPerformance = new JPanel();
		panelGraph = new JPanel();
		title1 = compFactory.createTitle("HPSsim 2.0 ");
		progressBar1 = new JProgressBar();
		spinner1 = new JSpinner();

		//======== HPSsimWindow ========
		{
			HPSsimWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
							{0.01, 27, 21, 26, 27, 25, 25, 25, TableLayout.PREFERRED, 25, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
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
						panelConfiguration.add(ncpu, new TableLayoutConstraints(2, 2, 3, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label4 ----
						label4.setText("GPU");
						label4.setFont(new Font("Segoe UI", Font.PLAIN, 12));
						label4.setLabelFor(ngpu);
						panelConfiguration.add(label4, new TableLayoutConstraints(4, 2, 4, 2, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
						panelConfiguration.add(ngpu, new TableLayoutConstraints(5, 2, 6, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
						panelConfiguration.add(vSpacer1, new TableLayoutConstraints(1, 3, 7, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label11 ----
						label11.setText("Simulation");
						label11.setFont(new Font("Segoe UI", Font.ITALIC, 16));
						panelConfiguration.add(label11, new TableLayoutConstraints(1, 4, 7, 4, TableLayoutConstraints.CENTER, TableLayoutConstraints.FULL));

						//---- label1 ----
						label1.setText("Numero di job");
						panelConfiguration.add(label1, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.RIGHT, TableLayoutConstraints.CENTER));

						//---- sliderJob ----
						sliderJob.setValue(0);
						sliderJob.addChangeListener(new ChangeListener() {
							@Override
							public void stateChanged(ChangeEvent e) {
								sliderJobStateChanged(e);
							}
						});
						panelConfiguration.add(sliderJob, new TableLayoutConstraints(2, 5, 4, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- textFieldNjob ----
						textFieldNjob.setEnabled(false);
						textFieldNjob.setEditable(false);
						textFieldNjob.setText("0");
						panelConfiguration.add(textFieldNjob, new TableLayoutConstraints(5, 5, 5, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label9 ----
						label9.setText("Media arrivo");
						panelConfiguration.add(label9, new TableLayoutConstraints(6, 5, 6, 5, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
						panelConfiguration.add(textFieldQVGA, new TableLayoutConstraints(7, 5, 7, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label6 ----
						label6.setText("Scheduler");
						panelConfiguration.add(label6, new TableLayoutConstraints(1, 7, 1, 7, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- comboBoxScheduler ----
						comboBoxScheduler.setModel(new DefaultComboBoxModel<>(new String[] {
							"Priority Round Robin",
							"Completely Fair Scheduler"
						}));
						panelConfiguration.add(comboBoxScheduler, new TableLayoutConstraints(2, 7, 5, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label8 ----
						label8.setText("Time Slice");
						panelConfiguration.add(label8, new TableLayoutConstraints(6, 7, 6, 7, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));
						panelConfiguration.add(textFieldTimeSlice, new TableLayoutConstraints(7, 7, 7, 7, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label7 ----
						label7.setText("Queue");
						panelConfiguration.add(label7, new TableLayoutConstraints(1, 8, 1, 8, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- comboBoxQueue ----
						comboBoxQueue.setModel(new DefaultComboBoxModel<>(new String[] {
							"FIFO",
							"Highest Priority First",
							"Shortest Job First",
							"Round Robin",
							"Random Queue"
						}));
						panelConfiguration.add(comboBoxQueue, new TableLayoutConstraints(2, 8, 5, 8, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label12 ----
						label12.setText("RT Job Prob");
						panelConfiguration.add(label12, new TableLayoutConstraints(1, 10, 1, 10, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderRTJob ----
						sliderRTJob.setValue(0);
						panelConfiguration.add(sliderRTJob, new TableLayoutConstraints(2, 10, 2, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label13 ----
						label13.setText("0%");
						panelConfiguration.add(label13, new TableLayoutConstraints(3, 10, 3, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label14 ----
						label14.setText("OpenCL Job ");
						panelConfiguration.add(label14, new TableLayoutConstraints(5, 10, 5, 10, TableLayoutConstraints.RIGHT, TableLayoutConstraints.FULL));

						//---- sliderOpenCl ----
						sliderOpenCl.setValue(0);
						panelConfiguration.add(sliderOpenCl, new TableLayoutConstraints(6, 10, 6, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- label15 ----
						label15.setText("0%");
						panelConfiguration.add(label15, new TableLayoutConstraints(7, 10, 7, 10, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- resetButton ----
						resetButton.setText("Reset");
						resetButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								resetButtonActionPerformed(e);
								resetButtonActionPerformed(e);
							}
						});
						panelConfiguration.add(resetButton, new TableLayoutConstraints(6, 12, 6, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- okButton ----
						okButton.setText("OK");
						okButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								okButtonActionPerformed(e);
							}
						});
						panelConfiguration.add(okButton, new TableLayoutConstraints(7, 12, 7, 12, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					}
					hpssimTab.addTab("Configuration", panelConfiguration);


					//======== panelProcesses ========
					{
						panelProcesses.setLayout(new GridLayout());
					}
					hpssimTab.addTab("Processes", panelProcesses);


					//======== panelPerformance ========
					{

						GroupLayout panelPerformanceLayout = new GroupLayout(panelPerformance);
						panelPerformance.setLayout(panelPerformanceLayout);
						panelPerformanceLayout.setHorizontalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGap(0, 540, Short.MAX_VALUE)
						);
						panelPerformanceLayout.setVerticalGroup(
							panelPerformanceLayout.createParallelGroup()
								.addGap(0, 366, Short.MAX_VALUE)
						);
					}
					hpssimTab.addTab("Performance", panelPerformance);


					//======== panelGraph ========
					{

						GroupLayout panelGraphLayout = new GroupLayout(panelGraph);
						panelGraph.setLayout(panelGraphLayout);
						panelGraphLayout.setHorizontalGroup(
							panelGraphLayout.createParallelGroup()
								.addGap(0, 540, Short.MAX_VALUE)
						);
						panelGraphLayout.setVerticalGroup(
							panelGraphLayout.createParallelGroup()
								.addGap(0, 366, Short.MAX_VALUE)
						);
					}
					hpssimTab.addTab("Graph", panelGraph);

				}
				hpssimWindow.add(hpssimTab);
				hpssimTab.setBounds(new Rectangle(new Point(10, 40), hpssimTab.getPreferredSize()));

				//---- title1 ----
				title1.setFont(title1.getFont().deriveFont(title1.getFont().getSize() + 8f));
				hpssimWindow.add(title1);
				title1.setBounds(10, 11, 132, title1.getPreferredSize().height);
				hpssimWindow.add(progressBar1);
				progressBar1.setBounds(10, 445, 545, 29);

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
					.addGroup(GroupLayout.Alignment.TRAILING, HPSsimWindowContentPaneLayout.createSequentialGroup()
						.addGap(0, 0, Short.MAX_VALUE)
						.addComponent(hpssimWindow, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE))
			);
			HPSsimWindowContentPaneLayout.setVerticalGroup(
				HPSsimWindowContentPaneLayout.createParallelGroup()
					.addGroup(HPSsimWindowContentPaneLayout.createSequentialGroup()
						.addComponent(hpssimWindow, GroupLayout.PREFERRED_SIZE, 485, GroupLayout.PREFERRED_SIZE)
						.addGap(0, 4, Short.MAX_VALUE))
			);
			HPSsimWindow.pack();
			HPSsimWindow.setLocationRelativeTo(HPSsimWindow.getOwner());
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void resetPanelConfig() {
		panelConfiguration.removeAll();
		
		
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Marco delle lande
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
	private JLabel label1;
	private JSlider sliderJob;
	private JTextField textFieldNjob;
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
	private JLabel label13;
	private JLabel label14;
	private JSlider sliderOpenCl;
	private JLabel label15;
	private JButton resetButton;
	private JButton okButton;
	private JPanel panelProcesses;
	private JPanel panelPerformance;
	private JPanel panelGraph;
	private JLabel title1;
	private JProgressBar progressBar1;
	private JSpinner spinner1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
