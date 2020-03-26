package ru.balezz.model

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class ImgSize (

        @XmlElement val id: Long,
        @XmlElement val width: Int,
        @XmlElement val height: Int,
        @XmlElement val depth: Int
) : Serializable {
    constructor(): this(1,0, 0, 0)
}