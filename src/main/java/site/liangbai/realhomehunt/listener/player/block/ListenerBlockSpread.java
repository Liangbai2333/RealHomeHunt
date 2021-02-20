package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.SpreadFireAttribute;

@Plugin.EventSubscriber
public final class ListenerBlockSpread implements Listener {
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        Block block = event.getBlock();

        if (!ResidenceManager.isOpened(block.getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(block.getLocation());

        if (residence != null) {
            try {
                IAttributable<Boolean> igniteAttribute = residence.getAttribute(SpreadFireAttribute.class);

                if (!igniteAttribute.get() && block.getType() == Material.FIRE) {
                    event.setCancelled(true);
                }

            } catch (Throwable ignored) { }

        }
    }
}
