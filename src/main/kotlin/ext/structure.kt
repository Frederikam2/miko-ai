package ext

import screeps.api.structures.Structure


/**
 * Delta between hitsMax and hits
 */
val Structure.hpDelta get() = hitsMax - hits

/**
 * The structures percentage of from full hp
 */
val Structure.hpPercent get() = ((hitsMax - hpDelta) / hitsMax) * 100

fun Structure.isFullHp() = hits >= hitsMax
fun Structure.isMissingHp() = !isFullHp()


