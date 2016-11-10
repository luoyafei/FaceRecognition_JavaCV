package com.facerec.main;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;

import com.facerec.util.ImageFile.Image;
import com.facerec.util.ImageUtil;
import com.facerec.util.TemplateMatch;

public class FaceRecognition {
	
	private static CvMemStorage storage;
	private static CvHaarClassifierCascade cascade;
	private static CvSeq sign;
	private static final String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
	private static FrameGrabber grabber = null; //摄像头人脸检测
	private volatile static boolean openCam = false;
	private static TemplateMatch tmatch = new TemplateMatch();
	private static final String faceResult = "检测結果 : ";
	private volatile JTextField faceText = new JTextField(faceResult);
	private static volatile Vector<Image> images = ImageUtil.getImageFile().getImageFiles();
	
	public FaceRecognition() {
		
		ExecutorService es = Executors.newFixedThreadPool(2);
		es.execute(new Runnable() {
			JFrame jf = new JFrame("人脸识别");
			JPanel jp = new JPanel(new GridLayout(2, 1));
			JButton button = new JButton();
			
			@Override
			public void run() {
				addSomeEvent();
				
				button.setText("开启/关闭	摄像头");
				faceText.setEditable(false);
				
				jp.add(button);
				jp.add(faceText);
				jf.add(jp);
				
//				jf.setSize(400, 200);
				jf.pack();
				jf.setLocation(500, 200);
				jf.setVisible(true);
			}
			
			private void addSomeEvent() {
				
				jf.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.out.println("jf close");
						jf.dispose();
						jf.setVisible(false);
						try {
							grabber.trigger();
//							grabber.stop();
						} catch (Exception e1) {e1.printStackTrace();}
					}
				});
				
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						openCam = !openCam;
						/**
						 * 关闭摄像头
						 */
						try {
							grabber.trigger();
//							grabber.stop();
						} catch (Exception e1) {e1.printStackTrace();}
//						System.out.println(openCam);
					}
				});
			}
			
		});
		es.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					grabber = FrameGrabber.createDefault(0);
					grabber.start();
				} catch (Exception e) {e.printStackTrace();}
				
				OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
//				IplImage grabbedImage = converter.convert(grabber.grab());
				IplImage grabbedImage = null;

		        while(true) {
		        	/*try {
		        		Thread.sleep(3000);
		        	} catch(InterruptedException e) {}*/
		        	if(openCam) {
		            	try {
							grabbedImage = converter.convert(grabber.grab());//获取一帧图片
							detectFace(grabbedImage);//将人脸画出
			    			String matchName = tmatch.matchTemplate(grabbedImage.clone(), images);//匹配,返回匹配的名称
System.out.println("匹配的名称:" + matchName);
			    			faceText.setText(faceResult + matchName);
			    			
			    			//grabbedImage = converter.convert(grabber.grab());
						} catch (Exception e) {e.printStackTrace();}
		    	    }
		        }
			}
		});
	}
	
	 private static void detectFace(IplImage src){

			cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
			storage = CvMemStorage.create();
			
			sign = cvHaarDetectObjects(
					src,
					cascade,
					storage,
					1.2,
					2,
					CV_HAAR_DO_CANNY_PRUNING);

			cvClearMemStorage(storage);

			int total_Faces = sign.total();		
	        if(total_Faces==0)
	        	return;
	        else {
	        	CvRect r = new CvRect(cvGetSeqElem(sign, 0));
				cvRectangle (
						src, 
						cvPoint(r.x(), r.y()), 
						cvPoint(r.width() + r.x(), r.height() + r.y()),
						CvScalar.RED,
						2,
						CV_AA,
						0);
	        }
			/*for(int i = 0; i < total_Faces; i++){
				CvRect r = new CvRect(cvGetSeqElem(sign, i));
				
				cvRectangle (
						src, 
						cvPoint(r.x(), r.y()), 
						cvPoint(r.width() + r.x(), r.height() + r.y()),
						CvScalar.RED,
						2,
						CV_AA,
						0);
				//cvSetImageROI(src, r);
				break;
			}*/
	        
			cvShowImage("Result", src);
			cvWaitKey(30);
		}
}
