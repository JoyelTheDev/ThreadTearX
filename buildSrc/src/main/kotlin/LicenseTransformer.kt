import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.apache.tools.zip.ZipEntry          
import org.apache.tools.zip.ZipOutputStream    
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.Input
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class LicenseTransformer : Transformer {

    private val include: MutableCollection<String> = HashSet()
    private val exclude: MutableCollection<String> = HashSet()
    private val seen: MutableCollection<String> = ArrayList()
    private val data: ByteArrayOutputStream = ByteArrayOutputStream()

    @Input
    lateinit var destinationPath: String

    override fun getName(): String = "LicenseTransformer"

    override fun canTransformResource(element: FileTreeElement?): Boolean {
        return element?.let {
            val path = it.relativePath.pathString
            include.contains(path) && !exclude.contains(path)
        } ?: false
    }

    override fun hasTransformedResource(): Boolean = data.size() > 0

    override fun transform(context: TransformerContext?) {
        context?.run {
            `is`.use {
                it.bufferedReader().use { reader ->
                    val content = reader.readText().replace("\r\n", "\n")
                    val trimmed = content.trim()
                    if (!seen.contains(trimmed)) {
                        data.bufferedWriter().use { writer ->
                            writer.write(content)
                            writer.write("\n${"-".repeat(20)}\n\n")
                        }
                        seen.add(trimmed)
                    }
                }
            }
        }
    }

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        val entry = ZipEntry(destinationPath)
        entry.time = TransformerContext.getEntryTimestamp(preserveFileTimestamps, entry.time)
        os.putNextEntry(entry)
        ByteArrayInputStream(data.toByteArray()).copyTo(os)
        os.closeEntry()
        data.reset()
    }

    fun include(vararg paths: String) {
        this.include.addAll(paths)
    }

    fun exclude(vararg paths: String) {
        this.exclude.addAll(paths)
    }
}
