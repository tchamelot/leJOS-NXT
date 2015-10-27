package com.nxt.comm;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.pc.comm.NXTConnector;
import com.observer.*;

public class Comm extends Thread implements Observable{
	
	private NXTConnector conn;
	private InputStream 	inStream;
	private OutputStream 	outStream;
	private byte[] inputData = new byte[5];
	private byte[] outputData = new byte[3];
	private int byteRead = 0;
	
	private ArrayList<Observer> obsList = new ArrayList<Observer>();
	
	private volatile boolean running = true;
	private boolean closed = false;
	
	public final static byte EOC = -128;


	public Comm(String URL) throws IOException{
		conn = new NXTConnector();
		System.out.println("Linking PC to NXT ...\n\r");
		if (!conn.connectTo(URL)) {
			System.err.println("No NXT found");
			System.exit(1);
		}
		
		System.out.println("Creating Streams...");
		inStream  = conn.getInputStream();
		for(int idx = 0; idx < inputData.length; idx++)
			inputData[idx] = 0;
		outStream = conn.getOutputStream();
		for(int idx = 0; idx < outputData.length; idx++)
		outputData[idx] = 0;
		
		if(confirmation() != true){
			throw new IOException();
		}
		System.out.println("NXT connected");
		
	}
	
	private boolean confirmation() {
		byte key[] = {0, 1, 2};
		byte receipt[] = new byte[3];
	    boolean validation = false;
	    
		try {
			outStream.write(key);
			outStream.flush();
			
	        this.inStream.read(receipt);
	        System.out.println("key: " + receipt[0] + " " + receipt[1] + " " + receipt[2]);
	        
	        if(receipt[0] != key[0] || receipt[1] != key[1] || receipt[2] != key[2]) {
	        	System.out.println("Validation failed : bad key");
	        	validation = false;
	            this.conn.close();
	        }
	        
	        else {
	        	validation = true;
	        	System.out.println("Validation completed");
	        }
	    }
		catch (Exception e){
	    	System.out.println("connection error " + e.getMessage());
		}
		
		return validation;
	}

	private void readData () throws InterruptedException, IOException{
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
			System.out.println("Commmunication closed");
		}
		catch(IOException e){
			System.out.print("closeStream IOException");
		}
	}

	
	public void run(){
		try{
			while(running){
				this.sendData();
				this.readData();
				
				if(this.byteRead == 1)
						this.close();
				
				this.updateObs();
			}
		}
		catch(InterruptedException e){
		}
		catch(IOException e){
		}
		finally{
			closeStream();
			this.closed = true;
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
	
	public boolean isClosed(){
		return this.closed;
	}
	
	public boolean isRunning(){
		return this.running;
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