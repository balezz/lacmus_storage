package ru.balezz.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class Annotation(
        @XmlElement val folder: String,

        @Id @XmlElement(name="filename") val id: Long,
        @XmlElement val source: Source,
        @XmlElement(name="size") val imgSize: ImgSize,
        @XmlElement val segmented: Int,
        @XmlElement val annoObject: ArrayList<AnnoObject>
){
    constructor():this("JPEGImages", 1, Source(), ImgSize(), 0, arrayListOf<AnnoObject>())
}

enum class ImageStatus {
    OK
}


