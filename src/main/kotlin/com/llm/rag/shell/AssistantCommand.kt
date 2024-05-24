package com.llm.rag.shell

import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.shell.command.annotation.Command

@Command
class AssistantCommand(
        private val chatClient: ChatClient,
        private val vectorStore: VectorStore) {


    @Value("classpath:/prompts/prompt-reference.st")
    private val sbPromptTemplate: Resource? = null


    @Command(command = ["q"])
    fun question(message: String): String {
        val promptTemplate = PromptTemplate(sbPromptTemplate)
        val similarDocs = findRelatedDocuments(message)

        val promptParameters = mapOf(
                "input" to message,
                "documents" to similarDocs.joinToString("\n")
        )

        return chatClient.call(promptTemplate.create(promptParameters))
                .result
                .output
                .content
    }

    private fun findRelatedDocuments(message: String): List<String> {
        val similarDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3))
        return similarDocuments.stream().map { obj: Document -> obj.content }.toList()
    }
}