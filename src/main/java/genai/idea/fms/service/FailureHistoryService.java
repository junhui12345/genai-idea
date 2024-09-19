package genai.idea.fms.service;

import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.repository.FailureHistoryRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FailureHistoryService {

    @Autowired
    private FailureHistoryRepository failureHistoryRepository;

    @Autowired
    private EmbeddingModel embeddingModel;

    public List<FailureHistory> findAll() {
        return failureHistoryRepository.findAll();
    }

    public Optional<FailureHistory> findById(Integer id) {
        return failureHistoryRepository.findById(id);
    }

    public FailureHistory save(FailureHistory failureHistory) {
        return failureHistoryRepository.save(failureHistory);
    }

    public void deleteById(Integer id) {
        failureHistoryRepository.deleteById(id);
    }

    public float[] embedFailureHistory(FailureHistory failureHistory) {
        String content = failureHistory.getEquipment().getName() + " " +
                failureHistory.getFailureType() + " " +
                failureHistory.getDescription();
        return embeddingModel.embed(content);
    }
}