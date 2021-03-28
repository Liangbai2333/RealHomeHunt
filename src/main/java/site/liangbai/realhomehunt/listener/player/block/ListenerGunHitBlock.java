package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import site.liangbai.craftingdeadapi.api.event.bukkit.block.GunHitBlockEvent;
import site.liangbai.realhomehunt.processor.Processors;

public final class ListenerGunHitBlock implements Listener {
    @EventHandler
    public void onGunHitBlock(GunHitBlockEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) return;

        Player player = ((Player) entity);

        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(player, event.getGun(), event.getBlock());
    }
}
