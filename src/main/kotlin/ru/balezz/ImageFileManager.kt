package ru.balezz

import ru.balezz.model.Annotation
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.logging.Logger


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
    private fun getImagePath(annotation: Annotation?): Path {
        assert(annotation != null)
        return TARGET_DIR.resolve(annotation?.id.toString() + ".jpg")
    }

    /**
     * This method returns true if the specified ImageMeta has binary
     * data stored on the file system.
     *
     * @param annotation
     * @return
     */
    fun hasImageData(annotation: Annotation?): Boolean {
        val source = getImagePath(annotation)
        return Files.exists(source)
    }

    /**
     * This method copies the binary data for the given video to
     * the provided output stream. The caller is responsible for
     * ensuring that the specified ImageMeta has binary data associated
     * with it. If not, this method will throw a FileNotFoundException.
     *
     * @param annotation
     * @param out
     * @throws IOException
     */
    @Throws(IOException::class)
    fun loadImageData(annotation: Annotation): OutputStream? {
        val source = getImagePath(annotation)
        val out = ByteArrayOutputStream()
        if (!Files.exists(source)) {
            throw FileNotFoundException("Unable to find the referenced image file for imageId:"
                    + annotation.id)
        }
        LOG.info("Loading image path: $source")
        Files.copy(source, out)
        return out
    }

    /**
     * This method reads all of the data in the provided InputStream and stores
     * it on the file system. The data is associated with the ImageMeta object that
     * is provided by the client caller.
     *
     * @param annotation
     * @param imageData
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveImageData(annotation: Annotation?, imageData: InputStream?) {
        assert(imageData != null)
        val target = getImagePath(annotation)
        LOG.info("Saving image path: $target")
        Files.copy(imageData, target, StandardCopyOption.REPLACE_EXISTING)
    }

}