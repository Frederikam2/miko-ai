package util

import ext.getPercentUsed
import memory.role
import memory.room
import roles.Harvester
import roles.Hauler
import screeps.api.*
import screeps.api.structures.StructureContainer

object TickData {

    var creepsByHome: Map<String, List<Creep>> = mapOf()
        private set
    private var energyLevels: Map<String, EnergyLevel> = mapOf()

    fun refresh() {
        val newMap = mutableMapOf<String, MutableList<Creep>>()
        Game.creeps.values.forEach {
            newMap.getOrPut(it.memory.room) { mutableListOf() }.add(it)
        }
        creepsByHome = newMap
    }

    fun refreshEnergyLevels() {
        val newMap = mutableMapOf<String, EnergyLevel>()
        Game.rooms.values.forEach {
            if (it.controller?.run { my && level > 0 } == true) return@forEach
            newMap[it.name] = computeEnergyLevel(it)
        }
        energyLevels = newMap
    }

    private fun computeEnergyLevel(room: Room): EnergyLevel {
        val creeps = creepsByHome[room.name] ?: emptyList()

        var harvesters = 0
        var haulers = 0
        creeps.forEach {
            when(it.memory.role) {
                is Harvester -> harvesters++
                is Hauler -> haulers++
            }
        }

        if (haulers == 0) return PrimitiveMode

        val energyPercentage = getEnergyFillPercentage(room)
        val sourceCount = room.find(FIND_SOURCES).size
        if (harvesters + haulers < sourceCount * 2 && energyPercentage < 20) {
            return LowEnergyLevel
        }

        val hasStorage = room.storage != null

        return if (hasStorage) when {
            energyPercentage > 75 -> HighEnergyLevel(3)
            energyPercentage > 50 -> HighEnergyLevel(2)
            energyPercentage > 25 -> HighEnergyLevel(1)
            else -> MediumEnergyLevel
        } else when {
            energyPercentage > 75 -> HighEnergyLevel(2)
            energyPercentage > 35 -> HighEnergyLevel(1)
            else -> MediumEnergyLevel
        }
    }

    /**
     * Calculates the percentage (0-100) of energy fill in either storage or containers.
     * Storage is preferred.
     */
    private fun getEnergyFillPercentage(room: Room): Float = if (room.storage != null) {
        room.storage!!.store.getPercentUsed(RESOURCE_ENERGY)
    } else {
        // If storage is unavailable, use containers
        val containers = room.find(FIND_STRUCTURES).filter { it.structureType == STRUCTURE_CONTAINER }
                .unsafeCast<Array<StructureContainer>>()

        containers.fold(0f) {
            acc, c -> acc + c.store.getPercentUsed(RESOURCE_ENERGY)
        } / containers.size
    }

}