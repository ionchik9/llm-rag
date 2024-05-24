package com.llm.rag.loader

import jakarta.annotation.PostConstruct
import org.springframework.ai.reader.ExtractedTextFormatter
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Component

@Component
class DocumentsLoader(
        private val jdbcClient: JdbcClient,
        private val vectorStore: VectorStore) {


    @Value("classpath:/docs/info.pdf")
    private val pdfResource: Resource? = null


    @PostConstruct
    fun init() {
        val count = jdbcClient.sql("select count(*) from vector_store")
                .query(Int::class.java)
                .single()

        if (count == 0) {
            val config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(ExtractedTextFormatter
                            .Builder()
                            .withNumberOfBottomTextLinesToDelete(0)
                            .withNumberOfTopPagesToSkipBeforeDelete(0)
                            .build())
                    .withPagesPerDocument(1)
                    .build()

            val pdfReader = PagePdfDocumentReader(pdfResource, config)
            val textSplitter = TokenTextSplitter()
            vectorStore.accept(textSplitter.apply(pdfReader.get()))
        }
    }
}