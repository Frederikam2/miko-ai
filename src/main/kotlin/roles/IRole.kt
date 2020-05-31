package roles

import screeps.api.*

@Suppress("unused")
val roles: dynamic = object {
    val upgrader = Upgrader
    val harvester = Harvester
    val hauler = Hauler
    val builder = Builder
}

interface IRole {
    val name: String

    /**
     * @return an array of body parts to spawn, or null to not spawn
     */
    fun getSpawnParts(budget: Int, roomMemory: RoomMemory): Array<BodyPartConstant>?

    /**
     * Lifecycle event: Called when a creep has started spawning
     */
    fun onSpawning(room: Room, memory: CreepMemory) {}

    fun run(creep: Creep)
}
