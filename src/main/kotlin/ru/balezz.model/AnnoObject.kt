package ru.balezz.model

import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
data class AnnoObject(
        @XmlElement val name: String,
        @XmlElement val pose: String,
        @XmlElement val truncated: Int,
        @XmlElement val difficult: Int,
        @XmlElement val bndbox: BoundingBox
){
    constructor() : this("Pedestrian", "Unspecified", 0, 0, BoundingBox())
}


fun main() {
    val anno = AnnoObject()
    println(convertToXml(anno))
//    val jaxbContext = JAXBContext.newInstance(AnnoObject::class.java)
//    val marshaller = jaxbContext.createMarshaller()
//    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
//    val stringWriter = StringWriter()
//    stringWriter.use { marshaller.marshal(anno, stringWriter) }
//    println(stringWriter)
}

inline fun <reified T> convertToXml(anno: T) : StringWriter
{
    val jaxbContext = JAXBContext.newInstance(T::class.java)
    val marshaller = jaxbContext.createMarshaller()
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    val stringWriter = StringWriter()
    stringWriter.use { marshaller.marshal(anno, stringWriter) }
    return stringWriter
}