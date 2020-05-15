package memory

import screeps.api.RoomMemory
import screeps.utils.memory.memory

external object SourceAssignment {
    var id: String
    var harvester: String?
}



var RoomMemory.sources by memory<Array<SourceAssignment>>()