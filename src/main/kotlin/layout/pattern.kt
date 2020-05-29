package layout

import screeps.api.*

interface IBuildPattern {
    val name: String

    /**
     * Maybe build something, if you'd like!
     */
    fun buildNext(room: Room)

    fun canBuildRoad(pos: RoomPosition): Boolean
}

fun suggestNextStructure(room: Room): StructureConstant? {
    listOf<StructureConstant>(
            STRUCTURE_EXTENSION,
            STRUCTURE_TOWER
            //STRUCTURE_STORAGE,
            //STRUCTURE_TERMINAL,
            //STRUCTURE_EXTRACTOR,
            //STRUCTURE_LAB,
            //STRUCTURE_FACTORY,
            // etc
    ).forEach {
        if (hasRoomFor(room, it)) return it
    }
    return null
}

// This could probably be optimized
private fun hasRoomFor(room: Room, structure: StructureConstant): Boolean {
    val lvl = room.controller?.level ?: throw IllegalArgumentException("Room does not have controller")
    val cap = CONTROLLER_STRUCTURES[structure]!![lvl]!!
    if (cap == 0) return false
    val built = room.find(FIND_MY_STRUCTURES).count { it.structureType == structure }
    return built >= cap
}
