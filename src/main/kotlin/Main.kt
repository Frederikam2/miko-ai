import memory.role
import memory.sources
import screeps.api.*
import screeps.utils.contains
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete
import strucures.runSpawnLogic
import util.TickData

/**
 * Called by Screeps
 */
@Suppress("unused")
fun loop() {
    //delete memories of creeps that have passed away
    houseKeeping(Game.creeps)
    TickData.refresh()

    for ((_, creep) in Game.creeps) {
        val role = creep.memory.role
        try {
            role.loop(creep)
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

val Room.myCreeps get() = find(FIND_MY_CREEPS)

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            console.log("deleting obsolete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }

    for ((_, room) in Game.rooms) {
        val sources = room.memory.sources ?: continue
        sources.forEach {
            if (!Game.creeps.contains(it.harvester ?: "")) {
                it.harvester = null
            }
            if (!Game.creeps.contains(it.hauler ?: "")) {
                it.harvester = null
            }
        }
    }
}
