package memory

import roles.IRole
import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.api.Game
import screeps.api.Room
import screeps.api.get
import screeps.utils.memory.memory
import screeps.utils.memory.memoryWithSerializer

val CreepMemory.isGathering get() = state == "gathering"
val CreepMemory.isDepositing get() = state == "depositing"
fun CreepMemory.setGathering() { state = "gathering"}
fun CreepMemory.setDepositing() { state = "depositing"}

@Suppress("RemoveExplicitTypeArguments")
var CreepMemory.role: IRole by memoryWithSerializer<IRole>({ throw notInitialised() }, { it.name }, { roles.roles[it] as IRole })
var CreepMemory.room by memory<String> { throw notInitialised() }
val Creep.homeRoom get(): Room? = Game.rooms[memory.room]
var CreepMemory.target by memory<String>()
var CreepMemory.state by memory<String>()
var CreepMemory.source by memory<String>()

fun notInitialised(): IllegalStateException {
    throw IllegalStateException("This memory property must be initialised before being accessed")
}
