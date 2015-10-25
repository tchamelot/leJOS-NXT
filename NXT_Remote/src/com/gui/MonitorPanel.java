package com.gui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.BorderFactory;

import java.awt.Dimension;
import java.awt.Font;

public class MonitorPanel extends JPanel{
	private JLabel[] motorLabel = new JLabel[3];
	private JLabel[] sensorLabel = new JLabel[2];
	private byte[] data = new byte[5];
	
	public MonitorPanel(){
		Box motorBox =  Box.createVerticalBox();
		for(int idx = 0; idx < motorLabel.length; idx++){
			motorLabel[idx] = new JLabel();
			motorLabel[idx].setPreferredSize(new Dimension(150, 25));
			motorLabel[idx].setHorizontalAlignment(JLabel.LEFT);
			motorLabel[idx].setFont(new Font("Fixedsys Normal", Font.BOLD, 18));
			motorBox.add(motorLabel[idx]);
			
		}
		motorBox.add(Box.createVerticalGlue());
		motorBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Box sensorBox = Box.createVerticalBox();
		for(int idx = 0; idx < sensorLabel.length; idx++){
			sensorLabel[idx] = new JLabel();
			sensorLabel[idx].setPreferredSize(new Dimension(250, 25));
			sensorLabel[idx].setHorizontalAlignment(JLabel.LEFT);
			sensorLabel[idx].setFont(new Font("Fixedsys Normal", Font.BOLD, 18));
			sensorBox.add(sensorLabel[idx]);
		}
		sensorBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Box mainBox = Box.createHorizontalBox();
		mainBox.add(motorBox);
		mainBox.add(sensorBox);
		mainBox.add(Box.createHorizontalGlue());
		mainBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(mainBox);
	}
	
	private void interpretData(){
		motorLabel[0].setText(new String("Motor 1: " + data[0] + '°'));
		
		motorLabel[1].setText(new String("Motor 2: " + data[1] + "cm"));
		
		if(data[2] == 0)
			motorLabel[2].setText(new String("Motor 3: Opened"));
		else
			motorLabel[2].setText(new String("Motor 3: Closed"));
		
		if(data[3] == 0)
			sensorLabel[0].setText(new String("Presence Sensor: None"));
		else
			sensorLabel[0].setText(new String("Presence Sensor: Detected"));
		
		if(data[4] < 35 && data[4] > 25)
			sensorLabel[1].setText(new String("Color Sensor: Blue"));
		else
			if(data[4] < 55 && data[4] > 45)
				sensorLabel[1].setText(new String("Color Sensor: Red"));
			else
				sensorLabel[1].setText(new String("Color Sensor : Undefined"));
	}

	public void setData(byte[] data){
		this.data = data;
		interpretData();
	}
}
