package edu.xitaymin.filetreeparser

import Directory
import File
import FileTree
import edu.xitaymin.filetreeparser.exception.FileTreeParsingException
import java.util.Scanner
import java.util.LinkedList
import java.io.File as FileIO

private const val DIRECTORY_SEPARATOR = "|"
const val ROOT = "root"
private const val PATH_SEPARATOR = "/"
private const val FILE_SEPARATOR = ':'

class FileTreeParser(pathFromSourceRoot: String) {
    private val scanner = Scanner(FileIO(pathFromSourceRoot))
    private fun getNextLine() = if (scanner.hasNext()) scanner.nextLine() else null

    fun parseFileTree(): FileTree {
        val nextLine = getNextLine() ?: throw FileTreeParsingException("Cannot build file tree from empty file")
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

        val fullPath = "${parent.fullPath}$PATH_SEPARATOR$directoryName"
        val currentDirectory = Directory(directorySize, fullPath)
        parent.directories.add(currentDirectory)
        fileTree.directoriesByPath[fullPath] = currentDirectory

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
        val file = File(fileName, fileSize)
        directory.files.add(file)
        var locations = fileTree.fileToPath[file]
        if (locations == null) {
            locations = LinkedList<String>()
            fileTree.fileToPath[file] = locations
        }
        locations.add("${parent.fullPath}$PATH_SEPARATOR$fileName")
    }
}