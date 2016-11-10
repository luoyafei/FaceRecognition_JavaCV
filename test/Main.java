import java.util.Vector;

import com.facerec.util.ImageFile;
import com.facerec.util.ImageFile.Image;
import com.facerec.util.ImageUtil;

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
		System.out.println(contours.size());
		return null;
	}
	
	public static void main(String[] args) {
		
//		Mat img = imread("F:\\luoyafei.jpg");
		IplImage img = cvLoadImage("F:/luoyafei.jpg");
		Mat mat = new Mat(img);
		System.out.println(mat);
		extractFeatures(mat, null, null);
		
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
