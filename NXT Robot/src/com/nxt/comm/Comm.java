package com.nxt.comm;

import java.io.InputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
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
	private int byteRead = 0;
	
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
			for(int idx = 0; idx < inputData.length; idx++)
				inputData[idx] = 0;
			outStream = conn.openDataOutputStream();
			for(int idx = 0; idx < outputData.length; idx++)
				outputData[idx] = 0;
			
			if( confirmation() != true){
				throw new NXTCommException();
			}
			LCD.drawString("Connected", 0, 2);
			LCD.clear();
		}
		Sound.beep();
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

	private void readData() throws InterruptedException, IOException{
		if(running){
			this.byteRead = this.inStream.read(inputData);
		} 
		else 
			throw new InterruptedException();
	}
		
	private void sendData() throws InterruptedException, IOException{
		if(running){
			this.outStream.write(outputData);
			this.outStream.flush();
		} 
		else
			throw new InterruptedException();
	}

	private void closeStream(){
		try{
			outStream.write(Comm.EOC);
			outStream.flush();
			outStream.close();
			inStream.close();
		}
		catch(IOException e){
		}
		Sound.twoBeeps();
	}
	
	
	public void run(){
		try {
			while(running){
				this.readData();
				
				if(this.byteRead == 1)
						this.close();
				
				this.updateObs();
				this.sendData();
			}
		} 
		catch (InterruptedException e) {
		}
		catch (IOException e) {
		}
		finally{
			closeStream();
			closed = true;
		}
	}
	
	public void close(){
		this.running = false;
	}
	
	public void waitForClosure(){
		this.running = false;
		while(closed != true){
			
		}
	}
	
	public boolean isRunning() {
		return this.running;
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
