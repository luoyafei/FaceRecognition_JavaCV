package com.facerec.util;

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvMinMaxLoc;
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_TM_CCORR_NORMED;
import static org.bytedeco.javacpp.opencv_imgproc.cvMatchTemplate;

/**
 * 
import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import static org.bytedeco.javacpp.opencv_calib3d.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

 */

import java.util.Vector;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;

import com.facerec.util.ImageFile.Image;


public class TemplateMatch {

	private opencv_core.IplImage matchSample = null;

    public void setMatchSample(String filename) {
        matchSample = cvLoadImage(filename);
    }

    /**
     * 返回匹配的模板名称呼
     * @param source
     * @param images
     * @return
     */
    public String matchTemplate(IplImage source, Vector<Image> images) {
    	
    	for(int i = 0; i < images.size(); i++) {
    		String name = images.get(i).getPeopleName();
    		
    		System.out.println("名稱：" + name);
    		
    		for(String nameUrl : images.get(i).getPeopleNameItemNames()) {
    			matchSample = cvLoadImage(nameUrl);
    			if(matchTemplate(source)) {
    				return name;
    			}
    		}
    	}
    	return "老王";
    }
    
    public boolean matchTemplate(IplImage source) {
    	
        IplImage result = cvCreateImage(opencv_core.cvSize(
                source.width() - this.matchSample.width() + 1,
                source.height() - this.matchSample.height() + 1),
                opencv_core.IPL_DEPTH_32F, 1);

        opencv_core.cvZero(result);
        cvMatchTemplate(source, this.matchSample, result, CV_TM_CCORR_NORMED);
        opencv_core.CvPoint maxLoc = new opencv_core.CvPoint();
        opencv_core.CvPoint minLoc = new opencv_core.CvPoint();
        double[] minVal = new double[20];
        double[] maxVal = new double[20];
        DoublePointer minVal_d = new DoublePointer(minVal);
        DoublePointer maxVal_d = new DoublePointer(maxVal);
        cvMinMaxLoc(result, minVal_d, maxVal_d, minLoc, maxLoc, null);
//        cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);
//        matchRes = maxVal[0] > 0.99f ? true : false;

//        System.out.println(maxVal_d.get());
        cvReleaseImage(result);
        boolean resultBool = maxVal_d.get() > 0.97f ? true : false;
//System.out.println("匹配结果boolean : " + resultBool);
        return resultBool;
    }
}