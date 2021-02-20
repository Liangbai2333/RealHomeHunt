package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.IgniteAttribute;

@Plugin.EventSubscriber
public final class ListenerBlockIgnite implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (!ResidenceManager.isOpened(block.getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence != null) {
            try {
                IAttributable<Boolean> igniteAttribute = residence.getAttribute(IgniteAttribute.class);

                if (!igniteAttribute.get() && block.getType() == Material.FIRE) {
                    event.setCancelled(true);
                }

            } catch (Throwable ignored) { }

        }
    }
}
