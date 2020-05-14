package memory

import screeps.api.CreepMemory
import screeps.utils.memory.memory

val CreepMemory.isGathering get() = state == "gathering"
val CreepMemory.isDepositing get() = state == "depositing"
fun CreepMemory.setGathering() { state = "gathering"}
fun CreepMemory.setDepositing() { state = "depositing"}

var CreepMemory.role by memory<String?>()
var CreepMemory.room by memory<String?>()
var CreepMemory.spawn by memory<String?>()
var CreepMemory.target by memory<String?>()
var CreepMemory.state by memory<String?>()
