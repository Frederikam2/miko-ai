package roles

import ext.assignSource
import ext.findBestSpawn
import ext.getSource
import memory.isDepositing
import memory.isGathering
import memory.setDepositing
import memory.setGathering
import memory.target
import screeps.api.*
import screeps.api.structures.StructureSpawn

object Harvester : IRole {
    override val name = "harvester"

    override fun spawn(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }

    override fun loop(creep: Creep) {
        if (creep.memory.target == null) creep.room.assignSource(creep)

        // Empty "belly"
        if (creep.store.getUsedCapacity() == 0) {
            creep.memory.setGathering()
            creep.memory.target = creep.room.getSource(creep)?.id
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
            val source = Game.getObjectById<Source>(creep.memory.target)!!
            if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source)
            }
        }
    }

 }