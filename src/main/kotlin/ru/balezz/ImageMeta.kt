package ru.balezz

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class ImageMeta(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long,
        val title: String,
        val duration: Long,
        val location: String,
        val subject: String,
        val contentType: String,

        @JsonIgnore
        val dataUrl: String
) {
    enum class ImageStatus {
        OK
    }
}

