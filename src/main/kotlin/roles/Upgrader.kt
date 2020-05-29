package roles

import ext.findBestSpawn
import screeps.api.*
import screeps.utils.memory.memory

object Upgrader : IRole {
    override val name = "upgrader"
    private var CreepMemory.isUpgrading by memory { false }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }

    override fun run(creep: Creep) {
        if (creep.store.getFreeCapacity() <= 0)
            creep.memory.isUpgrading = true

        if (creep.store.getUsedCapacity() <= 0)
            creep.memory.isUpgrading = false

        if (creep.memory.isUpgrading) {
            val controller = creep.room.controller!!
            if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller)
            }
        } else {
            val spawn = creep.room.findBestSpawn()
            if (creep.withdraw(spawn, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(spawn)
            }
        }
    }

}