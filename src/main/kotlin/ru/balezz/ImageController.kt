package ru.balezz

import com.google.common.collect.Lists
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

import retrofit.mime.TypedFile
import ru.balezz.ImageSvcApi.Companion.IMAGE_SVC_PATH
import ru.balezz.ImageSvcApi.Companion.IMAGE_DATA_PATH
import ru.balezz.ImageSvcApi.Companion.IMAGE_META_PATH
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

@Controller
@Component
class ImageController(
    private val imageRepo: ImageRepository
) {

    val imageFileMan = ImageFileManager()

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageList(): Collection<ImageMeta> {
        return Lists.newArrayList(imageRepo.findAll())
    }

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.POST])
    @ResponseBody
    fun addImageMeta(imageMeta: ImageMeta): ImageMeta {
        imageRepo.save(imageMeta)
        return imageMeta
    }

    @RequestMapping(IMAGE_META_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageMeta(id: Long): Optional<ImageMeta> {
        return imageRepo.findById(id)
    }

    @RequestMapping(IMAGE_DATA_PATH, method = [RequestMethod.POST])
    @ResponseBody
    fun setImageData(id: Long, imageData: TypedFile): ImageMeta.ImageStatus {
        val imageMeta = imageRepo.findById(id)
        val inputStream = imageData.`in`()
        try {

            imageFileMan.saveImageData(imageMeta.get(), inputStream)
        } catch (e: IOException){
            throw ImageNotFoundException()
        }
        return ImageMeta.ImageStatus.OK
    }

    @RequestMapping(IMAGE_DATA_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageData(id: Long): ByteArray {
        val imageMeta = imageRepo.findById(id)
        try {
            val outputStream = imageFileMan.loadImageData(imageMeta.get())
            return (outputStream as ByteArrayOutputStream).toByteArray()
        } catch (e: IOException) {
            throw ImageNotFoundException()
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class ImageNotFoundException : IOException()
}
