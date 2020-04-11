package de.maxi.multihomes.events;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.maxi.multihomes.Main;

public class ParticleEffects {
	private Main plugin;
	
	public ParticleEffects(Main plugin) {
		this.plugin = plugin;
	}
	
	public void teleporting(Player p) {
		new BukkitRunnable() {
			double t = 0;
			double r = 1.5;
			Location loc = p.getLocation();
			
			@Override
			public void run() {
				t += 0.5;
				for(double theta = 0; theta <= 2*Math.PI; theta += Math.PI/8) {
					double x = r*Math.cos(theta);
					double y = t/5;
					double z = r*Math.sin(theta);
					
					loc.add(x, y, z);
					loc.getWorld().spawnParticle(Particle.PORTAL, loc, 0);
					loc.subtract(x, y, z);
				}
				
				if(t > 10) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public void homeset(Player p) {
		new BukkitRunnable() {
			double t = 0;
			double r = 1.5;
			Location loc = p.getLocation();
			
			@Override
			public void run() {
				t += 0.5;
				
				for(double theta = 0; theta <= 2*Math.PI; theta += Math.PI/8) {
					double x = r*Math.cos(theta);
					double y = t/5;
					double z = r*Math.sin(theta);
					
					loc.add(x, y, z);
					loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0);
					loc.subtract(x, y, z);
				}
				
				if(t > 10) {
					cancel();
				}
			}
			
		}.runTaskTimer(plugin, 0, 1);
	}
	
	public void homedel(Player p, Location homeloc) {

		new BukkitRunnable() {
			double t = 0;
			double r = 1.5;
			Location loc = p.getLocation();
			
			@Override
			public void run() {
				t += 0.5;
				
				for(double theta = 0; theta <= 2*Math.PI; theta += Math.PI/8) {
					double x = r*Math.cos(theta);
					double y = 2.25 - t/5;
					double z = r*Math.sin(theta);
					
					loc.add(x, y, z);
					loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 0);
					loc.subtract(x, y, z);
					homeloc.add(x, y, z);
					p.spawnParticle(Particle.FIREWORKS_SPARK, homeloc, 0);
					homeloc.subtract(x, y, z);
				}
				
				if(t > 10) {
					cancel();
				}
			}
			
		}.runTaskTimer(plugin, 0, 1);
	}
}
