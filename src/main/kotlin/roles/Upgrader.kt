package roles

import ext.findBestSource
import memory.*
import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.Source
import screeps.api.WORK
import screeps.api.structures.StructureController

object Upgrader : IRole {
    override val name = "upgrader"
    override fun loop(creep: Creep) {
        // Empty "belly"
        if (creep.store.getUsedCapacity() == 0) {
            creep.memory.setGathering()
            creep.memory.target = creep.room.findBestSource().id
        }

        // Full "belly"
        if (creep.store.getFreeCapacity() == 0) {
            // change our state to hauling
            creep.memory.setDepositing()
            creep.memory.target = creep.room.controller?.id
        }

        if (creep.memory.isDepositing) {
            val controller = Game.getObjectById<StructureController>(creep.memory.target)!!
            if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller)
            }
        }

        if (creep.memory.isGathering) {
            val source = Game.getObjectById<Source>(creep.memory.target)!!
            if (creep.harvest(source) == ERR_NOT_IN_RANGE) {
                creep.moveTo(source)
            }
        }
    }

    override fun spawn(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }
}