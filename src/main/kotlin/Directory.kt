import java.util.*

data class Directory(val directoryName: String, val directorySize: Long, val parent: Directory?, val fullPath: String) : Comparable<Directory> {
    val files = ArrayList<TreeFile>()
    val directories = LinkedList<Directory>() //todo make it be tree
    var totalSize = 0L
    override fun compareTo(other: Directory): Int = this.fullPath.compareTo(other.fullPath)
    override fun toString(): String {
        return """$directoryName|$directorySize files count = ${files.size} directories count = ${directories.size} full path = $fullPath
            |directories = ${directories.map { it.fullPath }}
        """.trimMargin()
    }
}