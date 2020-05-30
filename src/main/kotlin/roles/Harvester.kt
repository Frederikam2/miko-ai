package roles

import ext.*
import memory.*
import screeps.api.*
import screeps.api.structures.StructureContainer
import screeps.utils.memory.memory
import util.Logger

object Harvester : IRole {
    override val name = "harvester"
    private var CreepMemory.isHarvesting by memory { false }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant>? {
        return when {
            budget >= 300 -> arrayOf(WORK, CARRY, CARRY, MOVE, MOVE) // 300
            else -> arrayOf(WORK, CARRY, MOVE) // 200
        }
    }

    override fun run(creep: Creep) {
        val sourceAssignment = creep.room.getOrAssignSource(creep) ?: return
        val source = Game.getObjectById<Source>(sourceAssignment.id)!!
        // TODO: check to make sure actually exists

        if (creep.homeRoomMemory.primitiveHarvesters) {
            handlePrimitiveMode(creep, source)

            return
        }

        if (handleContainer(creep, source)) return

        when (val status = creep.harvest(source)) {
            OK, ERR_NOT_ENOUGH_RESOURCES -> return
            ERR_NOT_IN_RANGE -> {
                creep.warn("Having to move to assigned source to harvest", true)
                creep.moveTo(source)
            }
            else -> {
                creep.error("Failed to harvest from assigned source: '$status'", true)
            }
        }
    }

    private fun handlePrimitiveMode(creep: Creep, source: Source) {
        // Store is full
        if (creep.store.isFull())
            creep.memory.isHarvesting = false

        if (creep.store.isEmpty())
            creep.memory.isHarvesting = true

        if (creep.memory.isHarvesting) {
            if (creep.harvest(source) == ERR_NOT_IN_RANGE) creep.moveTo(source)
        } else {
            val spawn = creep.room.findBestSpawn()

            when (creep.transfer(spawn, RESOURCE_ENERGY)) {
                ERR_FULL -> {
                    // if spawn full and we have at least 50% free room to carry, go get more energy
                    val cap = (creep.store.getCapacity() ?: 100).toFloat()
                    val percentFree = ((creep.store.getFreeCapacity() / cap) * 100)
                    if (percentFree >= 50) creep.memory.isHarvesting = true
                }
                ERR_NOT_IN_RANGE -> creep.moveTo(spawn)
            }
        }
    }

    /**
     * Handles container logic.
     * @return true if this creep if busy working on the container for this turn
     */
    private fun handleContainer(creep: Creep, source: Source): Boolean {
        if (creep.store.getUsedCapacity() < 25) return false

        val assignment = creep.room.getOrAssignSource(creep) ?: return false
        if (assignment.container == null) {
            var pos = assignment.containerPos
            if (pos == null) {
                // We have never placed a container for this source. The ID is produced on the following tick
                createContainerSite(creep.room, source, assignment)
                return false
            }

            // Check if the container site is placed, but not remembered
            pos = RoomPosition(pos.x, pos.y, pos.roomName)
            val site = pos.lookFor(LOOK_CONSTRUCTION_SITES)!!.firstOrNull { it.structureType == STRUCTURE_CONTAINER }
            if (site != null) {
                assignment.container = site.id
                println("Found container site at $pos with ID ${site.id}")
            }

            // Check if there is already a container at the position
            // We reach this state with any new container
            val container = pos.lookFor(LOOK_STRUCTURES)!!.firstOrNull { it.structureType == STRUCTURE_CONTAINER }
            if (container != null) {
                assignment.container = container.id
                println("Found container at $pos with ID ${container.id}")
            }

            if (assignment.container == null) {
                println("Couldn't find or create container or container site!")
            }
        }

        return false

//        val site = Game.constructionSites[assignment.container!!]
//        if (site != null) {
//            if (creep.build(site) == ERR_NOT_IN_RANGE) {
//                creep.moveTo(site)
//            }
//            return true
//        }
//
//        // Check if the container needs repair, if it exists
//        val container = Game.getObjectById<StructureContainer>(assignment.container)
//        return if (container != null) {
//            if (container.hitsMax - container.hits > 100) {
//                if (creep.repair(container) == ERR_NOT_IN_RANGE) {
//                    creep.moveTo(container)
//                }
//                return true
//            }
//            return false
//        } else {
//            println("Warning: Expected a site or container to be here")
//            false
//        }
    }

    /**
     * Create a container construction site.
     */
    private fun createContainerSite(room: Room, source: Source, assignment: SourceAssignment) {
        var position = assignment.containerPos
        if (position == null) {
            val path = PathFinder.search(room.find(FIND_MY_SPAWNS).first().pos, source.pos)
            position = path.path.last()
            assignment.containerPos = position
        }

        Logger.info("Creating container at $position")
        position.createConstructionSite(STRUCTURE_CONTAINER)
    }
}
