package memory

import roles.IRole
import screeps.api.*
import screeps.utils.memory.memory
import screeps.utils.memory.memoryWithSerializer

@Suppress("RemoveExplicitTypeArguments")
var CreepMemory.role: IRole by memoryWithSerializer<IRole>({ throw notInitialised() }, { it.name }, { roles.roles[it] as IRole })
var CreepMemory.room by memory<String> { throw notInitialised() }

val Creep.homeRoom get(): Room? = Game.rooms[memory.room]
val Creep.homeRoomMemory
    get(): RoomMemory = Memory.rooms[memory.room] ?: throw IllegalStateException("Universe broke :c")

fun notInitialised(): IllegalStateException {
    throw IllegalStateException("This memory property must be initialised before being accessed")
}
