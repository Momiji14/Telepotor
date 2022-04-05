package momineko.teleportor.teleportor;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class main extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        reload();

        Bukkit.getPluginManager().registerEvents(new SneakListener(), plugin);
        Bukkit.getPluginCommand("teleportor").setExecutor(new CommandTeleportor());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        Teleportor.stopAll();
        File dir = new File(getDataFolder(), "Teleports/");
        if (!dir.exists()) {
            try {
                dir.mkdirs();
                File example = new File(getDataFolder(), "Teleports/Example.yml");
                if (!example.exists()) example.createNewFile();
                FileConfiguration data = YamlConfiguration.loadConfiguration(example);
                data.set("Location.w", "world");
                data.set("Location.x", 0d);
                data.set("Location.y", 64d);
                data.set("Location.z", 0d);
                data.set("Location.yaw", 0f);
                data.set("Location.pitch", 0f);
                data.set("TargetLocation.w", "world");
                data.set("TargetLocation.x", 64d);
                data.set("TargetLocation.y", 64d);
                data.set("TargetLocation.z", 64d);
                data.set("TargetLocation.yaw", 180f);
                data.set("TargetLocation.pitch", 0f);
                data.set("Radius", 2);
                data.set("Particle", String.valueOf(Particle.SPELL_WITCH));
                data.set("Sound", String.valueOf(Sound.ENTITY_PLAYER_LEVELUP));
                data.save(example);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (File file : dumpFile(dir)) {
            try {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                String id = file.getName().replace(".yml", "");
                World world = Bukkit.getWorld(data.getString("Location.w", "world"));
                double x = data.getDouble("Location.x");
                double y = data.getDouble("Location.y");
                double z = data.getDouble("Location.z");
                float yaw = (float) data.getDouble("Location.yaw");
                float pitch = (float) data.getDouble("Location.pitch");
                Location location = new Location(world, x, y, z, yaw, pitch);
                World world2 = Bukkit.getWorld(data.getString("TargetLocation.w", "world"));
                double x2 = data.getDouble("TargetLocation.x");
                double y2 = data.getDouble("TargetLocation.y");
                double z2 = data.getDouble("TargetLocation.z");
                float yaw2 = (float) data.getDouble("TargetLocation.yaw");
                float pitch2 = (float) data.getDouble("TargetLocation.pitch");
                Location location2 = new Location(world2, x2, y2, z2, yaw2, pitch2);
                double radius = data.getDouble("Radius", 2);
                Particle particle = Particle.valueOf(data.getString("Particle", String.valueOf(Particle.SPELL_WITCH)));
                Sound sound = Sound.valueOf(data.getString("Sound", String.valueOf(Sound.ENTITY_PLAYER_LEVELUP)));
                Teleportor teleportor = Teleportor.create(id);
                teleportor.setLocation(location).setTargetLocation(location2);
                teleportor.setParticle(particle).setRadius(radius).setSound(sound);
                teleportor.run();
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().warning(file.getName() + "のロード中にエラーが発生しました");
            }
        }
    }

    public static List<File> dumpFile(File file) {
        List<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File tmpFile : files) {
            if (!tmpFile.getName().equals(".sync")) {
                if (tmpFile.isDirectory()) {
                    list.addAll(dumpFile(tmpFile));
                } else {
                    list.add(tmpFile);
                }
            }
        }
        return list;
    }

}
