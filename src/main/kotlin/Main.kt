import layout.LayoutController
import memory.role
import memory.sources
import screeps.api.*
import screeps.utils.contains
import screeps.utils.isEmpty
import screeps.utils.unsafe.delete
import strucures.Spawn
import util.Logger
import util.TickData

/**
 * How often to run Spawn logic (ticks)
 */
const val SPAWN_TICK_RATE = 1

/**
 * How often to run Layout controller logic (ticks)
 */
const val LAYOUT_CONTROLLER_TICK_RATE = 5

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
            role.run(creep)
        } catch (e: Throwable) {
            println("${creep.name}: $e")
        }
    }

    if (Game.time % SPAWN_TICK_RATE == 0) {
        for ((_, room) in Game.rooms) {
            try {
                Spawn.run(room)
            } catch (e: Throwable) {
                Logger.error("Spawn failure: $e", "room/${room.name}")
            }
        }
    }

    if (Game.time % LAYOUT_CONTROLLER_TICK_RATE == 0) {
        for ((_, room) in Game.rooms) {
            try {
                LayoutController.run(room)
            } catch (e: Throwable) {
                Logger.error("LayoutController failure: $e", "room/${room.name}")
            }
        }
    }
}

private fun houseKeeping(creeps: Record<String, Creep>) {
    if (Game.creeps.isEmpty()) return  // this is needed because Memory.creeps is undefined

    for ((creepName, _) in Memory.creeps) {
        if (creeps[creepName] == null) {
            Logger.info("Pruning obsolete memory", "creep/$creepName")
            delete(Memory.creeps[creepName])
        }
    }

    for ((_, room) in Game.rooms) {
        val sources = room.memory.sources ?: continue
        sources.forEach {
            if (!Game.creeps.contains(it.harvester ?: "")) it.harvester = null
            if (!Game.creeps.contains(it.hauler ?: "")) it.hauler = null
            if (Game.structures.contains(it.container ?: "")) it.container = null
        }
    }
}
