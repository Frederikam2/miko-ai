package memory

import screeps.api.RoomMemory
import screeps.api.RoomPosition
import screeps.utils.memory.memory

external object SourceAssignment {
    var id: String
    var harvester: String?
    var container: String?
    var containerPos: RoomPosition?
}

var RoomMemory.sources by memory<Array<SourceAssignment>>()