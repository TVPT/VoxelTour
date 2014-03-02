package com.thevoxelbox.tour;

import java.io.File;
import java.util.logging.Logger;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class VoxelTour extends JavaPlugin {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static Server s;
    public static HashSet<File> plugins = new HashSet<File>();
    public TourHandler tourHandler = new TourHandler();

    @Override
    public void onDisable() {
    	if(tourHandler.saveTours()) {
        	log.info("[VoxelTours] Tours saved.");
        } else {
        	log.info("[VoxelTours] Tours failed to save; you're fucked.");
        }
    }

    @Override
    public void onEnable() {
        s = getServer();
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled! Tour away!.");
        if(tourHandler.loadTours(s)) {
        	log.info("[VoxelTours] Tours loaded.");
        } else {
        	log.info("[VoxelTours] Tours failed to load.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	String commandName = command.getName().toLowerCase();
    	if(commandName.equals("voxeltour")) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(args.length == 0) {
    				p.sendMessage(ChatColor.AQUA + "VoxelTour is working!");
	    			return true;
    			} else {
    				if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("help")) {
    					p.sendMessage(ChatColor.DARK_AQUA + "Voxel Tours! An automated touring system from The Voxel Box!");
    					p.sendMessage(ChatColor.AQUA + "/listtours " + ChatColor.DARK_GRAY + "-- Provides a list of all avaliable tours!");
    					p.sendMessage(ChatColor.AQUA + "/starttour <tour name> " + ChatColor.DARK_GRAY + "-- Starts you on your tour!");
    					p.sendMessage(ChatColor.AQUA + "/nextlocation " + ChatColor.DARK_GRAY + "-- Takes you to the next location in your tour!");
    					p.sendMessage(ChatColor.AQUA + "/endtour " + ChatColor.DARK_GRAY + "-- Ends your tour and takes you back to where you started!");
    					p.sendMessage(ChatColor.AQUA + "/listpoints " + ChatColor.DARK_GRAY + "-- Lists the points in your seletced tour!");
    					if(p.hasPermission("voxeltour.admin")) {
    						p.sendMessage(ChatColor.AQUA + "/createtour <tour name> " + ChatColor.DARK_GRAY + "-- Creates a new tour");
    						p.sendMessage(ChatColor.AQUA + "/deletetour <tour name> " + ChatColor.DARK_GRAY + "-- Deletes a new tour");
    						p.sendMessage(ChatColor.AQUA + "/setfocus <tour name> " + ChatColor.DARK_GRAY + "-- Places a tour into your working slot");
    						p.sendMessage(ChatColor.AQUA + "/addpoint <point name> " + ChatColor.DARK_GRAY + "-- Creates a Tour Point at your current location");
    						p.sendMessage(ChatColor.AQUA + "/setmessage <message> " + ChatColor.DARK_GRAY + "-- Adds a message to your last created point");
    						p.sendMessage(ChatColor.AQUA + "/setmessagebyindex <index> <message> " + ChatColor.DARK_GRAY + "-- Adds a message to the specified point (by numerical index see /listpoints)");
    						p.sendMessage(ChatColor.AQUA + "/deletepoint <point name> " + ChatColor.DARK_GRAY + "-- Deletes the specified point");
    						p.sendMessage(ChatColor.AQUA + "/savetours " + ChatColor.DARK_GRAY + "-- It is recommended you use this after creating a tour incase of server crashes");
    						p.sendMessage(ChatColor.AQUA + "/loadtours " + ChatColor.DARK_GRAY + "-- Reloads the Tour file. " + ChatColor.DARK_RED + "WARNING! Deletes all unsaved tours!");
    					}
    				}
    			}
    			return true;
	    			
    		}
    	}
    	if(commandName.equals("starttour") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(tourHandler.startTour(args[0], p)) {
    			} else {
    				p.sendMessage(ChatColor.AQUA + "Cannot find the tour specified!");
    			}
    			return true;
    		}
    	}
    	if(commandName.equals("endtour")) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(tourHandler.endTour(p)) {
    				p.sendMessage(ChatColor.AQUA + "Sending you back to where you started!");
    			} else {
    				p.sendMessage(ChatColor.AQUA + "You are not currently in a tour.");
    			}
    			return true;
    		}
    	}
    	if(commandName.equals("nextlocation") || commandName.equals("nextloc") || commandName.equals("nextpoint")) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			tourHandler.nextPoint(p);
    			return true;
    		}
    	}
    	if(commandName.equals("createtour") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				tourHandler.createTour(args[0], p);
	    			p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " created!");
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    			}
    		}
    	}
    	if(commandName.equals("setfocus") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				if(tourHandler.setFocus(args[0], p)) {
	    				p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " selected!");
	    			} else {
	    				p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " doesn't exist!");
	    			}
	    			
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    			}
    		}
    	}
    	
    	if(commandName.equals("deletetour") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    	    		if(tourHandler.deleteTour(args[0])) {
	    				p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " deleted!");
	    			} else {
	    				p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " doesn't exist!");
	    			}
	    			
	    			return true;
    	    	} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    			}
	    			
    		}
    	}
    	if(commandName.equals("addpoint") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    	    		if(!(tourHandler.focus.get(p.getName()) == null)) {
	    				tourHandler.focus.get(p.getName()).addPoint(args[0], p.getLocation());
	    				p.sendMessage(ChatColor.AQUA + "Point " + args[0] + " created!");
	    				return true;
	    			}
	    			p.sendMessage(ChatColor.AQUA + "No tour focus");
	    			return true;
    	    	} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    			}
	    			
    		}
    	}
    	if(commandName.equals("deletepoint") && args.length == 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				if(!(tourHandler.focus.get(p.getName()) == null)) {
		    			if(tourHandler.focus.get(p.getName()).removePoint(args[0])) {
		    				p.sendMessage(ChatColor.AQUA + "Point " + args[0] + " deleted!");
		    			} else {
		    				p.sendMessage(ChatColor.AQUA + "Tour " + args[0] + " doesn't exist!");
		    			}
	    			}
	    			p.sendMessage(ChatColor.AQUA + "No tour focus");
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    				return true;
    			}
	    			
    		}
    	}
    	if(commandName.equals("setmessage") && args.length > 0) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				String _args = "";
	    			for(String s: args) {
	    				_args += s;
	    				if(!args[args.length-1].equals(s)) {
	    					_args += " ";
	    				}
	    			}
	    			
	    			if(!(tourHandler.focus.get(p.getName()) == null)) {
						if(!(tourHandler.focus.get(p.getName()).pointList.isEmpty())) {
							tourHandler.focus.get(p.getName()).pointList.get(tourHandler.focus.get(p.getName()).pointList.size()-1).setMessage(_args);
						}
	    			}
	    			p.sendMessage(ChatColor.AQUA + "Message added!");
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    				return true;
    			}
    			
    		}
    	}
    	if(commandName.equals("setmessagebyindex") && args.length > 1) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				int index = 0;
	    			if(!(tourHandler.focus.get(p.getName()) == null)) {
						if(!(tourHandler.focus.get(p.getName()).pointList.isEmpty())) {
		    				try {
		    					index = Integer.parseInt(args[0]);
		    				} catch(Exception e) {
		    					p.sendMessage(ChatColor.RED + "Correct usage: /addMessageByIndex <Numerical Index> <message>");
		    					p.sendMessage(ChatColor.RED + "Use /listpoints to see the numerical indices of points");
		    					return true;
		    				}
		    				String _args = "";
		        			for(String s: args) {
		        				_args += s;
		        				if(!args[args.length-1].equals(s)) {
		        					_args += " ";
		        				}
		        			}
		        			
		    				_args = _args.substring(args[0].length() + 1);
		    				tourHandler.focus.get(p.getName()).pointList.get(index).setMessage(_args);
		    				p.sendMessage(ChatColor.AQUA + "Message added to index: " + index + "!");
		    				return true;
						}
	    			}
	    			p.sendMessage(ChatColor.AQUA + "No tour focus");
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    				return true;
    			}
	    			
    		}
    	}
    	if(commandName.equals("listtours")) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			p.sendMessage(ChatColor.DARK_GRAY + "Tour list: (Count: " + tourHandler.tours.size() + ")");
    			int i = 0;
    			for(Tour t: tourHandler.tours) {
    				p.sendMessage(ChatColor.DARK_GRAY + "" + i + ") " + ChatColor.AQUA + t.name + ChatColor.BLUE + " (Points: " + t.pointList.size() + ")");
    				i++;
    			}
    			return true;
    		}
    	}
    	if(commandName.equals("listpoints")) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(!(tourHandler.focus.get(p.getName()) == null)) {
    				p.sendMessage(ChatColor.DARK_GRAY + "Point list: (Count: " + tourHandler.focus.get(p.getName()).pointList.size() + ")");
	    			int i = 0;
	    			for(TourPoint po: tourHandler.focus.get(p.getName()).pointList) {
	    				p.sendMessage(ChatColor.DARK_GRAY + "" + i + ") " + ChatColor.AQUA + po.name + ChatColor.BLUE + " " + po.loc.getBlockX() + " " + po.loc.getBlockY() + " " + po.loc.getBlockZ());
	    				i++;
	    			}
	    			return true;
    			}
    			if(!(tourHandler.ongoing.get(p.getName()) == null)) {
    				p.sendMessage(ChatColor.DARK_GRAY + "Point list: (Count: " + tourHandler.ongoing.get(p.getName()).pointList.size() + ")");
	    			int i = 0;
	    			for(TourPoint po: tourHandler.ongoing.get(p.getName()).pointList) {
	    				p.sendMessage(ChatColor.DARK_GRAY + "" + i + ") " + ChatColor.AQUA + po.name + ChatColor.BLUE + " " + po.loc.getBlockX() + " " + po.loc.getBlockY() + " " + po.loc.getBlockZ());
	    				i++;
	    			}
	    			return true;
    			}
	    			
    			p.sendMessage(ChatColor.AQUA + "No tour selected");
    			return true;
    		}
    	}
    	
    	if(commandName.equals("savetours") && args.length == 0) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				if(tourHandler.saveTours()) {
	    				p.sendMessage(ChatColor.AQUA + "Save successful!");
	    			} else {
	    				p.sendMessage(ChatColor.DARK_RED + "Saving failed, you're boned.");
	    			}
	    			
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    				return true;
    			}
	    			
    		}
    	}
    	if(commandName.equals("loadtours") && args.length == 0) {
    		if(sender instanceof Player) {
    			Player p = (Player) sender;
    			if(p.hasPermission("voxeltour.admin")) {
    				if(tourHandler.loadTours(s)) {
	    				p.sendMessage(ChatColor.AQUA + "Load successful!");
	    			} else {
	    				p.sendMessage(ChatColor.DARK_RED + "Loading failed, you're boned.");
	    			}
	    			return true;
    			} else {
    				p.sendMessage(ChatColor.RED + "Insufficient Permissions (Must be opped)");
    				return true;
    			}
	    			
    		}
    	}
        return false;
    }
    
}
