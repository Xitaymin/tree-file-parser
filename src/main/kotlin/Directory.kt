
data class Directory(val directoryName: String, val directorySize: Int, val parent: Directory?) {
    val files = ArrayList<TreeFile>()
    val directories = ArrayList<Directory>()
}