package strucures

import memory.role
import memory.room
import roles.Harvester
import roles.IRole
import roles.Upgrader
import screeps.api.CreepMemory
import screeps.api.FIND_MY_CREEPS
import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.Game
import screeps.api.OK
import screeps.api.Room
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.jsObject

val Room.myCreeps get() = find(FIND_MY_CREEPS)

fun runSpawnLogic(room: Room) {
    val creeps = room.myCreeps
    val spawn = room.find(FIND_MY_SPAWNS).firstOrNull() { it.spawning == null }
    spawn ?: return

    val harvesters = creeps.count { it.memory.role == Harvester.name }
    val upgraders = creeps.count { it.memory.role == Upgrader.name }

    if (harvesters == 0) {
        spawn.handleSpawn(Harvester, room.energyAvailable)
        return
    }

    if (harvesters < room.find(FIND_SOURCES).size) {
        if (spawn.handleSpawn(Harvester, room.energyCapacityAvailable)) return
    } else if (upgraders < 2) {
        if (spawn.handleSpawn(Upgrader, room.energyCapacityAvailable)) return
    }
}

/**
 * @return if the spawn was successful
 */
private fun StructureSpawn.handleSpawn(role: IRole, budget: Int): Boolean {
    val parts = role.spawn(budget)?: return false
    val roomName = room.name
    val creepMemory = jsObject<CreepMemory> {
        this.role = role.name
        this.room = roomName
    }
    val status = spawnCreep(parts, "${role.name}-${Game.time.toString()}", jsObject {
        memory = creepMemory
    })

    if (status == OK) role.onSpawn(room, creepMemory)

    return status == OK
}