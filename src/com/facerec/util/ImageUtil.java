package com.facerec.util;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.VideoInputFrameGrabber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class ImageUtil {

	private static final String XML_FILE = "D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml";
	private static final Gson gson = new Gson();

	private static final CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(XML_FILE));
	private static final CvMemStorage storage = CvMemStorage.create();

	public static boolean dealSampleFaceImage(IplImage src, String fileUrl) {
		
		IplImage imgTemp = src.clone();
		
		
		CvSeq sign = cvHaarDetectObjects(src, cascade, storage, 1.2, 2, CV_HAAR_DO_CANNY_PRUNING);
		cvClearMemStorage(storage);
		int total_Faces = sign.total();
		if (total_Faces == 0)
			return false;
		for (int i = 0; i < total_Faces; i++) {
			CvRect r = new CvRect(cvGetSeqElem(sign, i));
			/*cvRectangle(src, cvPoint(r.x(), r.y()), cvPoint(r.width() + r.x(), r.height() + r.y()), CvScalar.RED, 2,
					CV_AA, 0);*/
			cvSetImageROI(imgTemp, r);
		}
		
//		cvWaitKey(0);
		return cvSaveImage(fileUrl, imgTemp)==1 ? true : false;
//		System.out.println(cvSaveImage(fileUrl, imgTemp));
	}
	
	public static void saveFileJson(ImageFile imageFile) {
		String filesContent = gson.toJson(imageFile);
		File file = new File("files.json");
System.out.println(imageFile.toString());
		try {
			FileOutputStream fileOutput = new FileOutputStream(file);
			fileOutput.write(filesContent.getBytes());
		} catch(IOException e) {}
	}
	
	@SuppressWarnings("finally")
	public static ImageFile getImageFile() {
		ImageFile file = null;
		try {
			file =  gson.fromJson(new FileReader("files.json"), ImageFile.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
System.out.println("files.json 文件加载失败！没有找到！");
			file = null;			
		} finally {
			return file;
		}
	}
}
