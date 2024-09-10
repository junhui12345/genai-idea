package genai.idea.fms.controller;

import genai.idea.fms.domain.MaintenanceHistory;
import genai.idea.fms.service.MaintenanceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-history")
public class MaintenanceHistoryController {

    @Autowired
    private MaintenanceHistoryService maintenanceHistoryService;

    @GetMapping
    public List<MaintenanceHistory> getAllMaintenanceHistories() {
        return maintenanceHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceHistory> getMaintenanceHistoryById(@PathVariable Integer id) {
        return maintenanceHistoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MaintenanceHistory createMaintenanceHistory(@RequestBody MaintenanceHistory maintenanceHistory) {
        return maintenanceHistoryService.save(maintenanceHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceHistory> updateMaintenanceHistory(@PathVariable Integer id, @RequestBody MaintenanceHistory maintenanceHistory) {
        return maintenanceHistoryService.findById(id)
                .map(existingMaintenanceHistory -> {
                    maintenanceHistory.setMaintenanceId(id);
                    return ResponseEntity.ok(maintenanceHistoryService.save(maintenanceHistory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenanceHistory(@PathVariable Integer id) {
        return maintenanceHistoryService.findById(id)
                .map(maintenanceHistory -> {
                    maintenanceHistoryService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}