package memory

import screeps.api.MemoryMarker
import screeps.api.RoomPosition
import screeps.utils.memory.MemoryMappingDelegate
import kotlin.properties.ReadWriteProperty

fun positionDelegate(): ReadWriteProperty<MemoryMarker, RoomPosition?> {
    @Suppress("RemoveExplicitTypeArguments")
    return MemoryMappingDelegate<RoomPosition?>({ null }, { "${it!!.x}:${it.y}:${it.roomName}"}, { str ->
        val (x, y, room) = str.split(':')
        RoomPosition(x.toInt(), y.toInt(), room)
    })
}

fun positionDelegate(default: () -> RoomPosition): ReadWriteProperty<MemoryMarker, RoomPosition> {
    return MemoryMappingDelegate(default, { "${it.x}:${it.y}:${it.roomName}"}, { str ->
        val (x, y, room) = str.split(':')
        RoomPosition(x.toInt(), y.toInt(), room)
    })
}
