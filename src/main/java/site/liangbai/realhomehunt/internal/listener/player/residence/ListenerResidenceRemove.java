package site.liangbai.realhomehunt.internal.listener.player.residence;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.event.residence.ResidenceRemoveEvent;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.internal.command.subcommand.impl.ShowCommand;

@EventSubscriber
public class ListenerResidenceRemove implements Listener {
    @EventHandler
    public void onResidenceRemove(ResidenceRemoveEvent event) {
        String owner = event.getResidence().getOwner();

        if (ShowCommand.SHOW_CACHES.containsKey(owner)) {
            EffectGroup effectGroup = ShowCommand.SHOW_CACHES.get(owner);

            effectGroup.turnOff();

            ShowCommand.SHOW_CACHES.remove(owner);
        }
    }
}
