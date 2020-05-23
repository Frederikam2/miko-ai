package memory

import ext.warn
import screeps.api.*
import screeps.utils.memory.memory
import screeps.utils.unsafe.jsObject

external object SourceAssignment {
    var id: String
    var harvester: String?
    var container: String?
    var containerPos: RoomPosition?
}

var RoomMemory.primitiveHarvesters by memory { false }
var RoomMemory.sources by memory<Array<SourceAssignment>>()


fun Room.getOrAssignSource(creep: Creep): SourceAssignment? {
    // Assign source
    if (memory.sources == null) {
        util.info("Room '${name}' sources: ${memory.sources}")
        memory.sources = find(FIND_SOURCES)
                .map { jsObject<SourceAssignment> { id = it.id } }
                .toTypedArray()
    }

    var assignment: SourceAssignment? = null
    memory.sources!!.forEach {
        if (it.harvester == creep.name) return it
        if (it.harvester == null) assignment = it
    }

    if (assignment == null) {
        creep.warn("Failed to find assignment in room '$name'", true)
        return null
    }
    assignment!!.harvester = creep.name

    return assignment
}
