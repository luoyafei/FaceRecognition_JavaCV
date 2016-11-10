package com.facerec.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Main {
	
	public static void main(String[] args) {
		new MyFrame();
	}
}

@SuppressWarnings("serial")
class MyFrame extends JFrame {

	private static JFrame jf = new JFrame("人脸识别系统");
	private static JPanel jp = new JPanel(new GridLayout(2, 1));

	private JPanel sampleTraining  = new JPanel();
	private JPanel faceRecognition  = new JPanel();

	private JButton sampleButton = new JButton("样本训练");
	private JButton faceButton = new JButton("人脸识别");

	private void bindEvent4Buttons() {
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		sampleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SampleTraining.newInstance();
			}
		});
		faceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println("faceButton is clicked!");
				new FaceRecognition();
			}
		});
	}
	
	public MyFrame() {
		jf.setLocation(500, 200);
		jf.setSize(400, 200);
		jf.setLayout(new BorderLayout());

		jp.setBorder(new TitledBorder(new EtchedBorder(), "欢迎来到人脸识别系统", TitledBorder.CENTER, TitledBorder.TOP));
		
		sampleTraining.add(sampleButton);
		faceRecognition.add(faceButton);
		
		jp.add(sampleTraining);
		jp.add(faceRecognition);
		
		jf.add(jp);
		
//		jf.pack();
		jf.setResizable(false);
		jf.setVisible(true);
		
		bindEvent4Buttons();
	}
}