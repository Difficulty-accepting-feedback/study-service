package com.grow.study_service.group.chat.infra.persistence.mapper;

import com.grow.study_service.group.chat.domain.model.Chat;
import com.grow.study_service.group.chat.infra.persistence.entity.ChatJpaEntity;

public class ChatMapper {

	public static Chat toDomain(ChatJpaEntity e) {
		return Chat.of(
			e.getChatId(),
			e.getGroupId(),
			e.getSenderId(),
			e.getContent(),
			e.getMessageType(),
			e.getCreatedAt()
		);
	}

	public static ChatJpaEntity toEntity(Chat d) {
		return ChatJpaEntity.builder()
			.chatId(d.getChatId())
			.groupId(d.getGroupId())
			.senderId(d.getSenderId())
			.content(d.getContent())
			.messageType(d.getMessageType())
			.createdAt(d.getCreatedAt())
			.build();
	}
}