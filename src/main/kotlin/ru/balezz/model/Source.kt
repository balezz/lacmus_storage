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
class Source(
       @Id @GeneratedValue var id: Long?,
       @XmlElement var database: String
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (super.equals(other)) return true
        other as Source
        return other.database == database
    }
}
