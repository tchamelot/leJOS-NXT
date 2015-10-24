package com.nxt.sensor;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

public class ColorSensor extends SensorAbstract{
	
	private LightSensor sensor;
	
	public ColorSensor(){
		
	}
	
	public ColorSensor(SensorPort port){
		super(port);
		sensor = new LightSensor(this.port);
	}
	
	@Override
	public byte read() {
		return (byte)this.sensor.readValue();
	}

}
