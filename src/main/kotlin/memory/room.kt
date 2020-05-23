package memory

import ext.warn
import roles.Harvester
import roles.Hauler
import screeps.api.*
import screeps.api.structures.StructureContainer
import screeps.utils.memory.memory
import screeps.utils.unsafe.jsObject

external object SourceAssignment {
    var id: String
    var harvester: String?
    var hauler: String?
    var container: String?
}

// Note: No delegates on member properties on external objects
var SourceAssignment.containerPos: RoomPosition? by roomPosition()

val SourceAssignment.containerStruct: StructureContainer?
    get() = Game.getObjectById<Identifiable>(container) as? StructureContainer

var RoomMemory.primitiveHarvesters by memory { false }
var RoomMemory.sources by memory<Array<SourceAssignment>>()

/**
 * @return the [SourceAssignment] for a given [Creep] if it exists
 * @throws [UnsupportedOperationException] if the creep role is neither [Hauler] or [Harvester]
 */
fun RoomMemory.getExistingAssignment(creep: Creep): SourceAssignment? = when (creep.memory.role) {
    Harvester -> sources?.find { it.harvester == creep.name }
    Hauler -> sources?.find { it.hauler == creep.name }
    else -> throw UnsupportedOperationException("Not a valid role for ${creep.name}")
}

/**
 * Gets the [SourceAssignment] for a given [Creep]. It must be a [Hauler] or [Harvester].
 * A new assignment is assigned if the creep does not already have one.
 *
 * @return the new or existing [SourceAssignment], or null in exceptional cases.
 * @throws [UnsupportedOperationException] if the creep role is neither [Hauler] or [Harvester]
 */
fun Room.getOrAssignSource(creep: Creep): SourceAssignment? {
    if (memory.sources == null) {
        util.info("Room '${name}' sources: ${memory.sources}")
        memory.sources = find(FIND_SOURCES)
                .map { jsObject<SourceAssignment> { id = it.id } }
                .toTypedArray()
    }

    val role = creep.memory.role
    var assignment: SourceAssignment? = null
    when (role) {
        Harvester -> memory.sources!!.forEach {
            if (it.harvester == creep.name) return it
            if (it.harvester == null) assignment = it
        }
        Hauler -> memory.sources!!.forEach {
            if (it.hauler == creep.name) return it
            if (it.hauler == null) assignment = it
        }
        else -> throw UnsupportedOperationException("Not a valid role for ${creep.name}")
    }

    if (assignment == null) {
        creep.warn("Failed to find assignment in room '$name'", true)
        return null
    }

    when (role) {
        Harvester -> assignment!!.harvester = creep.name
        Hauler -> assignment!!.hauler = creep.name
    }

    return assignment
}
