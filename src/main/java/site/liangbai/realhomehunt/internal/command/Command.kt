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

package site.liangbai.realhomehunt.internal.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.RealHomeHuntPlugin
import site.liangbai.realhomehunt.api.cache.SelectCache
import site.liangbai.realhomehunt.api.event.residence.ResidenceAdministratorEvent.Give
import site.liangbai.realhomehunt.api.event.residence.ResidenceAdministratorEvent.Remove
import site.liangbai.realhomehunt.api.event.residence.ResidenceCreateEvent
import site.liangbai.realhomehunt.api.event.residence.ResidenceSetAttributeEvent
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.common.confirm.ConfirmModule
import site.liangbai.realhomehunt.common.confirm.ConfirmProcessors
import site.liangbai.realhomehunt.common.particle.EffectGroup
import site.liangbai.realhomehunt.internal.task.PlayerBackTask
import site.liangbai.realhomehunt.util.*
import site.liangbai.realhomehunt.util.kt.*
import taboolib.common.platform.command.*
import taboolib.platform.util.sendLang
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@CommandHeader(name = "rh", aliases = ["rhh", "realhomehunt"], permission = "rh.command", permissionDefault = PermissionDefault.TRUE)
internal object Command {
    val showCaches: MutableMap<String, EffectGroup> = ConcurrentHashMap()

    @CommandBody(permission = "rh.command.help", optional = true, permissionDefault = PermissionDefault.TRUE)
    val help = subCommand {
        execute<CommandSender> { sender, context, _ ->
            sender.sendLang("command-help-common", context.name)

            if (sender.hasPermission("rh.reload")) {
                sender.sendLang("command-help-reload", context.name)
            }
        }
    }

    @CommandBody(permission = "rh.reload", optional = true)
    val reload = subCommand {
        execute<CommandSender> { sender, _, _ ->
            if (!sender.hasPermission("rh.reload")) {
                sender.sendLang("command-reload-have-not-permission", "rh.reload")
                return@execute
            }

            RealHomeHuntPlugin.init()

            sender.sendLang("command-reload-success")
        }
    }

    @CommandBody(permission = "rh.command.create", optional = true, permissionDefault = PermissionDefault.TRUE)
    val create = subCommand {
        execute<Player> { sender, _, _ ->
            sender.commandCreate()
        }
    }

    @CommandBody(permission = "rh.command.remove", optional = true, permissionDefault = PermissionDefault.TRUE)
    val remove = subCommand {
        execute<Player> { sender, context, _ ->
            val residence = ResidenceManager.getResidenceByOwner(sender.name)

            if (residence == null) {
                sender.sendLang("command-remove-have-not-residence")
                return@execute
            }

            ConfirmModule.pushConfirmCache(
                sender,
                residence,
                Config.confirmWaitMills,
                ConfirmProcessors.REMOVE_CONFIRM_PROCESSOR
            )

            ConfirmModule.sendPrepare(sender, context.name, Config.confirmWaitMills)
        }
    }

    @CommandBody(permission = "rh.command.info", optional = true, permissionDefault = PermissionDefault.TRUE)
    val info = subCommand {
        dynamic(optional = true) {
            suggestion<CommandSender> { _, _ ->
                ResidenceManager.getResidences().map { it.owner }
            }

            execute<CommandSender> { sender, _, argument ->
                sender.commandInfo(argument)
            }
        }

        execute<CommandSender> { sender, _, _ ->
            sender.commandInfo()
        }
    }

    @CommandBody(permission = "rh.command.administrator", optional = true, permissionDefault = PermissionDefault.TRUE)
    val administrator = subCommand {
        dynamic {
            suggestion<Player>(uncheck = true) { sender, _ ->
                val residence = ResidenceManager.getResidenceByOwner(sender.name)

                val players = Bukkit.getOnlinePlayers()
                    .map { it.name }
                    .filter {
                        residence == null || !residence.isAdministrator(
                            it
                        )
                    }
                    .toMutableList()

                players.addAll(residence?.administrators ?: emptyList())

                players
            }

            dynamic {
                suggestion<Player> { _, _ ->
                    listOf("true", "false")
                }

                execute<Player> { sender, context, argument ->
                    argument.isBoolean().apply {
                        if (!this) {
                            sender.sendLang("command-administrator-unknown-param")
                        } else context.argument(-1)?.let { sender.commandAdministrator(it, argument.toBoolean()) }
                    }
                }
            }
        }
    }

    @CommandBody(permission = "rh.command.confirm", optional = true, permissionDefault = PermissionDefault.TRUE)
    val confirm = subCommand {
        execute<Player> { sender, _, _ ->
            if (!ConfirmModule.hasConfirmCache(sender)) {
                sender.sendLang("command-confirm-nothing")
                return@execute
            }

            val pair = ConfirmModule.getConfirmProcessor(sender)

            pair.value.process(sender, pair.key)
        }
    }

    @CommandBody(permission = "rh.command.setspawn", optional = true, permissionDefault = PermissionDefault.TRUE)
    val setspawn = subCommand {
        execute<Player> { sender, _, _ ->
            sender.commandSetSpawn()
        }
    }

    @CommandBody(permission = "rh.command.unselect", optional = true, permissionDefault = PermissionDefault.TRUE)
    val unselect = subCommand {
        execute<Player> { sender, _, _ ->
            SelectCache.pop(sender)
            sender.sendLang("command-unselect-success")
        }
    }

    @CommandBody(permission = "rh.command.back", optional = true, permissionDefault = PermissionDefault.TRUE)
    val back = subCommand {
        val teleportPlayers: MutableSet<UUID> = mutableSetOf()

        dynamic(optional = true, permission = "rh.unlimited.back") {
            suggestion<Player> { sender, _ ->
                if (sender.hasPermission("rh.unlimited.back")) {
                    ResidenceManager.getResidences().map { it.owner }
                } else ResidenceManager.getResidences()
                    .filter {
                        it.isAdministrator(
                            sender.name
                        ) && !it.isOwner(sender.name)
                    }
                    .map { it.owner }
            }

            execute<Player> { sender, context, argument ->
                sender.commandBack(context.name, teleportPlayers, argument)
            }
        }

        execute<Player> { sender, context, _ ->
            sender.commandBack(context.name, teleportPlayers)
        }
    }

    @CommandBody(permission = "rh.command.show", optional = true, permissionDefault = PermissionDefault.TRUE)
    val show = subCommand {
        execute<Player> { sender, _, _ ->
            sender.commandShow()
        }
    }

    @CommandBody(permission = "rh.command.set", optional = true, permissionDefault = PermissionDefault.TRUE)
    val set = subCommand {
        dynamic {
            suggestion<Player> { _, _ ->
                AttributeMap.getTypes()
            }

            dynamic {
                suggestion<Player> { sender, context ->
                    val type: String = context.argument(-1)!!

                    if (type.isEmpty()) {
                        return@suggestion null
                    }

                    val residence = ResidenceManager.getResidenceByOwner(sender.name) ?: return@suggestion null

                    val attributeClass = AttributeMap.getMap(type)

                   if (attributeClass == null) null else residence.getAttributeWithoutType(attributeClass)
                        .allowValues()
                }

                execute<Player> { sender, context, argument ->
                    sender.commandSet(context.argument(-1)!!, argument)
                }
            }
        }
    }

    @CommandBody
    val main = mainCommand {
        execute<CommandSender> { sender, context, argument ->
            if (argument.isEmpty()) {
                sender.sendLang("command-help-common", context.name)

                if (sender.hasPermission("rh.reload")) {
                    sender.sendLang("command-help-reload", context.name)
                }

                return@execute
            }

            sender.sendLang("command-unknown-sub-command", context.name)
        }
    }

    private fun Player.commandSet(attributeType: String, value: String) {
        val residence = ResidenceManager.getResidenceByOwner(name)

        if (residence == null) {
            sendLang("command-set-have-not-residence")
            return
        }

        val attributeClass = AttributeMap.getMap(attributeType)

        if (attributeClass == null) {
            sendLang("command-set-unknown-attribute", attributeType)
            return
        }

        val attribute = residence.getAttributeWithoutType(attributeClass)

        if (attribute.allow(value)) {
            val event = ResidenceSetAttributeEvent(this, residence, attribute, value)
            if (!event.post()) return
            val attributeName = attribute.name
            attribute.force(event.value)
            sendLang("command-set-success", attributeName, value)
            residence.save()
        } else {
            sendLang("command-set-not-allow", attribute.allowValues())
        }
    }

    private fun Player.commandShow() {
        if (!ResidenceManager.isOpened(world)) {
            sendLang("command-show-not-opened")
            return
        }

        val residence = ResidenceManager.getResidenceByOwner(name)

        if (residence == null) {
            sendLang("command-show-have-not-residence")
            return
        }

        if (showCaches.containsKey(name)) {
            val effectGroup = showCaches[name]
            effectGroup?.turnOff()
            showCaches.remove(name)
            sendLang("command-show-success-off")
        } else {
            showCaches[name] = Zones.startShowWithBlockLocation(this, residence.left, residence.right)
            sendLang("command-show-success-on")
        }
    }

    private fun Player.commandBack(label: String, cache: MutableSet<UUID>, target: String = name) {
        val residence: Residence?

        if (target == name) {
            residence = ResidenceManager.getResidenceByOwner(name)
            if (residence == null) {
                sendLang("command-back-other-not-have-residence")
                return
            }
            if (!residence.isAdministrator(name) && !hasPermission("rh.unlimited.back")) {
                sendLang("command-back-other-not-have-permission")
                return
            }
        } else {
            residence = ResidenceManager.getResidenceByOwner(target)
            if (residence == null) {
                sendLang("command-back-self-not-have-residence", label)
                return
            }
        }

        if (hasPermission("rh.unlimited.back")) {
            Locations.teleportAfterChunkLoaded(this, residence.spawn)
            return
        }

        val seconds = Config.teleportMills / 20

        cache.add(uniqueId)

        PlayerBackTask.tryTeleportPlayer(
            this, residence.owner, residence.spawn, seconds
        ) { cache.remove(uniqueId) }
    }

    private fun Player.commandSetSpawn() {
        val residence = ResidenceManager.getResidenceByOwner(name)

        if (residence == null) {
            sendLang("command-setspawn-have-not-residence")
            return
        }

        if (!residence.isOwner(name)) {
            sendLang("command-setspawn-is-not-owner")
            return
        }

        val blockLocation = Locations.toBlockLocation(location)

        if (!ResidenceManager.isInResidence(blockLocation, residence)) {
            sendLang("command-setspawn-not-in-residence")
            return
        }

        residence.spawn = location

        residence.save()

        sendLang("command-setspawn-success")
    }

    private fun Player.commandAdministrator(target: String, give: Boolean) {
        val residence = ResidenceManager.getResidenceByOwner(name)

        if (residence == null) {
            sendLang("command-administrator-have-not-residence")
            return
        }

        if (give) {
            if (residence.isAdministrator(target)) {
                sendLang("command-administrator-already-is-administrator", target)
                return
            }
            if (!Give(residence, this, target).post()) return
            residence.addAdministrator(target)
            sendLang("command-administrator-success-give", target)
        } else {
            if (!residence.isAdministrator(target)) {
                sendLang("command-administrator-is-not-administrator", target)
                return
            }
            if (!Remove(residence, this, target).post()) return
            residence.removeAdministrator(target)
            sendLang("command-administrator-success-delete", target)
        }

        residence.save()
    }

    private fun CommandSender.commandInfo(target: String = name) {
        if (target == name) {
            val residence = ResidenceManager.getResidenceByOwner(name)
            if (residence == null) {
                sendLang("command-info-self-have-not-residence")
                return
            }
            sendLang(
                "command-info-self-show",
                residence.owner,
                residence.getAdministratorListString(),
                residence.getAttributeListString()
            )
        } else {
            val residence = ResidenceManager.getResidenceByOwner(target)
            if (residence == null) {
                sendLang("command-info-other-have-not-residence")
                return
            }
            sendLang(
                "command-info-other-show",
                target,
                residence.owner,
                residence.getAdministratorListString(),
                residence.getAttributeListString()
            )
        }
    }

    private fun Player.commandCreate() {
        if (!ResidenceManager.isOpened(world)) {
            sendLang("command-create-not-opened")
            return
        }

        val old = ResidenceManager.getResidenceByOwner(name)

        if (old != null) {
            SelectCache.pop(this)
            sendLang("command-create-has-old")
            return
        }

        val loc1 = SelectCache.require(SelectCache.SelectType.FIRST, name)
        val loc2 = SelectCache.require(SelectCache.SelectType.SECOND, name)

        if (loc1 == null || loc2 == null) {
            sendLang("command-create-not-select-zone")
            return
        }

        if (loc1.world != loc2.world) {
            sendLang("command-create-points-not-in-same-world")
            return
        }

        if (!ResidenceManager.isOpened(loc1.world!!)) {
            sendLang("command-create-not-opened")
            return
        }

        val distanceInfo = Locations.countDistanceInfo(loc1, loc2)

        val sizeSetting = Config.residence.sizeLimit

        if (!hasPermission("rh.unlimited.create")) {
            if (distanceInfo.x > sizeSetting.x || distanceInfo.y > sizeSetting.y || distanceInfo.z > sizeSetting.z) {
                sendLang(
                    "command-create-too-large-zone",
                    sizeSetting.x,
                    sizeSetting.y,
                    sizeSetting.z
                )
                return
            }
        }

        if (ResidenceManager.hasResidence(loc1, loc2)) {
            SelectCache.pop(this)
            sendLang("command-create-contains-other")
            return
        }

        val residence = Residence.Builder().owner(this).left(loc1).right(loc2).build()

        val preEvent = ResidenceCreateEvent.Pre(this, residence)

        if (!preEvent.post()) return

        if (!hasPermission("rh.unlimited.create") && !preEvent.isCheckBlock) {
            for (info in Config.block.ignore.ignoreBlockInfoList) {
                val count = Blocks.containsBlockAndReturnCount(info, residence)
                if (count > info.amount) {
                    sendLang(
                        "command-create-contains-ignore-block",
                        info.full.let { it ?: info.suffix.ifEmpty { info.prefix } }
                    )
                    return
                }
            }
        }

        val defaultSpawn = Locations.getAverageLocation(loc1.world, loc1, loc2)

        residence.spawn = defaultSpawn

        if (!ResidenceCreateEvent.Post(this, residence).post()) return

        ResidenceManager.register(residence)

        residence.save()

        SelectCache.pop(this)

        Sounds.playLevelUpSound(this, 1, 0.0)

        sendLang("command-create-success")

        sendToAll("command-create-send-to-all", name)
    }
}