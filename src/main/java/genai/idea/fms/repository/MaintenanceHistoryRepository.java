package genai.idea.fms.repository;

import genai.idea.fms.domain.MaintenanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Integer> {
}