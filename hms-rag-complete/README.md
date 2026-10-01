# HMS RAG - complete learning example

This project shows the RAG flow explicitly instead of hiding it behind an advisor.

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring AI 2.0.1
- PostgreSQL for relational HMS data
- Local profile: Spring AI SimpleVectorStore in memory
- Prod profile: PostgreSQL + pgvector
- OpenAI chat model and embedding model

## RAG flow

### Ingestion

1. POST a medical record.
2. MedicalRecordService saves it in PostgreSQL.
3. After the transaction commits, MedicalRecordCreatedEvent is handled.
4. ClinicalChunker splits long notes.
5. ClinicalDocumentIndexer creates Spring AI Document objects with metadata.
6. VectorStore.add() obtains embeddings and stores them.

### Question answering

1. POST a patient question to /api/v1/rag/ask.
2. ClinicalRetrievalService embeds the question through the VectorStore implementation.
3. Similarity search returns the top matching chunks for that patient only.
4. ClinicalRagService builds a context string from those chunks.
5. ChatClient sends system instructions + retrieved context + question to the LLM.
6. The endpoint returns the generated answer and source chunks.

## Profiles

### local

Uses SimpleVectorStore. It requires no pgvector installation. The vectors live in application memory and disappear when the application stops. PostgreSQL still stores patients and medical records.

### prod

Uses PgVectorStore and the same RAG application code. Run schema-prod.sql in a PostgreSQL database that has the pgvector extension available.

## Example requests

Create patient:

```json
POST /api/v1/patients
{
  "firstName": "Anita",
  "lastName": "Rao",
  "email": "anita@example.com",
  "phoneNumber": "9999999999",
  "dateOfBirth": "1984-06-12"
}
```

Create medical record:

```json
POST /api/v1/records
{
  "patientId": 1,
  "diagnosis": "Hypertension",
  "doctorNotes": "Patient reports chest discomfort during exercise and occasional shortness of breath."
}
```

Ask RAG:

```json
POST /api/v1/rag/ask
{
  "patientId": 1,
  "question": "What heart-related symptoms are documented for this patient?"
}
```

Example response shape:

```json
{
  "patientId": 1,
  "question": "What heart-related symptoms are documented for this patient?",
  "answer": "The available record documents chest discomfort during exercise and occasional shortness of breath.",
  "sources": [
    {
      "recordId": 1,
      "chunkNumber": 1,
      "diagnosis": "Hypertension",
      "similarityScore": 0.82,
      "text": "Patient reports chest discomfort during exercise and occasional shortness of breath."
    }
  ]
}
```

## Important production note

This is a learning/reference implementation. A real clinical system needs authentication, resource-level authorization, audit logging, encryption, privacy controls, retries/dead-letter handling for failed indexing, and clinical validation. The LLM should not be treated as an independent source of medical truth.
