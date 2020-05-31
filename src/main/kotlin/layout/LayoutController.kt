package layout

import screeps.api.FIND_CONSTRUCTION_SITES
import screeps.api.Room
import screeps.api.RoomMemory
import screeps.utils.memory.memoryWithSerializer



object LayoutController {
    private val patterns: dynamic = object {
        val manual = ManualPattern
    }

    @Suppress("RemoveExplicitTypeArguments")
    private val RoomMemory.layout by memoryWithSerializer<IBuildPattern>({ ManualPattern }, { it.name }, { patterns[it] as IBuildPattern })

    fun run(room: Room) {
        if (room.find(FIND_CONSTRUCTION_SITES).isNotEmpty()) return

        val layout = room.memory.layout
        layout.buildNext(room)
    }
}