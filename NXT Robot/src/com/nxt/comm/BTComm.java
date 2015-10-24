package com.nxt.comm;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

public class BTComm extends CommAbstract{
	
	public BTComm()  throws NXTCommException {
		super((NXTConnection)(Bluetooth.waitForConnection(30000, NXTConnection.PACKET)));
		
	}
}