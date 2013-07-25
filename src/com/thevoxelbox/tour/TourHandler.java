package com.thevoxelbox.tour;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TourHandler {
	public List<Tour> tours = new ArrayList<Tour>();
	public HashMap<String, Tour> focus = new HashMap<String, Tour>();
	public HashMap<String, Tour> ongoing = new HashMap<String, Tour>();
	public HashMap<String, Location> startPos = new HashMap<String, Location>();
	
	public boolean startTour(String name, Player p) {
		for(Tour t: tours) {
			if(t.name.equalsIgnoreCase(name) && !(t.firstPoint() == null)) {
				ongoing.put(p.getName(), t);
				t.points.put(p.getName(), t.firstPoint());
				startPos.put(p.getName(), p.getLocation());
				p.sendMessage(ChatColor.AQUA + "Welcome to " + t.firstPoint().name + "!");
				sendPlayerToPoint(t.firstPoint(), p);
				return true;
			}
		}
		return false;
	}
	public boolean endTour(Player p) {
		if(ongoing.containsKey(p.getName())) {
			sendPlayerToStart(p);
			ongoing.get(p.getName()).points.remove(p.getName());
			ongoing.remove(p.getName());
			p.sendMessage(ChatColor.AQUA + "We hope you enjoyed your tour!");
			p.sendMessage(ChatColor.AQUA + "View other avaliable tours using /listtours");
			return true;
		}
		return false;
			
	}
	public void sendPlayerToStart(Player p) {
		p.teleport(startPos.get(p.getName()), TeleportCause.PLUGIN);
		startPos.remove(p.getName());
	}
	public void nextPoint(Player p) {
		if(ongoing.containsKey(p.getName())) {
			Tour tour = ongoing.get(p.getName());
			if(tour.points.containsKey(p.getName())) {
				if(tour.points.get(p.getName()).index >= tour.pointList.size()-1) {
					endTour(p);
				} else {
					TourPoint nextPoint = tour.getPoint(tour.points.get(p.getName()).index + 1);
					tour.points.put(p.getName(), nextPoint);
					p.sendMessage(ChatColor.AQUA + "Welcome to " + nextPoint.name + "!");
					sendPlayerToPoint(nextPoint, p);
					return;
				}
			} else {
				p.sendMessage(ChatColor.RED + "You have not started a tour yet!");
				ongoing.remove(p.getName());
				return;
			}
		} else {
			p.sendMessage(ChatColor.RED + "You have not started a tour yet!");
			return;
		}		
	}
	public void sendPlayerToPoint(TourPoint po, Player p) {
		p.teleport(po.loc, TeleportCause.PLUGIN);
		if(!po.message.isEmpty()) p.sendMessage(repalceColourCodes(po.message));
	}
	public String repalceColourCodes(String s) {
		return s.replace('&', '§');
	}

	public void createTour(String name, Player p) {
		Tour tour = new Tour(name);
		tours.add(tour);
		focus.put(p.getName(), tour);
	}
	public void createTour(String name) {
		Tour tour = new Tour(name);
		tours.add(tour);
	}
	public boolean deleteTour(String name) {
		for(Tour t: tours) {
			if(t.name.equalsIgnoreCase(name)) {
				tours.remove(t);
				return true;
			}
		}
		return false;
	}
	public boolean setFocus(String name, Player p) {
		for(Tour t: tours) {
			if(t.name.equalsIgnoreCase(name)) {
				focus.put(p.getName(), t);
				return true;
			}
		}
		return false;
	}
	public boolean saveTours() {
		JSONObject json = new JSONObject();
		try {
			
			JSONArray tours_json = new JSONArray();
			for(Tour t: tours) {
				JSONObject tour_json = new JSONObject();
				tour_json.put("name", t.name);
				JSONArray points_json = new JSONArray();
				for(TourPoint p: t.pointList) {
					JSONObject point_json = new JSONObject();
					point_json.put("name", p.name);
					point_json.put("message", p.message);
					point_json.put("world", p.loc.getWorld().getName());
					point_json.put("x", p.loc.getBlockX());
					point_json.put("y", p.loc.getBlockY());
					point_json.put("z", p.loc.getBlockZ());
					points_json.add(point_json);
				}
				tour_json.put("points", points_json);
				tours_json.add(tour_json);
			}
			json.put("tours", tours_json);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String out = json.toString();
		try {
			Writer fbo = null;
			File f = new File("plugins/VoxelTour/Tours.json.txt");
            if (!f.getParentFile().isDirectory()) {
            	File f0 = new File("plugins/VoxelTour/");
                f0.mkdirs();
            }
			fbo = new FileWriter(f);
			fbo.write(out);
			fbo.close();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public boolean loadTours(Server p) {
		tours.clear();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("plugins/VoxelTour/Tours.json.txt"));
			JSONObject jsonObject = (JSONObject) obj;
			
			JSONArray tours = (JSONArray) jsonObject.get("tours");
			Iterator<JSONObject> iterator = tours.iterator();
			while (iterator.hasNext()) {
				JSONObject tourObject = (JSONObject) iterator.next();
				Tour tour = new Tour((String) tourObject.get("name"));
				
				JSONArray points = (JSONArray) tourObject.get("points");
				Iterator<JSONObject> points_iterator = points.iterator();
				while (points_iterator.hasNext()) {
					JSONObject pointObject = (JSONObject) points_iterator.next();
					Location loc = new Location(p.getWorld(pointObject.get("world").toString()), Long.parseLong(pointObject.get("x").toString()),Long.parseLong(pointObject.get("y").toString()),Long.parseLong(pointObject.get("z").toString()));
					TourPoint point = new TourPoint((String) pointObject.get("name"), loc, tour.pointList.size());
					point.setMessage((String) pointObject.get("message"));
					tour.addPoint(point);
				}
				this.tours.add(tour);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
