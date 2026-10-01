package com.hospital.hms.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicalRetrievalService {

    private final VectorStore vectorStore;

    public ClinicalRetrievalService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> retrieve(Long patientId, String question) {
        SearchRequest request = SearchRequest.builder()
                .query(question)
                .topK(5)
                .similarityThreshold(0.55)
                .filterExpression("patientId == " + patientId)
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);
        return documents == null ? List.of() : documents;
    }
}
