package com.gui;

import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import com.nxt.comm.*;
import com.observer.*;
import com.gui.MonitorPanel;

public class Window extends JFrame implements KeyListener{
	
	private MonitorPanel monitor = new MonitorPanel();
	private boolean isMonitoring = true;
	
	private byte outputData[] = {0, 0, 0};
	private Comm comm;
		
	public Window() {
		this.setTitle("NXT Interface");
		this.setSize(500, 200);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(false);

		this.setContentPane(monitor);
		this.setVisible(true);
		
		this.addKeyListener(this);
		
		process();
	}
	
	private void process(){
		try{
			//Bluetooth: "btspp://" USB: "usb://"
			comm = new Comm("usb://");
			comm.addObs(new Observer(){
				public void updateData(byte[] data){
					monitor.setData(data);
				}
			});
			comm.start();
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		} 
	 }

	public void keyPressed(KeyEvent e) {
		if(isMonitoring){
			if((e.getKeyCode()==KeyEvent.VK_ENTER))
				outputData[2] = 1;
			if((e.getKeyCode()==KeyEvent.VK_UP))
				outputData[1] = 1;
			if((e.getKeyCode()==KeyEvent.VK_DOWN))
				outputData[1] = -1;
			if((e.getKeyCode()==KeyEvent.VK_RIGHT))
				outputData[0] = -1;
			if((e.getKeyCode()==KeyEvent.VK_LEFT))
				outputData[0] = 1;
			this.comm.setData(outputData);
		}	
	}

	public void keyReleased(KeyEvent e) {
		if(isMonitoring){
			if((e.getKeyCode()==KeyEvent.VK_ENTER))
				outputData[2] = 0;
			if((e.getKeyCode()==KeyEvent.VK_UP))
				outputData[1] = 0;
			if((e.getKeyCode()==KeyEvent.VK_DOWN))
				outputData[1] = 0;
			if((e.getKeyCode()==KeyEvent.VK_RIGHT))
				outputData[0] = 0;			
			if((e.getKeyCode()==KeyEvent.VK_LEFT))
				outputData[0] = 0;
			this.comm.setData(outputData);
		}
		
	}

	public void keyTyped(KeyEvent e) {
	}
	
}
