/*
 *
 * Copyright 2014 Jules White
 * Copyright 2020 Andrey Labintsev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ru.balezz.storage

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.balezz.model.ImgAnno
import ru.balezz.model.ImageStatus

/**
 * This interface defines an API for a Image REST Service.
 * The interface is used to provide a contract for client/server interactions.
 * The interface is annotated with Retrofit annotations
 * so that clients can automatically convert the interface
 * into a client capable of sending the appropriate HTTP requests.
 *
 * The HTTP API endpoints:
 *
 * GET /image
 *   - Returns the list of images annotations in JSON format,
 *     that have been added to the server.
 *     The images meta information persisted in PostgreSQL.
 *
 *
 * POST /image
 *   - Adds image annotation as an application/xml request body.
 *     The XML string generate an instance of the Annotation class
 *     when deserialized by Java's default JAXB library.
 *   - Returns the JSON representation of the ImageMeta object that
 *     was stored along with any updates to that object.
 *     --The server generate a unique identifier for the Annotation
 *     object and assign it while saving in database.
 *     The returned Annotation XML should include this server-generated
 *     identifier so that the client can refer to it when uploading the
 *     binary jpeg content for the Annotation.
 *    -- The server also generates a "data url" for the Image.
 *     The "data url" is the url of the binary data for a
 *     Image (e.g., the raw jpeg data).
 *
 *
 * GET /image/{id}/json
 *   - Returns the image annotation that have been added to the server as JSON.
 *
 *
 * GET /image/{id}/xml
 *   - Returns the image annotation that have been added to the server as XML.
 *
 *
 * POST /image/{id}/data
 *   - The binary jpeg data for the image should be provided in a request
 *     with the key "data". The id in the path should be
 *     replaced with the unique identifier generated by the server for the
 *     Annotation. A client create an Annotation first by sending a POST to /image
 *     and getting the identifier for the newly created Annotation object before
 *     sending a POST to /image/{id}/data.
 *     Image data saved as {id}.jpg file in specified directory.
 *
 *
 * GET /image/{id}/data
 *   - Returns the binary jpeg data (if any) for the image with the given
 *     identifier. If no jpeg data has been uploaded for the specified image,
 *     then the server should return a 404 status code.
 *
 *
 * @author jules
 * @author balezz
 *
 */
interface ImageSvcApi {

    companion object {
        const val DATA_PARAMETER =  "data"
        const val ID_PARAMETER =    "id"
        const val IMAGE_SVC_PATH =  "/image"
        const val IMAGE_DATA_PATH = "$IMAGE_SVC_PATH/{id}/data"
        const val IMAGE_XML_PATH =  "$IMAGE_SVC_PATH/{id}/xml"
        const val IMAGE_JSON_PATH = "$IMAGE_SVC_PATH/{id}/json"
    }

    @GET(IMAGE_SVC_PATH)
    fun getImageList(): Call<List<ImgAnno>>

    @POST(IMAGE_SVC_PATH)
    fun addAnnotation(@Body imgAnno: ImgAnno): Call<ImgAnno>

    @GET(IMAGE_JSON_PATH)
    fun getImageJson(@Path(DATA_PARAMETER) id: Long): Call<ImgAnno>

    @GET(IMAGE_XML_PATH)
    fun getImageXml( @Path(DATA_PARAMETER) id: Long): Call<ImgAnno>


    @POST(IMAGE_DATA_PATH)
    fun setImageData(
            @Path(ID_PARAMETER) id: Long,
            @Body imageData: ByteArray): Call<ImageStatus>

    @GET(IMAGE_DATA_PATH)
    fun getImageData( @Path(ID_PARAMETER) id: Long): Call<ByteArray>
}
