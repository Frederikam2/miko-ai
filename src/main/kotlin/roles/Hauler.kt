package roles

import ext.*
import memory.*
import screeps.api.*
import screeps.api.structures.StructureContainer
import screeps.api.structures.StructureExtension
import screeps.utils.memory.memory
import util.noHarvestersBehavior
import kotlin.math.max

object Hauler : IRole {
    override val name = "hauler"
    private var CreepMemory.isGathering by memory { false }
    private var CreepMemory.target by memory { "" }

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
        if (creep.store.isFull()) {
            creep.memory.isGathering = false
            creep.memory.target = ""
        } else if (creep.store.isEmpty()) {
            creep.memory.isGathering = true
            creep.memory.target = ""
        }

        if (noHarvestersBehavior(creep, true)) return

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
                else -> status.unexpected(creep, "withdrawing from container")
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
        val homeRoom = creep.homeRoom
        if (homeRoom == null) {
            // If we have no assignment, and we are not at home, move there so we can get visibility
            creep.moveTo(RoomPosition(25, 25, creep.room.name))
            return
        }

        var target = Game.getObjectById<StoreOwner>(creep.memory.target)
        if (target == null) {
            // handle extensions
            val extensions = homeRoom.find(FIND_MY_STRUCTURES).filter { it.structureType == STRUCTURE_EXTENSION } as List<StructureExtension>
            if (extensions.isNotEmpty()) {
                val extension = extensions
                        .filter { it.store.getFreeCapacity(RESOURCE_ENERGY) > 0 }
                        .maxBy { it.store.getFreeCapacity(RESOURCE_ENERGY) ?: 0 }

                if (extension != null) target = extension
            }

            // handle spawn
            if (target == null) {
                val spawn = homeRoom.findBestSpawn()
                if (spawn.store.isEmpty() || spawn.store.isLow(RESOURCE_ENERGY)) target = spawn
            }

            // handle storage
            if (target == null) {
                val storage = homeRoom.storage
                if (storage != null && storage.store.isNotFull()) target = storage
            }

            // if all else fails, default to spawn
            if (target == null) target = homeRoom.findBestSpawn()

            creep.memory.target = target.id
        }

        when (val status = creep.transfer(target, RESOURCE_ENERGY)) {
            OK -> Unit
            ERR_NOT_IN_RANGE -> creep.moveTo(target)
            ERR_FULL -> creep.memory.target = ""
            else -> status.unexpected(creep, "delivering energy")
        }
    }
}