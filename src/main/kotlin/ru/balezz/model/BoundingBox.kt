package ru.balezz.model

import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.xml.bind.annotation.*

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class BoundingBox(
        @Id @GeneratedValue
        @XmlTransient var id: Long?,
        @XmlElement var xmin:  Int,
        @XmlElement var ymin:  Int,
        @XmlElement var xmax:  Int,
        @XmlElement var ymax:  Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) return true
        other as BoundingBox
        return (other.xmin == xmin &&
                other.xmax == xmax &&
                other.ymin == ymin &&
                other.ymax == ymax )
    }
}