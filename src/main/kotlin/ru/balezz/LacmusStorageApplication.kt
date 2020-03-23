package ru.balezz

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableAutoConfiguration
@SpringBootApplication
class LacmusStorageApplication

fun main(args: Array<String>) {
	runApplication<LacmusStorageApplication>(*args)
}



