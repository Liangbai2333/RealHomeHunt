package site.liangbai.realhomehunt.processor;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IGunHitBlockProcessor {
    void processGunHitBlock(Player player, ItemStack gun, Block block);
}
