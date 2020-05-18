package ext

import screeps.api.Creep

fun Creep.info(message: String, say: Boolean = false) {
    if (say) this.say(message)
    util.info(message, this.name)
}

fun Creep.debug(message: String) = util.debug(message, this.name)
fun Creep.warn(message: String) = util.warn(message, this.name)
fun Creep.error(message: String) = util.debug(message, this.name)
