package com.facerec.main;


import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.javacpp.opencv_core.IplImage;

import com.facerec.util.ImageFile;
import com.facerec.util.ImageFile.Image;
import com.facerec.util.ImageUtil;

public class SampleTraining {
	
	private static JFrame jf = new JFrame("样本训练");
	private static JPanel jp = new JPanel(new GridLayout(1, 2));
	private static final String parentDir = "F:/javacv_picture/";
	private static volatile SampleTraining st = new SampleTraining();
	
	private static volatile ImageFile imageFile = ImageUtil.getImageFile();
	
	JTextField nameField = new JTextField("输入样本姓名");
	JButton entryButton = new JButton("开始录入样本");
	
	public static SampleTraining newInstance() {
		if(st == null) {
			synchronized (st) {
				if(st == null) {
					st = new SampleTraining();
				}
			}
		}
		jf.setVisible(true);
		return st;
	}
	
	private SampleTraining() {
		jf.setLocation(500, 200);
		jf.setSize(400, 100);
		
		jp.setLayout(new GridLayout(1, 2));
		
		jp.add(nameField);
		jp.add(entryButton);
		
		jf.add(jp);
		
		jf.setVisible(true);
		
		bindEvent4Buttons();
	}
	
	private void bindEvent4Buttons() {
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				jf.dispose();
//				jf.setVisible(false);
			}
		});
		entryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				if(name != null && name.trim().hashCode() != 0 && !name.trim().equals("输入样本姓名")) {
					if(st.showFileChooser(name) > 0) {
						JOptionPane.showMessageDialog(jp, "录入样本结束", "提示", JOptionPane.OK_OPTION);
						jf.dispose();
					}
//					jf.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(jp, "请输入一个正确的样本姓名", "样本名错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private int showFileChooser(String name) {
		return st.dealFile(initFileChooser(), name);
	}
	private JFileChooser initFileChooser() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		return chooser;
	}
	private int dealFile(JFileChooser chooser, String name) {
		
		if(chooser.showOpenDialog(jp) == JFileChooser.APPROVE_OPTION) {
			
			Vector<Image> face_files = null;
			if(imageFile == null)
				imageFile = new ImageFile();
			
			if(imageFile.getImageFiles() == null)
				face_files = new Vector<>();
			
			else
				face_files = imageFile.getImageFiles();
			
			Image face = new Image();
			face.setPeopleName(name);
			Vector<String> faceUrls = new Vector<>();
			
			
			File[] files = chooser.getSelectedFiles();
			String parentDir_names = parentDir + name;
			new File(parentDir_names).mkdirs();
			
			for(int i = faceUrls.size(), j = 0; i < files.length + faceUrls.size() && j < files.length; i++,j++) {
				String fileUrl = parentDir_names + "/" + name + "_" + i + ".jpg";
//				System.out.println(fileUrl);
//				System.out.println(files[i].getAbsolutePath().replaceAll("[\\\\]", "/"));
				String srcUrl = files[j].getAbsolutePath().replaceAll("[\\\\]", "/").trim();
				IplImage src = cvLoadImage(srcUrl);
				if(ImageUtil.dealSampleFaceImage(src, fileUrl)) {
					faceUrls.add(fileUrl);
				}
			}
System.out.println("录入样本数：" + files.length + ",成功个数：" + faceUrls.size());

			face.setPeopleNameItemNames(faceUrls);
			
			face_files.add(face);
			
			imageFile.setImageFiles(face_files);
			
			ImageUtil.saveFileJson(imageFile);
			
			/**
			 * 将成功录入的个数返回
			 */
			return faceUrls.size();
		} else
			return -1;
	} 
	
}
