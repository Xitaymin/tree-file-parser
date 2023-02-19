package edu.xitaymin.filetreeparser

import edu.xitaymin.filetreeparser.exception.FileTreeParsingException
import java.io.File
import java.util.Scanner


private const val DIRECTORY_SEPARATOR = "|"
private const val ROOT = "root"
const val PATH_SEPARATOR = "/"
private const val FILE_SEPARATOR = ':'

class FileTreeParser(absoluteFilePath: String) {
    private val scanner = Scanner(File(absoluteFilePath))
    private fun getNextLine() = if (scanner.hasNext()) scanner.nextLine() else null

    fun parseFileTree(): FileTree {
        val nextLine = getNextLine() ?: throw FileTreeParsingException("Cannot build file tree from empty file")
        val root = Directory(nextLine.split(DIRECTORY_SEPARATOR)[1].toLong(), ROOT)
        val fileTree = FileTree(root)

        while (scanner.hasNext()) {
            processLine(root)
        }

        scanner.close()

        return fileTree
    }

    private fun processLine(
        parent: Directory
    ): Directory {
        val line = getNextLine() ?: return parent

        var directory = parent
        val indexOfSeparator = line.indexOf(DIRECTORY_SEPARATOR)
        val isSeparatorPresent = indexOfSeparator != -1
        if (isSeparatorPresent) {
            directory = parseDirectory(line, indexOfSeparator, parent)
        } else {
            addParsedFileToParentDirectory(line, directory)
        }
        return directory
    }

    private fun parseDirectory(
        line: String, indexOfSeparator: Int, parent: Directory
    ): Directory {
        val directoryName = line.substring(0, indexOfSeparator)
        val directorySize = line.substring(indexOfSeparator + 1, line.length).toLong()

        val fullPath = "${parent.fullPath}$PATH_SEPARATOR$directoryName"
        val currentDirectory = Directory(directorySize, fullPath)
        parent.directoriesByName[directoryName] = currentDirectory

        for (i in 1..directorySize) {
            processLine(currentDirectory)
        }

        return currentDirectory
    }

    private fun addParsedFileToParentDirectory(
        line: String, directory: Directory
    ) {
        val fileParameters = line.split(FILE_SEPARATOR)
        val fileName = fileParameters[0]
        val fileSize = fileParameters[1].toLong()
        val file = File(fileName, fileSize)
        file.parent = directory
        directory.files.add(file)
    }
}


