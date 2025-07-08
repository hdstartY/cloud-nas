package org.hdstart.cloud.chat.controller;

import jakarta.servlet.http.HttpSession;
import org.hdstart.cloud.chat.handler.ChatWebSocketHandler;
import org.hdstart.cloud.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChatController {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;

    @GetMapping("/chat")
    public String chat(@RequestParam String username, Model model, HttpSession session) {
        session.setAttribute("username", username);
        model.addAttribute("username", username);
        return "chat";
    }

    @GetMapping("/chat/getStatus")
    public Result<Boolean> getStatus (@RequestParam("receiveId") Integer receiveId) {

        Boolean isOnline = chatWebSocketHandler.getReceiveStatus(receiveId);
        return Result.success(isOnline);
    }
}

