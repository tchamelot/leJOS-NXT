package com.nxt.robot;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;

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
	
	private Thread task;
	private boolean running = true;
	
	public Robot(){
		try{
			comm = new Comm(Comm.USB_MODE);
			comm.addObs(new Observer(){
				public void updateData(byte[] data){
					inputData = data;
				}
			});
			motors[0] = new RotationMotor(Motor.B);
			motors[1] = new TranslationMotor(Motor.A);
			motors[2] = new ClawMotor(Motor.C);
			
			sensor[0] = new PresenceSensor(SensorPort.S1);
			sensor[1] = new ColorSensor(SensorPort.S2);
			Button.ESCAPE.addButtonListener(new ButtonListener(){
				public void buttonPressed(Button b) {
					running = false;
				}
				public void buttonReleased(Button b) {
					
				}
			});
			task = new Thread(new Task());
		}
		catch(NXTCommException e){
		}
	}
		
	public void start(){
		if((comm != null) && (task != null)){
			comm.start();
			task.start();
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
	
	private class Task implements Runnable{
		 public void run(){
			while(running && (!comm.isClosed())){
				motorsDriver();
				report();
			}
			if(!comm.isClosed()){
					comm.waitForClosure();
				}
		}
	}

	public static void main(String[] args){
		Robot robot = new Robot();
		robot.start();
	}
}