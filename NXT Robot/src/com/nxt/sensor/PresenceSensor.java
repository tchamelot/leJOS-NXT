package com.nxt.sensor;

import lejos.nxt.TouchSensor;
import lejos.nxt.SensorPort;

public class PresenceSensor extends SensorAbstract{

	private TouchSensor sensor;
	
	public PresenceSensor(){
		
	}
	
	public PresenceSensor(SensorPort port){
		super(port);
		sensor = new TouchSensor(this.port);
	}
	
	@Override
	public byte read() {
		byte pressed;
		if(this.sensor.isPressed() == true)
			pressed = 1;
		else 
			pressed = 0;
		return pressed;
	}
}