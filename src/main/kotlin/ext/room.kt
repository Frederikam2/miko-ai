package ext

import screeps.api.FIND_MY_SPAWNS
import screeps.api.Room
import screeps.api.structures.StructureSpawn

fun Room.findBestSpawn(): StructureSpawn {
    val spawns = this.find(FIND_MY_SPAWNS)

    return spawns[0]
}
