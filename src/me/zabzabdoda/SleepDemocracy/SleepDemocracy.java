package me.zabzabdoda.SleepDemocracy;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SleepDemocracy extends JavaPlugin implements Listener{
	
	private static int playerCount;
	private static double percent;
	private static int inBed;
	private SkipNight sn;
	public static String tag =ChatColor.GREEN+"["+ ChatColor.DARK_GREEN+"SleepDemocracy"+ChatColor.GREEN + "] ";
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) this);
		System.out.println(tag+"Enabling SleepDemocracy...");
		playerCount = Bukkit.getServer().getOnlinePlayers().size();
		File file = new File(this.getDataFolder() + File.separator + "config.yml");

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
				Properties p = new Properties();
				FileWriter fw = new FileWriter(file);
				p.put("Players-Needed-Percent", "0.5");
				p.store(fw, "You can edit this file, make sure Players-Needed-Percent \nis a number between 0 and 1");
				percent = 0.5;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			
			try {
				Properties p = new Properties();
				FileReader fr = new FileReader(file);
				p.load(fr);
				percent = Double.parseDouble((String) p.get("Players-Needed-Percent"));
			} catch (IOException e) {
				System.out.println(tag+"Could not find or read file, setting players needed to 0.5");
				percent = 0.5;
			} catch (ClassCastException e) {
				System.out.println(tag+"config.yml is corrupt delete it and reload, setting players needed to 0.5");
				percent = 0.5;
			} catch (Exception e) {
				System.out.println(tag+"Something went wrong reading the file, setting players needed to 0.5");
				percent = 0.5;
			}
		}
		
	}
	
	public void onDisable() {
		System.out.println(tag+"disabling SleepDemocracy...");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("sleep")) {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("skip")) {
						sn = new SkipNight(true);
						sn.runTaskTimer(this, 0, 1);
					}
				}
				else if(args.length == 2) {
					if(args[0].equalsIgnoreCase("add")) {
						if(args[1].equalsIgnoreCase("player")) {
							playerCount++;
							player.sendMessage("Added Player, total: " + playerCount);
						}else if(args[1].equalsIgnoreCase("bed")) {
							inBed++;
							player.sendMessage("Added Bed, total: " + inBed);
						}
					}else if(args[0].equalsIgnoreCase("subtract")) {
						if(args[1].equalsIgnoreCase("player")) {
							playerCount--;
							player.sendMessage("Subtracted Player, total: " + playerCount);
						}else if(args[1].equalsIgnoreCase("bed")) {
							inBed--;
							player.sendMessage("Subtracted Bed, total: " + inBed);
						}
					}
				}
			}
		}
		return true;
	}
	
	public int getMaxPlayersNeeded() {
		double num = (playerCount * percent);
		num = Math.round(num);
		return (int) num;
	}
	
	@EventHandler
	public void sleep(PlayerBedEnterEvent e) {
		if(e.getBedEnterResult().equals(BedEnterResult.OK)) {
			Bukkit.getLogger().log(e.getPlayer().getDisplayName() + " has entered a bed");
			inBed++;
			sendPlayerTitle(ChatColor.GREEN + tag + inBed + " players sleeping out of the needed " + getMaxPlayersNeeded(), 10000);
			if(inBed >= getMaxPlayersNeeded()) {
				//skip night
				System.out.println("At Max needed");
				sendPlayerTitle(tag+ "Skipping night...", 10000);
				//STUFF
				sn = new SkipNight(false);
				sn.runTaskTimer(this, 0, 1);
			}
		}
	}
	
	@EventHandler
	public void leaveBed(PlayerBedLeaveEvent e) {
		if(e.getPlayer().getWorld().getTime() >= SkipNight.NIGHT && e.getPlayer().getWorld().getTime() < 23400) {
		inBed--;
		if(inBed < 0) {
			inBed = 0;
		}
		System.out.println(e.getPlayer().getWorld().getTime());
		sendPlayerTitle(tag + inBed + " players sleeping out of the needed " + getMaxPlayersNeeded(), 10000);
		if(inBed < getMaxPlayersNeeded()) {
			sn.cancel();
		}
			System.out.println(e.getPlayer().getDisplayName() + " has left a bed");
		}

	}
	
	public static void sendPlayerTitle(String subtitle, int stay) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(subtitle);
		}
		//ActionBarAPI.sendActionBarToAllPlayers(subtitle, stay);
	}
	
	public static void setInBed(int i) {
		inBed = i;
	}
	
	@EventHandler
	public void timeSkip(TimeSkipEvent e) {
		if(e.getSkipReason().equals(TimeSkipEvent.SkipReason.NIGHT_SKIP)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void login(PlayerJoinEvent e) {
		playerCount++;
	}
	
	@EventHandler
	public void logout(PlayerQuitEvent e) {
		playerCount--;
	}
	
	
	
}
