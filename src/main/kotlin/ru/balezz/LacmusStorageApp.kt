package ru.balezz

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize
import javax.servlet.MultipartConfigElement

@EnableAutoConfiguration
@ComponentScan
@Configuration
@SpringBootApplication
class LacmusStorageApplication {

	@Bean
	fun multipartConfiguration(): MultipartConfigElement {
		val factory = MultipartConfigFactory()
		factory.setMaxFileSize(MAX_REQUEST_SIZE)
		factory.setMaxRequestSize(MAX_REQUEST_SIZE)
		return factory.createMultipartConfig()
	}

	companion object {
		private val MAX_REQUEST_SIZE = DataSize.ofMegabytes(10)
	}

}

fun main(args: Array<String>) {
	runApplication<LacmusStorageApplication>(*args)
}
