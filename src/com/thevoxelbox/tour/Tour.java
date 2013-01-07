package com.thevoxelbox.tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

public class Tour {
	public String name;
	public List<TourPoint> pointList = new ArrayList<TourPoint>();
	public HashMap<String, TourPoint> points = new HashMap<String, TourPoint>();
	
	public Tour(String name) {
		this.name = name;
	}
	public void addPoint(String name, Location loc) {
		TourPoint point = new TourPoint(name, loc, pointList.size());
		pointList.add(point);
	}
	public void addPoint(TourPoint point) {
		pointList.add(point);
		
	}
	public TourPoint getPoint(int index) {
		return pointList.get(index);
	}
	public TourPoint firstPoint() {
		if(pointList.size() > 0) {
			return pointList.get(0);
		}
		return null;
	}
	public boolean removePoint(String name) {
		for(TourPoint p: pointList) {
			if(p.name.equalsIgnoreCase(name)) {
				pointList.remove(p);
				return true;
			}
		}
		return false;
	}
}
