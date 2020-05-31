package ext

import screeps.api.ResourceConstant
import screeps.api.Store

fun Store.isFull() = getFreeCapacity() <= 0
fun Store.isNotFull() = getFreeCapacity() > 0
fun Store.isEmpty() = getUsedCapacity() <= 0
fun Store.isNotEmpty() = getUsedCapacity() > 0

fun Store.isLow(resourceConstant: ResourceConstant): Boolean {
    val capacity = getCapacity(resourceConstant) ?: return false
    val used = getUsedCapacity(resourceConstant) ?: return false

    val percentUsed: Float = (used.toFloat() / capacity.toFloat()) * 100
    if (percentUsed < 85) return true

    return false
}