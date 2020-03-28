package ru.balezz.storage

import com.google.common.collect.Lists

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_SVC_PATH
import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_DATA_PATH
import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_JSON_PATH
import ru.balezz.model.ImgAnno
import ru.balezz.model.ImageStatus
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.logging.Logger

@Controller
@Component
class ImageController(
    private val imageRepo: ImageRepository
) {
    val LOG = Logger.getLogger(this::class.java.name)
    val imageFileMan = ImageFileManager()

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageAnnoList(): ArrayList<ImgAnno> {
        return Lists.newArrayList(imageRepo.findAll())
    }

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.POST])
    @ResponseBody
    fun addImageAnno(@RequestBody imgAnno: ImgAnno): ImgAnno {
        val saved = imageRepo.save(imgAnno)
        LOG.info("Saved annotation: $saved")
        return saved
    }

    @RequestMapping(IMAGE_JSON_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getAnnotationJson(@PathVariable id: Long): ImgAnno {
        try {
            return imageRepo.findById(id).get()
        } catch (e: Exception) {
            throw ImageNotFoundException()
        }
    }

    @RequestMapping(IMAGE_DATA_PATH, method = [RequestMethod.POST])
    @ResponseBody
    fun setImageData(
            @PathVariable id: Long,
            @RequestBody imageData: ByteArray
    ): ImageStatus {
        LOG.info("Set image data id: $id")
        try {
            val imageMeta = imageRepo.findById(id).get()
            val inputStream = imageData.inputStream()
            imageFileMan.saveImageData(imageMeta, inputStream)
            return ImageStatus.OK
        } catch (e: Exception){
            e.printStackTrace()
            throw ImageNotFoundException()
        }
    }

    @RequestMapping(IMAGE_DATA_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageData(@PathVariable id: Long): ByteArray {
        LOG.info("Get image data id: $id")
        try {
            val imageMeta = imageRepo.findById(id).get()
            val outputStream = imageFileMan.loadImageData(imageMeta)
            return (outputStream as ByteArrayOutputStream).toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            throw ImageNotFoundException()
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class ImageNotFoundException : IOException()
}
