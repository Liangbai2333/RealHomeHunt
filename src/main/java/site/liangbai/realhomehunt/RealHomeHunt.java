/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt;

import com.craftingdead.core.event.GunEvent;
import net.minecraftforge.fml.ModList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import site.liangbai.dynamic.Dynamic;
import site.liangbai.forgeeventbridge.event.EventBridge;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.internal.listener.forge.player.EventHolderGunHitBlock;
import site.liangbai.realhomehunt.internal.task.PlayerGlowTask;
import site.liangbai.realhomehunt.internal.task.PlayerMoveToResidenceMessageTask;
import site.liangbai.realhomehunt.util.Console;

public final class RealHomeHunt extends JavaPlugin {
    private static RealHomeHunt inst;

    private static final String FORGE_EVENT_BRIDGE_MOD_ID = "forgeeventbridge";

    @Override
    public void onEnable() {
        inst = this;

        checkForgeEventBridgeInst();

        initConfigurationSerializer();

        Config.init(this);

        LocaleManager.init(this);

        ResidenceManager.init(this, Config.storage.type);

        Dynamic.installWithAccepted(this, getClass().getPackage().getName());

        initForgeEventHolder();

        initTasks();

        processSuccess();
    }

    @Override
    public void onDisable() {
        saveResidences();

        ResidenceManager.getStorage().close();

        cancelTasks();
    }

    public static RealHomeHunt getInst() {
        return inst;
    }

    public void saveResidences() {
        ResidenceManager.getResidences().forEach(Residence::save);
    }

    private void initForgeEventHolder() {
        new EventHolderGunHitBlock().register(EventBridge.builder()
                .target(GunEvent.HitBlock.class).build());
    }

    private void checkForgeEventBridgeInst() {
        if (!ModList.get().isLoaded(FORGE_EVENT_BRIDGE_MOD_ID)) {
            Bukkit.getPluginManager().disablePlugin(this);

            throw new IllegalStateException("can not found Forge-Event-Bridge mod, please install it.");
        }
    }

    private void initConfigurationSerializer() {
        ConfigurationSerialization.registerClass(Residence.class);
        ConfigurationSerialization.registerClass(Residence.IgnoreBlockInfo.class);

        AttributeMap.registerAttributeSerializer();
    }

    private void initTasks() {
        PlayerMoveToResidenceMessageTask.setup(this);
        PlayerGlowTask.setup(this);
    }

    private void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void processSuccess() {
        Console.sendRawMessage(ChatColor.GREEN + "Succeed in enabling " + getName() + " v" + getDescription().getVersion() + " plugin, unique author: Liangbai.");
    }
}
