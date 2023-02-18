import java.io.File
import java.util.*
import kotlin.collections.HashMap

class FileTreeProcessor() {
    val scanner = Scanner(File("C:\\Users\\baras\\IdeaProjects\\FileTreeParser\\src\\main\\resources\\input.txt"))

    val pathSeparator = "/"
    fun getNextLine() = scanner.nextLine()

    fun process(directoriesByPath: HashMap<String, Directory>, fileToPath: HashMap<TreeFile, LinkedList<String>>): Directory {

        val root = Directory("root", getNextLine().split("|")[1].toLong(), null, "root")

        while (scanner.hasNext()) {
            processLine(root, directoriesByPath, fileToPath)
        }

        scanner.close()

        return root
    }

    fun processLine(
        parent: Directory,
        directoriesByPath: HashMap<String, Directory>,
        fileToPath: HashMap<TreeFile, LinkedList<String>>
    ): Directory {

        val line = getNextLine()

        if (line == null) return parent

        var directory = parent
        val indexOfSeparator = line.indexOf('|')
        if (indexOfSeparator != -1) {
            val directoryName = line.substring(0, indexOfSeparator)
            val directorySize = line.substring(indexOfSeparator + 1, line.length).toLong()

            val fullPath = parent.fullPath + pathSeparator + directoryName
            directory = Directory(directoryName, directorySize, parent, fullPath)
            parent.directories.add(directory)
            directoriesByPath[fullPath] = directory

//            parent.totalSize=+directory.totalSize
//            println(directory)

            for (i in 1..directorySize) {
                processLine(directory, directoriesByPath, fileToPath)
            }

        } else {
            val fileParameters = line.split(':')
            val fileName = fileParameters[0]
            val fileSize = fileParameters[1].toLong()
            val file = TreeFile(fileName, fileSize, directory)
            directory.files.add(file)
            var locations = fileToPath[file]
            if(locations == null){
                locations = LinkedList<String>()
                fileToPath[file] = locations
            }
            locations.add(parent.fullPath + pathSeparator + fileName)

        }

        return directory
    }
}

fun main() {

    val fileTree = FileTree().of()

//    println(fileTree.getDirectorySize("root/dir_lt/dir_ko/dir_mu"))

    fileTree.getDuplicates()
}

class FileTree() {

    private val directoriesByPath = HashMap<String, Directory>()
    private val fileToPath = HashMap<TreeFile, LinkedList<String>>()
    lateinit var root: Directory

    fun of(): FileTree {
        val fileTree = FileTree()
        fileTree.root = FileTreeProcessor().process(fileTree.directoriesByPath, fileTree.fileToPath)
        fileTree.directoriesByPath["root"] = fileTree.root
        return fileTree
    }

//    fun getDirectorySize(path: Path): Long {
//        //todo convert path to string
//        //todo find target directory
//        //todo calculate sum of elements size + nested directories
//
//        return getDirectorySize(path.toString())
//    }

    fun getDirectorySize(path: String): Long {

        val directory = directoriesByPath[path]

        return calculateSize(directory)

    }

    private fun calculateSize(directory: Directory?): Long {
        val filesTotalSize = directory?.files?.sumOf { it.fileSize } ?: 0
        val nestedDirectoriesSize = directory?.directories?.sumOf { calculateSize(it) } ?: 0
        return filesTotalSize + nestedDirectoriesSize
    }

    fun getDuplicates(){
        fileToPath.values.filter { it.size > 1 }.forEach{ println(it) }
    }
}


