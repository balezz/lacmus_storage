package ru.balezz.model

import java.io.Serializable
import javax.persistence.*
import javax.persistence.CascadeType.ALL
import javax.xml.bind.annotation.*


/**
 * Here we don’t use data classes with val properties
 * because JPA is not designed to work with immutable classes
 * or the methods generated automatically by data classes.
 * @see https://spring.io/guides/tutorials/spring-boot-kotlin
 * */
@Entity
@XmlRootElement(name="annotation")
@XmlAccessorType(XmlAccessType.FIELD)
data class ImgAnnotation(

        // todo fix XlmElement names serialization
        @Id @GeneratedValue
        @XmlTransient
        var id: Long?,

        @XmlElement var filename: Long? = id,

        @XmlElement var folder: String?,

        @XmlElement var source: Source?,

        @XmlElement var size: ImgSize?,

        @XmlElement var segmented: Int?,

        @ElementCollection
        @OneToMany(cascade = [ALL])
        @XmlElementRef(name="object")
        @XmlElement
        var anObject: List<DetectedObject>?

): Serializable {
        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other?.javaClass != javaClass) return false

                other as ImgAnnotation
                return (other.filename == filename &&
                        other.folder == folder &&
                        other.source == source &&
                        other.size == size &&
                        other.segmented == segmented &&
                        other.anObject == anObject )
        }
}




