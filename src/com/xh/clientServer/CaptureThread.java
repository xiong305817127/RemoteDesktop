package com.xh.clientServer;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 发送图片的线程
 * 
 * @author wfg
 *
 */
public class CaptureThread extends Thread {
	private DataOutputStream dataOutputStream;

	private Toolkit tk;
	private Dimension dm;
	private Rectangle rec;
	private Robot robot;

	public boolean isOk = true;

	public CaptureThread(DataOutputStream dataOutputStream) throws AWTException {
		this.dataOutputStream = dataOutputStream;
		tk = Toolkit.getDefaultToolkit();
		dm = tk.getScreenSize();
		// 根据屏幕设定图片的大小
		rec = new Rectangle(0, 0, (int) dm.getWidth(), (int) dm.getHeight());
		robot = new Robot();
	}

	@Override
	public void run() {
		while (isOk) {
			byte[] data = createCature();
			try {
				dataOutputStream.writeInt(data.length);
				dataOutputStream.write(data);
				dataOutputStream.flush();
				try {
					Thread.sleep(1000 / 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
				isOk = false;
			}

		}

	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean ok) {
		isOk = ok;
	}

	private byte[] createCature() {
		// 获得一个屏幕的截图
		BufferedImage bimage = robot.createScreenCapture(rec);
		//// 创建一段内存流
		ByteArrayOutputStream byout = new ByteArrayOutputStream();
		try {
			// 将图片数据写入内存流中
			ImageIO.write(bimage, "jpg", byout);
		} catch (IOException e) {
			System.out.println("截屏图片写入内存流中出现异常");
			e.printStackTrace();
		}
		return byout.toByteArray();
	}

}
