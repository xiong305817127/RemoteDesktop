package com.xh.manageServer.frame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.ObjectOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class MyJframe extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ObjectOutputStream ous;

	private JLabel img_jLabel = new JLabel();

	public MyJframe(String title, ObjectOutputStream ous) {
		super(title);
		this.ous = ous;
		
		// 获取屏幕的边界
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		// 获取底部任务栏高度
		int taskBarHeight = screenInsets.bottom;
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth =  (int) screenDimension.getWidth() ;
		int screenHeight =  (int) screenDimension.getHeight() - taskBarHeight;
		
		setSize( screenWidth, screenHeight); // 设置打开窗口为屏幕大小
		setVisible(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(3);
		try { // 显示方式
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		
		img_jLabel.setSize(screenWidth, screenHeight); // 设置打开远程屏幕大小
		add(img_jLabel);
	}

	/*
	 * type 1 原图显示 2 按照比例缩放 3 滚动显示
	 */
	public  void setImgLabel(ImageIcon icon, int type) {
		switch (type) {
		case 1:

			break;
		case 2:
			showImagByRatio(icon);
			break;
		case 3:

			break;
		default:
			break;
		}
	}

	private void showImagByRatio(ImageIcon icon) {
		int imgWidth = icon.getIconWidth();// 获得图片宽度
		int imgHeight = icon.getIconHeight();// 获得图片高度
		int conWidth = img_jLabel.getWidth();// 得到组件宽度
		int conHeight = img_jLabel.getHeight();// 得到组件高度
		int reImgWidth = conWidth;// 保存图片更改宽度后的值
		int reImgHeight = conHeight;// 保存图片更改高度后的值
		if (conHeight != 0 && imgHeight != 0 && imgWidth / imgHeight >= conWidth / conHeight) {
			if (imgWidth > conWidth) {
				reImgWidth = conWidth;
				reImgHeight = imgHeight * reImgWidth / imgWidth;
			} else {
				reImgWidth = imgWidth;
				reImgHeight = imgHeight;
			}
		} else {
			if (imgWidth > conWidth) {
				reImgHeight = conHeight;
				reImgWidth = imgWidth * reImgHeight / imgHeight;
			} else {
				reImgWidth = imgWidth;
				reImgHeight = imgHeight;
			}
		}
		icon = new ImageIcon(icon.getImage().getScaledInstance(reImgWidth, reImgHeight, Image.SCALE_DEFAULT));
		img_jLabel.setIcon(icon);
		img_jLabel.repaint();// 销掉以前画的背景

	}

}
