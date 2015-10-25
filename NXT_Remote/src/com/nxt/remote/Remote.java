package com.nxt.remote;

import java.io.IOException;

import com.gui.Window;
import com.nxt.comm.*;
 

public class Remote {

	public static void main(String[] args) {
		Window window = new Window();
		
	}
	
	public void test(){
		Comm comm;
		try{
			comm = new Comm("usb://");
			comm.start();
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}
