package genai.idea.fms.repository;

import genai.idea.fms.domain.FailureHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailureHistoryRepository extends JpaRepository<FailureHistory, Integer> {
}