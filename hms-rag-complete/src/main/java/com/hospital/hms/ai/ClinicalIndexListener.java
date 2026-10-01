package com.hospital.hms.ai;

import com.hospital.hms.ehr.MedicalRecordCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ClinicalIndexListener {

    private final ClinicalDocumentIndexer clinicalDocumentIndexer;

    public ClinicalIndexListener(ClinicalDocumentIndexer clinicalDocumentIndexer) {
        this.clinicalDocumentIndexer = clinicalDocumentIndexer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMedicalRecordCreated(MedicalRecordCreatedEvent event) {
        clinicalDocumentIndexer.index(event);
    }
}
