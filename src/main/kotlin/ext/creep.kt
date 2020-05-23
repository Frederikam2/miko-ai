package ext

import screeps.api.Creep

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
