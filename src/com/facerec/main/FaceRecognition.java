package com.facerec.main;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.HashMap;
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
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import com.facerec.util.ImageUtil;
import com.facerec.util.TemplateMatch;

public class FaceRecognition {

	final private static String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
	private volatile static boolean openCam = true;
	final private static String trainingDir = "F:/facereg";
	private static HashMap<Integer, String> faceMap = new HashMap<>();

	private static void initMap() {
		File root = new File(trainingDir);
		FilenameFilter imgFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
			}
		};

		File[] imageFiles = root.listFiles(imgFilter);

		for (File image : imageFiles) {
			String num = image.getName().replaceAll("\\D", "");
			String name = image.getName().split("\\d")[0];
			int label = Integer.parseInt(num);
			faceMap.put(label, name);
		}
	}
	
	
	 private static String compareFace(Mat testImage) {

	        File root = new File(trainingDir);

	        FilenameFilter imgFilter = new FilenameFilter() {
	            public boolean accept(File dir, String name) {
	                name = name.toLowerCase();
	                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
	            }
	        };

	        File[] imageFiles = root.listFiles(imgFilter);

	        MatVector images = new MatVector(imageFiles.length);
	        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
	        
	        
	        IntBuffer labelsBuf = labels.getIntBuffer();

	        int counter = 0;

	        for (File image : imageFiles) {
	            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
	            String num = image.getName().replaceAll("\\D", "");
	            int label = Integer.parseInt(num);
//	            int label = 1;

	            images.put(counter, img);

	            labelsBuf.put(counter, label);

	            counter++;
	        }

//	        FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
//	        FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
	        FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();

	        faceRecognizer.train(images, labels);

	        int predictedLabel = faceRecognizer.predict(testImage);
	        System.out.println("Predicted label: " + faceMap.get(predictedLabel));
	        return  faceMap.get(predictedLabel);
	    }
	

	public FaceRecognition() {
		initMap();

		ExecutorService es = Executors.newFixedThreadPool(2);
		es.execute(new Runnable() {

			@Override
			public void run() {
				OpenCVFrameGrabber grabber = null;
				OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		        CascadeClassifier face_cascade = new CascadeClassifier(XML_FILE);
		        FaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer();
//		        lbphFaceRecognizer.load(trainedResult);
		        try {
		            grabber = OpenCVFrameGrabber.createDefault(0);
		            grabber.start();
		        } catch (Exception e) {
		            System.err.println("Failed start the grabber.");
		        }

		        Frame videoFrame = null;
		        Mat videoMat = new Mat();
		        while (openCam) {
		        	/*try {
		        		Thread.sleep(300);
		        	} catch(InterruptedException e) {}*/
		            try {
						videoFrame = grabber.grab();
					} catch (Exception e) {
						e.printStackTrace();
					}
		            videoMat = converterToMat.convert(videoFrame);
		            Mat videoMatGray = new Mat();
		            // Convert the current frame to grayscale:
		            cvtColor(videoMat, videoMatGray, COLOR_BGRA2GRAY);
		            equalizeHist(videoMatGray, videoMatGray);

		            Point p = new Point();
		            RectVector faces = new RectVector();
		            // Find the faces in the frame:
		            face_cascade.detectMultiScale(videoMatGray, faces);

		            // At this point you have the position of the faces in
		            // faces. Now we'll get the faces, make a prediction and
		            // annotate it in the video. Cool or what?
		            for (int i = 0; i < faces.size(); i++) {
		                Rect face_i = faces.get(i);

		                Mat face = new Mat(videoMatGray, face_i);
		                
		                resize(face, face, new Size(200, 200));
		                // If fisher face recognizer is used, the face need to be
		                // resized.
		                // resize(face, face_resized, new Size(im_width, im_height),
		                // 1.0, 1.0, INTER_CUBIC);

		                // Now perform the prediction, see how easy that is:
//		                int prediction = lbphFaceRecognizer.predict(face);
		                String faceName = compareFace(face);
		                // And finally write all we've found out to the original image!
		                // First of all draw a green rectangle around the detected face:
		                rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

		                // Create the text we will annotate the box with:
		                String box_text = "MingZi:" + faceName;
		                // Calculate the position for annotated text (make sure we don't
		                // put illegal values in there):
		                int pos_x = Math.max(face_i.tl().x() - 10, 0);
		                int pos_y = Math.max(face_i.tl().y() - 10, 0);
		                // And now put it into the image:
		                putText(videoMat, box_text, new Point(pos_x, pos_y), FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
		            }
		            // Show the result:
		            imshow("face_recognizer", videoMat);

		            char key = (char) waitKey(20);
		            // Exit this loop on escape:
		            if (key == 27) {
		                destroyAllWindows();
		                break;
		            }
		        }
			}
		});
		es.execute(new Runnable() {
			@Override
			public void run() {
			}
		});
	}
}
