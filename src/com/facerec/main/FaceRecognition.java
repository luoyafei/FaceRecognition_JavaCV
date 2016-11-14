package com.facerec.main;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_highgui.destroyAllWindows;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;


public class FaceRecognition {

	final private static String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
	private volatile static boolean openCam = true;														//死循环标志变量
	final private static String trainingDir = "F:/javacv_picture";										//样本库地址
	private static HashMap<Integer, String> faceMap = new HashMap<>();									//人脸与名字对应hashmap	
	private static FaceRecognizer faceRecognizer = createLBPHFaceRecognizer();
	private static volatile LinkedBlockingQueue<Mat> frameQueue = new LinkedBlockingQueue<>();			//线程通信队列
	private static volatile String faceName = "";														//线程通信名字
	
	/**
	 * 用于初始化hashmap对象和FaceRecognizer对象
	 */
	private static void initHashMapAndFaceRecognizer() {
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
        
		@SuppressWarnings("deprecation")
		IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            String num = image.getName().replaceAll("\\D", "");
            int label = Integer.parseInt(num);
            String name = image.getName().split("_")[0];
			faceMap.put(label, name);
			
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }

        faceRecognizer.train(images, labels);
	}

	public FaceRecognition() {
		initHashMapAndFaceRecognizer();

		ExecutorService es = Executors.newFixedThreadPool(2);
		/**
		 * 不停的抓取视屏中的帧
		 */
		es.execute(new Runnable() {

			@SuppressWarnings("resource")
			@Override
			public void run() {
				OpenCVFrameGrabber grabber = null;
				OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		        CascadeClassifier face_cascade = new CascadeClassifier(XML_FILE);
		        
//		        FaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer();
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

//		            Point p = new Point();
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

//		                int prediction = lbphFaceRecognizer.predict(face);
//		                String faceName = compareFace(face);
		                
		                frameQueue.offer(face);//放入队列
		                
		                // And finally write all we've found out to the original image!
		                // First of all draw a green rectangle around the detected face:
		                rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

		                // Create the text we will annotate the box with:
		                String box_text = "name is : " + faceName;
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
		/**
		 * 不停的识别
		 */
		es.execute(new Runnable() {
			@Override
			public void run() {
				while(true) {
					if(frameQueue.peek() != null) {
						faceName = compareFace(frameQueue.poll());
					}
				}
			}
		});
	}
	/**
	 * 识别的代码
	 * @param testImage
	 * @return
	 */
	private static String compareFace(Mat testImage) {
		if(testImage != null) {
			int predictedLabel = faceRecognizer.predict(testImage);
	        System.out.println("名字: " + faceMap.get(predictedLabel));
	        return  faceMap.get(predictedLabel);	
		} else
			return "null";
    }
}
