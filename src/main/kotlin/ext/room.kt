package ext

import memory.sources
import screeps.api.*
import screeps.api.structures.StructureSpawn

fun Room.findBestSpawn(): StructureSpawn {
    val spawns = this.find(FIND_MY_SPAWNS)

    return spawns[0]
}

/**
 * Find the best storage structure for resource type
 */
fun Room.findBestStore(resource: ResourceConstant = RESOURCE_ENERGY, includeAssigned: Boolean = false): StoreOwner {
    var target: StoreOwner? = null

    // use storage if it exists and is not empty
    val storage = storage
    if (storage != null) target = storage

    // no storage
    if (target == null) {
        var containers = (find(FIND_MY_STRUCTURES)
                .filter { it.structureType == STRUCTURE_CONTAINER } as List<StoreOwner>)
                .filter { it.store.isNotEmpty() }
                .sortedByDescending { it.store.getPercentUsed() }

        // remove assigned containers
        if (!includeAssigned && memory.sources != null) {
            val assigned = memory.sources!!.filter { it.container != null }.map { it.container }

            containers = containers.filter { !assigned.contains(it.id) }
        }

        if (containers.isNotEmpty()) target = containers.first()
    }

    // If all else fails use spawn
    if (target == null) target = findBestSpawn()

    return target
}
