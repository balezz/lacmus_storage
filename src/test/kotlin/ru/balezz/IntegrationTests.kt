package ru.balezz


import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.balezz.model.ImageStatus
import ru.balezz.model.ImgAnnotation
import ru.balezz.storage.ImageSvcApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.logging.Logger
import javax.imageio.ImageIO
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller


@SpringBootTest
class IntegrationTests {

	private val LOG = Logger.getLogger(this::class.java.name)

	private val SERVER = "http://127.0.0.1:8080"

	private val testImagePath = "src/test/resources/JPEGImages/1.jpg"
	private val testImageFile = File(testImagePath)
	private val testImageData = Files.readAllBytes(Paths.get(testImagePath))

	private val testAnnoPath = "src/test/resources/Annotations/1.xml"
	private val testAnnotation = unmarshallXml(testAnnoPath)

    var gson = GsonBuilder()
            .setLenient()
            .create()

	private val retrofit = Retrofit.Builder()
			.baseUrl(SERVER)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create(gson))
			.build()

	val imageSvc = retrofit.create(ImageSvcApi::class.java)

	/**
	 *  Ensure that controller properly unmarshall ImageMeta object from request data
	 *  and sent it back to client, so HTTP API works properly.
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddImageAnnotation() {
		LOG.info("testAddImageMeta: testAnnotation = ${marshallToXml(testAnnotation)}")
		val call = imageSvc.addAnnotation(testAnnotation)
		val received = call.execute().body()

		LOG.info("testAddImageMeta: receivedAnnotation = ${marshallToXml(received!!)}")
		assertEquals(testAnnotation, received)
	}


	/**
	 *  Ensure that imageMeta can be added to server database
	 *  and controller return updated List<ImgAnnotation>
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddGetAnnotation() {
		LOG.info("testImageAnnotaton: $testAnnotation")
		imageSvc.addAnnotation(testAnnotation)
		val call = imageSvc.getAnnotationsList()
		val storedList = call.execute().body()
		LOG.info("Stored image list: $storedList")
		assertTrue(storedList!!.contains(testAnnotation))
	}


	/**
	 *  Ensure that imageMeta can be added to server database
	 *  and controller return ImgAnnotation by Id
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddGetAnnotationById() {
		LOG.info("testAddGetAnnotationById: $testAnnotation")
		val callAdd = imageSvc.addAnnotation(testAnnotation)
		val id = callAdd.execute().body()!!.id!!
		val callGet = imageSvc.getAnnotation(id)
		val receivedAnno = callGet.execute().body()
		LOG.info("Received Annotation: $receivedAnno")
		assertEquals(receivedAnno, testAnnotation)
	}

	/**
	 *   Ensure that server returns testImageMeta data saved
	 *   and linked with existence ImageMeta element
	 * */
	@Test
	fun testAddGetImageData() {
		val call = imageSvc.addAnnotation(testAnnotation)
		val received = call.execute().body()
		val id = received!!.id!!
		assertEquals(received, testAnnotation)

        val requestFile = RequestBody
                .create(MediaType.parse("multipart/form-data"), testImageFile)
        val data = MultipartBody
                .Part.createFormData("data", testImageFile.name, requestFile)

		val callSet = imageSvc.setImageData(id, data)
		val status = callSet.execute().body()!!
		LOG.info("Image save status: $status")
		assertEquals(status, ImageStatus.OK)


		val callGet = imageSvc.getImageData(id)
		val responseData = callGet.execute().body()!!.bytes()
		// assertTrue(Arrays.equals(testImageData, baosTestImageData))
		// assertTrue(Arrays.equals(testImageData, responseData))
		assertTrue(Arrays.equals(testImageData, responseData))
	}


	/**
	 * Ensure that server properly indicate to the client with a 404 response when the client
	 * sends a request for raw data for a testImageMeta that does not have any saved data.
	 * */
	@Test
	@Throws(Exception::class)
	fun testGetNonExistentImageData() {
		try {
			imageSvc.getImageData(Long.MAX_VALUE)
		} catch (e: HttpException) {
			assertEquals(404, e.response()!!.code())
		}
	}


	/**
	 *  Ensure that server produce a 404 error if a client attempts to submit
	 *  testImageMeta data for a ImageMeta object that does not exist.
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddNonExistentImageData() {
		try {
            val requestFile = RequestBody
                    .create(MediaType.parse("multipart/form-data"), testImageFile)
            val body = MultipartBody
                    .Part.createFormData("data", testImageFile.name, requestFile)
			imageSvc.setImageData(Long.MAX_VALUE, body)
		} catch (e: HttpException) {
			assertEquals(404, e.response()!!.code())
		}

	}

	private final fun unmarshallXml(path: String): ImgAnnotation {
		val jaxbContext = JAXBContext.newInstance(ImgAnnotation::class.java)
		val unmarshaller = jaxbContext.createUnmarshaller()
		return unmarshaller.unmarshal(File(path)) as ImgAnnotation
	}

	private final fun marshallToXml(anno: ImgAnnotation) : StringWriter
	{
		val jaxbContext = JAXBContext.newInstance(ImgAnnotation::class.java)
		val marshaller = jaxbContext.createMarshaller()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		val stringWriter = StringWriter()
		stringWriter.use { marshaller.marshal(anno, stringWriter) }
		return stringWriter
	}

	private final fun pathToByteArray(path: String): ByteArray? {
		val bImage = ImageIO.read(File(path))
		val baos = ByteArrayOutputStream()
		ImageIO.write(bImage, "jpg", baos)
		return baos.toByteArray()
	}
}

