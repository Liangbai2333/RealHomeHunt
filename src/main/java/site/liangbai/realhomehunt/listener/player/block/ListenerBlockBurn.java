package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.BurnAttribute;

@Plugin.EventSubscriber
public final class ListenerBlockBurn implements Listener {
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();

        if (!ResidenceManager.isOpened(block.getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence != null) {
            try {
                IAttributable<Boolean> burnAttribute = residence.getAttribute(BurnAttribute.class);

                if (!burnAttribute.get()) {
                    event.setCancelled(true);
                }

            } catch (Throwable ignored) { }

        }
    }
}
