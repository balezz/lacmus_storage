package ru.balezz

import org.springframework.context.annotation.Bean
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


class ImageFileManager {

    private val TARGET_DIR_NAME = "JPEGImages"
    private val TARGET_DIR = Paths.get(TARGET_DIR_NAME)

    init {
        if (!Files.exists(TARGET_DIR)) {
            Files.createDirectories(TARGET_DIR)
        }
    }

    /**
     *  Private helper method for resolving image file paths
     */
    private fun getImagePath(imageMeta: ImageMeta?): Path {
        assert(imageMeta != null)
        return TARGET_DIR.resolve(TARGET_DIR_NAME + imageMeta?.id.toString() + ".jpg")
    }

    /**
     * This method returns true if the specified ImageMeta has binary
     * data stored on the file system.
     *
     * @param imageMeta
     * @return
     */
    fun hasImageData(imageMeta: ImageMeta?): Boolean {
        val source = getImagePath(imageMeta)
        return Files.exists(source)
    }

    /**
     * This method copies the binary data for the given video to
     * the provided output stream. The caller is responsible for
     * ensuring that the specified ImageMeta has binary data associated
     * with it. If not, this method will throw a FileNotFoundException.
     *
     * @param imageMeta
     * @param out
     * @throws IOException
     */
    @Throws(IOException::class)
    fun loadImageData(imageMeta: ImageMeta): OutputStream? {
        val source = getImagePath(imageMeta)
        val out = ByteArrayOutputStream()
        if (!Files.exists(source)) {
            throw FileNotFoundException("Unable to find the referenced image file for imageId:"
                    + imageMeta.id)
        }
        Files.copy(source, out)
        return out
    }

    /**
     * This method reads all of the data in the provided InputStream and stores
     * it on the file system. The data is associated with the ImageMeta object that
     * is provided by the client caller.
     *
     * @param imageMeta
     * @param imageData
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveImageData(imageMeta: ImageMeta?, imageData: InputStream?) {
        assert(imageData != null)
        val target = getImagePath(imageMeta)
        Files.copy(imageData, target, StandardCopyOption.REPLACE_EXISTING)
    }

}