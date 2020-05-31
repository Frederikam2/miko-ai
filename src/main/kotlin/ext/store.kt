package ext

import screeps.api.RESOURCE_ENERGY
import screeps.api.ResourceConstant
import screeps.api.Store


/**
 * Determine if store is full
 */
fun Store.isFull(resource: ResourceConstant = RESOURCE_ENERGY) = (getFreeCapacity(resource) ?: 0) == 0
fun Store.isNotFull(resource: ResourceConstant = RESOURCE_ENERGY) = !isFull(resource)

/**
 * Determine if store is empty
 */
fun Store.isEmpty(resource: ResourceConstant = RESOURCE_ENERGY) = (getUsedCapacity(resource) ?: 0) == 0
fun Store.isNotEmpty(resource: ResourceConstant = RESOURCE_ENERGY) = !isEmpty(resource)

/**
 * Get percentage of store that is being used
 */
fun Store.getPercentUsed(resource: ResourceConstant = RESOURCE_ENERGY): Float {
    val totalCapacity = getCapacity(resource) ?: return 0.00f
    val usedCapacity = getUsedCapacity(resource) ?: return 0.00f

    return (usedCapacity.toFloat() / totalCapacity.toFloat()) * 100
}
fun Store.getPercentFree(resource: ResourceConstant = RESOURCE_ENERGY) = 100.00f - getPercentUsed()

/**
 * Determine if store has less then threshold (inclusive) of given resource type
 */
fun Store.isLow(resource: ResourceConstant = RESOURCE_ENERGY, threshold: Int = 30) = getPercentUsed(resource) <= threshold
fun Store.isNotLow(resource: ResourceConstant, threshold: Int = 30) = !isLow(resource, threshold)