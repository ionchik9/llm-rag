package com.llm.rag

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RagApplication

fun main(args: Array<String>) {
	runApplication<RagApplication>(*args)
}
