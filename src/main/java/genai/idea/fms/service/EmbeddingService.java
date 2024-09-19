package genai.idea.fms.service;

import genai.idea.fms.domain.FailureHistory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] embedText(String text) {
        return embeddingModel.embed(text);
    }

    public float[] embedFailureHistory(FailureHistory failureHistory) {
        String content = String.format("%s %s %s",
                failureHistory.getFailureType(),
                failureHistory.getDescription(),
                failureHistory.getEquipment().getType());
        return embedText(content);
    }
}