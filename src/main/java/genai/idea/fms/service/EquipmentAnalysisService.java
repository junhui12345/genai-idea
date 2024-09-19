package genai.idea.fms.service;

import genai.idea.fms.domain.Equipment;
import genai.idea.fms.domain.FailureHistory;
import genai.idea.fms.domain.MaintenanceHistory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EquipmentAnalysisService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private OllamaChatModel chatModel;

    @Autowired
    private FailurePredictionService failurePredictionService;

    @Value("classpath:/prompts/most-maintenance.st")
    private Resource mostMaintenanceResource;

    @Value("classpath:/prompts/least-maintenance.st")
    private Resource leastMaintenanceResource;

    @Value("classpath:/prompts/recent-maintenance.st")
    private Resource recentMaintenanceResource;

    @Value("classpath:/prompts/high-failure-probability.st")
    private Resource highFailureProbabilityResource;

    public String findEquipmentWithMostMaintenanceHistory() {
        List<Equipment> equipmentList = equipmentService.findAll();

        Optional<Equipment> equipmentWithMostMaintenance = equipmentList.stream()
                .max(Comparator.comparingInt(e -> e.getMaintenanceHistories().size()));

        if (equipmentWithMostMaintenance.isPresent()) {
            Equipment equipment = equipmentWithMostMaintenance.get();
            Optional<MaintenanceHistory> lastMaintenance = equipment.getMaintenanceHistories().stream()
                    .max(Comparator.comparing(MaintenanceHistory::getMaintenanceDate));

            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(mostMaintenanceResource);
            Map<String, Object> model = new HashMap<>();
            model.put("name", equipment.getName());
            model.put("id", equipment.getEquipmentId());
            model.put("count", equipment.getMaintenanceHistories().size());

            // Add these new variables
            lastMaintenance.ifPresent(maintenance -> {
                model.put("date", maintenance.getMaintenanceDate());
                model.put("type", maintenance.getMaintenanceType());
                model.put("description", maintenance.getDescription());
            });

            String prompt = promptTemplate.create(model).getContents();

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
            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(leastMaintenanceResource);
            Map<String, Object> model = Map.of(
                    "name", equipment.getName(),
                    "id", equipment.getEquipmentId(),
                    "count", equipment.getMaintenanceHistories().size()
            );
            String prompt = promptTemplate.create(model).getContents();

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
            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(recentMaintenanceResource);
            Map<String, Object> model = Map.of(
                    "name", equipment.getName(),
                    "id", equipment.getEquipmentId(),
                    "date", maintenance.getMaintenanceDate(),
                    "type", maintenance.getMaintenanceType(),
                    "description", maintenance.getDescription(),
                    "lastMaintenanceDate", maintenance.getMaintenanceDate(),
                    "lastFailureDate", getMostRecentDate(equipment.getFailureHistories(), FailureHistory::getFailureDate),
                    "riskScore", calculateRiskScore(equipment),
                    "maintenanceCount", equipment.getMaintenanceHistories().size(),
                    "failureCount", equipment.getFailureHistories().size()
            );
            String prompt = promptTemplate.create(model).getContents();

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

    public Equipment findEquipmentWithHighestFailureProbability() {
        List<Equipment> allEquipment = equipmentService.findAll();
        return allEquipment.stream()
                .max(Comparator.comparingDouble(this::calculateFailureProbability))
                .orElseThrow(() -> new RuntimeException("No equipment found"));
    }

    private double calculateFailureProbability(Equipment equipment) {
        List<FailureHistory> similarFailures = failurePredictionService.findSimilarFailures(equipment, 5);
        long recentFailures = similarFailures.stream()
                .filter(f -> f.getFailureDate().isAfter(LocalDate.now().minusMonths(6)))
                .count();
        return similarFailures.isEmpty() ? 0 : (double) recentFailures / similarFailures.size();
    }

    public String findEquipmentWithHighFailureProbability() {
        Equipment equipment = findEquipmentWithHighestFailureProbability();
        double failureProbability = calculateFailureProbability(equipment);

        SystemPromptTemplate promptTemplate = new SystemPromptTemplate(highFailureProbabilityResource);
        Map<String, Object> model = Map.of(
                "name", equipment.getName(),
                "id", equipment.getEquipmentId(),
                "failureProbability", String.format("%.2f", failureProbability),
                "failureCount", equipment.getFailureHistories().size(),
                "maintenanceCount", equipment.getMaintenanceHistories().size(),
                "lastFailureDate", getMostRecentDate(equipment.getFailureHistories(), FailureHistory::getFailureDate),
                "lastMaintenanceDate", getMostRecentDate(equipment.getMaintenanceHistories(), MaintenanceHistory::getMaintenanceDate),
                "count", equipment.getFailureHistories().size() + equipment.getMaintenanceHistories().size()
        );
        String prompt = promptTemplate.create(model).getContents();

        ChatResponse response = chatModel.call(
                new Prompt(
                        prompt,
                        OllamaOptions.builder()
                                .withTemperature(0F)
                                .build()
                )
        );

        return response.getResult().getOutput().getContent();
    }

    //public String findEquipmentWithHighFailureProbability() {
    //        List<Equipment> equipmentList = equipmentService.findAll();
    //
    //        Optional<Equipment> equipmentWithHighestRisk = equipmentList.stream()
    //                .max(Comparator.comparingDouble(this::calculateRiskScore));
    //
    //        if (equipmentWithHighestRisk.isPresent()) {
    //            Equipment equipment = equipmentWithHighestRisk.get();
    //            double riskScore = calculateRiskScore(equipment);
    //
    //            SystemPromptTemplate promptTemplate = new SystemPromptTemplate(highFailureProbabilityResource);
    //            Map<String, Object> model = Map.of(
    //                    "name", equipment.getName(),
    //                    "id", equipment.getEquipmentId(),
    //                    "riskScore", String.format("%.2f", riskScore),
    //                    "failureCount", equipment.getFailureHistories().size(),
    //                    "maintenanceCount", equipment.getMaintenanceHistories().size(),
    //                    "lastFailureDate", getMostRecentDate(equipment.getFailureHistories(), FailureHistory::getFailureDate),
    //                    "lastMaintenanceDate", getMostRecentDate(equipment.getMaintenanceHistories(), MaintenanceHistory::getMaintenanceDate),
    //                    "count", equipment.getFailureHistories().size() + equipment.getMaintenanceHistories().size() // 전체 이벤트 수
    //            );
    //            String prompt = promptTemplate.create(model).getContents();
    //
    //            ChatResponse response = chatModel.call(
    //                    new Prompt(
    //                            prompt,
    //                            OllamaOptions.builder()
    //                                    .withTemperature(0F)
    //                                    .build()
    //                    )
    //            );
    //
    //            return response.getResult().getOutput().getContent();
    //        } else {
    //            return "No equipment found with failure history.";
    //        }
    //    }

    private double calculateRiskScore(Equipment equipment) {
        long daysSinceLastFailure = getDaysSinceLastEvent(equipment.getFailureHistories(), FailureHistory::getFailureDate);
        long daysSinceLastMaintenance = getDaysSinceLastEvent(equipment.getMaintenanceHistories(), MaintenanceHistory::getMaintenanceDate);
        int failureCount = equipment.getFailureHistories().size();
        int maintenanceCount = equipment.getMaintenanceHistories().size();

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