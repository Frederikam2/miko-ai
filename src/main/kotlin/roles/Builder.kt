package roles

import ext.*
import memory.*
import screeps.api.*
import screeps.api.structures.Structure
import screeps.utils.memory.memory
import util.limitedHaulersBehavior
import util.noHarvestersBehavior
import util.primitiveHarvestersBehavior

object Builder : IRole {
    override val name = "builder"
    private var CreepMemory.isBuilding by memory { false }
    private var CreepMemory.target by memory{ "" }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant>? {
        return when {

            else -> arrayOf(WORK, CARRY, MOVE) // 200
        }
    }

    override fun run(creep: Creep) {
        if (creep.store.isFull()) {
            creep.memory.isBuilding = true
            creep.memory.target = ""
        }

        if (creep.store.isEmpty()) {
            creep.memory.isBuilding = false
            creep.memory.target = ""
        }

        // handle room behaviors
        if (noHarvestersBehavior(creep, true)) return
        if (primitiveHarvestersBehavior(creep, true)) return
        if (limitedHaulersBehavior(creep, true)) return

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

            var repairTarget = Game.getObjectById<Structure>(creep.memory.target)
            if (repairTarget == null) {
                repairTarget = homeRoom.find(FIND_STRUCTURES)
                        .filter { it.structureType == STRUCTURE_CONTAINER }
                        .filter { it.hitsMax - it.hits != 0 }
                        .maxBy { it.hitsMax - it.hits }

                if (repairTarget != null) creep.memory.target = repairTarget.id
            }

            if (repairTarget == null) {
                creep.warn("No build or repair targets available")

                return
            }

            when (val status = creep.repair(repairTarget)) {
                OK -> Unit
                ERR_NOT_IN_RANGE -> creep.moveTo(repairTarget)
                else -> status.unexpected(creep, "repairing structure")
            }
        } else {
//            val structures = homeRoom.find(FIND_STRUCTURES)

            var container: StoreOwner? = Game.getObjectById<StoreOwner>(creep.memory.target)
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
                OK, ERR_NOT_ENOUGH_RESOURCES -> Unit
                ERR_NOT_IN_RANGE -> creep.moveTo(container)
                else -> status.unexpected(creep, "withdrawing energy from container")
            }
        }
    }
}