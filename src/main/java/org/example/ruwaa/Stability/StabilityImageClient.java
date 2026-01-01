package org.example.ruwaa.Stability;

import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Attachments;
import org.example.ruwaa.Repository.AttachmentsRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class StabilityImageClient {

    private final WebClient webClient;
    private final AttachmentsRepository attachmentsRepository;

    public StabilityImageClient(
            @Value("${stability.api.base-url}") String baseUrl,
            @Value("${stability.api.key}") String apiKey,
            AttachmentsRepository attachmentsRepository) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey) // Stability uses Authorization header for API key :contentReference[oaicite:3]{index=3}
                .build();
        this.attachmentsRepository = attachmentsRepository;
    }

    public byte[] upscale(Integer postId, ImproveMode mode) {
        Attachments a = attachmentsRepository.findAttachmentOfPost(postId).orElseThrow(() -> new ApiException("no attachment found"));
        MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return a.getName();
            }

            @Override
            public @Nullable String getOriginalFilename() {
                return a.getName();
            }

            @Override
            public @Nullable String getContentType() {
                return "";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return a.getData();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {

            }
        };
        String path = switch (mode) {
            case UPSCALE_FAST -> "/v2beta/stable-image/upscale/fast";
            case UPSCALE_CONSERVATIVE -> "/v2beta/stable-image/upscale/conservative";
            case UPSCALE_CREATIVE -> "/v2beta/stable-image/upscale/creative";
        };

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("image", toFilePart(file));

        // Optional: some endpoints accept output_format; keep default PNG unless you confirm otherwise in your account/doc.
        // form.add("output_format", "png");

        // Request image bytes directly by setting Accept to image/* :contentReference[oaicite:4]{index=4}
        return webClient.post()
                .uri(path)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG, MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(form)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Stability error: " + resp.statusCode() + " - " + body)))
                )
                .bodyToMono(byte[].class)
                .block();
    }

    private HttpEntity<ByteArrayResource> toFilePart(MultipartFile file) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return (file.getOriginalFilename() != null) ? file.getOriginalFilename() : "input.png";
                }
            };

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(resolveMediaType(file.getContentType()));
            headers.setContentDispositionFormData("image", resource.getFilename());
            return new HttpEntity<>(resource, headers);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read upload", e);
        }
    }

    private MediaType resolveMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) return MediaType.APPLICATION_OCTET_STREAM;
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
