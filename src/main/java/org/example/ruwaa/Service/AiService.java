package org.example.ruwaa.Service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ruwaa.DTOs.WorkPostDTO;
import org.example.ruwaa.Model.Attachments;
import org.example.ruwaa.Model.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public String askAI(String prompt){
        String body = "{\n" +
                "  \"model\": \"gpt-4o-mini\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"user\", \"content\": \"" + prompt.replace("\"","\\\"") + "\"}\n" +
                "  ]\n" +
                "}";

        String raw = webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            JsonNode json = mapper.readTree(raw);
            return json.get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

        } catch (Exception e) {
            return "AI parsing error: " + e.getMessage() + "\n Raw: " + raw;
        }

    }


    public String dtoPost(Post p){
       WorkPostDTO dto = new WorkPostDTO(p.getContent(),true,p.getAttachment(),p.getCategory().getName());

        String dtoString = "Work Post Details: Content: "+
                dto.getContent()+", possible category : "+dto.getCategory()+", Attachments: ";

        if (dto.getAttachments() == null) {
           dtoString+= " none";
        } else {

            dtoString+= "*"+dto.getAttachments().getName()+"( "+
                    dto.getAttachments().getData().toString()+")";

        }
        System.out.println(dtoString);
        return dtoString;
    }

}
