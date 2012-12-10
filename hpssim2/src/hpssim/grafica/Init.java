package hpssim.grafica;

import hpssim.hardware.Hardware;
import hpssim.scheduler.EventWorkFlow;
import hpssim.scheduler.policy.scheduling.IScheduler;
import hpssim.simulator.Simulator;

import java.awt.Choice;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * 
 * @author Luigi Giorgio Claudio Mancini
 */
public class Init {

	private JFrame frame;
	private JTextField text_QT;
	private JTextField text_ClassRate;
	private JTextField text_RealTimeJPro;
	private JTextField text_AVGTA;
	private JTextField text_PercOpenCLJ;
	private JTextField text_nCPU;
	private JTextField text_nGPU;
	private JTextField text_njobs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Init window = new Init();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Init() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(SystemColor.menu);
		frame.setBounds(100, 100, 450, 395);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{46, 106, 38, 86, 86, 0};
		gridBagLayout.rowHeights = new int[]{32, 0, 20, 20, 20, 14, 20, 12, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblHpsSim = new JLabel("HPS sim");
		GridBagConstraints gbc_lblHpsSim = new GridBagConstraints();
		gbc_lblHpsSim.gridwidth = 5;
		gbc_lblHpsSim.insets = new Insets(0, 0, 5, 0);
		gbc_lblHpsSim.gridx = 0;
		gbc_lblHpsSim.gridy = 0;
		frame.getContentPane().add(lblHpsSim, gbc_lblHpsSim);
		
		JLabel lblNjobs = new JLabel("njobs");
		GridBagConstraints gbc_lblNjobs = new GridBagConstraints();
		gbc_lblNjobs.anchor = GridBagConstraints.WEST;
		gbc_lblNjobs.insets = new Insets(0, 0, 5, 5);
		gbc_lblNjobs.gridx = 1;
		gbc_lblNjobs.gridy = 1;
		frame.getContentPane().add(lblNjobs, gbc_lblNjobs);
		
		text_njobs = new JTextField();
		text_njobs.setText("10");
		GridBagConstraints gbc_text_njobs = new GridBagConstraints();
		gbc_text_njobs.insets = new Insets(0, 0, 5, 5);
		gbc_text_njobs.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_njobs.gridx = 3;
		gbc_text_njobs.gridy = 1;
		frame.getContentPane().add(text_njobs, gbc_text_njobs);
		text_njobs.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("qt");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 2;
		frame.getContentPane().add(lblNewLabel, gbc_lblNewLabel);
		
		text_QT = new JTextField();
		text_QT.setText("210");
		GridBagConstraints gbc_text_QT = new GridBagConstraints();
		gbc_text_QT.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_QT.anchor = GridBagConstraints.NORTH;
		gbc_text_QT.insets = new Insets(0, 0, 5, 5);
		gbc_text_QT.gridx = 3;
		gbc_text_QT.gridy = 2;
		frame.getContentPane().add(text_QT, gbc_text_QT);
		text_QT.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("classificationRate");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 3;
		frame.getContentPane().add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		text_ClassRate = new JTextField();
		text_ClassRate.setText("0.9");
		GridBagConstraints gbc_text_ClassRate = new GridBagConstraints();
		gbc_text_ClassRate.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_ClassRate.anchor = GridBagConstraints.NORTH;
		gbc_text_ClassRate.insets = new Insets(0, 0, 5, 5);
		gbc_text_ClassRate.gridx = 3;
		gbc_text_ClassRate.gridy = 3;
		frame.getContentPane().add(text_ClassRate, gbc_text_ClassRate);
		text_ClassRate.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("realTimeJobsProb");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 4;
		frame.getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		text_RealTimeJPro = new JTextField();
		text_RealTimeJPro.setText("0.4");
		GridBagConstraints gbc_text_RealTimeJPro = new GridBagConstraints();
		gbc_text_RealTimeJPro.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_RealTimeJPro.anchor = GridBagConstraints.NORTH;
		gbc_text_RealTimeJPro.insets = new Insets(0, 0, 5, 5);
		gbc_text_RealTimeJPro.gridx = 3;
		gbc_text_RealTimeJPro.gridy = 4;
		frame.getContentPane().add(text_RealTimeJPro, gbc_text_RealTimeJPro);
		text_RealTimeJPro.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("percentOpenCLjob");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 1;
		gbc_lblNewLabel_3.gridy = 5;
		frame.getContentPane().add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		text_PercOpenCLJ = new JTextField();
		text_PercOpenCLJ.setText("0.25");
		GridBagConstraints gbc_text_PercOpenCLJ = new GridBagConstraints();
		gbc_text_PercOpenCLJ.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_PercOpenCLJ.insets = new Insets(0, 0, 5, 5);
		gbc_text_PercOpenCLJ.gridx = 3;
		gbc_text_PercOpenCLJ.gridy = 5;
		frame.getContentPane().add(text_PercOpenCLJ, gbc_text_PercOpenCLJ);
		text_PercOpenCLJ.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("avgta");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 6;
		frame.getContentPane().add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		text_AVGTA = new JTextField();
		text_AVGTA.setText("230");
		GridBagConstraints gbc_text_AVGTA = new GridBagConstraints();
		gbc_text_AVGTA.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_AVGTA.insets = new Insets(0, 0, 5, 5);
		gbc_text_AVGTA.anchor = GridBagConstraints.NORTH;
		gbc_text_AVGTA.gridx = 3;
		gbc_text_AVGTA.gridy = 6;
		frame.getContentPane().add(text_AVGTA, gbc_text_AVGTA);
		text_AVGTA.setColumns(10);
		
		JLabel lblNcpu = new JLabel("nCpu");
		GridBagConstraints gbc_lblNcpu = new GridBagConstraints();
		gbc_lblNcpu.anchor = GridBagConstraints.WEST;
		gbc_lblNcpu.insets = new Insets(0, 0, 5, 5);
		gbc_lblNcpu.gridx = 1;
		gbc_lblNcpu.gridy = 7;
		frame.getContentPane().add(lblNcpu, gbc_lblNcpu);
		
		text_nCPU = new JTextField();
		text_nCPU.setText("4");
		GridBagConstraints gbc_text_nCPU = new GridBagConstraints();
		gbc_text_nCPU.insets = new Insets(0, 0, 5, 5);
		gbc_text_nCPU.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_nCPU.gridx = 3;
		gbc_text_nCPU.gridy = 7;
		frame.getContentPane().add(text_nCPU, gbc_text_nCPU);
		text_nCPU.setColumns(10);
		
		JLabel lblNgpu = new JLabel("ngpu");
		GridBagConstraints gbc_lblNgpu = new GridBagConstraints();
		gbc_lblNgpu.anchor = GridBagConstraints.WEST;
		gbc_lblNgpu.insets = new Insets(0, 0, 5, 5);
		gbc_lblNgpu.gridx = 1;
		gbc_lblNgpu.gridy = 8;
		frame.getContentPane().add(lblNgpu, gbc_lblNgpu);
		
		text_nGPU = new JTextField();
		text_nGPU.setText("1");
		GridBagConstraints gbc_text_nGPU = new GridBagConstraints();
		gbc_text_nGPU.insets = new Insets(0, 0, 5, 5);
		gbc_text_nGPU.fill = GridBagConstraints.HORIZONTAL;
		gbc_text_nGPU.gridx = 3;
		gbc_text_nGPU.gridy = 8;
		frame.getContentPane().add(text_nGPU, gbc_text_nGPU);
		text_nGPU.setColumns(10);
		
		JLabel lblScheduler = new JLabel("scheduler");
		GridBagConstraints gbc_lblScheduler = new GridBagConstraints();
		gbc_lblScheduler.anchor = GridBagConstraints.WEST;
		gbc_lblScheduler.insets = new Insets(0, 0, 5, 5);
		gbc_lblScheduler.gridx = 1;
		gbc_lblScheduler.gridy = 9;
		frame.getContentPane().add(lblScheduler, gbc_lblScheduler);
		
		final Choice choice_scheduler = new Choice();
		choice_scheduler.add("FIFO");
		choice_scheduler.add("RoundRobin");
		choice_scheduler.add("PR");
		
		GridBagConstraints gbc_choice_scheduler = new GridBagConstraints();
		gbc_choice_scheduler.insets = new Insets(0, 0, 5, 5);
		gbc_choice_scheduler.gridx = 3;
		gbc_choice_scheduler.gridy = 9;
		frame.getContentPane().add(choice_scheduler, gbc_choice_scheduler);
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.gridx = 4;
		gbc_btnOk.gridy = 12;
		
		JButton btnOk = new JButton("ok");
		
		frame.getContentPane().add(btnOk, gbc_btnOk);
		
		
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Hardware hw = new Hardware(Integer.valueOf(text_nCPU.getText()),Integer.valueOf(text_nGPU.getText()));                
                Simulator sim = new Simulator(hw, Integer.valueOf(text_njobs.getText()), Integer.valueOf(text_QT.getText()) , Double.valueOf(text_ClassRate.getText()) , Double.valueOf(text_RealTimeJPro.getText()) , Double.valueOf( text_PercOpenCLJ.getText()), Integer.valueOf(text_AVGTA.getText()));
                
//                IScheduler s = new HPSSimScheduler(IScheduler.FIFO);
                sim.init();
                try {
					sim.simulate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
		
	}
}
