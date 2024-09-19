package genai.idea.fms.domain;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "Equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id")
    private Integer equipmentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "status", length = 20)
    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "equipment")
    private List<FailureHistory> failureHistories;

    @JsonManagedReference
    @OneToMany(mappedBy = "equipment")
    private List<MaintenanceHistory> maintenanceHistories;
}