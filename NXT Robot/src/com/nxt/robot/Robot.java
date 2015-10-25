package com.nxt.robot;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.LCD;

import java.io.IOException;

import com.nxt.comm.*;
import com.nxt.motor.*;
import com.nxt.sensor.*;
import com.nxt.observer.*;

public class Robot {
	private MotorAbstract motors[] = new MotorAbstract[3];
	private SensorAbstract sensor[] = new SensorAbstract[2];
	private Comm comm;
	
	private byte outputData[] = {0, 0, 0, 0, 0};
	private byte inputData[] = {0, 0, 0};
	
	public Robot(){
		boolean stop = false;
		try{
			this.comm = new Comm(Comm.USB_MODE);
			comm.addObs(new Observer(){
				public void updateData(byte[] data){
					inputData = data;
				}
			});
			comm.start();

			motors[0] = new RotationMotor(Motor.B);
			motors[1] = new TranslationMotor(Motor.A);
			motors[2] = new ClawMotor(Motor.C);
			
			sensor[0] = new PresenceSensor(SensorPort.S1);
			sensor[1] = new ColorSensor(SensorPort.S2);
			do{		
				motorsDriver();
				report();
				stop = Button.ESCAPE.isDown() || comm.isClosed();
			}while(!stop);
			//LCD.drawString("End", 0, 0);
			if(!comm.isClosed()){
				comm.end();
				comm.join();
			}
		}
		catch(NXTCommException e){
			
		} 
		catch (InterruptedException e) {
		}
	}
		
	private void report(){
		for(int idx = 0; idx < motors.length; idx++)
			outputData[idx] = motors[idx].report();
		for(int idx = 0; idx < sensor.length; idx++)
			outputData[idx + motors.length] = sensor[idx].read();
		this.comm.setData(outputData);
	}

	private void motorsDriver(){
		for(int idx = 0; idx < motors.length; idx++){
			switch(inputData[idx]){
			case 1 :
				motors[idx].forward();
				break;
			case -1 :
				motors[idx].backward();
				break;
			case 0 :
				motors[idx].stop();
				break;
			default :
				motors[idx].stop();
				break;
			}
		}
	}

	public static void main(String[] args){
		Robot robot = new Robot();
		LCD.drawString("End", 0, 1);
	}
	
	public static void test() throws IOException, InterruptedException{
		Comm comm;
		try {
			comm = new Comm(Comm.USB_MODE);
			comm.start();
			while(Button.waitForAnyEvent(0) != Button.ID_ESCAPE){
				
				}
			comm.end();
			comm.join();
		}
		catch (NXTCommException e) {
		}
	}
}