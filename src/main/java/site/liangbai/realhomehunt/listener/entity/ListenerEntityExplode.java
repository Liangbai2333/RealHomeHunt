package site.liangbai.realhomehunt.listener.entity;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.impl.ExplodeAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Plugin.EventSubscriber
public final class ListenerEntityExplode implements Listener {
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!ResidenceManager.isOpened(event.getEntity().getWorld())) return;

        List<Block> blocks = event.blockList();

        blocks.stream()
                .filter(it -> {
                    if (it == null) return false;

                    if (it.getType().isAir()) return false;

                    Residence residence = ResidenceManager.getResidenceByLocation(it.getLocation());

                    try {
                        return residence != null && !residence.getAttribute(ExplodeAttribute.class).get();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();

                        return false;
                    }
                })
                .collect(Collectors.toSet())
                .forEach(blocks::remove);
    }
}
