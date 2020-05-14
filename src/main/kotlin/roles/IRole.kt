package roles

import screeps.api.BodyPartConstant
import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.api.Room

interface IRole {
    val name: String
    fun loop(creep: Creep)

    /**
     * @return an array of body parts to spawn, or null to not spawn
     */
    fun spawn(budget: Int): Array<BodyPartConstant>?
    fun onSpawn(room: Room, memory: CreepMemory) {}
}
