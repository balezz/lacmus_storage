package ru.balezz.storage

import com.google.common.collect.Lists

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_SVC_PATH
import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_DATA_PATH
import ru.balezz.storage.ImageSvcApi.Companion.IMAGE_ANNO_PATH
import ru.balezz.model.ImgAnnotation
import ru.balezz.model.ImageStatus
import ru.balezz.storage.ImageSvcApi.Companion.DATA_PARAMETER
import java.io.IOException
import java.util.logging.Logger

@Controller
@Component
class ImageController(
    private val imageRepo: ImageRepository
) {
    val LOG = Logger.getLogger(this::class.java.name)

    // todo DI
    val imageFileMan = ImageFileManager()

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getImageAnnoList(): ArrayList<ImgAnnotation> {
        return Lists.newArrayList(imageRepo.findAll())
    }

    @RequestMapping(IMAGE_SVC_PATH, method = [RequestMethod.POST])
    @ResponseBody
    fun addAnnotationJson(@RequestBody imgAnno: ImgAnnotation): ImgAnnotation {
        val saved = imageRepo.save(imgAnno)
        LOG.info("Saved annotation: $saved")
        return saved
    }

    @RequestMapping(IMAGE_ANNO_PATH, method = [RequestMethod.GET])
    @ResponseBody
    fun getAnnotationJson(@PathVariable id: Long): ImgAnnotation {
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
            @RequestParam(DATA_PARAMETER) imageData: MultipartFile
    ): ImageStatus {
        LOG.info("Set image data id: $id")
        try {
            val imageAnno = imageRepo.findById(id).get()
            LOG.info("Annotation for saving image: $imageAnno")
            val inputStream = imageData.inputStream
            imageFileMan.saveImageData(imageAnno, inputStream)
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
            val imgAnno = imageRepo.findById(id).get()
            val imgData = imageFileMan.loadImageData(imgAnno)
            LOG.info("getImageData out size: ${imgData!!.size}")
            return imgData
        } catch (e: Exception) {
            e.printStackTrace()
            throw ImageNotFoundException()
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class ImageNotFoundException : IOException()
}
