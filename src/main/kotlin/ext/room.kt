package ext

import screeps.api.Creep
import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.Room
import screeps.api.RoomMemory
import screeps.api.structures.StructureSpawn
import screeps.utils.memory.memory

var RoomMemory.sources by memory<MutableMap<String, MutableList<String>>?>()

fun Room.assignSource(creep: Creep): String {
    if (memory.sources == null) {
        memory.sources = find(FIND_SOURCES)
                .associate { it.id to mutableListOf<String>() }
                .toMutableMap()
    }

    val entry = memory.sources!!.entries.first { it.value.isEmpty() }
    this.memory.sources!![entry.key]?.add(creep.id)

    return entry.key
}

fun Room.findBestSpawn(): StructureSpawn {
    val spawns = this.find(FIND_MY_SPAWNS)

    return spawns[0]
}
