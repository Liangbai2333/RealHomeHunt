/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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

package site.liangbai.realhomehunt.api.residence

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable
import site.liangbai.realhomehunt.api.residence.attribute.create2
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager.isOpened
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager.remove
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager.save
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.common.database.converter.IJsonEntity
import site.liangbai.realhomehunt.util.Locations.toBlockLocation
import site.liangbai.realhomehunt.util.contains
import java.util.*
import java.util.stream.Collectors

class Residence : ConfigurationSerializable {
    // Data
    lateinit var owner: String
    lateinit var left: Location
    lateinit var right: Location
    lateinit var spawn: Location
    lateinit var administrators: MutableList<String>
    lateinit var ignoreBlockCounterList: MutableList<IgnoreBlockCounter>
    lateinit var attributes: MutableList<IAttributable<*>>

    private constructor(left: Location, right: Location, owner: Player) : this(left, right, owner.name)

    private constructor(left: Location, right: Location, owner: String) {
        this.left = left
        this.right = right
        this.owner = owner
        administrators = ArrayList()
        ignoreBlockCounterList = LinkedList()
        attributes = LinkedList()
    }

    @Suppress("UNCHECKED_CAST")
    constructor(map: Map<String, Any>) {
        owner = map["owner"] as String
        administrators = map["administrators"] as MutableList<String>
        left = map["left"] as Location
        right = map["right"] as Location
        ignoreBlockCounterList = map["ignoreBlockInfoList"] as MutableList<IgnoreBlockCounter>
        attributes = if ("attributes" in map) {
            map["attributes"] as MutableList<IAttributable<*>>
        } else {
            LinkedList()
        }
        spawn = map["spawn"] as Location
    }

    constructor() {}

    private fun Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo.getDefaultTypeName(): String {
        return if (full != null) {
            full!!
        } else if (prefix.isEmpty() && suffix.isEmpty()) {
            "null".also { throw IllegalStateException("Error config which has unusual ignore block data.") }
        } else {
            prefix + suffix
        }
    }

    private fun Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo.matchType(name: String): Boolean {
        if (full != null && full!!.isNotEmpty() && full.equals(
                name,
                ignoreCase = true
            )
        ) {
            return true
        }
        if (prefix.isEmpty() && suffix.isEmpty()) return false
        return name.startsWith(prefix) && name.endsWith(suffix)
    }

    // Method
    fun getIgnoreBlockCounter(ignoreBlockInfo: Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo): IgnoreBlockCounter {
        return ignoreBlockCounterList.firstOrNull { ignoreBlockInfo.matchType(it.type) } ?: IgnoreBlockCounter(ignoreBlockInfo.getDefaultTypeName())
                .also { ignoreBlockCounterList.add(it) }
    }

    inline fun <reified T : IAttributable<Boolean>> checkBooleanAttribute(): Boolean {
        return getAttribute(T::class.java).get()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAttribute(attributeClass: Class<out IAttributable<T>>): IAttributable<T> {
        return getAttributeWithoutType(attributeClass) as IAttributable<T>
    }

    fun getAttributeWithoutType(attributeClass: Class<out IAttributable<*>>): IAttributable<*> {
        return Objects.requireNonNull(
            attributes.firstOrNull { it.javaClass == attributeClass } ?: attributeClass.create2()
                .also { attributes.add(it) }
        )
    }

    internal fun destroyBlock(block: Block) {
        val upType = block.type
        val info = Config.block.ignore.getByMaterial(upType)
        if (info != null) {
            val counter = getIgnoreBlockCounter(info)
            if (counter.count > 0) {
                counter.deleteCount()
                save()
            }
        }
    }

    val onlineMembers: List<Player>
        get() {
            val players: MutableList<Player> = ArrayList()
            val owner = Bukkit.getPlayerExact(owner)
            if (owner != null) players.add(owner)
            administrators
                .mapNotNull { Bukkit.getPlayerExact(it) }
                .forEach { players.add(it) }
            return players
        }

    fun isOwner(owner: String): Boolean {
        return this.owner == owner
    }

    fun isAdministrator(player: Player): Boolean {
        return isAdministrator(player.name)
    }

    fun isAdministrator(player: String): Boolean {
        return player in administrators || owner == player
    }

    fun addAdministrator(player: String) {
        if (isAdministrator(player)) return
        administrators.add(player)
    }

    fun removeAdministrator(player: String) {
        administrators.remove(player)
    }

    fun save() {
        save(this)
    }

    fun remove() {
        remove(this)
    }

    fun findPlayersIn(): List<Player> {
        return Bukkit.getOnlinePlayers().stream()
            .parallel()
            .filter { isOpened(it.world) }
            .filter { toBlockLocation(it.location) in this }
            .collect(Collectors.toList())
    }

    fun hasEnemyIn(): Boolean {
        return findPlayersIn().any { !isAdministrator(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val residence = other as Residence
        return owner == residence.owner
    }

    override fun hashCode(): Int {
        return Objects.hash(owner)
    }

    override fun serialize(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["owner"] = owner
        map["administrators"] = administrators
        map["left"] = left
        map["right"] = right
        map["ignoreBlockInfoList"] = ignoreBlockCounterList
        map["attributes"] = attributes
        map["spawn"] = spawn
        return map
    }

    class IgnoreBlockCounter : ConfigurationSerializable,
        IJsonEntity<IgnoreBlockCounter> {
        lateinit var type: String
            private set
        var count = 0
            private set

        constructor(type: String) {
            this.type = type
        }

        // For converter
        constructor()

        constructor(map: Map<String?, Any?>) {
            type = map["type"] as String
            count = map["count"] as Int
        }

        fun increaseCount(count: Int) {
            this.count += count
        }

        fun increaseCount() {
            count++
        }

        fun deleteCount() {
            count--
        }

        override fun serialize(): Map<String, Any> {
            val map: MutableMap<String, Any> = HashMap()
            map["type"] = type
            map["count"] = count
            return map
        }

        override fun convertToDatabaseColumn(attribute: IgnoreBlockCounter): String {
            val jsonObject = JsonObject()
            jsonObject.addProperty("type", attribute.type)
            jsonObject.addProperty("count", attribute.count)
            return jsonObject.toString()
        }

        override fun convertToEntityAttribute(dbData: String): IgnoreBlockCounter {
            val jsonObject = JsonParser.parseString(dbData).asJsonObject
            val counter = IgnoreBlockCounter(jsonObject["type"].asString)
            counter.count = jsonObject["count"].asInt
            return counter
        }
    }

    class Builder {
        private lateinit var owner: String
        private lateinit var left: Location
        private lateinit var right: Location
        fun owner(owner: Player): Builder {
            return owner(owner.name)
        }

        fun owner(owner: String): Builder {
            this.owner = owner
            return this
        }

        fun left(left: Location): Builder {
            this.left = left.clone()
            return this
        }

        fun right(right: Location): Builder {
            this.right = right.clone()
            return this
        }

        fun build(): Residence {
            return Residence(left, right, owner)
        }
    }
}