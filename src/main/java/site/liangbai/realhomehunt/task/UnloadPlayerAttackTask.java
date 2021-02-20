package site.liangbai.realhomehunt.task;

import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.residence.Residence;

public final class UnloadPlayerAttackTask extends BukkitRunnable {
    private final Residence residence;

    private final String attack;

    public UnloadPlayerAttackTask(Residence residence, String attack) {
        this.residence = residence;
        this.attack = attack;
    }

    @Override
    public void run() {
        residence.removeAttack(attack);
    }
}
