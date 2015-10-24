package com.nxt.sensor;

import lejos.nxt.SensorPort;

public abstract class SensorAbstract {
	
	protected SensorPort port;
	
	public SensorAbstract(){
		
	}
	
	public SensorAbstract(SensorPort port){
		this.port = port;
	}
	
	abstract public byte read();
		
	}
