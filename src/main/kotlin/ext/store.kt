package ext

import screeps.api.Store

fun Store.isFull() = getFreeCapacity() <= 0
fun Store.isEmpty() = getUsedCapacity() <= 0