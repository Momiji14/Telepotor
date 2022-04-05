package momineko.teleportor.teleportor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CommandTeleportor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            help(sender);
        } else if (args.length >= 2) {
            String id = args[1];
            if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
                File file = getFile(id);
                if (file.exists()) {
                    sender.sendMessage("§aすでに§e[" + id + "]§aは存在しています");
                } else {
                    try {
                        file.createNewFile();
                        sender.sendMessage("§e[" + id + "]§aを作成しました");
                        sender.sendMessage("§a各種設定を行った後§e[run]§aしてください");
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage("§cファイルの作成に失敗しました");
                        sender.sendMessage("§cConsoleを確認してください");
                    }
                }
                return true;
            }
            Teleportor teleportor = Teleportor.get(id);
            if (teleportor == null) {
                sender.sendMessage("§e[" + id + "]§aは存在しません");
                return true;
            }
            File file = getFile(id);
            if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
                teleportor.stop();
                Teleportor.delete(id);
                file.delete();
                sender.sendMessage("§e[" + id + "]§aを削除しました");
                return true;
            }
            FileConfiguration data = getData(file);
            if (args[0].equalsIgnoreCase("setLocation")) {
                try {
                    Location location = null;
                    if (args.length == 2 && sender instanceof Player player) {
                        location = player.getLocation().clone();
                    } else if (args.length == 9) {
                        String world = args[3];
                        double x = Double.parseDouble(args[4]);
                        double y = Double.parseDouble(args[5]);
                        double z = Double.parseDouble(args[6]);
                        float yaw = Float.parseFloat(args[7]);
                        float pitch = Float.parseFloat(args[8]);
                        location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                    }
                    if (location != null) {
                        data.set("Location.w", location.getWorld().getName());
                        data.set("Location.x", location.getX());
                        data.set("Location.y", location.getY());
                        data.set("Location.z", location.getZ());
                        data.set("Location.yaw", location.getYaw());
                        data.set("Location.pitch", location.getPitch());
                        teleportor.setLocation(location);
                        sender.sendMessage("§e[" + id + "]§aの§b座標§aを設定しました");
                    }
                } catch (Exception e) {
                    helpSetLocation(sender);
                }
            } else if (args[0].equalsIgnoreCase("setTargetLocation")) {
                try {
                    Location location = null;
                    if (args.length == 2 && sender instanceof Player player) {
                        location = player.getLocation().clone();
                    } else if (args.length == 9) {
                        String world = args[3];
                        double x = Double.parseDouble(args[4]);
                        double y = Double.parseDouble(args[5]);
                        double z = Double.parseDouble(args[6]);
                        float yaw = Float.parseFloat(args[7]);
                        float pitch = Float.parseFloat(args[8]);
                        location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                    }
                    if (location != null) {
                        data.set("TargetLocation.w", location.getWorld().getName());
                        data.set("TargetLocation.x", location.getX());
                        data.set("TargetLocation.y", location.getY());
                        data.set("TargetLocation.z", location.getZ());
                        data.set("TargetLocation.yaw", location.getYaw());
                        data.set("TargetLocation.pitch", location.getPitch());
                        teleportor.setTargetLocation(location);
                        sender.sendMessage("§e[" + id + "]§aの§b転移先§aを設定しました");
                    }
                } catch (Exception e) {
                    helpSetLocation(sender);
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("setParticle")) {
                try {
                    Particle particle = Particle.valueOf(args[2]);
                    data.set("Particle", particle.toString());
                    teleportor.setParticle(particle);
                    sender.sendMessage("§e[" + id + "]§aの§bパーティクル§aを設定しました");
                } catch (Exception e) {
                    helpSetParticle(sender);
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("setRadius")) {
                try {
                    double radius = Double.parseDouble(args[2]);
                    data.set("Radius", radius);
                    teleportor.setRadius(radius);
                    sender.sendMessage("§e[" + id + "]§aの§b半径§aを設定しました");
                } catch (Exception e) {
                    helpSetRadius(sender);
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("setSound")) {
                try {
                    Sound sound = Sound.valueOf(args[2]);
                    data.set("Sound", sound.toString());
                    teleportor.setSound(sound);
                    sender.sendMessage("§e[" + id + "]§aの§bサウンド§aを設定しました");
                } catch (Exception e) {
                    helpSetParticle(sender);
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("run")) {
                teleportor.run();
                sender.sendMessage("§e[" + id + "]§aを起動しました");
            } else if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
                teleportor.stop();
                sender.sendMessage("§e[" + id + "]§aを停止しました");
            }
            try {
                data.save(file);
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage("§cファイルの書き込みに失敗しました");
                sender.sendMessage("§cConsoleを確認してください");
            }
        }
        return true;
    }

    public File getFile(String id) {
        return new File(main.plugin.getDataFolder(), "Teleports/" + id + ".yml");
    }

    public FileConfiguration getData(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public void help(CommandSender sender) {
        helpCreate(sender);
        helpDelete(sender);
        helpSetLocation(sender);
        helpSetTargetLocation(sender);
        helpSetParticle(sender);
        helpSetRadius(sender);
        helpSetSound(sender);
        helpRun(sender);
        helpStop(sender);
    }

    public void helpCreate(CommandSender sender) {
        sender.sendMessage("§e/telepotor create <id> §a転移ゲートを作成します");
    }

    public void helpDelete(CommandSender sender) {
        sender.sendMessage("§e/telepotor delete <id> §a転移ゲートを削除します");
    }

    public void helpSetLocation(CommandSender sender) {
        sender.sendMessage("§e/telepotor setLocation <id> [<world> <x> <y> <z> <yaw> <pitch>] §a転移ゲートの座標を設定します");
    }

    public void helpSetTargetLocation(CommandSender sender) {
        sender.sendMessage("§e/telepotor setTargetLocation <id> [<world> <x> <y> <z> <yaw> <pitch>] §a転移ゲートの転移先を設定します");
    }

    public void helpSetParticle(CommandSender sender) {
        sender.sendMessage("§e/telepotor setParticle <id> <particle> §a転移ゲートのパーティクルを設定します");
    }

    public void helpSetRadius(CommandSender sender) {
        sender.sendMessage("§e/telepotor setRadius <id> <radius> §a転移ゲートの半径を設定します");
    }

    public void helpSetSound(CommandSender sender) {
        sender.sendMessage("§e/telepotor setSound <id> <sound> §a転移ゲートのサウンドを設定します");
    }

    public void helpRun(CommandSender sender) {
        sender.sendMessage("§e/telepotor run <id> §a転移ゲートの有効にします");
    }
    public void helpStop(CommandSender sender) {
        sender.sendMessage("§e/telepotor stop <id> §a転移ゲートの無効にします");
    }

}
