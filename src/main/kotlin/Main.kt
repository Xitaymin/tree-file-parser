import java.io.File
import java.util.Scanner

class FileTreeProcessor(){
    val scanner = Scanner(File("C:\\Users\\baras\\IdeaProjects\\FileTreeParser\\src\\main\\resources\\input.txt"))

    fun getNextLine() = scanner.nextLine()

    fun process (){

        val root = Directory("root", getNextLine().split("|")[1].toInt(), null)

        while (scanner.hasNext()){
            processLine(root)
        }

        println(root)
    }

     fun processLine(parent: Directory): Directory {

         val line = getNextLine()

        if (line == null) return parent

        var directory = parent
        val indexOfSeparator = line.indexOf('|')
        if (indexOfSeparator != -1) {
            val directoryName = line.substring(0, indexOfSeparator)
            val directorySize = line.substring(indexOfSeparator + 1, line.length).toInt()
            directory = Directory(directoryName, directorySize, parent)


            for(i in 1..directorySize){
                processLine(directory)
            }

        } else {
            val fileParameters = line.split(':')
            val fileName = fileParameters[0]
            val fileSize = fileParameters[1].toInt()
            val file = TreeFile(fileName, fileSize, directory)
            parent.files.add(file)

        }

         return directory
    }
}
//todo for directory add field total size
fun main() {
    FileTreeProcessor().process()
}


