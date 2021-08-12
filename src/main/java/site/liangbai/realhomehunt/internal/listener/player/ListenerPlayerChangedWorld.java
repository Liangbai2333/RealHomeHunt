package site.liangbai.realhomehunt.internal.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.cache.SelectCache;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.internal.command.subcommand.impl.ShowCommand;

@EventSubscriber
public final class ListenerPlayerChangedWorld implements Listener {
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        SelectCache.pop(event.getPlayer());

        String name = event.getPlayer().getName();

        if (ShowCommand.SHOW_CACHES.containsKey(name)) {
            EffectGroup effectGroup = ShowCommand.SHOW_CACHES.get(name);

            effectGroup.turnOff();

            ShowCommand.SHOW_CACHES.remove(name);
        }
    }
}
