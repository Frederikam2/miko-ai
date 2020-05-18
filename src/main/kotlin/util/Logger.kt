package util

private enum class LogType {
    DEBUG, INFO, WARN, ERROR
}

private fun log(type: LogType, message: String, subject: String? = null) {
    val tag = if (subject != null) "[$subject/${type.name}]" else "[${type.name}]:"
    val color = when(type) {
        LogType.DEBUG -> "#efefef"
        LogType.INFO -> "#ffffff"
        LogType.WARN -> "#FFFACD"
        LogType.ERROR -> "#b20d00"
    }

    console.log(tag, "color: $color", " $message")
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