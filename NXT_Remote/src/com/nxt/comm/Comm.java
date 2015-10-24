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
	private byte[] inputData 	= new byte[5];
	private byte[] outputData = new byte[3];
	
	private ArrayList<Observer> obsList = new ArrayList<Observer>();


	public Comm(String URL) throws IOException{
		conn = new NXTConnector();
		System.out.println("Linking PC to NXT ...\n\r");
		if (!conn.connectTo(URL)) {
			System.err.println("No NXT found");
			System.exit(1);
		}
		
		System.out.println("Creating Streams...");
		inStream  = conn.getInputStream();
		inputData[0] = 0;
		inputData[1] = 0;
		inputData[2] = 0;
		inputData[3] = 0;
		inputData[4] = 0;
		outStream = conn.getOutputStream();
		outputData[0] = 0;
		outputData[1] = 0;
		outputData[2] = 0;
		
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

	private void readData(){

		try{
			//if(this.inStream.available() == 5)
				this.inStream.read(inputData);
		} 
		catch (IOException e){
		}
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
		while(true){
			this.sendData();
			this.readData();
			this.updateObs();
		}
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