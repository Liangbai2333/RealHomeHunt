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

package site.liangbai.realhomehunt.residence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
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
import site.liangbai.realhomehunt.task.UnloadWarnTask;
import site.liangbai.realhomehunt.util.Messages;
import site.liangbai.realhomehunt.util.Sounds;
import site.liangbai.realhomehunt.util.Titles;

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


    private boolean canWarn = true;

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
    public IgnoreBlockInfo getIgnoreBlockInfo(Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo ignoreBlockInfo) {
        return ignoreBlockInfoList.stream()
                .filter(info -> {
                    String name = info.type.toUpperCase();

                    if (ignoreBlockInfo.full != null && ignoreBlockInfo.full.equalsIgnoreCase(name)) return true;

                    return name.startsWith(ignoreBlockInfo.prefix) && name.endsWith(ignoreBlockInfo.suffix);
                })
                .findFirst()
                .orElseGet(() -> {
                    IgnoreBlockInfo info;

                    if (ignoreBlockInfo.full != null) {
                        info = new IgnoreBlockInfo(ignoreBlockInfo.full);
                    } else {
                        String typeName = ignoreBlockInfo.prefix + ignoreBlockInfo.suffix;

                        if (typeName.isEmpty()) typeName = "null";

                        info = new IgnoreBlockInfo(typeName);
                    }

                    ignoreBlockInfoList.add(info);

                    return info;
                });
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

    public void attackBy(Player attacker) {
        Player owner = Bukkit.getPlayerExact(getOwner());

        Sounds.playSound(attacker, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 0);

        if (owner != null && owner.isOnline()) {
            Locale locale = LocaleManager.require(owner);

            Sounds.playSound(owner, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 0);

            Titles.sendTitle(owner, locale.asString("action.hitBlock.self.title", attacker.getName()), locale.asString("action.hitBlock.self.subTitle", attacker.getName()));

            if (Config.showActionBar) {
                DynamicActionBar actionBar = new DynamicActionBar(locale.asString("action.hitBlock.self.actionBar.show", attacker.getName()), 5, 20);

                actionBar.show(owner, Config.actionBarShowMills);
            }
        }

        Messages.sendToAll("action.hitBlock.all.message", getOwner(), attacker.getName());

        addAttack(attacker.getName());
    }

    public void addAttack(String attack) {
        attacks.add(attack);

        new UnloadPlayerAttackTask(this, attack).runTaskLater(RealHomeHunt.plugin, Config.unloadPlayerAttackMills);
    }

    public boolean hasAttack(String attack) {
        return attacks.contains(attack);
    }

    public void warn(Player sender) {
        setCanWarn(false);

        getOnlineMembers().forEach(member -> {
            Locale locale = LocaleManager.require(member);

            Titles.sendTitle(member, locale.asString("action.warn.title", sender.getName()), locale.asString("action.warn.subTitle", sender.getName()));

            Sounds.playSound(member, Sound.ENTITY_PLAYER_LEVELUP, 3, 0.5);
        });

        new UnloadWarnTask(this).runTaskLater(RealHomeHunt.plugin, Config.unloadWarnMills);
    }

    public boolean canWarn() {
        return canWarn;
    }

    public void setCanWarn(boolean canWarn) {
        this.canWarn = canWarn;
    }

    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        Player owner = Bukkit.getPlayerExact(getOwner());

        if (owner != null) players.add(owner);

        getAdministrators().forEach(administrator -> {
            Player player = Bukkit.getPlayerExact(administrator);

            if (player != null) players.add(player);
        });

        return players;
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
