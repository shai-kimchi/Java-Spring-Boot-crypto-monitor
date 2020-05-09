package com.lian.shai.currencymonitor.data;

import org.elasticsearch.client.core.TermVectorsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentWords {
    private Map<String, Integer> wordCount;
    private String id;

    public DocumentWords(String id) {
        wordCount = new HashMap<>();
        this.id = id;
    }

    public Map<String, Integer> getWordCount() {
        return wordCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addTermVector(TermVectorsResponse response) {
        for (TermVectorsResponse.TermVector tv : response.getTermVectorsList()) {
            if (tv.getTerms() != null) {
                List<TermVectorsResponse.TermVector.Term> terms =
                        tv.getTerms();
                for (TermVectorsResponse.TermVector.Term term : terms) {
                    wordCount.put(term.getTerm(), term.getTermFreq());
                }
            }
        }
    }
}
