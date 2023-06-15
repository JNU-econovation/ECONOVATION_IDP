package com.econovation.idp.config.slack.config;


import com.econovation.idpcommon.helper.SpringEnvironmentHelper;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.LayoutBlock;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Helper;
import org.springframework.stereotype.Component;

@Component
@Helper
@RequiredArgsConstructor
public class SlackHelper {
    // develop environment
    private final SpringEnvironmentHelper springEnvironmentHelper;

    private final MethodsClient methodsClient;

    public void sendNotification(String channelId, List<LayoutBlock> layoutBlocks) {
        if (!springEnvironmentHelper.isProdAndStagingProfile()) {
            return;
        }
        // message sender
        ChatPostMessageRequest chatPostMessageRequest =
                ChatPostMessageRequest.builder()
                        .channel(channelId)
                        .text("")
                        .blocks(layoutBlocks)
                        .build();
        // exception case
        try {
            methodsClient.chatPostMessage(chatPostMessageRequest);
        } catch (SlackApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
