package roles

import ext.*
import memory.*
import screeps.api.*
import screeps.api.structures.StructureContainer
import screeps.utils.memory.memory
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

    override fun run(creep: Creep) {
        if (creep.store.isFull()) creep.memory.isGathering = false
        else if (creep.store.isEmpty()) creep.memory.isGathering = true

        // No Harvesters: Behavior override
        if (creep.homeRoomMemory.noHarvesters && !creep.store.isEmpty()) {
            val spawn = creep.homeRoom?.findBestSpawn()
            if (spawn !== null) {
                if (creep.transfer(spawn, RESOURCE_ENERGY) != OK)
                    creep.moveTo(spawn)
            }
        }

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

        val container = containerPos.lookFor(LOOK_STRUCTURES)!!.firstOrNull { it.structureType == STRUCTURE_CONTAINER }
        if (container != null) {
            when (val status = creep.withdraw(container as StoreOwner, RESOURCE_ENERGY)) {
                OK, ERR_NOT_ENOUGH_RESOURCES -> Unit
                else -> status.unexpected(creep, "withdrawling from container")
            }
            return
        }

        val dropped = containerPos.lookFor(LOOK_ENERGY)?.firstOrNull()
        if (dropped != null) {
            creep.pickup(dropped).expectOk(creep, "picking up")
            return
        }
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