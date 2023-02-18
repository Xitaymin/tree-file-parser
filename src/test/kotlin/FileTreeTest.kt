import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

private const val SMALL_TEST_FILE = "C:\\Users\\baras\\IdeaProjects\\FileTreeParser\\src\\main\\resources\\simpleInput.txt"
private const val BIG_TEST_FILE = "C:\\Users\\baras\\IdeaProjects\\FileTreeParser\\src\\main\\resources\\input.txt"

internal class FileTreeTest {

    private companion object {
        @JvmStatic
        fun directoryArguments() = Stream.of(
            Arguments.of("root", 36923799),
            Arguments.of("root/dir_aa", 35880217),
            Arguments.of("root/dir_mn", 490672),
            Arguments.of("root/dir_mn/dir_bb/dir_bb", 0)
        )
    }

    @Test
    fun createFileTree() {
        val start = System.nanoTime()
        val fileTree = getFileTree(BIG_TEST_FILE)
        val stop = System.nanoTime()

        println("Tree creation took " + (stop-start)/1_000_000)

        val startFind = System.nanoTime()
        val duplicates = fileTree.getDuplicates()
        val stopFind = System.nanoTime()
        println(duplicates)
        println("Finding duplicates took " + (stopFind-startFind)/1_000_000)

        val startCount = System.nanoTime()
        var directorySize = fileTree.getDirectorySize("root")
        val stopCount = System.nanoTime()

        println("Counting root directory size took " + (stopCount-startCount)/1_000_000)
        println(directorySize)

        directorySize = fileTree.getDirectorySize("root/dir_kz/dir_vl")
        println(directorySize)

        directorySize = fileTree.getDirectorySize("root/dir_lt/dir_ko/dir_mu")
        println(directorySize)
    }

    private fun getFileTree(path: String) =
        FileTreeParser(path)
            .parseFileTree()

    @ParameterizedTest
    @MethodSource("directoryArguments")
    fun getDirectorySize(directory: String, expectedSize: Long) {
        val fileTree = getFileTree(SMALL_TEST_FILE)
        val start = System.nanoTime()
        val directorySize = fileTree.getDirectorySize(directory)
        val stop = System.nanoTime()
        assertEquals(expectedSize, directorySize)
        println((stop-start)/1_000_000)
    }

    @Test
    fun getDuplicates() {
        val fileTree = getFileTree(SMALL_TEST_FILE)

        val duplicatedFiles = fileTree.getDuplicates()

        val expectedFiles =
            listOf("root/dir_aa/file_tq", "root/dir_cd/file_tq", "root/dir_kb/dir_bs/file_tq", "root/dir_mn/file_tq")

        assertEquals(expectedFiles, duplicatedFiles)
    }

    
}