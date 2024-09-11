package genai.idea.fms.service;

import genai.idea.fms.domain.Equipment;
import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.domain.MaintenanceHistory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentAnalysisService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private OllamaChatModel chatModel;

    public String findEquipmentWithMostMaintenanceHistory() {
        List<Equipment> equipmentList = equipmentService.findAll();

        Optional<Equipment> equipmentWithMostMaintenance = equipmentList.stream()
                .max(Comparator.comparingInt(e -> e.getMaintenanceHistories().size()));

        if (equipmentWithMostMaintenance.isPresent()) {
            Equipment equipment = equipmentWithMostMaintenance.get();
            String prompt = String.format(
                    "Equipment '%s' (ID: %d) has the most maintenance history with %d records. " +
                    "Please provide a brief analysis of why this equipment might require more maintenance than others." +
                    "Please provide your response in Korean.",
                    equipment.getName(),
                    equipment.getEquipmentId(),
                    equipment.getMaintenanceHistories().size()
            );

            ChatResponse response = chatModel.call(
                    new Prompt(
                            prompt,
                            OllamaOptions.builder()
                                    .withTemperature(0F)
                                    .build()
                    )
            );

            return response.getResult().getOutput().getContent();
        } else {
            return "No equipment found with maintenance history.";
        }
    }

    public String findEquipmentWithLeastMaintenanceHistory() {
        List<Equipment> equipmentList = equipmentService.findAll();

        Optional<Equipment> equipmentWithLeastMaintenance = equipmentList.stream()
                .min(Comparator.comparingInt(e -> e.getMaintenanceHistories().size()));

        if (equipmentWithLeastMaintenance.isPresent()) {
            Equipment equipment = equipmentWithLeastMaintenance.get();
            String prompt = String.format(
                    "Equipment '%s' (ID: %d) has the least maintenance history with %d records. " +
                            "Please provide a brief analysis of why this equipment might require less maintenance than others. " +
                            "Consider factors such as age, type, usage, or quality. " +
                            "Please provide your response in Korean.",
                    equipment.getName(),
                    equipment.getEquipmentId(),
                    equipment.getMaintenanceHistories().size()
            );

            ChatResponse response = chatModel.call(
                    new Prompt(
                            prompt,
                            OllamaOptions.builder()
                                    .withTemperature(0F)
                                    .build()
                    )
            );

            return response.getResult().getOutput().getContent();
        } else {
            return "No equipment found with maintenance history.";
        }
    }

    public String findMostRecentMaintenanceHistory() {
        List<Equipment> equipmentList = equipmentService.findAll();

        Optional<MaintenanceHistory> mostRecentMaintenance = equipmentList.stream()
                .flatMap(e -> e.getMaintenanceHistories().stream())
                .max(Comparator.comparing(MaintenanceHistory::getMaintenanceDate));

        if (mostRecentMaintenance.isPresent()) {
            MaintenanceHistory maintenance = mostRecentMaintenance.get();
            Equipment equipment = maintenance.getEquipment();
            String prompt = String.format(
                    "The most recent maintenance was performed on equipment '%s' (ID: %d) on %s. " +
                            "The maintenance type was '%s' and the description was '%s'. " +
                            "Please provide a brief analysis of this maintenance event, considering its timing, type, and potential impact on the equipment's performance. " +
                            "Also, suggest any follow-up actions or monitoring that might be necessary. " +
                            "Please provide your response in Korean.",
                    equipment.getName(),
                    equipment.getEquipmentId(),
                    maintenance.getMaintenanceDate(),
                    maintenance.getMaintenanceType(),
                    maintenance.getDescription()
            );

            ChatResponse response = chatModel.call(
                    new Prompt(
                            prompt,
                            OllamaOptions.builder()
                                    .withTemperature(0F)
                                    .build()
                    )
            );

            return response.getResult().getOutput().getContent();
        } else {
            return "No maintenance history found for any equipment.";
        }
    }

    public String findEquipmentWithHighFailureProbability() {
        List<Equipment> equipmentList = equipmentService.findAll();

        Optional<Equipment> equipmentWithHighestRisk = equipmentList.stream()
                .max(Comparator.comparingDouble(this::calculateRiskScore));

        if (equipmentWithHighestRisk.isPresent()) {
            Equipment equipment = equipmentWithHighestRisk.get();
            double riskScore = calculateRiskScore(equipment);

            String prompt = String.format(
                    "Equipment '%s' (ID: %d) has the highest probability of failure with a risk score of %.2f. " +
                            "This equipment has had %d failures and %d maintenance events. " +
                            "The most recent failure was on %s and the most recent maintenance was on %s. " +
                            "Please provide a detailed analysis of why this equipment might be at high risk of failure. " +
                            "Consider factors such as frequency of past failures, maintenance history, and time since last failure/maintenance. " +
                            "Also, suggest preventive measures that could be taken to reduce the risk of failure. " +
                            "Please provide your response in Korean.",
                    equipment.getName(),
                    equipment.getEquipmentId(),
                    riskScore,
                    equipment.getFailureHistories().size(),
                    equipment.getMaintenanceHistories().size(),
                    getMostRecentDate(equipment.getFailureHistories(), FailureHistory::getFailureDate),
                    getMostRecentDate(equipment.getMaintenanceHistories(), MaintenanceHistory::getMaintenanceDate)
            );

            ChatResponse response = chatModel.call(
                    new Prompt(
                            prompt,
                            OllamaOptions.builder()
                                    .withTemperature(0F)
                                    .build()
                    )
            );

            return response.getResult().getOutput().getContent();
        } else {
            return "No equipment found with failure history.";
        }
    }

    private double calculateRiskScore(Equipment equipment) {
        long daysSinceLastFailure = getDaysSinceLastEvent(equipment.getFailureHistories(), FailureHistory::getFailureDate);
        long daysSinceLastMaintenance = getDaysSinceLastEvent(equipment.getMaintenanceHistories(), MaintenanceHistory::getMaintenanceDate);
        int failureCount = equipment.getFailureHistories().size();
        int maintenanceCount = equipment.getMaintenanceHistories().size();

        // 간단한 위험도 점수 계산 예시
        // 실제 구현에서는 더 복잡한 로직이 필요할 수 있습니다
        return (failureCount * 10.0) / (daysSinceLastFailure + 1) +
                (maintenanceCount * 5.0) / (daysSinceLastMaintenance + 1);
    }

    private <T> long getDaysSinceLastEvent(List<T> events, java.util.function.Function<T, LocalDate> dateExtractor) {
        Optional<LocalDate> lastEventDate = events.stream()
                .map(dateExtractor)
                .max(LocalDate::compareTo);
        return lastEventDate.map(date -> LocalDate.now().toEpochDay() - date.toEpochDay())
                .orElse(Long.MAX_VALUE);
    }

    private <T> String getMostRecentDate(List<T> events, java.util.function.Function<T, LocalDate> dateExtractor) {
        return events.stream()
                .map(dateExtractor)
                .max(LocalDate::compareTo)
                .map(Object::toString)
                .orElse("N/A");
    }
}