package util

import ext.findBestSpawn
import ext.isEmpty
import memory.*
import screeps.api.*

fun dumpEnergySpawn(creep: Creep) {
    val spawn = creep.homeRoom?.findBestSpawn()
    if (spawn !== null && !creep.store.isEmpty()) {
        if (creep.transfer(spawn, RESOURCE_ENERGY) != OK)
            creep.moveTo(spawn)
    }
}

fun noHarvestersBehavior(creep: Creep, dumpEnergy: Boolean = false): Boolean {
    if (!creep.homeRoomMemory.noHarvesters) return false
    if (dumpEnergy) dumpEnergySpawn(creep)

    return true
}

fun primitiveHarvestersBehavior(creep: Creep, dumpEnergy: Boolean = false): Boolean {
    if (!creep.homeRoomMemory.primitiveHarvesters) return false
    if (dumpEnergy) dumpEnergySpawn(creep)

    return true
}

fun limitedHaulersBehavior(creep: Creep, dumpEnergy: Boolean = false): Boolean {
    if (creep.homeRoomMemory.limitedHaulers) return false
    if (dumpEnergy) dumpEnergySpawn(creep)

    return true
}