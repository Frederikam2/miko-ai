package layout

import screeps.api.Room
import screeps.api.RoomPosition

object ManualPattern : IBuildPattern {
    override val name = "manual"
    override fun buildNext(room: Room) = Unit
    override fun canBuildRoad(pos: RoomPosition) = true
}
