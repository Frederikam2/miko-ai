package util

import ext.*
import memory.*
import screeps.api.*
import screeps.utils.memory.memory

//private var CreepMemory.gatherTarget by memory { "" }
//fun gatherResource(creep: Creep, room: Room? = null, resource: ResourceConstant = RESOURCE_ENERGY): Boolean {
//    var selectedRoom = room
//    if (selectedRoom == null) {
//        // attempt to use homeRoom
//        if (creep.homeRoom != null) selectedRoom = creep.homeRoom
//    }
//
//    if (selectedRoom == null) return false

//    // use prior target or assign a new one
//    var target: StoreOwner? = Game.getObjectById(creep.memory.gatherTarget)
//    if (target == null) {
//        // use storage if it exists and is not empty
//        val storage = selectedRoom.storage
//        if (storage != null && storage.store.isNotEmpty()) target = storage

//        // no storage or it's empty, try to find a container
//        if (target == null) {
//            val containers = (selectedRoom.find(FIND_MY_STRUCTURES)
//                    .filter { it.structureType == STRUCTURE_CONTAINER } as List<StoreOwner>)
//                    .filter { it.store.isNotEmpty() }
//                    .sortedByDescending { it.store.getPercentUsed() }


//            if (containers.isNotEmpty()) target = containers.first()
//        }

//        // If all else fails pull from spawn
//        if (target == null) target = selectedRoom.findBestSpawn()
//
//        // save target in memory
//        creep.memory.gatherTarget = target.id
//    }

//    when (val status = creep.withdraw(target, RESOURCE_ENERGY)) {
//        OK, ERR_NOT_ENOUGH_RESOURCES -> Unit
//        ERR_NOT_IN_RANGE -> creep.moveTo(target)
//        else -> status.unexpected(creep, "withdrawing energy from container")
//    }
//}

fun dumpEnergySpawn(creep: Creep) {
    val spawn = creep.homeRoom?.findBestSpawn()
    if (spawn != null && !creep.store.isEmpty()) {
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
    if (!creep.homeRoomMemory.limitedHaulers) return false
    if (dumpEnergy) dumpEnergySpawn(creep)

    return true
}