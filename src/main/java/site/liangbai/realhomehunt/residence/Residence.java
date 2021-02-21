package site.liangbai.realhomehunt.residence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.actionbar.impl.DynamicActionBar;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.task.UnloadPlayerAttackTask;
import site.liangbai.realhomehunt.util.MessageUtil;
import site.liangbai.realhomehunt.util.TitleUtil;

import java.util.*;

public final class Residence implements ConfigurationSerializable {
    private final Location left;

    private final Location right;

    private final String owner;

    private Location spawn;

    private final List<String> administrators;

    private final List<IgnoreBlockInfo> ignoreBlockInfoList;

    private final List<IAttributable<?>> attributes;

    private final List<String> attacks = new ArrayList<>();

    private Residence(Location left, Location right, Player owner) {
        this.left = left;
        this.right = right;
        this.owner = owner.getName();
        this.administrators = new ArrayList<>();
        this.ignoreBlockInfoList = new LinkedList<>();
        this.attributes = new LinkedList<>();
    }

    @SuppressWarnings("unchecked")
    public Residence(Map<String, Object> map) {
        this.owner = (String) map.get("owner");
        this.administrators = (List<String>) map.get("administrators");
        this.left = (Location) map.get("left");
        this.right = (Location) map.get("right");
        this.ignoreBlockInfoList = (List<IgnoreBlockInfo>) map.get("ignoreBlockInfoList");

        if (map.containsKey("attributes")) {
            this.attributes = (List<IAttributable<?>>) map.get("attributes");
        } else {
            this.attributes = new LinkedList<>();
        }

        if (map.containsKey("spawn")) {
            setSpawn((Location) map.get("spawn"));
        }
    }

    @NotNull
    public IgnoreBlockInfo getIgnoreBlockInfo(@NotNull Material type) {
        for (IgnoreBlockInfo info : ignoreBlockInfoList) {
            if (info.type.equals(type.name())) return info;
        }

        IgnoreBlockInfo info = new IgnoreBlockInfo(type.name());

        ignoreBlockInfoList.add(info);

        return info;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> IAttributable<T> getAttribute(Class<? extends IAttributable<T>> attribute) throws Throwable {
        return (IAttributable<T>) getAttributeWithoutType(attribute);
    }

    @NotNull
    public IAttributable<?> getAttributeWithoutType(Class<? extends IAttributable<?>> attribute) throws Throwable {
        for (IAttributable<?> iAttributable : attributes) {
            if (iAttributable.getClass().equals(attribute)) return iAttributable;
        }

        IAttributable<?> iAttributable = attribute.getConstructor().newInstance();

        attributes.add(iAttributable);

        return iAttributable;
    }

    public void attackBy(String attack) {
        Player owner = Bukkit.getPlayerExact(getOwner());

        if (owner != null && owner.isOnline()) {
            Locale locale = LocaleManager.require(owner);

            TitleUtil.sendTitle(owner, locale.asString("action.hitBlock.self.title", attack), locale.asString("action.hitBlock.self.subTitle", attack));

            DynamicActionBar actionBar = new DynamicActionBar(locale.asString("action.hitBlock.self.actionBar.show", attack), 5, 20);

            actionBar.show(owner, Config.actionBarShowMills);
        }

        MessageUtil.sendToAll("action.hitBlock.all.message", getOwner(), attack);

        addAttack(attack);
    }

    public void addAttack(String attack) {
        attacks.add(attack);

        new UnloadPlayerAttackTask(this, attack).runTaskLater(RealHomeHunt.plugin, Config.unloadPlayerAttackMills);
    }

    public boolean hasAttack(String attack) {
        return attacks.contains(attack);
    }

    public void removeAttack(String attack) {
        attacks.remove(attack);
    }

    public String getOwner() {
        return owner;
    }

    public Location getRight() {
        return right;
    }

    public Location getLeft() {
        return left;
    }

    public Location getSpawn() {
        return spawn;
    }

    public boolean isOwner(String player) {
        return getOwner().equals(player);
    }

    public void setSpawn(@NotNull Location spawn) {
        this.spawn = spawn.clone();
    }

    public boolean isAdministrator(Player player) {
        return isAdministrator(player.getName());
    }

    public boolean isAdministrator(String player) {
        return administrators.contains(player) || getOwner().equals(player);
    }

    public void addAdministrator(String player) {
        if (isAdministrator(player)) return;

        administrators.add(player);
    }

    public List<String> getAdministrators() {
        return administrators;
    }

    public void removeAdministrator(String player) {
        administrators.remove(player);
    }

    public void save() {
        ResidenceManager.save(this);
    }

    public void remove() {
        ResidenceManager.remove(this);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("owner", getOwner());

        map.put("administrators", administrators);

        map.put("left", getLeft());

        map.put("right", getRight());

        map.put("ignoreBlockInfoList", ignoreBlockInfoList);

        map.put("attributes", attributes);

        if (getSpawn() != null) {
            map.put("spawn", getSpawn());
        }

        return map;
    }

    public static final class IgnoreBlockInfo implements ConfigurationSerializable {
        private final String type;

        private int count;

        public IgnoreBlockInfo(String type) {
            this.type = type;
        }

        public IgnoreBlockInfo(Map<String, Object> map) {
            this.type = (String) map.get("type");
            this.count = (int) map.get("count");
        }

        public String getType() {
            return type;
        }

        public int getCount() {
            return count;
        }

        public void increaseCount() {
            count++;
        }

        public void deleteCount() { count--; }

        @NotNull
        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();

            map.put("type", getType());

            map.put("count", getCount());

            return map;
        }
    }

    public static final class Builder {
        private Player owner;

        private Location left;

        private Location right;

        public Builder owner(Player owner) {
            this.owner = owner;

            return this;
        }

        public Builder left(Location left) {
            this.left = left.clone();

            return this;
        }

        public Builder right(Location right) {
            this.right = right.clone();

            return this;
        }

        public Residence build() {
            return new Residence(left, right, owner);
        }
    }
}
