package ru.balezz

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ImageMeta(
        @Id
        val id: Long,
        val title: String,
        val location: String,
        val contentType: String
) {


    override fun toString(): String {
        return "$title, $id, $location, $contentType"
    }
}

enum class ImageStatus {
    OK
}
