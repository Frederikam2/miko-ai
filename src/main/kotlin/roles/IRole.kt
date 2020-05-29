package roles

import screeps.api.BodyPartConstant
import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.api.Room

val roles: dynamic = object {
    val upgrader = Upgrader
    val harvester = Harvester
    val hauler = Hauler
}

interface IRole {
    val name: String

    /**
     * @return an array of body parts to spawn, or null to not spawn
     */
    fun getSpawnParts(budget: Int): Array<BodyPartConstant>?

    /**
     * Lifecycle event: Called when a creep has started spawning
     */
    fun onSpawning(room: Room, memory: CreepMemory) {}

    fun run(creep: Creep)
}
