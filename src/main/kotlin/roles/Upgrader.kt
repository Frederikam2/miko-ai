package roles

import ext.findBestSpawn
import ext.isEmpty
import ext.isFull
import memory.homeRoom
import memory.homeRoomMemory
import memory.noHarvesters
import screeps.api.*
import screeps.utils.memory.memory

object Upgrader : IRole {
    override val name = "upgrader"
    private var CreepMemory.isUpgrading by memory { false }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }

    override fun run(creep: Creep) {
        if (creep.store.isFull()) creep.memory.isUpgrading = true
        if (creep.store.isEmpty()) creep.memory.isUpgrading = false

        // No Harvesters: Behavior override
        if (creep.homeRoomMemory.noHarvesters && !creep.store.isEmpty()) {
            val spawn = creep.homeRoom?.findBestSpawn()
            if (spawn !== null) {
                if (creep.transfer(spawn, RESOURCE_ENERGY) != OK)
                    creep.moveTo(spawn)
            }
        }

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