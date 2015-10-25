package com.nxt.comm;

import java.io.InputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.NXTConnection;

import com.nxt.observer.Observable;
import com.nxt.observer.Observer;

public class Comm extends Thread implements Observable{
	private InputStream inStream;
	private DataOutputStream outStream;
	private NXTConnection conn;
	private byte[] inputData = new byte[3];
	private byte[] outputData = new byte[5];
	
	private ArrayList<Observer> obsList = new ArrayList<Observer>();
	
	protected volatile boolean running = true;
	protected boolean closed = false;

	public final static int USB_MODE = 0;
	public final static int BLUETOOTH_MODE = 1;
	public final static byte EOC = -128;	
		
	public Comm(int mode)  throws NXTCommException {
		switch(mode){
		case USB_MODE:
			conn = USB.waitForConnection(30000, NXTConnection.PACKET);
			break;
		case BLUETOOTH_MODE:
			conn = Bluetooth.waitForConnection(30000, NXTConnection.PACKET);
			break;
		default:
			throw new NXTCommException();
		}
		
		LCD.clear();
		LCD.drawString("Connecting to PC", 0, 0);
		
		if(conn == null){
			throw new NXTCommException();
		}
		else{
			LCD.drawString("Creating streams", 0, 1);
			inStream = conn.openInputStream();
			inputData[0] = 0;
			inputData[1] = 0;
			inputData[2] = 0;
			outStream = conn.openDataOutputStream();
			outputData[0] = 0;
			outputData[1] = 0;
			outputData[2] = 0;
			outputData[3] = 0;
			outputData[4] = 0;
			
			if( confirmation() != true){
				throw new NXTCommException();
			}
			LCD.drawString("Connected", 0, 2);
			LCD.clear();
		}
	}
	
	private boolean confirmation() {
		byte key[] = {0, 1, 2};
		byte receipt[] = new byte[3];
	    boolean validation = false;
	    
		try {
	        this.inStream.read(receipt);
	        LCD.drawString("key: " + receipt[0] + " " + receipt[1] + " " + receipt[2], 0, 3);
	        
	        if(receipt[0] != key[0] || receipt[1] != key[1] || receipt[2] != key[2]) {
	        	validation = false;
	            this.conn.close();
	            LCD.drawString("Bad key received", 0, 4);
	        }
	        
	        else {
	        	validation = true;
	        	LCD.drawString("Key received", 0, 4);
	        	outStream.write(key);
	        	outStream.flush();
	            
	        }
	    }
		catch (Exception e){
	    	LCD.clear();
			LCD.drawString("connection error " + e.getMessage(), 0, 0);
		}
		
		return validation;
	}

	private int readData(){
		int byteNumber = 0;
		try{
			byteNumber = this.inStream.read(inputData);
		} 
		catch (IOException e){
		}
		return byteNumber;
	}
		
	private void sendData(){
		try{
			this.outStream.write(outputData);
			this.outStream.flush();
		} 
		catch (IOException e){
		}
	}

	public void run(){
		try {
			while(running){
				LCD.clear();
				if(this.readData() == 1){
					if(inputData[0] == Comm.EOC){
						this.end();
					}
				}
				isRunning();
				this.sendData();
				isRunning();
				this.updateObs();
			}
		} 
		catch (InterruptedException e) {
			try {
				outStream.write(Comm.EOC);
				inStream.close();
				outStream.close();	
				USB.usbReset();
				closed = true;
				Thread.currentThread().interrupt();
			} 
			catch (IOException e1) {
					
			}
		}			
	}
	
	public void end(){
		this.running = false;
	}
	
	private void isRunning() throws InterruptedException{
		if(this.running == false)
			throw new InterruptedException();
	}

	public boolean isClosed(){
		return closed;
	}
	
	public void addObs(Observer obs) {
		this.obsList.add(obs);
	}
	
	public void updateObs() {
		for(Observer obs : this.obsList){
			obs.updateData(this.inputData);
		}
	}

	public void removeObs() {
		this.obsList = new ArrayList<Observer>();
	}
	
	public void setData(byte[] data){
		this.outputData = data;
	}


}
