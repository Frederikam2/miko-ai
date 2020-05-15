package roles

import memory.isDepositing
import memory.isGathering
import memory.setDepositing
import memory.setGathering
import memory.target
import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_SPAWNS
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.RESOURCE_ENERGY
import screeps.api.WORK
import screeps.api.structures.StructureController
import screeps.api.structures.StructureSpawn

object Upgrader : IRole {
    override val name = "upgrader"
    override fun loop(creep: Creep) {
        // Empty "belly"
        if (creep.store.getUsedCapacity() == 0) {
            creep.memory.setGathering()
            creep.memory.target = creep.room.find(FIND_MY_SPAWNS).firstOrNull()?.id
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
            val target = Game.getObjectById<StructureSpawn>(creep.memory.target)?: return
            creep.transfer(target, RESOURCE_ENERGY)
            if (creep.withdraw(target, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(target)
            }
        }
    }

    override fun spawn(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }
}