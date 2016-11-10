package com.facerec.util;

import java.util.Vector;

public class ImageFile {

	@Override
	public String toString() {
		return "ImageFile [imageFiles=" + imageFiles + "]";
	}
	
	public Vector<Image> getImageFiles() {
		return imageFiles;
	}
	public void setImageFiles(Vector<Image> imageFiles) {
		this.imageFiles = imageFiles;
	}
	/**
	 * 所有人的脸的集合
	 */
	private Vector<Image> imageFiles = null;
	
	/**
	 * @param peopleName 对应一个人脸样本的名称
	 * @param peopleNameItemNames 对应所有该人脸样本的所有图片的地址
	 * @author Diamond
	 *
	 */
	public static class Image{
		@Override
		public String toString() {
			return "Image [peopleName=" + peopleName + ", peopleNameItemNames=" + peopleNameItemNames + "]";
		}
		private String peopleName;
		private Vector<String> peopleNameItemNames;
		
		public String getPeopleName() {
			return peopleName;
		}
		public void setPeopleName(String peopleName) {
			this.peopleName = peopleName;
		}
		public Vector<String> getPeopleNameItemNames() {
			return peopleNameItemNames;
		}
		public void setPeopleNameItemNames(Vector<String> peopleNameItemNames) {
			this.peopleNameItemNames = peopleNameItemNames;
		}
	}
}
