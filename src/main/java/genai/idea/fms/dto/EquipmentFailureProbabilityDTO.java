package genai.idea.fms.dto;

import lombok.Data;

@Data
public class EquipmentFailureProbabilityDTO {
    private Integer equipmentId;
    private String name;
    private String type;
    private double failureProbability;
    private int totalFailures;
    private int totalMaintenances;
    private String lastFailureDate;
    private String lastMaintenanceDate;
}