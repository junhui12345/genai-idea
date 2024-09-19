package genai.idea.fms.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import genai.idea.fms.domain.Equipment;
import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.repository.FailureHistoryRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FailurePredictionService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private FailureHistoryRepository failureHistoryRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @PostConstruct
    public void init() {
        initializeVectorStore();
    }

    public void initializeVectorStore() {
        List<FailureHistory> allFailures = failureHistoryRepository.findAll();
        List<Document> documents = allFailures.stream()
                .map(this::createDocumentFromFailureHistory)
                .collect(Collectors.toList());
        vectorStore.add(documents);
    }

    private Document createDocumentFromFailureHistory(FailureHistory failure) {
        String content = String.format("%s %s %s",
                failure.getFailureType(),
                failure.getDescription(),
                failure.getEquipment().getType());
        float[] embedding = embeddingService.embedFailureHistory(failure);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("id", failure.getFailureId().toString());
        metadata.put("embedding", embedding);

        return new Document(content, metadata);
    }

    public List<FailureHistory> findSimilarFailures(Equipment equipment, int k) {
        String query = equipment.getType() + " " + equipment.getStatus();

        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(k)
        );

        List<String> similarFailureIds = similarDocuments.stream()
                .map(doc -> doc.getMetadata().get("id"))
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();

        return failureHistoryRepository.findAllById(similarFailureIds.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList()));
    }
}