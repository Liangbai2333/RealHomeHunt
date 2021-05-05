package site.liangbai.realhomehunt.listener.player;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.util.Blocks;

@Plugin.EventSubscriber
public final class ListenerPlayerClickDoor implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasPermission("rh.interact")) return;

        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block == null) return;

        if (!Blocks.isDoor(block)) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence == null) return;

        if (!residence.isAdministrator(event.getPlayer())) event.setCancelled(true);
    }
}
