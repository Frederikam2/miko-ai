package memory

import screeps.api.CreepMemory
import screeps.utils.memory.memory

val CreepMemory.isGathering get() = state == "gathering"
val CreepMemory.isDepositing get() = state == "depositing"
fun CreepMemory.setGathering() { state = "gathering"}
fun CreepMemory.setDepositing() { state = "depositing"}

var CreepMemory.role by memory { "none" }
var CreepMemory.room by memory<String> { throw notInitialised() }
var CreepMemory.target by memory<String>()
var CreepMemory.state by memory<String>()
var CreepMemory.source by memory<String>()

fun notInitialised(): IllegalStateException {
    throw IllegalStateException("This memory property must be initialised before being accessed")
}
