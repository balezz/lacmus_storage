package ru.balezz.model

import java.io.Serializable
import java.io.StringWriter
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.*

@Entity
@XmlRootElement(name = "object")
@XmlAccessorType(XmlAccessType.FIELD)
data class DetectedObject(

        @Id @GeneratedValue
        @XmlTransient val id: Long?,
        @XmlElement val name: String,
        @XmlElement val pose: String,
        @XmlElement val truncated: Int,
        @XmlElement val difficult: Int,
        @XmlElement val bndbox: BoundingBox
): Serializable {
        override fun equals(other: Any?): Boolean {
                if (super.equals(other)) return true
                other as DetectedObject
                return (other.name == name &&
                        other.pose == pose &&
                        other.truncated == truncated &&
                        other.difficult == difficult )
        }
}
