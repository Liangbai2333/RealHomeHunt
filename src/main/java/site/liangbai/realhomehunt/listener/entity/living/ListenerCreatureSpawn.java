package site.liangbai.realhomehunt.listener.entity.living;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.AnimalsAttribute;
import site.liangbai.realhomehunt.residence.attribute.impl.CreatureAttribute;
import site.liangbai.realhomehunt.residence.attribute.impl.MonsterAttribute;

@Plugin.EventSubscriber
public final class ListenerCreatureSpawn implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        LivingEntity entity = event.getEntity();

        Location spawnLocation = event.getLocation();

        Residence residence = ResidenceManager.getResidenceByLocation(spawnLocation);

        if (residence != null) {
            try {
                IAttributable<Boolean> creatureAttribute = residence.getAttribute(CreatureAttribute.class);

                if (!creatureAttribute.get()) {
                    event.setCancelled(true);

                    return;
                }

                if (entity instanceof Monster) {
                    IAttributable<Boolean> monsterAttribute = residence.getAttribute(MonsterAttribute.class);

                    if (!monsterAttribute.get()) {
                        event.setCancelled(true);

                        return;
                    }
                }

                if (entity instanceof Animals) {
                    IAttributable<Boolean> animalsAttribute = residence.getAttribute(AnimalsAttribute.class);

                    if (!animalsAttribute.get()) {
                        event.setCancelled(true);
                    }
                }
            } catch (Throwable ignored) { }
        }
    }
}
