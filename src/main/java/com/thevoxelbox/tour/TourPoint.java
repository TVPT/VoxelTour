package com.thevoxelbox.tour;

import org.bukkit.Location;

public class TourPoint {
	public Location loc;
	public int index;
	public String name;
	public String message;
	
	public TourPoint(String name, Location l, int i) {
		this.name = name;
		this.loc = l;
		this.index = i;
		
		message = "";
	}
	public void setMessage(String message) {
		this.message = message;
	}

}
