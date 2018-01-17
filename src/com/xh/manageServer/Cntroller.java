package com.xh.manageServer;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.ImageIcon;

import com.xh.manageServer.frame.MyJframe;

public class Cntroller extends Thread {

	private ObjectOutputStream ous;
	private DataInputStream ins;
	private MyJframe jf;

	private void showUI(String clientName) {
		jf = new MyJframe(clientName, ous);
		addListener(jf); // 添加监听
		jf.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				System.out.println(arg0.getNewValue());
			}
		});
	}


	@Override
	public void run() {
		try {
			// 读取图片数据
			while (true) {
				int len = ins.readInt();// 图片长度
				byte[] data = new byte[len];
				ins.readFully(data);
				// 将读到的数据生成为一个图标对象
				ImageIcon ic = new ImageIcon(data);
				// 放到界面上.加到标签上
				jf.setImgLabel(ic, 2);
			}
		} catch (Exception ef) {
			System.out.println("获取数据流出现异常");
			ef.printStackTrace();
		}
	}

	private void addListener(MyJframe jf) {
		jf.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				sentEvent(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				sentEvent(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		// 鼠标移动事件
		jf.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				sentEvent(arg0);
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				sentEvent(arg0);
			}

		});
		// 鼠标滑轮滑动事件
		jf.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				sentEvent(arg0);

			}
		});

		// 键盘事件
		jf.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				sentEvent(arg0);
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				sentEvent(arg0);

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				sentEvent(arg0);

			}
		});
	}

	private void sentEvent(InputEvent e) {
		try {
			ous.writeObject(e);
		} catch (IOException e1) {
			System.out.println("发送事件对象出现异常");
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			int port = 50090;
			if (args != null && args.length >= 1) {
				port = Integer.valueOf(args[0]);
			}
			
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("启动服务,端口:"+port);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("收到请求,IP:"+socket.getInetAddress().getHostAddress());
				
				Cntroller cn = new Cntroller();
				// 得到输入流，读取图片数据
				cn.ins = new DataInputStream(socket.getInputStream());
				// 得到输出流，发送事件对象
				cn.ous = new ObjectOutputStream(socket.getOutputStream());
				// 处理这两个流
				cn.showUI(socket.getInetAddress().getHostAddress());
				cn.start();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
