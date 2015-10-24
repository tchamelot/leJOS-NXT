package com.nxt.motor;


import lejos.nxt.NXTRegulatedMotor;

public abstract class MotorAbstract {
	protected NXTRegulatedMotor motor;
	
	public MotorAbstract(){
	}
	
	protected MotorAbstract(NXTRegulatedMotor m){
		motor = m;
		motor.resetTachoCount();
	}
		
	public void stop(){
		motor.stop();
	}
	
	public abstract void forward();
	
	public abstract void backward();
	
	public abstract byte report();
}
