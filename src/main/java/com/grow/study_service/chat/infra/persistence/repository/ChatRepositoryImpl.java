package com.grow.study_service.chat.infra.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grow.study_service.chat.domain.model.Chat;
import com.grow.study_service.chat.domain.repository.ChatRepository;
import com.grow.study_service.chat.infra.persistence.entity.ChatJpaEntity;
import com.grow.study_service.chat.infra.persistence.mapper.ChatMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepository {

	private final ChatJpaRepository chatJpaRepository;

	@Override
	public Chat save(Chat chat) {
		ChatJpaEntity saved = chatJpaRepository.save(ChatMapper.toEntity(chat));
		return ChatMapper.toDomain(saved);
	}

	@Override
	public Optional<Chat> findById(Long chatId) {
		return chatJpaRepository.findById(chatId)
			.map(ChatMapper::toDomain);
	}

	@Override
	public List<Chat> findByGroupId(Long groupId) {
		return chatJpaRepository.findByGroupId(groupId).stream()
			.map(ChatMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(Chat chat) {
		chatJpaRepository.delete(ChatMapper.toEntity(chat));
	}
}