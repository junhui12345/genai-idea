package genai.idea.fms.controller;

import genai.idea.fms.domain.Equipment;
import genai.idea.fms.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @GetMapping
    public List<Equipment> getAllEquipment() {
        return equipmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Integer id) {
        return equipmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentService.save(equipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipment> updateEquipment(@PathVariable Integer id, @RequestBody Equipment equipment) {
        return equipmentService.findById(id)
                .map(existingEquipment -> {
                    equipment.setEquipmentId(id);
                    return ResponseEntity.ok(equipmentService.save(equipment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Integer id) {
        return equipmentService.findById(id)
                .map(equipment -> {
                    equipmentService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}