import java.util.*

data class Directory( val directorySize: Long, val fullPath: String) {
    val files = ArrayList<TreeFile>()
    val directoriesByName = HashMap<String, Directory>()

}