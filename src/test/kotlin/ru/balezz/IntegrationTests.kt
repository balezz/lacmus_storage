package ru.balezz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

import java.io.File
import java.io.FileInputStream
import java.util.Arrays
import java.util.HashSet

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest

import retrofit2.Retrofit
import retrofit2.HttpException
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


import java.util.logging.Logger


@SpringBootTest
class IntegrationTests {
	companion object {
		private const val SERVER = "http://localhost:8080"
		val LOG = Logger.getLogger(this::class.java.name)
	}

	private val testImagePath = "src/test/resources/JPEGImages/1.jpg"

	private val testImageData = File(testImagePath)

	private val testImageMeta = ImageMeta(
			id = 1L,
			title = "TestImage",
			contentType = "image/jpeg",
			location = testImagePath
	)

	private val retrofit = Retrofit.Builder()
			.baseUrl(SERVER)
			.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	val imageSvc = retrofit.create(ImageSvcApi::class.java)

	/**
	 *  Ensure that controller properly unmarshall ImageMeta object from request data
	 *  and sent it back to client, so HTTP API works properly.
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddImageMetadata() {
		LOG.info("testAddImageMetadata: testImageMeta = $testImageMeta")
		imageSvc.addImageMeta(testImageMeta).subscribe {
			val received = it
			LOG.info("testAddImageMetadata: $received")
			assertEquals(testImageMeta.title, received.title)
			assertEquals(testImageMeta.contentType, received.contentType)
			assertEquals(testImageMeta.location, received.location)
			assertTrue(received.id > 0)
		}
	}


	/**
	 *  Ensure that imageMeta can be added to server database
	 *  and controller return updated Collection<ImageMeta>
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddGetImageMeta() {
		imageSvc.addImageMeta(testImageMeta)
		imageSvc.getImageList().subscribe {
			val storedList = it
			assertTrue(storedList!!.contains(testImageMeta))
		}

	}


	/**
	 *   Ensure that server returns 200 and testImageMeta data saved
	 *   and linked with existence ImageMeta element
	 * */
	@Test
	fun testAddGetImageData() {
		imageSvc.addImageMeta(testImageMeta).subscribe(){
			val received = it
			LOG.info("test Id: " + testImageMeta.id)
			LOG.info("received Id: " + received.id)
			assertEquals(received.id, testImageMeta.id)
		}



		imageSvc.setImageData(1L, testImageData.readBytes()).subscribe(){
			assertEquals(it, ImageStatus.OK)
		}



		imageSvc.getImageData(1L).subscribe() {

			val response = it
			assertEquals(200, response.code())

			val receivedImageData = response.body()
			val testFile = IOUtils.toByteArray(FileInputStream(testImageData))
			LOG.info("$testImageData " + String(testFile).slice(0..10))
			LOG.info("$receivedImageData " + String(receivedImageData!!).slice(0..10))
			assertTrue(Arrays.equals(testFile, receivedImageData))
		}
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


}
