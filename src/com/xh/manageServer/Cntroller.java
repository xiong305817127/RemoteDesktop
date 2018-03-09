package com.xh.manageServer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.xh.manageServer.frame.MyTabFrame;

public class Cntroller {


	private static MyTabFrame jf;

	private static void showUI() {
		jf = new MyTabFrame();
		jf.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				System.out.println(arg0.getNewValue());
			}
		});
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			int port = 50090;
			if (args != null && args.length >= 1) {
				port = Integer.valueOf(args[0]);
			}
			
			Cntroller.showUI();
			
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("启动服务,端口:"+port);
			while (true) {
				Socket socket = serverSocket.accept();
				String title = socket.getInetAddress().getHostAddress() ;
				System.out.println("收到请求,IP:"+socket.getInetAddress().getHostAddress());
				
				// 得到输入流，读取图片数据
				DataInputStream in = new DataInputStream(socket.getInputStream());
				// 得到输出流，发送事件对象
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				// 处理这两个流
				jf.addImagePane(title, in, out) ;
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
