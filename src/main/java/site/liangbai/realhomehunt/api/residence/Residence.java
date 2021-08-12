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

package site.liangbai.realhomehunt.api.residence;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.common.actionbar.impl.DynamicActionBar;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.common.database.converter.LocationConverter;
import site.liangbai.realhomehunt.common.database.converter.list.AttributableListConverter;
import site.liangbai.realhomehunt.common.database.converter.list.IJsonEntity;
import site.liangbai.realhomehunt.common.database.converter.list.IgnoreBlockInfoListConverter;
import site.liangbai.realhomehunt.common.database.converter.list.StringListConverter;
import site.liangbai.realhomehunt.internal.task.UnloadPlayerAttackTask;
import site.liangbai.realhomehunt.internal.task.UnloadWarnTask;
import site.liangbai.realhomehunt.util.Messages;
import site.liangbai.realhomehunt.util.Sounds;
import site.liangbai.realhomehunt.util.Titles;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "residences")
public final class Residence implements ConfigurationSerializable {

    @Id
    private String owner;

    @Convert(converter = LocationConverter.class)
    private Location left;

    @Convert(converter = LocationConverter.class)
    private Location right;

    @Convert(converter = LocationConverter.class)
    private Location spawn;

    @Convert(converter = StringListConverter.class)
    private List<String> administrators;

    @Convert(converter = IgnoreBlockInfoListConverter.class)
    private List<IgnoreBlockInfo> ignoreBlockInfoList;

    @Convert(converter = AttributableListConverter.class)
    private List<IAttributable<?>> attributes;

    @Transient
    private final List<String> attacks = new ArrayList<>();

    private boolean canWarn = true;

    private Residence(Location left, Location right, Player owner) {
        this(left, right, owner.getName());
    }

    private Residence(Location left, Location right, String owner) {
        this.left = left;
        this.right = right;
        this.owner = owner;
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

    public Residence() {
    }

    // Data

    @Id
    public String getOwner() {
        return owner;
    }

    @Convert(converter = LocationConverter.class)
    public Location getLeft() {
        return left;
    }

    @Convert(converter = LocationConverter.class)
    public Location getRight() {
        return right;
    }

    @Convert(converter = LocationConverter.class)
    public Location getSpawn() {
        return spawn;
    }

    @Convert(converter = StringListConverter.class)
    public List<String> getAdministrators() {
        return administrators;
    }

    @Convert(converter = IgnoreBlockInfoListConverter.class)
    public List<IgnoreBlockInfo> getIgnoreBlockInfoList() {
        return ignoreBlockInfoList;
    }

    @Convert(converter = AttributableListConverter.class)
    public List<IAttributable<?>> getAttributes() {
        return attributes;
    }

    @Transient
    public boolean isCanWarn() {
        return canWarn;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setLeft(Location left) {
        this.left = left;
    }

    public void setRight(Location right) {
        this.right = right;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setAdministrators(List<String> administrators) {
        this.administrators = administrators;
    }

    public void setIgnoreBlockInfoList(List<IgnoreBlockInfo> ignoreBlockInfoList) {
        this.ignoreBlockInfoList = ignoreBlockInfoList;
    }

    public void setAttributes(List<IAttributable<?>> attributes) {
        this.attributes = attributes;
    }

    public void setCanWarn(boolean canWarn) {
        this.canWarn = canWarn;
    }

    // Method

    @NotNull
    public IgnoreBlockInfo getIgnoreBlockInfo(Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo ignoreBlockInfo) {
        return ignoreBlockInfoList.stream()
                .filter(info -> {
                    String name = info.type.toUpperCase();

                    if (ignoreBlockInfo.full != null && ignoreBlockInfo.full.equalsIgnoreCase(name)) return true;

                    if (ignoreBlockInfo.prefix.isEmpty() && ignoreBlockInfo.suffix.isEmpty()) return false;

                    return name.startsWith(ignoreBlockInfo.prefix) && name.endsWith(ignoreBlockInfo.suffix);
                })
                .findFirst()
                .orElseGet(() -> {
                    IgnoreBlockInfo info;

                    if (ignoreBlockInfo.full != null) {
                        info = new IgnoreBlockInfo(ignoreBlockInfo.full);
                    } else {
                        String typeName;

                        if (ignoreBlockInfo.prefix.isEmpty() && ignoreBlockInfo.suffix.isEmpty()) {
                            typeName = "null";
                        } else {
                            typeName = ignoreBlockInfo.prefix + ignoreBlockInfo.suffix;
                        }

                        info = new IgnoreBlockInfo(typeName);
                    }

                    ignoreBlockInfoList.add(info);

                    return info;
                });
    }

    public boolean checkBooleanAttribute(Class<? extends IAttributable<Boolean>> attribute) {
        return getAttribute(attribute).get();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> IAttributable<T> getAttribute(Class<? extends IAttributable<T>> attribute) {
        return (IAttributable<T>) getAttributeWithoutType(attribute);
    }

    @NotNull
    public IAttributable<?> getAttributeWithoutType(Class<? extends IAttributable<?>> attribute) {
        return Objects.requireNonNull(attributes.stream()
                .filter(it -> it.getClass().equals(attribute))
                .findFirst()
                .orElseGet(() -> {
                    IAttributable<?> iAttributable = null;
                    try {
                        iAttributable = attribute.getConstructor().newInstance();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    attributes.add(iAttributable);

                    return iAttributable;
                }));
    }

    public void attackBy(Player attacker) {
        Player owner = Bukkit.getPlayerExact(getOwner());

        Sounds.playDragonAmbientSound(attacker, 1, 0);

        if (owner != null && owner.isOnline()) {
            Locale locale = LocaleManager.require(owner);

            Sounds.playDragonAmbientSound(owner, 1, 0);

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

        new UnloadPlayerAttackTask(this, attack).runTaskLater(RealHomeHuntPlugin.getInst(), Config.unloadPlayerAttackMills);
    }

    public boolean hasAttack(String attack) {
        return attacks.contains(attack);
    }

    public void warn(Player sender) {
        setCanWarn(false);

        getOnlineMembers().forEach(member -> {
            Locale locale = LocaleManager.require(member);

            Titles.sendTitle(member, locale.asString("action.warn.title", sender.getName()), locale.asString("action.warn.subTitle", sender.getName()));

            Sounds.playLevelUpSound(member, 3, 0.5);
        });

        new UnloadWarnTask(this).runTaskLater(RealHomeHuntPlugin.getInst(), Config.unloadWarnMills);
    }

    @Transient
    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        Player owner = Bukkit.getPlayerExact(getOwner());

        if (owner != null) players.add(owner);

        getAdministrators().stream()
                .map(Bukkit::getPlayerExact)
                .filter(Objects::nonNull)
                .forEach(players::add);

        return players;
    }

    public void removeAttack(String attack) {
        attacks.remove(attack);
    }

    public boolean isOwner(String owner) {
        return getOwner().equals(owner);
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

    public void removeAdministrator(String player) {
        administrators.remove(player);
    }

    public void save() {
        ResidenceManager.save(this);
    }

    public void remove() {
        ResidenceManager.remove(this);
    }

    public List<Player> findPlayersIn() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(it -> ResidenceManager.isOpened(it.getWorld()))
                .filter(it -> ResidenceManager.isInResidence(it.getLocation(), this))
                .collect(Collectors.toList());
    }

    public boolean hasEnemyIn() {
        return findPlayersIn().stream()
                .anyMatch(it -> !isAdministrator(it));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Residence residence = (Residence) o;
        return owner.equals(residence.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
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

    public static final class IgnoreBlockInfo implements ConfigurationSerializable, IJsonEntity<IgnoreBlockInfo> {
        private String type;

        private int count;

        public IgnoreBlockInfo(String type) {
            this.type = type;
        }

        public IgnoreBlockInfo() {
        }

        @SuppressWarnings("unused")
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

        @Override
        public String convertToDatabaseColumn(IgnoreBlockInfo attribute) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("type", getType());
            jsonObject.addProperty("count", getCount());

            return jsonObject.toString();
        }

        @Override
        public IgnoreBlockInfo convertToEntityAttribute(String dbData) {
            JsonObject jsonObject = new JsonParser().parse(dbData).getAsJsonObject();

            this.type = jsonObject.get("type").getAsString();
            this.count = jsonObject.get("count").getAsInt();

            return this;
        }
    }

    public static final class Builder {
        private String owner;

        private Location left;

        private Location right;

        public Builder owner(Player owner) {
            return owner(owner.getName());
        }

        public Builder owner(String owner) {
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
