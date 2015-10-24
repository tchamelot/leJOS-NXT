package com.nxt.motor;

import lejos.nxt.NXTRegulatedMotor;

public class ClawMotor extends MotorAbstract{

	
	public ClawMotor(){
		
	}
	
	public ClawMotor(NXTRegulatedMotor m){
		super(m);
	}
	
	/**
	 *Closed the Claw.
	 */
	public void forward(){
		if(motor.getTachoCount() != 50)
			motor.rotateTo(50, true);
		else
			motor.stop();
	}
	
	/**
	 *Unused function.
	 */
	public void backward(){
	}
	
	/**
	 *Open the claw. 
	 */
	public void stop(){
		if(motor.getTachoCount() != 0)
			motor.rotateTo(0, true);
		else
			motor.stop();
	}
	
	/**
	 *Return 1 if claw is closed.
	 *Return 0 if claw is opened.
	 */
	public byte report(){
		byte report = 0;//Opened(default)
		if(motor.getTachoCount() == 0)
			report = 0;//Opened
		if(motor.getTachoCount() == 50)
			report = 1;//Closed
		return report;
	}
}

