package util

import memory.room
import screeps.api.Creep
import screeps.api.Game
import screeps.api.values

object TickData {

    var creepsByHome: Map<String, List<Creep>> = mapOf()
        private set

    fun refresh() {
        val newMap = mutableMapOf<String, MutableList<Creep>>()
        Game.creeps.values.forEach {
            newMap.getOrPut(it.memory.room) { mutableListOf() }.add(it)
        }
        creepsByHome = newMap
    }

}