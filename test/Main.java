import java.util.Arrays;

import java.util.Vector;

import com.facerec.util.ImageFile;
import com.google.gson.Gson;
import com.facerec.util.ImageUtil;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

import static org.bytedeco.javacpp.opencv_face.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.ObjectFinder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;
public class Main {

	private static MatVector extractFeatures(Mat img, Vector<Integer> left, Vector<Integer> top) {
		MatVector output = new MatVector();
//		Mat
//		MatVector contours = new MatVector();
		Mat input = img.clone();
		Mat hierarchy = new Mat();
//		findContours(input, contours, hierarchy, RETR_CCOMP, CHAIN_APPROX_SIMPLE);
		
		/*if(contours.size() == 0) {
			return output;
		}
		*/
		return null;
	}

	private static void findContoursBasic(Mat img) {
		MatVector mv = new MatVector();
		findContours(img, mv, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
		Size size = new Size();
//		Mat.zeros(arg0, arg1);
//		MatExpr output = Mat.zeros(cvSize(img.rows(), img.cols()), CV_8UC3);
	}
	
/**
 * #include <QCoreApplication>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/nonfree/nonfree.hpp>

#include <QDebug>

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    // 读入图像
    cv::Mat image= cv::imread("c:/018.jpg",0);
    cv::namedWindow("Original Image");
    cv::imshow("Original Image", image);
    // 特征点的向量
    std::vector<cv::KeyPoint>keypoints;
        // 构造SIFT特征检测器
    cv::SiftFeatureDetector sift(
        0.03,  // 特征的阈值
        10.);  // 用于降低

    // 检测SIFT特征值
    sift.detect(image,keypoints);

    cv::drawKeypoints(image, // 原始图像
                      keypoints, // 特征点的向量
                      featureImage, // 生成图像
                      cv::Scalar(255,255,255), // 特征点的颜色
                      cv::DrawMatchesFlags::DRAW_RICH_KEYPOINTS); // 标志位

    cv::namedWindow("SIFT Features");
    cv::imshow("SIFT Features",featureImage);

    return a.exec();
}
 */
	
	private static void testSIFI() {
		Mat image = imread("F:/luoyafei.jpg");
		KeyPointVector keyPoints = new KeyPointVector();
		SIFT sift = new SIFT();
//		sift.detect(image, keyPoints);
		sift.detect(image, keyPoints);
//		sift.detectAndCompute
		System.out.println(keyPoints.capacity());
	}
    private static void detectForRegister(IplImage src,CvHaarClassifierCascade cascade,int flag,String name){ 

    	//从摄像头获取五张人脸注册  
            IplImage greyImg=null;  
            IplImage faceImg=null;  
            IplImage sizedImg=null;  
            IplImage equalizedImg=null;  
         // int countForResgister=0;//for pause to rejust facial expression  
      
            CvRect r ;  
            CvFont font = new CvFont();   
            cvInitFont(font,CV_FONT_HERSHEY_COMPLEX_SMALL, 1.0, 0.8,1,1,CV_AA);  
            
            greyImg = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1 );       
//            greyImg=convertImageToGreyscale(src);  
            cvConvertImage(greyImg, src);
//            cvConvertScale(greyImg, src);
            CvMemStorage storage = CvMemStorage.create();  
              
            CvSeq sign = cvHaarDetectObjects(  
                    greyImg,  
                    cascade,  
                    storage,  
                    1.1,  
                    3,  
                    flag);  
            cvClearMemStorage(storage);  
            if(sign.total()==1)//只会有一个脸部  
            {     
                r = new CvRect(cvGetSeqElem(sign, 0));
                sizedImg = greyImg.clone();
                cvSetImageROI(sizedImg, r);
//                faceImg = cropImage(greyImg, r);
//                sizedImg = resizeImage(faceImg);  
                equalizedImg = cvCreateImage(cvGetSize(sizedImg), 8, 1);      
                cvEqualizeHist(sizedImg, equalizedImg);       
                cvRectangle (  
                                src,  
                                cvPoint(r.x(), r.y()),  
                                cvPoint(r.width() + r.x(), r.height() + r.y()),  
                                CvScalar.WHITE,  
                                1,  
                                CV_AA,  
                                0);  
                cvPutText(src, "Need 5 photos" ,cvPoint(r.x()+30, r.y()- 50), font, CvScalar.GREEN);  
//                cvPutText(src, "This is No."+String.valueOf(countSavedFace)+" photos. " ,cvPoint(r.x()+30, r.y()- 30), font, CvScalar.GREEN);  
                /*if(countForResgister==10){                
                    cvSaveImage("img\\"+name+countSavedFace+".jpg",equalizedImg);  
                    countSavedFace++;     
                    countForResgister=0;  
                    cvWaitKey(800);  
                }  */
                cvSaveImage("F:/hello.jpg", equalizedImg);
                cvShowImage("", equalizedImg);
                waitKey(0);
                //                countForResgister++;  
                cvReleaseImage(greyImg);  
                cvReleaseImage(faceImg);  
                cvReleaseImage(sizedImg);  
                cvReleaseImage(equalizedImg);     
            }  
    }
    
	
	private static CvMemStorage storage;
	private static CvHaarClassifierCascade cascade;
	private static CvSeq sign;
	final private static String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
	final private static String SampleFiles = "F:/facereg";
	private static FrameGrabber grabber = null; //摄像头人脸检测
    private static void detectFace(IplImage src){
//	 	IplImage temp = src.clone();
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
        
		for(int i = 0; i < total_Faces; i++){
			CvRect r = new CvRect(cvGetSeqElem(sign, i));
			cvRectangle (
					src, 
					cvPoint(r.x(), r.y()), 
					cvPoint(r.width() + r.x(), r.height() + r.y()),
					CvScalar.RED,
					2,
					CV_AA,
					0);
//			cvSetImageROI(src, r);
			break;
		}
//		IplImage image = cvLoadImage("F/luoyafei.jpg");
//		cvResize(src, image);
		cvShowImage("Result", src);
		cvWaitKey(0);
//		return temp;
	}
    
	public static void main(String[] args) {
//		CascadeClassifier
		//cornerHarris
//		normalize
		
		Mat mat = imread("F:/javacv_picture/luoyafei/IMG_20161114_131335.jpg");
		Mat resizeMat = new Mat();
		resize(mat, resizeMat, new Size(720, 1280));
//		imshow("", resizeMat);
//		waitKey(0);
		detectFace(new IplImage(resizeMat));		
		
//		cvResize(image, resize, CV_BayerRG2BGR);
		//		imshow("", mat);
//		System.out.println(image.nChannels());
//		String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
//		CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
//		detectForRegister(image, cascade, 0, "hello");
		/*Mat out = mat;
		medianBlur(mat, out, 3);
		IplImage imageOut = image.clone();
		cvSmooth(image, imageOut);
		cvShowImage("", imageOut);*/
//		imshow("", out);
		waitKey(0);
//		testSIFI();
//		Mat img = imread("F:\\luoyafei.jpg");
		
	/*	SVM svm = new SVM(CV_WHOLE_SEQ);
		
		SIFT sift = new SIFT();
		KeyPoint kp = new KeyPoint();
		IplImage image = cvLoadImage("");
		Mat mat = imread("");
		sift.detect(mat, null);
		
		IplImage img = cvLoadImage("F:/luoyafei.jpg");
		IplImage temp = cvLoadImage("F:/luoluo.jpg");
		cvResize(temp, img);
		cvShowImage("img", img);
		cvShowImage("temp", temp);
		waitKey(0);
		cvReleaseImage(img);
		cvReleaseImage(temp);
		cvDestroyAllWindows();*/
//		Mat mat = new Mat(img);
//		System.out.println(mat);
//		extractFeatures(mat, null, null);
		
		/*ImageFile imageFile = ImageUtil.getImageFile();
		Vector<Image> images = imageFile.getImageFiles();
		for(int i = 0; i < images.size(); i++) {
			System.out.println(images.get(i).getPeopleName());
			for(String imageUrl : images.get(i).getPeopleNameItemNames()) {
				cvShowImage("", cvLoadImage(imageUrl));
			}
		}
		waitKey(0);*/
	}
}
