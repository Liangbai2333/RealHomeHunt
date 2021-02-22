package site.liangbai.realhomehunt.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.util.LocationUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerMoveToResidenceMessageTask extends BukkitRunnable {
    private final Map<String, String> moveToResidenceCache = new ConcurrentHashMap<>();

    public static void init(Plugin plugin) {
        new PlayerMoveToResidenceMessageTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> ResidenceManager.isOpened(it.getWorld()))
                .forEach(it -> {
                    Locale locale = LocaleManager.require(it);

                    String name = it.getName();

                    Location location = LocationUtil.toBlockLocation(it.getLocation());

                    Residence residence = ResidenceManager.getResidenceByLocation(location);

                    if (residence == null) {
                        if (moveToResidenceCache.containsKey(name)) {
                            String other = moveToResidenceCache.get(name);

                            it.sendMessage(locale.asString("action.residence.moveOut", other));

                            moveToResidenceCache.remove(name);
                        }
                    } else {
                        String lastResidence = moveToResidenceCache.get(name);

                        if (!residence.getOwner().equals(lastResidence)) {
                            String other = residence.getOwner();

                            if (lastResidence != null) {
                                it.sendMessage(locale.asString("action.residence.moveOut", lastResidence));
                            }

                            it.sendMessage(locale.asString("action.residence.moveIn", other));

                            moveToResidenceCache.put(name, other);
                        }
                    }
                });
    }
}
