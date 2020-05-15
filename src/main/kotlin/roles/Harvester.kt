package roles

import ext.findBestSpawn
import memory.SourceAssignment
import memory.isDepositing
import memory.isGathering
import memory.setDepositing
import memory.setGathering
import memory.source
import memory.sources
import memory.target
import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_SOURCES
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.RESOURCE_ENERGY
import screeps.api.Source
import screeps.api.WORK
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.jsObject

object Harvester : IRole {
    override val name = "harvester"

    override fun spawn(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }

    override fun loop(creep: Creep) {
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

    private fun Creep.getSource(): Source? {
        if (memory.source != null) {
            return Game.getObjectById(memory.source)
        }

        // Assign source
        if (room.memory.sources == null) {
            println(room.memory.sources)
            room.memory.sources = room.find(FIND_SOURCES)
                    .map { jsObject<SourceAssignment> { id = it.id } }
                    .toTypedArray()
        }

        println(JSON.stringify(room.memory.sources))

        val assignment = room.memory.sources!!.find { it.harvester == null }

        if (assignment == null) {
            println("Failed to find assignment for $name")
            return null
        }

        assignment.harvester = name
        memory.source = assignment.id
        println("Assigned $name to source $id in $room")
        return Game.getObjectById(memory.source)
    }

 }