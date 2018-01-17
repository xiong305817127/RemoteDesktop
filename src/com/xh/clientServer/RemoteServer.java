package com.xh.clientServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RemoteServer {

	private ObjectInputStream objectInputStream;
	private OutputStream ous;
	private Socket socket;
	
	static RemoteServer remoteServer;
	
	EventReadThread eventReadThread ;
	CaptureThread captureThread ;

	public void setupServer(Socket socket) throws Exception {
		InputStream ins = socket.getInputStream();
		// 对象输入流 读取事件对象
		objectInputStream = new ObjectInputStream(ins);
		ous = socket.getOutputStream();
		// 数据输出流，用以发送图片数据 1个int图片数据长度 图片的字节
		DataOutputStream dous = new DataOutputStream(ous);
		eventReadThread = new EventReadThread(objectInputStream);
		eventReadThread.start();
		captureThread = new CaptureThread(dous);
		captureThread.start();
		// }
	}
	
	public void close() {
		try {
			if(objectInputStream != null) {
				objectInputStream.close();
			}
			if(ous != null ) {
				ous.close();
			};
			if(socket != null ) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {
		boolean isOk = false;
		while (true) {
			try {
				Thread.sleep(1000);
				if(remoteServer != null && remoteServer.captureThread.isOk() && remoteServer.eventReadThread.isOk() ) {
					continue ;
				}
				
				if(remoteServer != null && ( !remoteServer.captureThread.isOk() ||  !remoteServer.eventReadThread.isOk() ) ) {
					remoteServer.captureThread.setOk(false); ;
					remoteServer.eventReadThread.setOk(false); 
					remoteServer.close();
				}
				
				String ip = "192.168.1.142";
				int port = 50090;

				if (args != null && args.length >= 1) {
					ip = args[0];
				}
				if (args != null && args.length >= 2) {
					port = Integer.valueOf(args[1]);
				}

				Socket sc = new Socket(ip, port);
				remoteServer = new RemoteServer();
				remoteServer.setupServer(sc);
				isOk = true;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
