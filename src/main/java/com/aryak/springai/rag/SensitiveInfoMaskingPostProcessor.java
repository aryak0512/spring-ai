package com.aryak.springai.rag;


import lombok.Builder;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;

@Builder
public class SensitiveInfoMaskingPostProcessor implements DocumentPostProcessor {

    @Override
    public List<Document> process(Query query, List<Document> documents) {

        return documents.stream().map(d -> {

            String text = d.getText();
            // do regex match and replace"[REMOVED]"
            return d.mutate().text(text).build();

        }).toList();

    }
}
