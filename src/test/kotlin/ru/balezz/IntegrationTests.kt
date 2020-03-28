package ru.balezz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

import java.io.File
import java.io.FileInputStream
import java.util.Arrays

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest

import retrofit2.Retrofit
import retrofit2.HttpException
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import ru.balezz.storage.ImageSvcApi
import ru.balezz.model.ImgAnno
import ru.balezz.model.ImageStatus


import java.io.StringWriter
import java.util.logging.Logger
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller


@SpringBootTest
class IntegrationTests {

	val LOG = Logger.getLogger(this::class.java.name)

	private val SERVER = "http://127.0.0.1:8080"

	private val testImagePath = "src/test/resources/JPEGImages/1.jpg"
	private val testImageData = File(testImagePath)

	private val testAnnoPath = "src/test/resources/Annotations/1.xml"
	private val testAnnotation = unmarshallXml(testAnnoPath)

	private val retrofit = Retrofit.Builder()
			.baseUrl(SERVER)
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
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
	 *  and controller return updated Collection<ImageMeta>
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddGetImageMeta() {
		LOG.info("testImageAnnotaton: " + testAnnotation)
		imageSvc.addAnnotation(testAnnotation)
		val call = imageSvc.getImageList()
		val storedList = call.execute().body()
		LOG.info("Stored image list: $storedList")
		assertTrue(storedList!!.contains(testAnnotation))
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

		val callSet = imageSvc.setImageData(id, testImageData.readBytes())
		val status = callSet.execute().body()
		assertEquals(status, ImageStatus.OK)


		val callGet = imageSvc.getImageData(id)
		val response = callGet.execute().body()
		val testFile = IOUtils.toByteArray(FileInputStream(testImageData))
		assertTrue(Arrays.equals(testFile, response))
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
			imageSvc.setImageData(Long.MAX_VALUE, testImageData.readBytes())
		} catch (e: HttpException) {
			assertEquals(404, e.response()!!.code())
		}

	}

	private final fun unmarshallXml(path: String): ImgAnno {
		val jaxbContext = JAXBContext.newInstance(ImgAnno::class.java)
		val unmarshaller = jaxbContext.createUnmarshaller()
		return unmarshaller.unmarshal(File(path)) as ImgAnno
	}

	private final fun marshallToXml(anno: ImgAnno) : StringWriter
	{
		val jaxbContext = JAXBContext.newInstance(ImgAnno::class.java)
		val marshaller = jaxbContext.createMarshaller()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
		val stringWriter = StringWriter()
		stringWriter.use { marshaller.marshal(anno, stringWriter) }
		return stringWriter
	}
}

