package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.manager.ResidenceManager;

@Plugin.EventSubscriber
public final class ListenerBlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("rh.break")) return;

        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(event.getBlock().getLocation());

        if (residence == null) return;

        if (!residence.isAdministrator(event.getPlayer())) event.setCancelled(true);

        if (Config.block.ignore.contains(event.getBlock().getType())) {
            Residence.IgnoreBlockInfo info = residence.getIgnoreBlockInfo(event.getBlock().getType());

            info.deleteCount();

            residence.save();
        } else {

            Block upBlock = event.getBlock().getRelative(0, 1, 0);

            saveUpBlock(upBlock, residence);
        }
    }

    public static void saveUpBlock(@NotNull Block block, Residence residence) {
        Material upType = block.getType();

        Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info = Config.block.ignore.getByMaterial(upType);

        if (info != null) {
            if (!info.isUpBreak()) return;

            Residence.IgnoreBlockInfo residenceIgnoreBlockInfo = residence.getIgnoreBlockInfo(upType);

            residenceIgnoreBlockInfo.deleteCount();

            residence.save();
        }
    }
}
