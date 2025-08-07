package com.grow.study_service.chat.domain.model;

import java.time.LocalDateTime;

import com.grow.study_service.chat.domain.enums.MessageType;

import lombok.Getter;

@Getter
public class Chat {
	private final Long chatId;
	private final Long groupId;
	private final Long senderId;
	private final String content;
	private final MessageType messageType;
	private final LocalDateTime createdAt;

	private Chat(Long chatId, Long groupId, Long senderId, String content, MessageType messageType, LocalDateTime createdAt) {
		this.chatId = chatId;
		this.groupId = groupId;
		this.senderId = senderId;
		this.content = content;
		this.messageType = messageType;
		this.createdAt = createdAt;
	}

	public static Chat create(Long groupId, Long senderId, String content, MessageType messageType, LocalDateTime now) {
		return new Chat(null, groupId, senderId, content, messageType, now);
	}

	public static Chat of(Long chatId, Long groupId, Long senderId, String content, MessageType messageType, LocalDateTime createdAt) {
		return new Chat(chatId, groupId, senderId, content, messageType, createdAt);
	}
}
