package ru.balezz.storage

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.balezz.model.ImgAnno
import java.util.*

@Repository
interface ImageRepository : CrudRepository<ImgAnno, Long> {

    override fun findById(id: Long) : Optional<ImgAnno>

}