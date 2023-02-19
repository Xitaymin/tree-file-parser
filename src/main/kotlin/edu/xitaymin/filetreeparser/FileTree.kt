
import edu.xitaymin.filetreeparser.ROOT

import java.util.LinkedList
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileTree(root: Directory) {

    val directoriesByPath: HashMap<String, Directory> = HashMap()
    val fileToPath: HashMap<File, LinkedList<String>> = HashMap()

    init {
        this.directoriesByPath[ROOT] = root
    }

    fun getDirectorySize(path: String): Long = calculateSize(directoriesByPath[path])

    private fun calculateSize(directory: Directory?): Long {
        val filesTotalSize = directory?.files?.sumOf { it.fileSize } ?: 0
        val nestedDirectoriesSize = directory?.directories?.sumOf { calculateSize(it) } ?: 0
        return filesTotalSize + nestedDirectoriesSize
    }

    fun getDuplicates() = fileToPath.values.filter { it.size > 1 }.flatten().toList()
}

data class Directory( val directorySize: Long, val fullPath: String) {
    val files = ArrayList<File>()
    val directories = LinkedList<Directory>()
}

data class File(val fileName: String, val fileSize: Long)

