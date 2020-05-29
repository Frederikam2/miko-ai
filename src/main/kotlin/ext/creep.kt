package ext

import screeps.api.Creep
import screeps.api.OK
import screeps.api.ScreepsReturnCode
import util.Logger

fun Creep.info(message: String, say: Boolean = false) {
    if (say) say(message)
    Logger.info(message, name)
}

fun Creep.warn(message: String, say: Boolean = false) {
    if (say) say(message)
    Logger.warn(message, name)
}

fun Creep.error(message: String, say: Boolean = false) {
    if (say) say(message)
    Logger.error(message, name)
}

fun Creep.debug(message: String) = Logger.debug(message, name)

fun ScreepsReturnCode.expectOk(creep: Creep, action: String?) {
    if (this == OK) return
    unexpected(creep, action)
}

fun ScreepsReturnCode.unexpected(creep: Creep, action: String?) {
    if (action == null) {
        creep.warn("Unexpected code: $this")
    } else {
        creep.warn("unexpected code while $action: $this")
    }
}
