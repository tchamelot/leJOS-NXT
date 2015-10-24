package com.nxt.motor;

import lejos.nxt.NXTRegulatedMotor;

public class TranslationMotor extends MotorAbstract{

	
	public TranslationMotor() {
		
	}
	
	public TranslationMotor(NXTRegulatedMotor m) {
		super(m);
	}
	
	public void forward() {
		motor.backward();
	}

	public void backward() {
		motor.forward();
	}

	
	public byte report() {
		byte report;
		report = (byte)((motor.getTachoCount() / 72));
		return report;
	}

}
