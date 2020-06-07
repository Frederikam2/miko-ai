package util

import screeps.api.BODYPART_COST
import screeps.api.BodyPartConstant
import screeps.api.get

val BodyPartConstant.cost: Int get() = BODYPART_COST[this]!!