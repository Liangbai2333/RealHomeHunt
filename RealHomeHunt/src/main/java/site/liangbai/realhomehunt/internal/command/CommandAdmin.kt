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

package site.liangbai.realhomehunt.internal.command

import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.protection.CuboidArea
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import site.liangbai.realhomehunt.RealHomeHuntPlugin.inst
import site.liangbai.realhomehunt.api.cache.SelectCache
import site.liangbai.realhomehunt.api.event.residence.*
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.attribute.getName
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config.StorageSetting.SqliteSetting
import site.liangbai.realhomehunt.common.expand.Expand
import site.liangbai.realhomehunt.common.expand.expand
import site.liangbai.realhomehunt.internal.storage.impl.SqliteStorage
import site.liangbai.realhomehunt.internal.storage.impl.YamlStorage
import site.liangbai.realhomehunt.util.Locations
import site.liangbai.realhomehunt.util.Zones
import site.liangbai.realhomehunt.util.kt.isBoolean
import site.liangbai.realhomehunt.util.sendToAll
import taboolib.common.platform.command.*
import taboolib.platform.util.sendLang
import java.io.File
import java.util.stream.Collectors

@CommandHeader(name = "rhadmin", aliases = ["rhhadmin", "realhomehuntadmin"], permission = "rh.admin", permissionDefault = PermissionDefault.OP)
internal object CommandAdmin {
    @CommandBody(permission = "rh.admin.help", optional = true)
    val help = subCommand {
        execute<CommandSender> { sender, context, _ ->
            sender.sendLang("command-admin-help", context.name)
        }
    }

    @CommandBody(permission = "rh.admin.create", optional = true)
    val create = subCommand {
        dynamic {
            suggestion<Player>(uncheck = true) { _, _ ->
                Bukkit.getOnlinePlayers()
                    .map { it.name }
                    .filter {
                        ResidenceManager.getResidenceByOwner(
                            it
                        ) == null
                    }
            }

            execute<Player> { sender, _, argument ->
                sender.commandCreate(argument)
            }
        }
    }

    @CommandBody(permission = "rh.admin.expand", optional = true)
    val expand = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            dynamic {
                suggestion<CommandSender> { _, _ ->
                    Expand.values().map { it.name.lowercase() }
                }

                dynamic {
                    suggestion<CommandSender>(uncheck = true) { _, _ ->
                        listOf("5", "10", "20", "30", "40", "50")
                    }

                    execute<CommandSender> { sender, context, argument ->
                        sender.commandExpand(context.argument(-2), context.argument(-1), argument)
                    }
                }
            }
        }
    }

    @CommandBody(permission = "rh.admin.import", optional = true)
    val import = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                val plugin: Plugin = inst

                val folder = plugin.dataFolder

                val files = folder.list()
                if (files == null) null else listOf(*files)
            }

            dynamic {
                suggestion<CommandSender> { _, _ ->
                    listOf("true", "false")
                }

                execute<CommandSender> { sender, context, argument ->
                    sender.commandImport(context.argument(-1), argument)
                }
            }
        }
    }

    @CommandBody(permission = "rh.admin.reelect", optional = true)
    val reelect = subCommand {
        dynamic {
            suggestion<Player> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            execute<Player> { sender, _, argument ->
                sender.commandReelect(argument)
            }
        }
    }

    @CommandBody(permission = "rh.admin.remove", optional = true)
    val remove = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            execute<CommandSender> { sender, _, argument ->
                sender.commandRemove(argument)
            }
        }
    }

    @CommandBody(permission = "rh.admin.set", optional = true)
    val set = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            dynamic {
                suggestion<CommandSender> { _, _ ->
                    AttributeMap.getTypes()
                }

                dynamic {
                    suggestion<CommandSender> { _, context ->
                        val type: String = context.argument(-1)

                        if (type.isEmpty()) {
                            return@suggestion null
                        }

                        val residence = ResidenceManager.getResidenceByOwner(context.argument(-2)) ?: return@suggestion null

                        val attributeClass = AttributeMap.getMap(type)

                        if (attributeClass == null) null else residence.getAttributeWithoutType(attributeClass)
                            .allowValues()
                    }

                    execute<CommandSender> { sender, context, argument ->
                        sender.commandSet(context.argument(-2), context.argument(-1), argument)
                    }
                }
            }
        }
    }

    @CommandBody(permission = "rh.admin.administrator", optional = true)
    val administrator = subCommand {
        dynamic {
            suggestion<CommandSender> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            dynamic {
                suggestion<CommandSender>(uncheck = true) { _, context ->
                    val residence = ResidenceManager.getResidenceByOwner(context.argument(-1))

                    Bukkit.getOnlinePlayers()
                        .map { it.name }
                        .filter { residence?.isAdministrator(it) ?: true }
                        .toMutableList()
                        .also { residence?.run { it.addAll(this.administrators) } }
                }

                dynamic {
                    suggestion<CommandSender> { _, _ ->
                        listOf("true", "false")
                    }

                    execute<CommandSender> { sender, context, argument ->
                        argument.isBoolean().apply {
                            if (!this) {
                                sender.sendLang("command-admin-administrator-unknown-param")
                            } else context.argument(-2)
                                .let { sender.commandAdministrator(it, context.argument(-1), argument.toBoolean()) }
                        }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "rh.admin.show", optional = true)
    val show = subCommand {
        dynamic {
            suggestion<Player> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            execute<Player> { sender, _, argument ->
                sender.commandShow(argument)
            }
        }
    }

    @CommandBody(permission = "rh.admin.translate", optional = true)
    val translate = subCommand {
        dynamic {
            suggestion<CommandSender>(uncheck = true) { _, _ ->
                Bukkit.getOnlinePlayers().stream()
                    .parallel()
                    .map { it.name }
                    .filter {
                        ResidenceManager.getResidenceByOwner(
                            it
                        ) == null
                    }
                    .collect(Collectors.toList())
            }

            execute<CommandSender> { sender, _, argument ->
                sender.commandTranslate(argument)
            }
        }
    }

    @CommandBody(permission = "rh.admin.translateall", optional = true)
    val translateall = subCommand {
        execute<CommandSender> { sender, _, _ ->
            sender.commandTranslateAll()
        }
    }

    @CommandBody(permission = "rh.admin.help")
    val main = mainCommand {
        execute<CommandSender> { sender, context, argument ->
            if (argument.isEmpty()) {
                sender.sendLang("command-admin-help", context.name)

                return@execute
            }

            sender.sendLang("command-unknown-sub-command", context.name)
        }
    }

    private fun CommandSender.commandTranslate(target: String) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) return

        val residencePlayer = ResidenceApi.getPlayerManager().getResidencePlayer(target)

        if (residencePlayer == null) {
            sendLang("command-admin-translate-failed", target)
        }

        val claimedResidence = residencePlayer.mainResidence

        var area: CuboidArea? = null

        if (claimedResidence == null || claimedResidence.mainArea.also { area = it } == null) {
            sendLang("command-admin-translate-failed", target)
            return
        }

        if (!ResidenceManager.isOpened(area!!.world)) {
            sendLang("command-admin-translate-failed", target)
            return
        }

        val residence: Residence =
            Residence.Builder().owner(target).left(area!!.highLocation).right(area!!.lowLocation).build()

        val defaultSpawn = Locations.getAverageLocation(residence.left.world, residence.left, residence.right)

        residence.spawn = defaultSpawn

        ResidenceManager.register(residence)

        residence.save()

        sendLang("command-admin-translate-success", target)
    }

    private fun CommandSender.commandTranslateAll() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) return

        var count = 0

        var sucCount = 0

        for (offlinePlayer in Bukkit.getOfflinePlayers()) {
            val residencePlayer = ResidenceApi.getPlayerManager().getResidencePlayer(offlinePlayer.name) ?: continue
            val claimedResidence = residencePlayer.mainResidence
            if (claimedResidence != null) {
                count++
                val area = claimedResidence.mainArea

                if (!ResidenceManager.isOpened(area.world)) return

                if (area != null) {
                    val residence =
                        Residence.Builder().owner(offlinePlayer.name!!).left(area.highLocation).right(area.lowLocation)
                            .build()
                    val defaultSpawn =
                        Locations.getAverageLocation(residence.left.world, residence.left, residence.right)
                    residence.spawn = defaultSpawn
                    ResidenceManager.register(residence)
                    residence.save()
                    sucCount++
                }
            }
        }

        sendLang("command-admin-translate-all-info", count, sucCount, count - sucCount)
    }

    private fun CommandSender.commandSet(target: String, attributeType: String, value: String) {
        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-set-have-not-residence", target)
            return
        }

        val attributeClass = AttributeMap.getMap(attributeType)

        if (attributeClass == null) {
            sendLang("command-admin-set-unknown-attribute", attributeType)
            return
        }

        val attribute = residence.getAttributeWithoutType(attributeClass)

        if (attribute.allow(value)) {
            val event = ResidenceSetAttributeEvent(if (this is Player) this else null, residence, attribute, value)
            if (!event.post()) return
            val attributeName = attribute.getName(this)
            attribute.force(event.value)
            sendLang("command-admin-set-success", target, attributeName, value)
            residence.save()
        } else {
            sendLang("command-admin-set-not-allow", attribute.allowValues())
        }
    }

    private fun CommandSender.commandAdministrator(target: String, who: String, give: Boolean) {
        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-administrator-have-not-residence", target)
            return
        }

        if (give) {
            if (residence.isAdministrator(who)) {
                sendLang("command-admin-administrator-already-is-administrator", target, who)
                return
            }
            if (!ResidenceAdministratorEvent.Give(residence, this, who).post()) return
            residence.addAdministrator(who)
            sendLang("command-admin-administrator-success-give", target, who)
        } else {
            if (!residence.isAdministrator(who)) {
                sendLang("command-admin-administrator-is-not-administrator", target, who)
                return
            }
            if (!ResidenceAdministratorEvent.Remove(residence, this, who).post()) return
            residence.removeAdministrator(who)
            sendLang("command-admin-administrator-success-delete", target, who)
        }

        residence.save()
    }

    private fun Player.commandShow(target: String) {
        if (!ResidenceManager.isOpened(world)) {
            sendLang("command-admin-show-not-opened")
            return
        }

        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-show-have-not-residence", target)
            return
        }

        if (Command.clearShowCacheFor(name, target)) {
            sendLang("command-admin-show-success-off", target)
        } else {
            Command.showCaches.computeIfAbsent(name) {
                mutableMapOf()
            }[target] = Zones.startShowWithBlockLocation(this, residence.left, residence.right)
            sendLang("command-admin-show-success-on", target)
        }
    }

    private fun CommandSender.commandImport(filePath: String, cleanOldString: String) {
        if (!cleanOldString.isBoolean()) {
            sendLang("command-admin-import-unknown-param4")
            return
        }

        val cleanOld = cleanOldString.toBoolean()

        val file = File(inst.dataFolder, filePath)

        if (!file.exists()) {
            sendLang("command-admin-import-unknown-file", file.name)
            return
        }

        val storageType = ResidenceManager.storageType.name

        if (file.isFile) {
            val setting = SqliteSetting()
            setting.onlyInPluginFolder = true
            setting.databaseFile = filePath
            val storage = SqliteStorage(inst, setting)
            if (cleanOld) {
                ResidenceManager.getResidences().forEach { it.remove() }
            }
            val residenceList = storage.loadAll()
            residenceList
                .forEach { ResidenceManager.register(it) }
            ResidenceManager.getResidences().forEach { it.save() }
            sendLang(
                "command-admin-import-save-to-storage",
                storageType,
                residenceList.size,
                storage.count()
            )
            return
        }

        if (file.isDirectory) {
            val storage = YamlStorage(inst, filePath)
            if (cleanOld) {
                ResidenceManager.getResidences().forEach { it.remove() }
            }
            val residenceList = storage.loadAll()
            residenceList.forEach { ResidenceManager.register(it) }
            ResidenceManager.getResidences().forEach { it.save() }
            sendLang(
                "command-admin-import-save-to-storage",
                storageType,
                residenceList.size,
                storage.count()
            )
        }
    }

    private fun CommandSender.commandRemove(target: String) {
        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-remove-have-not-residence")
            return
        }

        if (!ResidenceRemoveEvent(residence, if (this is Player) this else null).post()) return

        residence.remove()

        sendLang("command-admin-remove-success", target)
    }

    private fun Player.commandReelect(target: String) {
        if (!ResidenceManager.isOpened(player!!.world)) {
            sendLang("command-admin-reelect-not-opened")
            return
        }

        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-reelect-not-have-residence", target)
            return
        }

        val loc1 = SelectCache.require(SelectCache.SelectType.FIRST, player!!.name)
        val loc2 = SelectCache.require(SelectCache.SelectType.SECOND, player!!.name)

        if (loc1 == null || loc2 == null) {
            sendLang("command-admin-reelect-not-select-zone")
            return
        }

        if (loc1.world != loc2.world) {
            sendLang("command-admin-reelect-points-not-in-same-world")
            return
        }

        if (!ResidenceManager.isOpened(loc1.world!!)) {
            sendLang("command-admin-reelect-not-opened")
            return
        }

        if (ResidenceManager.hasResidence(loc1, loc2)) {
            SelectCache.pop(this)
            sendLang("command-admin-reelect-contains-other")
            return
        }

        val event = ResidenceReelectEvent(this, residence, loc1, loc2)

        if (!event.post()) return

        residence.left = loc1
        residence.right = loc2

        SelectCache.pop(this)

        sendLang("command-admin-reelect-success", target)
    }

    private fun CommandSender.commandExpand(target: String, expandType: String, sizeString: String) {
        val residence = ResidenceManager.getResidenceByOwner(target)

        if (residence == null) {
            sendLang("command-admin-expand-have-not-residence", target)
            return
        }

        val expand = Expand.matches(expandType)

        if (expand == null) {
            sendLang("command-admin-expand-unknown-expand", expandType)
            return
        }

        val size: Double = try {
            sizeString.toDouble()
        } catch (e: Throwable) {
            sendLang("command-admin-expand-must-be-number", sizeString)
            return
        }

        val preEvent = ResidenceExpandEvent.Pre(this, residence, expand, size)

        if (!preEvent.post()) return

        val pair = residence.expand(preEvent.expand, preEvent.size)

        val postEvent =
            ResidenceExpandEvent.Post(this, residence, pair.key, pair.value, preEvent.expand, preEvent.size)

        if (!postEvent.post()) return

        residence.left = postEvent.changedLeft
        residence.right = postEvent.changedRight

        sendLang("command-admin-expand-success", target, expandType, size)
    }

    private fun Player.commandCreate(target: String) {
        val old = ResidenceManager.getResidenceByOwner(target)

        if (old != null) {
            SelectCache.pop(this)
            sendLang("command-admin-create-has-old", target)
            return
        }

        val loc1 = SelectCache.require(SelectCache.SelectType.FIRST, player!!.name)
        val loc2 = SelectCache.require(SelectCache.SelectType.SECOND, player!!.name)

        if (loc1 == null || loc2 == null) {
            sendLang("command-admin-create-not-select-zone")
            return
        }

        if (loc1.world != loc2.world) {
            sendLang("command-admin-create-points-not-in-same-world")
            return
        }

        if (!ResidenceManager.isOpened(loc1.world!!)) {
            sendLang("command-create-not-opened")
            return
        }

        if (ResidenceManager.hasResidence(loc1, loc2)) {
            SelectCache.pop(this)
            sendLang("command-admin-create-contains-other")
            return
        }

        val residence = Residence.Builder().owner(target).left(loc1).right(loc2).build()

        if (!ResidenceCreateEvent.Pre(this, residence).post()) return

        val defaultSpawn = Locations.getAverageLocation(loc1.world, loc1, loc2)

        residence.spawn = defaultSpawn

        if (!ResidenceCreateEvent.Post(this, residence).post()) return

        ResidenceManager.register(residence)

        residence.save()

        SelectCache.pop(this)

        sendLang("command-admin-create-success", target)

        sendToAll("command-admin-create-send-to-all", name, target)
    }
}