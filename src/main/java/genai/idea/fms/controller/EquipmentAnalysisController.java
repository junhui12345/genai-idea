package genai.idea.fms.controller;

import genai.idea.fms.domain.Equipment;
import genai.idea.fms.service.EquipmentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class EquipmentAnalysisController {

    @Autowired
    private EquipmentAnalysisService equipmentAnalysisService;

    @GetMapping("/most-maintained-equipment")
    public String getMostMaintainedEquipmentAnalysis() {
        return equipmentAnalysisService.findEquipmentWithMostMaintenanceHistory();
    }

    @GetMapping("/least-maintained-equipment")
    public String getLeastMaintainedEquipmentAnalysis() {
        return equipmentAnalysisService.findEquipmentWithLeastMaintenanceHistory();
    }

    @GetMapping("/most-recent-maintenance")
    public String getMostRecentMaintenanceAnalysis() {
        return equipmentAnalysisService.findMostRecentMaintenanceHistory();
    }

    @GetMapping("/high-risk-equipment")
    public String getHighRiskEquipmentAnalysis() {
        return equipmentAnalysisService.findEquipmentWithHighFailureProbability();
    }

    @GetMapping("/highest-failure-probability")
    public Equipment getEquipmentWithHighestFailureProbability() {
        return equipmentAnalysisService.findEquipmentWithHighestFailureProbability();
    }
}