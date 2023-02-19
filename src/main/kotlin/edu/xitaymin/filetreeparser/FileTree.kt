package edu.xitaymin.filetreeparser

import edu.xitaymin.filetreeparser.exception.FileTreeOperationException
import java.util.ArrayList
import java.util.LinkedHashMap

class FileTree(private val root: Directory) {

    fun getDirectorySize(path: String): Long {
        val pathParts = path.split(PATH_SEPARATOR)

        var directory = root
        for (i: Int in 1 until pathParts.size){
            directory = directory.directoriesByName[pathParts[i]] ?: throw FileTreeOperationException("Not found directory by entered path")
        }

        return calculateSize(directory)
    }

    private fun calculateSize(directory: Directory): Long {
        val filesTotalSize = directory.files.sumOf { it.fileSize }
        val nestedDirectoriesSize = directory.directoriesByName.values.sumOf { calculateSize(it) }
        return filesTotalSize + nestedDirectoriesSize
    }

    fun getDuplicates(): HashSet<String> {
        val duplicatedFilesPaths = LinkedHashSet<String>()
        val fileToPath = HashMap<File, String>()

        findDuplicatedFilesInDirectory(duplicatedFilesPaths, fileToPath, root)

        return duplicatedFilesPaths
    }

    private fun findDuplicatedFilesInDirectory(
        result: LinkedHashSet<String>,
        fileToPath: java.util.HashMap<File, String>,
        directory: Directory
    ) {
        directory.files.forEach { addToResultSetIfDuplicateExists(it, result, fileToPath) }
        directory.directoriesByName.values.forEach { findDuplicatedFilesInDirectory(result, fileToPath, it) }
    }

    private fun addToResultSetIfDuplicateExists(
        file: File,
        result: LinkedHashSet<String>,
        fileToPath: java.util.HashMap<File, String>
    ) {
        val fileFullPath = "${file.parent.fullPath}$PATH_SEPARATOR${file.fileName}"
        val duplicatePath = fileToPath.put(file, fileFullPath)
        if (duplicatePath != null) {
            result.add(duplicatePath)
            result.add(fileFullPath)
        }
    }
}

data class Directory( val directorySize: Long, val fullPath: String) {
    val files = ArrayList<File>()
    val directoriesByName = LinkedHashMap<String, Directory>()
}

data class File(val fileName: String, val fileSize: Long){
    lateinit var parent: Directory
}