package ext

import screeps.api.Creep
import screeps.api.Game
import screeps.api.Memory
import screeps.api.Room
import screeps.api.RoomMemory
import screeps.api.get

fun Creep.info(message: String, say: Boolean = false) {
    if (say) say(message)
    util.info(message, name)
}

fun Creep.warn(message: String, say: Boolean = false) {
    if (say) say(message)
    util.warn(message, name)
}

fun Creep.error(message: String, say: Boolean = false) {
    if (say) say(message)
    util.error(message, name)
}

fun Creep.debug(message: String) = util.debug(message, name)
