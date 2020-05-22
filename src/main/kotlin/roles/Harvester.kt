package roles

import ext.findBestSpawn
import ext.info
import memory.SourceAssignment
import memory.homeRoom
import memory.isDepositing
import memory.isGathering
import memory.room
import memory.setDepositing
import memory.setGathering
import memory.source
import memory.sources
import memory.target
import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.CONTROLLER_DOWNGRADE
import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.Game
import screeps.api.LOOK_CONSTRUCTION_SITES
import screeps.api.LOOK_STRUCTURES
import screeps.api.MOVE
import screeps.api.Memory
import screeps.api.PathFinder
import screeps.api.RESOURCE_ENERGY
import screeps.api.RoomPosition
import screeps.api.STRUCTURE_CONTAINER
import screeps.api.Source
import screeps.api.WORK
import screeps.api.get
import screeps.api.structures.StructureContainer
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.jsObject

object Harvester : IRole {
    override val name = "harvester"

    override fun spawn(budget: Int): Array<BodyPartConstant>? {
        return when {
            budget >= 300 -> arrayOf(WORK, CARRY, CARRY, MOVE, MOVE) // 300
            else -> arrayOf(WORK, CARRY, MOVE) // 200
        }
    }

    override fun loop(creep: Creep) {
        val source = creep.getSource() ?: return

        if (creep.room.controller!!.ticksToDowngrade < CONTROLLER_DOWNGRADE[creep.room.controller!!.level]!!/1.5  ) {
            onPrimitiveMode(creep)
            return
        }

        if(handleContainer(creep, source)) return
        onPrimitiveMode(creep)

        //val primitiveMode = true
        // (primitiveMode) onPrimitiveMode(creep)
    }

    private fun onPrimitiveMode(creep: Creep) {
        // Empty "belly"
        if (creep.store.getUsedCapacity() == 0) {
            creep.memory.setGathering()
            creep.memory.target = null
        }

        // Full "belly"
        if (creep.store.getFreeCapacity() == 0) {
            // change our state to hauling
            creep.memory.setDepositing()
            creep.memory.target = creep.room.findBestSpawn().id
        }

        if (creep.memory.isDepositing) {
            val spawn = Game.getObjectById<StructureSpawn>(creep.memory.target)!!
            if (creep.transfer(spawn, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(spawn)
            }
        }

        if (creep.memory.isGathering) {
            val source = creep.getSource() ?: return
            if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source)
            }
        }
    }

    private fun Creep.getAssignment() = Memory.rooms[memory.room]?.sources?.firstOrNull { it.harvester == id }
    private fun Source.getAssignment() = room.memory.sources?.firstOrNull { it.id == id }

    private fun Creep.getSource(): Source? {
        if (memory.source != null) {
            return Game.getObjectById(memory.source)
        }

        // Assign source
        if (room.memory.sources == null) {
            println("Room '${room.name}' sources: ${room.memory.sources}")
            room.memory.sources = room.find(FIND_SOURCES)
                    .map { jsObject<SourceAssignment> { id = it.id } }
                    .toTypedArray()
        }

        val assignment = room.memory.sources!!.find { it.harvester == null }

        if (assignment == null) {
            println("Failed to find assignment for $name")
            return null
        }

        assignment.harvester = name
        memory.source = assignment.id
        this.info("I've been assigned to '$id'", true)
        return Game.getObjectById(memory.source)
    }

    /**
     * Handles container logic.
     * @return true if this creep if busy working on the container for this turn
     */
    private fun handleContainer(creep: Creep, source: Source): Boolean {
        if (creep.store.getUsedCapacity() < 25) return false

        val assignment = source.getAssignment() ?: return false
        if (assignment.container == null) {
            var pos = assignment.containerPos
            if (pos == null) {
                // We have never placed a container for this source. The ID is produced on the following tick
                createContainerSite(creep, source)
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

        val site = Game.constructionSites[assignment.container!!]
        if (site != null) {
            if (creep.build(site) == ERR_NOT_IN_RANGE) {
                creep.moveTo(site)
            }
            return true
        }

        // Check if the container needs repair, if it exists
        val container = Game.getObjectById<StructureContainer>(assignment.container)
        return if (container != null) {
            if (container.hitsMax - container.hits > 100) {
                if (creep.repair(container) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(container)
                }
                return true
            }
            return false
        } else {
            println("Warning: Expected a site or container to be here")
            false
        }
    }

    /**
     * Create a container construction site.
     */
    private fun createContainerSite(creep: Creep, source: Source) {
        val assignment = source.getAssignment() ?: return
        var pos = assignment.containerPos?.apply { RoomPosition(x, y, roomName) }
        if (pos == null) {
            val path = PathFinder.search(creep.homeRoom!!.find(FIND_MY_SPAWNS).first().pos, source.pos)
            pos = path.path.last()
            assignment.containerPos = pos
        }
        println("Creating container at $pos")
        pos.createConstructionSite(STRUCTURE_CONTAINER)
    }

 }