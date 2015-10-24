package com.nxt.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.comm.USB;
import lejos.nxt.comm.USBConnection;

public class NXTComm {
	
	protected USBConnection conn;
	protected DataInputStream dIs;
	protected DataOutputStream dOs;
		
	public NXTComm()  throws NXTCommException {
		LCD.clear();
		LCD.drawString("Connecting to PC", 0, 0);
		conn = USB.waitForConnection();
		
		if(conn == null){
			throw new NXTCommException();
		}
		
		else{
			LCD.drawString("Creating streams", 0, 1);
			dIs = conn.openDataInputStream();
			dOs = conn.openDataOutputStream();
			
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
	        this.dIs.read(receipt);
	        LCD.drawString("key: " + receipt[0] + " " + receipt[1] + " " + receipt[2], 0, 3);
	        
	        if(receipt[0] != key[0] || receipt[1] != key[1] || receipt[2] != key[2]) {
	        	validation = false;
	            this.conn.close();
	            LCD.drawString("Bad key received", 0, 4);
	        }
	        
	        else {
	        	validation = true;
	        	LCD.drawString("Key received", 0, 4);
	            dOs.write(key);
	            dOs.flush();
	            
	        }
	    }
		catch (Exception e){
	    	LCD.clear();
			LCD.drawString("connection error " + e.getMessage(), 0, 0);
		}
		
		return validation;
	}
	
	public byte[] readData(){
		byte data[] = new byte[32];
		
		try{
			this.dIs.read(data);
		} 
		catch (IOException e){
		}
		return data;
	}
		
	public void sendData(byte data[]){
		try{
			this.dOs.write(data);
			this.dOs.flush();
		} 
		catch (IOException e){
		}
	}

	public boolean available(){
		boolean test = false;
		if(conn.available() != 0)
			test = true;
		return test;
	}
}

