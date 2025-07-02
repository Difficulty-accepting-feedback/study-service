package com.grow.study_service.group.chat.domain.repository;

import java.util.List;
import java.util.Optional;

import com.grow.study_service.group.chat.domain.model.Chat;

public interface ChatRepository {
	Chat save(Chat chat);
	Optional<Chat> findById(Long chatId);
	List<Chat> findByGroupId(Long groupId);
	void delete(Chat chat);
}