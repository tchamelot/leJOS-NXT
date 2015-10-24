package com.nxt.motor;

import lejos.nxt.NXTRegulatedMotor;

public class RotationMotor extends MotorAbstract{
	
	public RotationMotor(){
		
	}
	
	public RotationMotor(NXTRegulatedMotor m){
		super(m);
	}
	
	public void forward(){
		motor.forward();
	}
	
	public void backward(){
		motor.backward();
	}
	
	public byte report(){
		byte report;
		report = (byte)(motor.getTachoCount() / 168);
		return report;
	}
}
