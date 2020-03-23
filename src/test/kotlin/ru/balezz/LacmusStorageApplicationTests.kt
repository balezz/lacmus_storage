package ru.balezz

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail

import java.io.File
import java.io.FileInputStream
import java.util.Arrays
import java.util.HashSet
import java.util.UUID

import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest

import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.mime.TypedFile


@SpringBootTest
class LacmusStorageApplicationTests {
	companion object {
		private const val SERVER = "http://localhost:8080"
	}

	private val testImagePath = "src/test/resources/JPEGImages/1.jpg"

	private val testImageData = File(testImagePath)

	private val testImageMeta = ImageMeta(
			id = UUID.randomUUID().leastSignificantBits,
			title = UUID.randomUUID().toString(),
			subject = UUID.randomUUID().toString(),
			contentType = "image/jpeg",
			duration = 123,
			location = testImagePath,
			dataUrl = "url"
	)

	private val imageSvc = RestAdapter.Builder()
			.setEndpoint(SERVER).build()
			.create(ImageSvcApi::class.java)


	/**
	 *  Ensure that controller properly unmarshall ImageMeta object from request data
	 *  and sent it back to client, so HTTP API works properly.
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddImageMetadata() {
		val received = imageSvc.addImageMeta(testImageMeta)
		assertEquals(testImageMeta.title, received.title)
		assertEquals(testImageMeta.duration, received.duration)
		assertEquals(testImageMeta.contentType, received.contentType)
		assertEquals(testImageMeta.location, received.location)
		assertEquals(testImageMeta.subject, received.subject)
		assertTrue(received.id > 0)
	}


	/**
	 *  Ensure that imageMeta can be added to server database
	 *  and controller return updated Collection<ImageMeta>
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddGetImageMeta() {
		imageSvc.addImageMeta(testImageMeta)
		val storedList = imageSvc.getImageList()
		assertTrue(storedList.contains(testImageMeta))
	}


	/**
	 *   Ensure that server returns 200 and testImageMeta data saved
	 *   and linked with existence ImageMeta element
	 * */
	@Test
	fun testAddImageData() {
		val recieved = imageSvc.addImageMeta(testImageMeta)
		val response = imageSvc.getImageData(recieved.id)
		assertEquals(200, response.status)

		val imageData = response.getBody().`in`()
		val originalFile = IOUtils.toByteArray(FileInputStream(testImageData))
		val retrievedFile = IOUtils.toByteArray(imageData)
		assertTrue(Arrays.equals(originalFile, retrievedFile))
	}


	/**
	 * Ensure that server properly indicate to the client with a 404 response when the client
	 * sends a request for raw data for a testImageMeta that does not have any saved data.
	 * */
	@Test
	@Throws(Exception::class)
	fun testGetNonExistantVideosData() {

		val nonExistantId = invalidImageId()
		try {
			val r = imageSvc.getImageData(nonExistantId)
			assertEquals(404, r.getStatus())
		} catch (e: RetrofitError) {
			assertEquals(404, e.getResponse().getStatus())
		}
	}


	/**
	 *  Ensure that server produce a 404 error if a client attempts to submit
	 *  testImageMeta data for a ImageMeta object that does not exist.
	 *
	 * */
	@Test
	@Throws(Exception::class)
	fun testAddNonExistantVideosData() {
		val nonExistantId = invalidImageId()
		try {
			imageSvc.setImageData(nonExistantId, TypedFile(testImageMeta.contentType, testImageData))
			fail("The client should receive a 404 error code and throw an exception if an invalid" + " testImageMeta ID is provided in setImageData()")
		} catch (e: RetrofitError) {
			assertEquals(400, e.getResponse().getStatus())
		}

	}


	/**
	 * Support method, generates invalid imageMeta id: Long
	 *
	 * */
	private fun invalidImageId(): Long {
		val ids = HashSet<Long>()
		val storedList = imageSvc.getImageList()
		for (imageMeta in storedList) {
			ids.add(imageMeta.id)
		}

		var nonExistantId = java.lang.Long.MIN_VALUE
		while (ids.contains(nonExistantId)) {
			nonExistantId++
		}
		return nonExistantId
	}

}
