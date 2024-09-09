package genai.idea.fms.controller;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllamaAiRestController {

    private final OllamaChatModel chatModel;

    @Autowired
    public OllamaAiRestController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/ollama/chat")
    public String generate(@RequestParam(value = "message") String message) {
        ChatResponse response = chatModel.call(
                new Prompt(
                        message,
                        OllamaOptions.builder()
                                .withModel(OllamaModel.LLAMA3_1)
                                .withTemperature(0.2F)
                                .build())
        );
        return response.getResult().getOutput().getContent();
    }

}
