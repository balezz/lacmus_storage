package ru.balezz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.balezz.model.Annotation
import java.util.*

@Repository
interface ImageRepository : CrudRepository<Annotation, Long> {

    override fun findById(id: Long) : Optional<Annotation>

}