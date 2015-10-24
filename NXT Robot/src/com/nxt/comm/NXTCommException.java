package com.nxt.comm;

import lejos.nxt.LCD;
import lejos.nxt.Button;

public class NXTCommException extends Exception{
	public NXTCommException(){
		LCD.drawString("Connection fail!", 0, 7);
		while(Button.waitForAnyPress() == Button.ID_ENTER);
	}
}