package momineko.teleportor.teleportor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SneakListener implements Listener {

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Teleportor.selectTeleportor(event.getPlayer());
        }
    }
}
