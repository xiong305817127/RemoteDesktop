package com.xh.manageServer.frame;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import java.awt.Component;

public class MyTabFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	List<ClientData> clients;

	public MyTabFrame() {
		super("控制平台 ");

		// 获取屏幕的边界
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		// 获取底部任务栏高度
		int taskBarHeight = screenInsets.bottom;
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenDimension.getWidth();
		int screenHeight = (int) screenDimension.getHeight() - taskBarHeight;

		setSize(screenWidth, screenHeight); // 设置打开窗口为屏幕大小
		setVisible(true);
		// setAlwaysOnTop(true);
		setDefaultCloseOperation(3);
		getContentPane().setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, screenWidth, screenHeight-40);
		getContentPane().add(tabbedPane);
		
		addComponentListener(new ComponentAdapter() {// 让窗口响应大小改变事件
			@Override
			public void componentResized(ComponentEvent e) {
				reSize();
				;
			}
		});

		tabbedPane.addMouseListener(new MouseAdapter() {// 让窗口响应大小改变事件
			public void mouseClicked(MouseEvent evt) {

				if (evt.getClickCount() == 2) {
					int index = tabbedPane.getSelectedIndex();
					if (index >= 0) {
						ClientData clientData = clients.get(index);
						clientData.setSuspend(!clientData.isSuspend());
					}
				}
			}
		});

		clients = new ArrayList<ClientData>();

		try { // 显示方式
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

	}

	public JLabel addImagePane(String title, DataInputStream in, ObjectOutputStream out) {
		JLabel lblNewLabel = new JLabel(title);
		addListener(lblNewLabel);
		tabbedPane.addTab(title, null, lblNewLabel, null);
		
		ClientData client = new ClientData(title, lblNewLabel, out, in);
		clients.add(client);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// 读取图片数据
					while (true) {

						int len = in.readInt();// 图片长度
						byte[] data = new byte[len];
						in.readFully(data);
						ImageIcon icon = new ImageIcon(data);
						if (!client.isSuspend() && in != null) {
							// 放到界面上.加到标签上
							showImagByRatio(client, icon);
						}
					}
				} catch (Exception ef) {
					System.out.println("获取数据流出现异常");
					ef.printStackTrace();
				}

			}
		}).start();

		return lblNewLabel;
	}

	private void showImagByRatio(ClientData clientData, ImageIcon icon) {

		if (clientData == null || icon == null) {
			return;
		}

		JLabel lblNewLabel = clientData.getImageLabel();
//		lblNewLabel.setSize(tabbedPane.getSize());
		lblNewLabel.setBounds(0, 0, tabbedPane.getWidth(), tabbedPane.getHeight());
		int imgWidth = icon.getIconWidth();// 获得图片宽度
		int imgHeight = icon.getIconHeight();// 获得图片高度

		clientData.setX(imgWidth);
		clientData.setY(imgHeight);

		int conWidth = lblNewLabel.getWidth();// 得到组件宽度
		int conHeight = lblNewLabel.getHeight()-20;// 得到组件高度

		ImageIcon icon1 = new ImageIcon(icon.getImage().getScaledInstance(conWidth, conHeight, Image.SCALE_DEFAULT));
		lblNewLabel.setIcon(icon1);
		lblNewLabel.repaint();// 销掉以前画的背景

	}

	private void reSize() {
		if (tabbedPane != null) {
			tabbedPane.setBounds(0, 0, getWidth(), getHeight()-40);

			clients.stream().forEach(client -> {
				ImageIcon icon1 = new ImageIcon(((ImageIcon)client.getImageLabel().getIcon()).getImage().getScaledInstance(tabbedPane.getWidth(), tabbedPane.getHeight(), Image.SCALE_DEFAULT));
				client.getImageLabel().setIcon(icon1);
				client.getImageLabel().repaint();// 销掉以前画的背景
			});
		}
	}

	private void addListener(Component image) {
		image.addMouseListener(new MouseListener() {

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
		image.addMouseMotionListener(new MouseMotionListener() {
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
		image.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				sentEvent(arg0);

			}
		});

		// 键盘事件
		image.addKeyListener(new KeyListener() {

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

		int index = tabbedPane.getSelectedIndex();
		ClientData clietData = clients.get(index);

		if (e instanceof MouseEvent) {
			MouseEvent ee = (MouseEvent) e;
			e = new MouseEvent(ee.getComponent(), ee.getID(), ee.getWhen(), ee.getModifiers(),
					clietData.getCurrentXPositon(ee.getX()), clietData.getCurrentYPositon(ee.getY()),
					ee.getClickCount(), ee.isPopupTrigger());
		}

		ObjectOutputStream out = clietData.getOut();

		if (out != null && !clietData.isSuspend()) {
			try {
				out.writeObject(e);
			} catch (IOException e1) {
				System.out.println("发送事件对象出现异常");
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		MyTabFrame jf = new MyTabFrame();
		jf.addImagePane("aaaa", null, null);
		jf.addImagePane("bbb", null, null);
		jf.addImagePane("ccc", null, null);
		System.out.println(jf.tabbedPane.getSelectedIndex());
	}

}

class ClientData {

	boolean isSuspend = false;

	String title;
	JLabel imageLabel;
	ObjectOutputStream out;
	DataInputStream in;

	int X = -1;
	int Y = -1;

	/**
	 * @param title
	 * @param imageLabel
	 * @param out
	 * @param in
	 */
	public ClientData(String title, JLabel imageLabel, ObjectOutputStream out, DataInputStream in) {
		super();
		this.title = title;
		this.imageLabel = imageLabel;
		this.out = out;
		this.in = in;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param 设置
	 *            title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the imageLabel
	 */
	public JLabel getImageLabel() {
		return imageLabel;
	}

	/**
	 * @param 设置
	 *            imageLabel
	 */
	public void setImageLabel(JLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

	/**
	 * @return the out
	 */
	public ObjectOutputStream getOut() {
		return out;
	}

	/**
	 * @param 设置
	 *            out
	 */
	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	/**
	 * @return the in
	 */
	public DataInputStream getIn() {
		return in;
	}

	/**
	 * @param 设置
	 *            in
	 */
	public void setIn(DataInputStream in) {
		this.in = in;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return X;
	}

	/**
	 * @param 设置
	 *            x
	 */
	public void setX(int x) {
		X = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return Y;
	}

	/**
	 * @param 设置
	 *            y
	 */
	public void setY(int y) {
		Y = y;
	}

	/**
	 * @return the isSuspend
	 */
	public boolean isSuspend() {
		return isSuspend;
	}

	/**
	 * @param 设置
	 *            isSuspend
	 */
	public void setSuspend(boolean isSuspend) {
		this.isSuspend = isSuspend;
	}

	public int getCurrentXPositon(int mouseX) {

		int conWidth = imageLabel.getWidth();// 得到组件宽度

		if (X <= 0 || conWidth <= 0) {
			return mouseX;
		}
		return (X * mouseX) / conWidth;
	}

	public int getCurrentYPositon(int mouseY) {

		int conHeight = imageLabel.getHeight();// 得到组件高度
		if (Y <= 0 || conHeight <= 0) {
			return mouseY;
		}
		return (Y * mouseY) / conHeight;
	}

}
