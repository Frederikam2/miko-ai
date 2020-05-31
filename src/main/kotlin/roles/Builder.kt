package roles

import ext.*
import memory.*
import screeps.api.*
import screeps.api.structures.Structure
import screeps.utils.memory.memory
import util.limitedHaulersBehavior
import util.noHarvestersBehavior
import util.primitiveHarvestersBehavior

object Builder : IRole {
    override val name = "builder"
    private var CreepMemory.isBuilding by memory { false }
    private var CreepMemory.target by memory{ "" }
    private var CreepMemory.energyTarget by memory{ "" }

    /**
     * List of structures to monitor for repair
     */
    private val repairStructures = listOf(
            STRUCTURE_ROAD, STRUCTURE_CONTAINER,
            STRUCTURE_TOWER
    )

    override fun getSpawnParts(budget: Int, roomMemory: RoomMemory): Array<BodyPartConstant>? {
        return when {
            else -> arrayOf(WORK, CARRY, MOVE) // 200
        }
    }

    override fun run(creep: Creep) {
        if (creep.store.isFull()) {
            creep.memory.isBuilding = true
            creep.memory.energyTarget = ""
        }
        if (creep.store.isEmpty()) {
            creep.memory.isBuilding = false
            creep.memory.energyTarget = ""
        }

        // handle room behaviors
        if (noHarvestersBehavior(creep, true)) return
        if (primitiveHarvestersBehavior(creep)) return
        if (limitedHaulersBehavior(creep)) return

        val homeRoom = creep.homeRoom ?: return

        // find "emergency" repairs
        val emergencyRepairs = findRepairTargets(homeRoom, true)

        // emergency repairs take priority over sites
        var site: ConstructionSite? = null
        if (emergencyRepairs.isEmpty()) {
            // find active construction sites
            val sites = homeRoom.find(FIND_CONSTRUCTION_SITES)
                    .sortedByDescending { it.progress }

            site = sites.firstOrNull()
        }

        // assign target
        var target = Game.getObjectById<Structure>(creep.memory.target)
        if (target == null) {
            if (emergencyRepairs.isNotEmpty()) target = emergencyRepairs.first()
            else {
                val repairs = findRepairTargets(homeRoom, false)
                if (repairs.isNotEmpty()) {
                    target = repairs.first()
                    creep.warn("Started emergency repair on '${target.structureType}'")
                }
            }

            // save target
            if (target != null) creep.memory.target = target.id
        }

        if (site == null && target == null) {
            // TODO: See if maybe we can help other roles while we have nothing todo
            // TODO: we have no target so drop off any extra energy we might have
            return
        }

        // not currently building, but to be here means we have a target or site, so go get some energy
        if (!creep.memory.isBuilding) {
            getEnergy(creep, homeRoom)
            return
        }

        // handle construction sites
        if (site != null) {
            when (val status = creep.build(site)) {
                OK -> Unit
                ERR_NOT_IN_RANGE -> creep.moveTo(site)
                else -> status.unexpected(creep, "building construction site")
            }
            return
        }

        // handle repair targets
        if (target != null) {
            when (val status = creep.repair(target)) {
                OK -> {
                    // when target reaches 100% repaired we can hop off it
                    if (target.hpPercent >= 100) creep.memory.target = ""
                }
                ERR_NOT_IN_RANGE -> creep.moveTo(target)
                else -> status.unexpected(creep, "repairing structure")
            }

            return
        }
    }

    private fun getEnergy(creep: Creep, room: Room) {
        var energyTarget = Game.getObjectById<StoreOwner>(creep.memory.energyTarget)
        if (energyTarget == null)  {
            energyTarget = room.findBestStore(includeAssigned = true)

            creep.memory.energyTarget = energyTarget.id
        }

        when (val status = creep.withdraw(energyTarget, RESOURCE_ENERGY)) {
            OK -> Unit
            ERR_NOT_IN_RANGE -> creep.moveTo(energyTarget)
            ERR_NOT_ENOUGH_RESOURCES -> creep.memory.energyTarget = ""
            else -> status.unexpected(creep, "withdrawing energy")
        }
    }

    private fun findRepairTargets(room: Room, emergencyOnly: Boolean = false): List<Structure> {
        var targets = room.find(FIND_STRUCTURES)
                .filter { repairStructures.contains(it.structureType) }
                .filter { it.isMissingHp() }
                .sortedBy{ it.hpPercent }

        if (emergencyOnly) {
            targets = targets.filter { it.hpPercent <= 50 }

            if (targets.isNotEmpty()) return targets

            return emptyList()
        }

        return targets
    }
}