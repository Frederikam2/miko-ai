package memory

import screeps.api.MemoryMarker
import screeps.api.RoomPosition
import screeps.utils.memory.MemoryMappingDelegate
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private fun RoomPosition.serialise() = "$x:$y:$roomName"
private fun deserialiseRoom(str: String): RoomPosition {
    val (x, y, room) = str.split(':')
    return RoomPosition(x.toInt(), y.toInt(), room)
}

fun memoryPositionDelegate(): ReadWriteProperty<MemoryMarker, RoomPosition?> {
    @Suppress("RemoveExplicitTypeArguments")
    return MemoryMappingDelegate<RoomPosition?>({ null }, { it!!.serialise() }, ::deserialiseRoom)
}

fun memoryPositionDelegate(default: () -> RoomPosition): ReadWriteProperty<MemoryMarker, RoomPosition> =
        MemoryMappingDelegate(default, RoomPosition::serialise, ::deserialiseRoom)

fun roomPosition() = RoomPositionDelegate()

/** Used with inner memory elements */
class RoomPositionDelegate : ReadWriteProperty<dynamic, RoomPosition?> {
    override fun getValue(thisRef: dynamic, property: KProperty<*>): RoomPosition? {
        val backing = thisRef[property.name] as? String ?: return null
        return deserialiseRoom(backing)
    }

    override fun setValue(thisRef: dynamic, property: KProperty<*>, value: RoomPosition?) {
        thisRef[property.name] = value?.serialise()
    }
}