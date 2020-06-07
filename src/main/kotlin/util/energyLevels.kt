package util

import screeps.api.CARRY
import screeps.api.MOVE
import screeps.api.WORK

sealed class EnergyLevel {
    /** Maximum energy budget to use while spawning */
    open val budgetCeiling: Int = Int.MAX_VALUE
}

/**
 * Energy level is dangerously low. Tiny harvesters will collect and deliver energy.
 * Triggered by having no haulers.
 */
object PrimitiveMode : EnergyLevel() {
    override val budgetCeiling = WORK.cost + CARRY.cost + MOVE.cost
}

/**
 * Energy production is barely on its feet. Spawn a small upgrader only if necessary.
 */
object LowEnergyLevel : EnergyLevel()

/**
 * Our energy production is optimal and we can afford to spend energy on buildings or upgrades
 * Max 0-1 builder and 1 upgrader.
 */
object MediumEnergyLevel : EnergyLevel()

/**
 * Abundant energy resources in storage. In the absence of a storage, containers are evaluated.
 * Like [MediumEnergyLevel], but will spawn larger upgraders and builders, and potentially more of them.
 */
class HighEnergyLevel(val upgradersOrBuilders: Int) : EnergyLevel()
