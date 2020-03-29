package ru.balezz.storage

import org.springframework.web.multipart.MultipartFile
import ru.balezz.model.ImgAnnotation
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger
import javax.imageio.ImageIO


class ImageFileManager {

    private val TARGET_DIR_NAME = "JPEGImages/"
    private val TARGET_DIR = Paths.get(TARGET_DIR_NAME)
    val LOG = Logger.getLogger(this::class.java.name)

    init {
        if (!Files.exists(TARGET_DIR)) {
            Files.createDirectories(TARGET_DIR)
        }
    }

    /**
     *  Private helper method for resolving image file paths
     */
    private fun getImagePath(imgAnno: ImgAnnotation?): Path {
        return TARGET_DIR.resolve(imgAnno?.id.toString() + ".jpg")
    }

    /**
     * This method returns true if the specified ImageMeta has binary
     * data stored on the file system.
     *
     * @param imgAnno
     * @return
     */
    fun hasImageData(imgAnno: ImgAnnotation?): Boolean {
        val source = getImagePath(imgAnno)
        return Files.exists(source)
    }

    /**
     * This method copies the binary data for the given video to
     * the provided output stream. The caller is responsible for
     * ensuring that the specified ImageMeta has binary data associated
     * with it. If not, this method will throw a FileNotFoundException.
     *
     * @param imgAnno
     * @throws IOException
     */
    @Throws(IOException::class)
    fun loadImageData(imgAnno: ImgAnnotation): ByteArray? {
        val source = getImagePath(imgAnno)
        LOG.info("loadImageData: $source")
        return Files.readAllBytes(source)
    }

    /**
     * This method reads all of the data in the provided InputStream and stores
     * it on the file system. The data is associated with the ImageMeta object that
     * is provided by the client caller.
     *
     * @param imgAnno
     * @param inputStream
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveImageData(imgAnno: ImgAnnotation?, inputStream: InputStream) {
        val target = getImagePath(imgAnno)
        LOG.info("Saving image path: $target")
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING)
    }

}