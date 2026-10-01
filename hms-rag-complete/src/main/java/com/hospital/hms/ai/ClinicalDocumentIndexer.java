package com.hospital.hms.ai;

import com.hospital.hms.ehr.MedicalRecordCreatedEvent;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ClinicalDocumentIndexer {

    private final VectorStore vectorStore;
    private final ClinicalChunker chunker;

    public ClinicalDocumentIndexer(VectorStore vectorStore, ClinicalChunker chunker) {
        this.vectorStore = vectorStore;
        this.chunker = chunker;
    }

    public void index(MedicalRecordCreatedEvent event) {
        List<String> chunks = chunker.chunk(event.doctorNotes());
        List<Document> documents = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            int chunkNumber = i + 1;
            String documentId = "medical-record-" + event.recordId() + "-chunk-" + chunkNumber;

            Document document = new Document(
                    documentId,
                    chunks.get(i),
                    Map.of(
                            "patientId", event.patientId(),
                            "recordId", event.recordId(),
                            "diagnosis", event.diagnosis(),
                            "chunkNumber", chunkNumber,
                            "sourceType", "medical_record"
                    )
            );

            documents.add(document);
        }

        if (!documents.isEmpty()) {
            // VectorStore.add() asks the configured EmbeddingModel to generate vectors,
            // then stores the text, metadata and vector in the active VectorStore.
            vectorStore.add(documents);
        }
    }
}
