package site.liangbai.realhomehunt.actionbar.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.actionbar.IActionBar;
import site.liangbai.realhomehunt.util.ActionBar;

public final class DynamicActionBar implements IActionBar, Cancellable {
    private final String message;
    private final long frame;
    private final int allShowDelay;

    private boolean cancelled;

    public DynamicActionBar(String message, long frame, int allShowDelay) {
        this.message = message;
        this.frame = frame;
        this.allShowDelay = allShowDelay;
    }

    @Override
    public void show(Player player) {
        new BukkitRunnable() {
            int count;

            int delayCount;

            int frameCount;

            @Override
            public void run() {
                if (DynamicActionBar.this.isCancelled()) {
                    cancel();

                    return;
                }

                if (count >= message.length()) {
                    if (allShowDelay > 0) {
                        if (delayCount < allShowDelay) {
                            delayCount++;

                            return;
                        }
                    }

                    delayCount = 0;
                    count = 0;
                }

                if (frame > 0) {
                    if (frameCount < frame) {
                        frameCount++;

                        return;
                    }

                    count++;

                    String showString = message.substring(0, count);

                    ActionBar.sendActionBar(player, showString);

                    frameCount = 0;
                }

            }
        }.runTaskTimer(RealHomeHunt.plugin, 0, 1);
    }

    @Override
    public void show(Player player, long mills) {
        show(player);

        Bukkit.getScheduler().runTaskLater(RealHomeHunt.plugin, () -> clear(player), mills);
    }

    @Override
    public void clear(Player player) {
        setCancelled(true);

        ActionBar.sendActionBar(player, "");
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
