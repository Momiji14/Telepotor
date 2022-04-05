package momineko.teleportor.teleportor;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Teleportor {
    private static final HashMap<String, Teleportor> map = new HashMap<>();

    public static Teleportor get(String id) {
        return map.get(id);
    }

    public static void delete(String id) {
        map.remove(id);
    }

    public static Teleportor create(String id) {
        Teleportor teleportor = new Teleportor(id);
        map.put(id, teleportor);
        return teleportor;
    }

    public static void selectTeleportor(Player player) {
        for (Teleportor teleportor : map.values()) {
            if (teleportor.getLocation().distance(player.getLocation()) < teleportor.getRadius()) {
                Location targetLocation = teleportor.getTargetLocation();
                if (targetLocation != null) {
                    player.teleportAsync(targetLocation);
                    player.playSound(targetLocation, teleportor.getSound(), 1, 1);
                    return;
                } else {
                    player.sendMessage("§c転移先が設定されていません §e[" + teleportor);
                }
            }
        }
    }

    public static void stopAll() {
        for (Teleportor teleportor : map.values()) {
            teleportor.stop();
        }
    }

    private final String id;
    private Location location;
    private Location targetLocation;
    private Particle particle = Particle.SPELL_WITCH;
    private Sound sound = Sound.ENTITY_PLAYER_LEVELUP;
    private double radius = 2;
    private BukkitTask task;

    private Teleportor(String id) {
        this.id = id;
    }

    public Teleportor setLocation(Location location) {
        this.location = location;
        return this;
    }

    public Teleportor setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
        return this;
    }

    public Teleportor setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public Teleportor setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public Teleportor setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public Location getLocation() {
        return location;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Sound getSound() {
        return sound;
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    public String getId() {
        return id;
    }

    public void run() {
        task = new BukkitRunnable() {
            int tick = 0;
            final double increment = (2 * Math.PI) / 90;
            final World world = location.getWorld();
            @Override
            public void run() {
                for (int i = 0; i < 4; i++) {
                    double angle = tick * increment;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location nLoc = new Location(world, location.getX() + x, location.getY(), location.getZ() + z);
                    Location nLoc2 = new Location(world, location.getX() - x, location.getY(), location.getZ() - z);
                    world.spawnParticle(particle, nLoc, 0, 0, 0.2, 0, 0);
                    world.spawnParticle(particle, nLoc2, 0, 0, 0.2, 0, 0);
                    tick++;
                }
            }
        }.runTaskTimerAsynchronously(main.plugin, 0, 1);
    }
}
