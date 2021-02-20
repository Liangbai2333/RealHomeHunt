package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.cache.SelectCache;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.util.BlockUtil;
import site.liangbai.realhomehunt.util.LocationUtil;
import site.liangbai.realhomehunt.util.MessageUtil;
import site.liangbai.realhomehunt.util.TitleUtil;

import java.util.Objects;

public final class CreateCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (!ResidenceManager.isOpened(player.getWorld())) {
            sender.sendMessage(locale.asString("command.create.notOpened"));

            return;
        }

        String name = sender.getName();

        Residence old = ResidenceManager.getResidenceByOwner(name);

        if (old != null) {
            sender.sendMessage(locale.asString("command.create.hasOld"));

            return;
        }

        Location loc1 = SelectCache.require(SelectCache.SelectType.FIRST, name);
        Location loc2 = SelectCache.require(SelectCache.SelectType.SECOND, name);

        if (loc1 == null || loc2 == null) {
            sender.sendMessage(locale.asString("command.create.notSelectZone"));

            return;
        }

        if (!Objects.equals(loc1.getWorld(), loc2.getWorld())) {
            sender.sendMessage(locale.asString("command.create.pointsNotInSameWorld"));

            return;
        }

        LocationUtil.DistanceInfo distanceInfo = LocationUtil.countDistanceInfo(loc1, loc2);

        Config.ResidenceSetting.ResidenceSizeSetting sizeSetting = Config.residence.sizeLimit;

        if (!sender.hasPermission("rh.unlimited.create")) {
            if (distanceInfo.getX() > sizeSetting.x ||
                            distanceInfo.getY() > sizeSetting.y ||
                            distanceInfo.getZ() > sizeSetting.z
            ) {
                sender.sendMessage(locale.asString("command.create.tooLargeZone", sizeSetting.x, sizeSetting.y, sizeSetting.z));

                return;
            }
        }

        if (ResidenceManager.containsResidence(loc1, loc2)) {
            SelectCache.pop(name);

            sender.sendMessage(locale.asString("command.create.containsOther"));

            return;
        }

        Residence residence = new Residence.Builder().owner(player).left(loc1).right(loc2).build();

        for (Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info : Config.block.ignore.ignoreBlockInfoList) {
            Material material = Material.matchMaterial(info.type);

            if (material == null) continue;

            int count = BlockUtil.containsBlockAndReturnCount(material, residence);

            if (count > info.amount) {
                sender.sendMessage(locale.asString("command.create.containsIgnoreBlock", material.name().toLowerCase()));

                return;
            }
        }

        Location defaultSpawn = LocationUtil.getAverageLocation(loc1.getWorld(), loc1, loc2);

        residence.setSpawn(defaultSpawn);

        ResidenceManager.register(residence);

        residence.save();

        SelectCache.pop(name);

        TitleUtil.sendTitle(player, locale.asString("command.create.successTitle"), locale.asString("command.create.successSubTitle"));

        MessageUtil.sendToAll("command.create.sendToAll", name);
    }
}
