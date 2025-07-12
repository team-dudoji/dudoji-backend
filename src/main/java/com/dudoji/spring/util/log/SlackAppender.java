package com.dudoji.spring.util.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Setter
public class SlackAppender extends AppenderBase<ILoggingEvent> {

    private String webhookUrl;
    private String channel;

    @Override
    public void start() {
        if (webhookUrl.isEmpty() || channel.isEmpty()) {
            addError("SlackAppender is not configured properly. Please set the webhookUrl and channel.");
            return;
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> payload = Map.of(
                "channel", "#" + channel,
                "username", "dudoji-server",
                "icon_emoji", ":mouse:",
                "text", event.getFormattedMessage()
        );
        HttpEntity request = new HttpEntity(payload, header);
        restTemplate.postForEntity(webhookUrl, request, String.class);
    }
}
