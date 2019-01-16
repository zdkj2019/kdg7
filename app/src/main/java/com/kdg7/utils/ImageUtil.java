package com.kdg7.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.kdg7.common.Constant;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Environment;


public class ImageUtil {


	public static Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		// Do not compress
		newOpts.inSampleSize = 1;
		newOpts.inPreferredConfig = Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);
	}

	/**
	 * 把Bitmap保存到指定路
	 */
	public static void storeImage(Bitmap bitmap, String outPath)
			throws FileNotFoundException {
		FileOutputStream os = new FileOutputStream(outPath);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
	}

	/**
	 * 根据指定的高宽缩放Bitmap
	 */
	public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Config.RGB_565;
		// Get bitmap info, but notice that bitmap is null now
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = pixelH;
		float ww = pixelW;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		return bitmap;
	}

	/**
	 * 根据指定的高宽缩放Bitmap
	 *
	 */
	public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, os);
		if (os.toByteArray().length / 1024 > 1024) {
			os.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, os);
		}
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();

		newOpts.inJustDecodeBounds = true;
		newOpts.inPreferredConfig = Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = pixelH;
		float ww = pixelW;

		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例

		is = new ByteArrayInputStream(os.toByteArray());
		bitmap = BitmapFactory.decodeStream(is, null, newOpts);
		return bitmap;
	}

	/**
	 * 压缩图片的质量，并生成图像到指定的路
	 *
	 */
	public static void compressAndGenImage(Bitmap image, String outPath,
			int maxSize) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while (os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}

		// Generate compressed image file
		FileOutputStream fos = new FileOutputStream(outPath);
		fos.write(os.toByteArray());
		fos.flush();
		fos.close();
	}

	/**
	 */
	public static String compressAndGenImage(Bitmap image, int maxSize,
			String type) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// scale
		int options = 100;
		// Store the bitmap into output stream(no compress)
		image.compress(Bitmap.CompressFormat.JPEG, options, os);
		// Compress by loop
		while (os.toByteArray().length / 1024 > maxSize) {
			// Clean up os
			os.reset();
			// interval 10
			options -= 10;
			image.compress(Bitmap.CompressFormat.JPEG, options, os);
		}
		String outPath = Constant.SAVE_PIC_PATH
				+ "/"
				+ String.valueOf(System.currentTimeMillis()).trim()
						.substring(4) + "." + type;
		// Generate compressed image file
		FileOutputStream fos = new FileOutputStream(outPath);
		fos.write(os.toByteArray());
		fos.flush();
		fos.close();
		return outPath;
	}

	/**
	 * 压缩图片的质量，并生成图像到指定的路
	 *
	 */
	public static void compressAndGenImage(String imgPath, String outPath,
			int maxSize, boolean needsDelete) throws IOException {
		compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

		// Delete original file
		if (needsDelete) {
			File file = new File(imgPath);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	/**
	 * 缩放后保存到指定路径
	 *
	 */
	public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW,
			float pixelH) throws FileNotFoundException {
		Bitmap bitmap = ratio(image, pixelW, pixelH);
		storeImage(bitmap, outPath);
	}

	/**
	 * 缩放后保存到指定路径
	 *
	 */
	public void ratioAndGenThumb(String imgPath, String outPath, float pixelW,
			float pixelH, boolean needsDelete) throws FileNotFoundException {
		Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
		storeImage(bitmap, outPath);

		// Delete original file
		if (needsDelete) {
			File file = new File(imgPath);
			if (file.exists()) {
				file.delete();
			}
		}
	}
}
