package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Model.Card;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${moyasar.api.key}")
    private String apikey;

    private static final String MOYASAR_API_KEY = "https://api.moyasar.com/v1/payments/";

    public ResponseEntity<String> processPayment(Double amount,Card card) {
        String url = "https://api.moyasar.com/v1/payments";

        String callBackUrl = "http://localhost:8080/api/v1/payment/thanks";

        String requestBody = String.format(
                "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s&" +
                        "source[month]=%s&source[year]=%s&amount=%d&currency=%s" +
                        "&callback_url=%s" ,
                card.getName(),
                card.getNumber(),
                card.getCvc(),
                card.getMonth(),
                card.getYear(),
                (int) (amount * 100),
                "SAR",
                callBackUrl
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apikey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public String getPaymentStatus(String paymentId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apikey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_KEY + paymentId, HttpMethod.GET, entity, String.class
        );

        return response.getBody();
    }
}

