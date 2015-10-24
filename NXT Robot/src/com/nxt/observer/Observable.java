package com.nxt.observer;

public interface Observable {
	public void addObs(Observer obs);
	public void updateObs();
	public void removeObs();
	
}
