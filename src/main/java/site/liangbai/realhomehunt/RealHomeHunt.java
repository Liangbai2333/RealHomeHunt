package site.liangbai.realhomehunt;

import com.craftingdead.core.event.GunEvent;
import net.minecraftforge.fml.ModList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import site.liangbai.forgeeventbridge.event.EventBridge;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.lrainylib.annotation.plugin.Info;
import site.liangbai.lrainylib.annotation.plugin.Permission;
import site.liangbai.realhomehunt.command.CommandTabCompiler;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.listener.forge.player.EventHolderGunHitBlock;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.map.AttributeMap;
import site.liangbai.realhomehunt.task.PlayerMoveToResidenceMessageTask;
import site.liangbai.realhomehunt.util.Console;

@Plugin(
        info = @Info(name = "RealHomeHunt", version = "1.1.1", authors = "Liangbai"),
        apiVersion = "1.13",
        softDepend = "Multiverse-Core",
        permissions = {
                @Permission(name = "rh.select", description = "Allows player to select the residence zone.", defaultValue = "true"),
                @Permission(name = "rh.unlimited.create", description = "Allows player ignore the zone size limit."),
                @Permission(name = "rh.unlimited.back", description = "Allows player to teleport to other residences."),
                @Permission(name = "rh.interact", description = "Allows player to click the residence's door."),
                @Permission(name = "rh.break", description = "Allows player to break blocks in residences."),
                @Permission(name = "rh.place", description = "Allows player to place blocks in residences."),
                @Permission(name = "rh.command.help", description = "Allows player to use the /rh help command.", defaultValue = "true"),
                @Permission(name = "rh.command.create", description = "Allows player to use the /rh create command.", defaultValue = "true"),
                @Permission(name = "rh.command.back", description = "Allows player to use the /rh back command.", defaultValue = "true"),
                @Permission(name = "rh.command.setspawn", description = "Allows player to use the /rh setspawn command.", defaultValue = "true"),
                @Permission(name = "rh.command.set", description = "Allows player to use the /rh set command.", defaultValue = "true"),
                @Permission(name = "rh.command.remove", description = "Allows player to use the /rh remove command.", defaultValue = "true"),
                @Permission(name = "rh.command.administrator", description = "Allows player to use the /rh administrator command.", defaultValue = "true"),
                @Permission(name = "rh.command.reload", description = "Allows player to use the /rh reload command."),
                @Permission(name = "rh.command.admin", description = "Allows player to use the /rh admin command."),
                @Permission(name = "rh.command.admin.help", description = "Allows player to use the /rh admin help command."),
                @Permission(name = "rh.command.admin.import", description = "Allows player to use the /rh admin import command."),
                @Permission(name = "rh.reload", description = "Allows player to reload the configuration.")
        }
)
public final class RealHomeHunt extends JavaPlugin {
    @Plugin.Instance
    public static RealHomeHunt plugin;

    @Override
    public void onEnable() {
        checkForgeEventBridgeInst();

        initConfigurationSerializer();

        Config.init(this);

        LocaleManager.init(this);

        ResidenceManager.init(this, Config.storage.type);

        initForgeEventHolder();

        initCommandTabCompiler();

        initTasks();

        processSuccess();
    }

    @Override
    public void onDisable() {
        saveResidences();

        cancelTasks();
    }

    public void saveResidences() {
        ResidenceManager.getResidences().forEach(Residence::save);
    }

    private void initCommandTabCompiler() {
        PluginCommand pluginCommand = getCommand("rh");

        if (pluginCommand == null) return;

        pluginCommand.setTabCompleter(new CommandTabCompiler());
    }

    private void initForgeEventHolder() {
        EventHolder<?> gunHitBlockEventHolder = new EventHolderGunHitBlock();
        gunHitBlockEventHolder.register(EventBridge.builder()
                .target(GunEvent.HitBlock.class)
                .build());
    }

    private void checkForgeEventBridgeInst() {
        if (!ModList.get().isLoaded("forgeeventbridge")) {
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
        PlayerMoveToResidenceMessageTask.init(this);
    }

    private void cancelTasks() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void processSuccess() {
        Console.sendRawMessage(ChatColor.GREEN + "Succeed in enabling " + getName() + " v" + getDescription().getVersion() + " plugin, author: " + getDescription().getAuthors() + ".");
    }
}
