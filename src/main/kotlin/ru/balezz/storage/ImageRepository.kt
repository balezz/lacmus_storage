package ru.balezz.storage

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.balezz.model.ImgAnnotation
import java.util.*

@Repository
interface ImageRepository : CrudRepository<ImgAnnotation, Long> {

    override fun findById(id: Long) : Optional<ImgAnnotation>

}