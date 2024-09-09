package genai.idea.fms.service;

import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.repository.FailureHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FailureHistoryService {

    @Autowired
    private FailureHistoryRepository failureHistoryRepository;

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
}