/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.gaara.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * tool for image creating
 * 
 * @author lichengwu
 * @created 2012-3-25
 * 
 * @version 1.0
 */
final public class ImageCreator {

	/**
	 * create image and write to OutputStream
	 * 
	 * @author lichengwu
	 * @created 2012-3-25
	 * 
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 * @param str
	 *            string write to image
	 * @param out
	 *            OutputStream to write img
	 * @throws Exception
	 */
	public static void create(int width, int height, String str, OutputStream out)
	        throws IOException {

		if (width <= 200) {
			width = 280;
			height = 136;
		}

		// font size
		int fontSize = width * 8 / 10 / (str.length() + 1);
		if (fontSize < 10) {
			fontSize = 10;
		}
		// set font
		Font font = new Font("Serif", Font.BOLD, fontSize);
		// new BufferedImage
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// get Graphics2D
		Graphics2D graph = (Graphics2D) bi.getGraphics();
		graph.setBackground(Color.white);
		graph.clearRect(0, 0, width, height);
		graph.setPaint(Color.red);
		graph.setFont(font);

		FontRenderContext context = graph.getFontRenderContext();

		Rectangle2D bounds = font.getStringBounds(str, context);

		double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight()) / 2;
		double ascent = -bounds.getY();
		double baseY = y + ascent;

		graph.drawString(str, (int) x, (int) baseY);
		ImageIO.write(bi, "jpg", out);
	}
}
