package util

import ext.warn
import screeps.api.Creep
import screeps.api.OK
import screeps.api.ScreepsReturnCode

private enum class LogType {
    DEBUG, INFO, WARN, ERROR
}

private fun log(type: LogType, message: String, subject: String? = null) {
    val tag = if (subject != null) "[${type.name}/$subject]" else "[${type.name}]:"
    val color = when (type) {
        LogType.DEBUG -> "#efefef"
        LogType.INFO -> "#ffffff"
        LogType.WARN -> "#fffacd"
        LogType.ERROR -> "#b20d00"
    }

    console.log("<span style=\"color: $color\">$tag</span> $message")
}

fun debug(message: String, subject: String? = null) {
    log(LogType.DEBUG, message, subject)
}

fun info(message: String, subject: String? = null) {
    log(LogType.INFO, message, subject)
}

fun warn(message: String, subject: String? = null) {
    log(LogType.WARN, message, subject)
}

fun error(message: String, subject: String? = null) {
    log(LogType.ERROR, message, subject)
}

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