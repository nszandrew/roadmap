package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.model.dto.openai.GPTCreateRoadMapItemRequestDTO;
import br.com.nszandrew.roadmap.model.dto.openai.GPTResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.api.model}")
    private String openAiModel;

    private final RestTemplate restTemplate;


    public OpenAiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public GPTResponseDTO generateRoadMap(GPTCreateRoadMapItemRequestDTO requestDTO) {
        String systemPrompt = buildSystemPrompt(requestDTO);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAiModel);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 1);
        requestBody.put("max_tokens", 5000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(openAiApiUrl, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> choice = choices.get(0);
                Map<String, String> message = (Map<String, String>) choice.get("message");
                String jsonContent = message.get("content");

                return parseJsonResponse(jsonContent);
            }
        }

        throw new RuntimeException("Falha ao gerar roadmap com ChatGPT");
    }

    private GPTResponseDTO parseJsonResponse(String jsonContent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonContent, GPTResponseDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao parsear JSON do ChatGPT: " + e.getMessage());
        }
    }

    /**
     * Constrói o prompt (role=system).
     */
    private String buildSystemPrompt(GPTCreateRoadMapItemRequestDTO dto) {
        StringBuilder sb = new StringBuilder();
        sb.append("Você é um assistente especializado em criar RoadMaps.\n\n")
                .append("O usuário fornece o seguinte JSON:\n\n")
                .append("{\n")
                .append("  \"topic\": \"").append(dto.topic()).append("\",\n")
                .append("  \"level\": \"").append(dto.level()).append("\",\n")
                .append("  \"objectives\": ").append(dto.objectives()).append("\n")
                .append("}\n\n")
                .append("Com base nessas informações:\n\n")
                .append("1) Crie no mínimo 10 e no máximo 20 itens em formato JSON.\n")
                .append("2) Cada item deve ter: title, description, links, duration, dificulty, ")
                .append("comment (apenas se dificulty >= 3), e orderIndex incrementando de 1 até N.\n")
                .append("3) O nível do usuário (").append(dto.level()).append(") influencia a dificulty e a descrição.\n")
                .append("4) Retorne tudo dentro de um único JSON sem texto adicional, com { \"roadmapitem\": [ ... ] }.\n")
                .append("5) Não escreva nada fora do JSON final.\n");

        return sb.toString();
    }
}
