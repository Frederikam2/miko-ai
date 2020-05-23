import java.io.FileInputStream

FileInputStream("surnames.txt").reader().useLines { lines ->
    println(lines.joinToString(prefix = "arrayOf(", postfix = ")") { "\"$it\""})
}
