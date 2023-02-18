import java.util.*

data class Directory( val directorySize: Long, val fullPath: String) {
    val files = ArrayList<TreeFile>()
    val directories = LinkedList<Directory>()


}