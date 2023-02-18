import java.io.File
import java.util.*
import kotlin.collections.HashMap

private const val DIRECTORY_SEPARATOR = "|"
private const val ROOT = "root"
private const val PATH_SEPARATOR = "/"
private const val FILE_SEPARATOR = ':'

class FileTreeParser(absoluteFilePath: String) {
    private val scanner = Scanner(File(absoluteFilePath))
    private fun getNextLine() = if (scanner.hasNext()) scanner.nextLine() else null

    fun parseFileTree(): FileTree {
        val nextLine = getNextLine() ?: throw RuntimeException("Cannot build file tree from empty file")
        val root = Directory(nextLine.split(DIRECTORY_SEPARATOR)[1].toLong(), ROOT)
        val fileTree = FileTree(root)

        while (scanner.hasNext()) {
            processLine(root, fileTree)
        }

        scanner.close()

        return fileTree
    }

    private fun processLine(
        parent: Directory, fileTree: FileTree
    ): Directory {
        val line = getNextLine() ?: return parent

        var directory = parent
        val indexOfSeparator = line.indexOf(DIRECTORY_SEPARATOR)
        if (indexOfSeparator != -1) {
            directory = parseDirectory(line, indexOfSeparator, parent, fileTree)
        } else {
            addParsedFileToParentDirectory(line, directory, fileTree, parent)
        }
        return directory
    }

    private fun parseDirectory(
        line: String, indexOfSeparator: Int, parent: Directory, fileTree: FileTree
    ): Directory {
        val directoryName = line.substring(0, indexOfSeparator)
        val directorySize = line.substring(indexOfSeparator + 1, line.length).toLong()

        val fullPath = parent.fullPath + PATH_SEPARATOR + directoryName
        val currentDirectory = Directory(directorySize, fullPath)
        parent.directoriesByName[directoryName] = currentDirectory

        for (i in 1..directorySize) {
            processLine(currentDirectory, fileTree)
        }
        return currentDirectory
    }

    private fun addParsedFileToParentDirectory(
        line: String, directory: Directory, fileTree: FileTree, parent: Directory
    ) {
        val fileParameters = line.split(FILE_SEPARATOR)
        val fileName = fileParameters[0]
        val fileSize = fileParameters[1].toLong()
        val file = TreeFile(fileName, fileSize)
        directory.files.add(file)
        var locations = fileTree.fileToPath[file]
        if (locations == null) {
            locations = LinkedList<String>()
            fileTree.fileToPath[file] = locations
        }
        locations.add(parent.fullPath + PATH_SEPARATOR + fileName)
    }
}

fun main() {
}

class FileTree(val root: Directory) {

    val fileToPath: HashMap<TreeFile, LinkedList<String>> = HashMap()


    //todo convert path to string

    fun getDirectorySize(path: String): Long {
        val splitedPath = path.split(PATH_SEPARATOR)
        //todo get target directory
        var directory = root
        for (i: Int in 1 until splitedPath.size){
            directory = directory.directoriesByName[splitedPath[i]] ?: throw RuntimeException("Not found directory by entered path")
        }
        return calculateSize(directory)
    }

    private fun calculateSize(directory: Directory): Long {
        val filesTotalSize = directory.files.sumOf { it.fileSize }
        val nestedDirectoriesSize = directory.directoriesByName.values.sumOf { calculateSize(it) }
        return filesTotalSize + nestedDirectoriesSize
    }

    fun getDuplicates(): List<String> {
        return fileToPath.values.filter { it.size > 1 }.flatten().toList()

    }
}


