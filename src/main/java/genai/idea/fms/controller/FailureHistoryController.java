package genai.idea.fms.controller;

import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.service.FailureHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/failure-history")
public class FailureHistoryController {

    @Autowired
    private FailureHistoryService failureHistoryService;

    @GetMapping
    public List<FailureHistory> getAllFailureHistories() {
        return failureHistoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FailureHistory> getFailureHistoryById(@PathVariable Integer id) {
        return failureHistoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FailureHistory createFailureHistory(@RequestBody FailureHistory failureHistory) {
        return failureHistoryService.save(failureHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FailureHistory> updateFailureHistory(@PathVariable Integer id, @RequestBody FailureHistory failureHistory) {
        return failureHistoryService.findById(id)
                .map(existingFailureHistory -> {
                    failureHistory.setFailureId(id);
                    return ResponseEntity.ok(failureHistoryService.save(failureHistory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFailureHistory(@PathVariable Integer id) {
        return failureHistoryService.findById(id)
                .map(failureHistory -> {
                    failureHistoryService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}