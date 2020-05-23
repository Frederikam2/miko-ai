package roles

import memory.room
import screeps.api.*
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

    override fun loop(creep: Creep) {
        if (creep.store.getFreeCapacity() <= 0) creep.memory.isGathering = false
        else if (creep.store.getUsedCapacity() <= 0) creep.memory.isGathering = true

        if (creep.memory.isGathering) gather(creep)
        else deliver(creep)
    }

    private fun gather(creep: Creep) {
        if (creep.room.name != creep.memory.room) {
            creep.moveTo(RoomPosition(25, 25, creep.room.name))
            return
        }

        // TODO
    }

    private fun deliver(creep: Creep) {
        // TODO
    }
}