package site.liangbai.realhomehunt.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.GlowAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.util.Locations;

/**
 * The type Player glow task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:07 上午
 */
public final class PlayerGlowTask extends BukkitRunnable  {
    public static void setup(Plugin plugin) {
        new PlayerGlowTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> !it.isDead() && ResidenceManager.isOpened(it.getWorld()))
                .forEach(it -> {
                    Location location = Locations.toBlockLocation(it.getLocation());

                    Residence residence = ResidenceManager.getResidenceByLocation(location);

                    if (residence.checkBooleanAttribute(GlowAttribute.class) && !it.isGlowing()) {
                        it.setGlowing(true);
                    }
                });
    }
}
