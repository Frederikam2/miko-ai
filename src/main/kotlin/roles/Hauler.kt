package roles

import ext.findBestSpawn
import ext.warn
import memory.*
import screeps.api.*
import screeps.utils.memory.memory
import util.expectOk
import util.unexpected
import kotlin.math.max

object Hauler : IRole {
    override val name = "hauler"
    private var CreepMemory.isGathering: Boolean by memory { true }

    override fun getSpawnParts(budget: Int): Array<BodyPartConstant> {
        // TODO: Optimize for roads
        val sizeStepCost = BODYPART_COST[CARRY]!! + BODYPART_COST[MOVE]!!
        val steps = max(1, budget / sizeStepCost)
        val parts = mutableListOf<BodyPartConstant>()

        repeat(steps) { parts.add(CARRY) }
        repeat(steps) { parts.add(MOVE) }

        return parts.toTypedArray()
    }

    override fun loop(creep: Creep) {
        if (creep.store.getFreeCapacity() <= 0) creep.memory.isGathering = false
        else if (creep.store.getUsedCapacity() <= 0) creep.memory.isGathering = true

        if (creep.memory.isGathering) gather(creep)
        else deliver(creep)
    }

    private fun gather(creep: Creep) {
        var assignment = creep.homeRoomMemory.getExistingAssignment(creep)

        if (assignment == null) {
            val homeRoom = creep.homeRoom
            if (homeRoom == null) {
                // If we have no assignment, and we are not at home, move there so we can get visibility
                creep.moveTo(RoomPosition(25, 25, creep.room.name))
                return
            } else {
                // Assign a new SourceAssignment
                assignment = homeRoom.getOrAssignSource(creep)
            }
        }

        assignment ?: return
        val containerPos = assignment.containerPos

        if (containerPos == null) {
            creep.warn("No container position", true)
            return
        }

        if(!creep.pos.isNearTo(containerPos)) {
            creep.moveTo(containerPos)
            return
        }

        val container = assignment.containerStruct

        if (container != null) {
            creep.withdraw(container, RESOURCE_ENERGY).expectOk(creep, "withdrawing from container")
            return
        }

        val dropped = containerPos.lookFor(LOOK_ENERGY)?.firstOrNull()
        if (dropped == null) {
            creep.say("Zzz")
            return
        }

        creep.pickup(dropped).expectOk(creep, "picking up")
    }

    private fun deliver(creep: Creep) {
        // TODO: Deliver to spawn room
        // TODO: Deliver to other structures than spawn

        val spawn = creep.room.findBestSpawn()
        if (creep.pos.isNearTo(spawn)) {
            when(val status = creep.transfer(spawn, RESOURCE_ENERGY)) {
                OK, ERR_FULL -> Unit
                else -> status.unexpected(creep, "delivering energy")
            }
        } else {
            creep.moveTo(spawn).expectOk(creep, "moving")
        }
    }
}