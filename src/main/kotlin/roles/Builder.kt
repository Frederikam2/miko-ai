package roles

import ext.*
import memory.homeRoom
import memory.homeRoomMemory
import memory.noHarvesters
import memory.notInitialised
import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureContainer
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureStorage
import screeps.utils.isNotEmpty
import screeps.utils.memory.memory
import screeps.utils.memory.memoryWithSerializer

object Builder : IRole {
    override val name = "builder"
    private var CreepMemory.isBuilding by memory { false }
    private var CreepMemory.container by memory<String>{ "" }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant>? {
        return when {
            else -> arrayOf(WORK, CARRY, MOVE) // 200
        }
    }

    override fun run(creep: Creep) {
        if (creep.store.isFull()) creep.memory.isBuilding = true
        if (creep.store.isEmpty()) creep.memory.isBuilding = false

        // No Harvesters: Behavior override
        if (creep.homeRoomMemory.noHarvesters && !creep.store.isEmpty()) {
            val spawn = creep.homeRoom?.findBestSpawn()
            if (spawn !== null) {
                if (creep.transfer(spawn, RESOURCE_ENERGY) != OK)
                    creep.moveTo(spawn)
            }
        }

        val homeRoom = creep.homeRoom
        if (homeRoom == null) {
            // We are not in home room for some reason. Let's move back
            creep.moveTo(RoomPosition(25, 25, creep.room.name))
            return
        }

        if (creep.memory.isBuilding) {
            val sites = homeRoom.find(FIND_CONSTRUCTION_SITES)
                    .sortedByDescending { it.progress }

            val site = sites.firstOrNull()
            if (site != null) {
                when(val status = creep.build(site)) {
                    OK -> Unit
                    ERR_NOT_IN_RANGE -> creep.moveTo(site)
                    else -> status.unexpected(creep, "building construction site")
                }

                return
            }

            val repair = (homeRoom.find(FIND_STRUCTURES)
                    .filter { it.structureType == STRUCTURE_CONTAINER })
                    .filter { it.hitsMax - it.hits != 0 }
                    .maxBy { it.hitsMax - it.hits }

            if (repair == null) {
                creep.info("Nothing todo", true)
                return
            }

            when (val status = creep.repair(repair)) {
                OK -> Unit
                ERR_NOT_IN_RANGE -> creep.moveTo(repair)
                else -> status.unexpected(creep, "repairing structure '${repair}'")
            }
        } else {
            val structures = homeRoom.find(FIND_STRUCTURES)

            var container: StoreOwner? = Game.getObjectById<StoreOwner>(creep.memory.container)
            if (container == null) {
                // default to spawn
                container = homeRoom.findBestSpawn()

                // find storage, and if its not empty use it
//                val storage: StructureStorage = structures.firstOrNull { it.structureType == STRUCTURE_STORAGE } as StructureStorage
//                if (storage.store.isNotEmpty()) container = storage
//                else {
//                    // storage didn't exist or was empty find fullest container
//                    var containers = (structures.filter{ it.structureType == STRUCTURE_CONTAINER }) as List<StoreOwner>
//                    if (containers.isNotEmpty()) {
//                        containers = containers.filter { it.store.getUsedCapacity() > 0 }
//                                .sortedBy { it.store.getFreeCapacity() }
//
//                        // TODO: find best container, IE one that has enough energy to fill us, etc...
//
//                        if (containers.isNotEmpty()) container = containers.first()
//                    }
//                }
            }

            when (val status = creep.withdraw(container, RESOURCE_ENERGY)) {
                OK -> Unit
                ERR_NOT_IN_RANGE -> creep.moveTo(container)
                else -> status.unexpected(creep, "withdrawing energy from '${container}'")
            }
        }
    }
}