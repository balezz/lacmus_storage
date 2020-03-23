package ru.balezz

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ImageRepository : CrudRepository<ImageMeta, Long> {

    override fun findById(id: Long) : Optional<ImageMeta>

}