package com.hospital.hms.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClinicalChunkerTest {

    private final ClinicalChunker chunker = new ClinicalChunker();

    @Test
    void shortTextStaysInOneChunk() {
        List<String> chunks = chunker.chunk("Short clinical note.");

        assertThat(chunks).containsExactly("Short clinical note.");
    }

    @Test
    void blankTextProducesNoChunks() {
        assertThat(chunker.chunk("   ")).isEmpty();
    }
}
