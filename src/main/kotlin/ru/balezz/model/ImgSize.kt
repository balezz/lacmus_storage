package ru.balezz.model

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class ImgSize (
        @Id @GeneratedValue var id: Long?,
        @XmlElement var width: Int,
        @XmlElement var height: Int,
        @XmlElement var depth: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ImgSize
        return (other.depth == depth &&
                other.height == height &&
                 other.width == width)
    }
}