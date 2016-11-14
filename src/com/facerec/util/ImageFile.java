package com.facerec.util;

import java.util.Vector;

import com.google.gson.Gson;

public class ImageFile {

	@Override
	public String toString() {
		return "ImageFile [imagefilesUrl=" + imagefilesUrl + "]";
	}
	Vector<String> imagefilesUrl = new Vector<>();
	public Vector<String> getImagefilesUrl() {
		return imagefilesUrl;
	}

	public void setImagefilesUrl(Vector<String> imagefilesUrl) {
		this.imagefilesUrl = imagefilesUrl;
	}
	public static void main(String[] args) {
		Vector<String> test = new Vector<>();
		test.add("select");
		test.add("hellow");
		test.add("hellod");
		test.add("hellos");
		ImageFile imf = new ImageFile();
		imf.setImagefilesUrl(test);
		Gson gson = new Gson();
//		ImageFile ifile = gson.fromJson("{\"imagefilesUrl\":[\"select\",\"hellow\",\"hellod\",\"hellos\"]}", ImageFile.class);
//		System.out.println(ifile.getImagefilesUrl().toString());
		System.out.println(gson.toJson(imf));
	}
}
