package me.zabzabdoda.SleepDemocracy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.scheduler.BukkitRunnable;

public class SkipNight extends BukkitRunnable {

	private int count = 0;
	private World world = null;
	private boolean override;
	public static int NIGHT = 12542;
	
	public SkipNight(boolean override) {
		this.override = override;
	}
	
	@Override
	public void run() {
		if(count == 0) {
			for(World w : Bukkit.getWorlds()) {
				if(w.getEnvironment().equals(Environment.NORMAL)) {
					world = w;
				}
			}
		}
		if(world.getTime() < 12542) {
			cancel();
			world.setStorm(false);
			SleepDemocracy.setInBed(0);
			SleepDemocracy.sendPlayerTitle(SleepDemocracy.tag+"Good Morning!",20);
		}else {	
			world.setTime(world.getTime()+100);
		}
	}

}
