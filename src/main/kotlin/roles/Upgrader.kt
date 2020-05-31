package roles

import ext.findBestSpawn
import ext.isEmpty
import ext.isFull
import screeps.api.*
import screeps.utils.memory.memory
import util.limitedHaulersBehavior
import util.noHarvestersBehavior

object Upgrader : IRole {
    override val name = "upgrader"
    private var CreepMemory.isUpgrading by memory { false }

    override fun getSpawnParts(budget: Int, roomMemory: RoomMemory): Array<BodyPartConstant>? {
        return arrayOf(WORK, CARRY, MOVE)
    }

    override fun run(creep: Creep) {
        if (creep.store.isFull()) creep.memory.isUpgrading = true
        if (creep.store.isEmpty()) creep.memory.isUpgrading = false

        // handle room behaviors
        if (noHarvestersBehavior(creep, true)) return
        if (limitedHaulersBehavior(creep, true)) return

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