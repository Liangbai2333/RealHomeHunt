package site.liangbai.realhomehunt.listener.player;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.util.PlayerMoveUtil;

@Plugin.EventSubscriber
public final class ListenerPlayerMove implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location loc1 = event.getFrom();

        Location loc2 = event.getTo();

        if (loc2 == null) return;

        if (loc1.getX() != loc2.getX() || loc1.getZ() != loc2.getZ()) {
            PlayerMoveUtil.push(event.getPlayer());
        }
    }
}
