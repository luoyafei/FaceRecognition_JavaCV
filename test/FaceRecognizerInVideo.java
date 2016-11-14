
import java.io.File;
import java.io.FilenameFilter;

import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

/**
 * This is an example how to detect face in a video file with javacv
 * @author Vincent He (chinadragon0515@gmail.com)
 *
 */
public class FaceRecognizerInVideo {

    public static void main(String[] args) throws Exception {

    	
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        /*if (args.length < 2) {
            System.out.println("Two parameters are required to run this program, first parameter is the analized video and second parameter is the trained result for fisher faces.");
        }*/

//        String videoFileName = "F:/luoyafei.mp4";//args[0];
//        String trainedResult = "F:/luoyafei.jpg";//args[1];
        String trainingDir = "F:/facereg";

        CascadeClassifier face_cascade = new CascadeClassifier("D:/OpenCV/opencv/sources/data/haarcascades/haarcascade_frontalface_default.xml");
        FaceRecognizer lbphFaceRecognizer = createLBPHFaceRecognizer();
//        lbphFaceRecognizer.load(trainedResult);
        
        File root = new File(trainingDir);
        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
            }
        };
        File[] imageFiles = root.listFiles(imgFilter);
        MatVector images = new MatVector(imageFiles.length);
        

        
        
//        File f = new File(videoFileName);

        OpenCVFrameGrabber grabber = null;
        try {
            grabber = OpenCVFrameGrabber.createDefault(0);
            grabber.start();
        } catch (Exception e) {
            System.err.println("Failed start the grabber.");
        }

        Frame videoFrame = null;
        Mat videoMat = new Mat();
        while (true) {
            videoFrame = grabber.grab();
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
                // If fisher face recognizer is used, the face need to be
                // resized.
                // resize(face, face_resized, new Size(im_width, im_height),
                // 1.0, 1.0, INTER_CUBIC);

                // Now perform the prediction, see how easy that is:
                int prediction = lbphFaceRecognizer.predict(face);

                // And finally write all we've found out to the original image!
                // First of all draw a green rectangle around the detected face:
                rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

                // Create the text we will annotate the box with:
                String box_text = "Prediction = " + prediction;
                // Calculate the position for annotated text (make sure we don't
                // put illegal values in there):
                int pos_x = Math.max(face_i.tl().x() - 10, 0);
                int pos_y = Math.max(face_i.tl().y() - 10, 0);
                // And now put it into the image:
                putText(videoMat, box_text, new Point(pos_x, pos_y),
                        FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
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

}
