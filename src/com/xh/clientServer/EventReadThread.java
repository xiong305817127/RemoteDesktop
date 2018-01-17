package com.xh.clientServer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

public class EventReadThread extends Thread {

	private ObjectInputStream objins;

	private boolean isOk = true;

	public EventReadThread(ObjectInputStream objins) {
		this.objins = objins;
	}

	@Override
	public void run() {
		while (isOk) {
			try {
				Object eventobj = objins.readObject();
				InputEvent e = (InputEvent) eventobj;
				actionEvent(e);
			} catch (Exception e) {
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

	// 回放事件的方法
	private void actionEvent(InputEvent e) {
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		// 是什么具体事件
		if (e instanceof KeyEvent) {
			KeyEvent ke = (KeyEvent) e;
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				robot.keyPress(ke.getKeyCode());
			}
			if (e.getID() == KeyEvent.KEY_RELEASED) {
				robot.keyRelease(ke.getKeyCode());
			}
		}
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			int type = me.getID();
			if (type == MouseEvent.MOUSE_PRESSED) { // 按下
				robot.mousePress(getMouseClick(me.getButton()));
			}
			if (type == MouseEvent.MOUSE_RELEASED) { // 放开
				robot.mouseRelease(getMouseClick(me.getButton()));
			}
			if (type == MouseEvent.MOUSE_MOVED) { // 移动
				robot.mouseMove(me.getX(), me.getY());
			}
			if (type == MouseEvent.MOUSE_DRAGGED) { // 拖动
				robot.mouseMove(me.getX(), me.getY());
			}
			if (type == MouseEvent.MOUSE_WHEEL) { // 滑轮滚动
				robot.mouseWheel(getMouseClick(me.getButton()));
			}
		}

	}

	// 根据发送事的Mouse事件对象，转变为通用的Mouse按键代码
	private int getMouseClick(int button) {
		if (button == MouseEvent.BUTTON1) {
			return InputEvent.BUTTON1_MASK;
		}
		if (button == MouseEvent.BUTTON2) {
			return InputEvent.BUTTON2_MASK;
		}
		if (button == MouseEvent.BUTTON3) {
			return InputEvent.BUTTON3_MASK;
		}
		return -1;
	}
}
