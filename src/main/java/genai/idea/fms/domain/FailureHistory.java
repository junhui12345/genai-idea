package genai.idea.fms.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "Failure_History")
public class FailureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "failure_id")
    private Integer failureId;

    @JsonBackReference
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "failure_date", nullable = false)
    private LocalDate failureDate;

    @Column(name = "failure_type", length = 50)
    private String failureType;

    @Column(name = "description")
    private String description;

    @Column(name = "downtime")
    private Float downtime;

    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;

    @JsonManagedReference
    @OneToMany(mappedBy = "failureHistory")
    private List<MaintenanceHistory> maintenanceHistories;

    // Getters and setters
}