package com.hospital.hms.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import com.hospital.hms.patient.PatientClient;
import com.hospital.hms.patient.PatientDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClinicalRagService {

    private final PatientClient patientClient;

    private final ClinicalRetrievalService retrievalService;

    private final ChatClient chatClient;


    public ClinicalRagService(
            PatientClient patientClient,
            ClinicalRetrievalService retrievalService,
            ChatClient.Builder chatClientBuilder) {

        this.patientClient = patientClient;

        this.retrievalService = retrievalService;

        this.chatClient =
            chatClientBuilder.build();
    }


    public RagResponse ask(
            Long patientId,
            String question) {


        /*
         * Get current Patient details
         * from your existing service.
         *
         * GET:
         * /order/pat/getPatient/{patientId}
         */
        PatientDto patient =
            patientClient.getPatientById(patientId);


        /*
         * R = RETRIEVAL
         */
        List<Document> documents =
            retrievalService.retrieve(
                patientId,
                question
            );


        if (documents.isEmpty()) {

            return new RagResponse(

                patientId,

                question,

                "The available medical history "
                    + "does not provide enough information.",

                List.of()
            );
        }


        /*
         * A = AUGMENTATION
         */
        String context =
            buildContext(documents);


        /*
         * G = GENERATION
         */
        String answer =
            chatClient
                .prompt()

                .system("""
                    You are a clinical information assistant.

                    Answer only using the patient's
                    medical information supplied
                    in the context.

                    Do not invent patient information.

                    Do not create a new diagnosis.

                    If the context does not contain
                    sufficient information, say that
                    clearly.
                    """)

                .user(user ->
                    user.text("""
                        Patient Name:
                        {patientName}

                        Medical Context:
                        {context}

                        Question:
                        {question}
                        """)

                    .param(
                        "patientName",
                        patient.name()
                    )

                    .param(
                        "context",
                        context
                    )

                    .param(
                        "question",
                        question
                    )
                )

                .call()

                .content();


        List<RagSource> sources =
            documents
                .stream()
                .map(this::toSource)
                .toList();


        return new RagResponse(
            patientId,
            question,
            answer,
            sources
        );
    }


    private String buildContext(
            List<Document> documents) {

        return documents
            .stream()

            .map(Document::getText)

            .collect(
                Collectors.joining(
                    "\n\n---\n\n"
                )
            );
    }

private Integer convertToInteger(Object value) {

    if (value instanceof Number number) {
        return number.intValue();
    }

    return Integer.valueOf(
            String.valueOf(value)
    );
}
    private RagSource toSource(Document document) {

    Map<String, Object> metadata =
            document.getMetadata();

    return new RagSource(
            convertToLong(
                    metadata.get("recordId")
            ),
            convertToInteger(
                    metadata.get("chunkNumber")
            ),
            String.valueOf(
                    metadata.get("diagnosis")
            ),
            document.getScore(),
            document.getText()
    );
}

    private Long convertToLong(
            Object value) {

        if (value instanceof Number number) {
            return number.longValue();
        }

        return Long.valueOf(
            String.valueOf(value)
        );
    }
}