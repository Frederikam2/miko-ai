package starter


import memory.role
import memory.sources
import roles.IRole
import roles.roles
import screeps.api.Creep
import screeps.api.Game
import screeps.api.Memory
import screeps.api.Record
import screeps.api.component1
import screeps.api.component2
import screeps.api.get
import screeps.api.iterator
import screeps.api.structures.StructureSpawn
import screeps.api.values
import screeps.utils.contains
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete
import strucures.runSpawnLogic

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns.values.firstOrNull() ?: return

    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)

    for ((_, creep) in Game.creeps) {
        val role = roles[creep.memory.role] as IRole?
        try {
            role?.loop(creep)
        } catch (e: Throwable) {
            println("${creep.name}: $e")
        }
    }

    for ((_, room) in Game.rooms) {
        try {
            runSpawnLogic(room)
        } catch (e: Throwable) {
            println("Failed doing spawn logic in $room")
            console.log(e)
        }
    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }

    for((_, room) in Game.rooms) {
        val sources = room.memory.sources ?: continue
        sources.forEach {
            if (!Game.creeps.contains(it.harvester ?: "")) {
                it.harvester = null
            }
        }
    }
}
