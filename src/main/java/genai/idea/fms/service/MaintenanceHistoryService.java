package genai.idea.fms.service;

import genai.idea.fms.domain.MaintenanceHistory;
import genai.idea.fms.repository.MaintenanceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceHistoryService {

    @Autowired
    private MaintenanceHistoryRepository maintenanceHistoryRepository;

    public List<MaintenanceHistory> findAll() {
        return maintenanceHistoryRepository.findAll();
    }

    public Optional<MaintenanceHistory> findById(Integer id) {
        return maintenanceHistoryRepository.findById(id);
    }

    public MaintenanceHistory save(MaintenanceHistory maintenanceHistory) {
        return maintenanceHistoryRepository.save(maintenanceHistory);
    }

    public void deleteById(Integer id) {
        maintenanceHistoryRepository.deleteById(id);
    }
}