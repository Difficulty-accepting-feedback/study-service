package com.grow.study_service.post.infra.persistence.repository.file;

import com.grow.study_service.post.domain.model.FileMeta;
import com.grow.study_service.post.domain.repository.FileMetaRepository;
import com.grow.study_service.post.infra.persistence.entity.FileMetaJpaEntity;
import com.grow.study_service.post.infra.persistence.mapper.FileMetaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileMetaRepositoryImpl implements FileMetaRepository {

    private final FileMetaMapper mapper;
    private final FileMetaJpaRepository jpaRepository;

    @Override
    public FileMeta save(FileMeta fileMeta) {
        FileMetaJpaEntity entity = mapper.toEntity(fileMeta);
        FileMetaJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * 파일 정보를 조회하는 메서드.
     * @param postId 게시글 ID (파일 정보를 조회할 게시글의 ID)
     * @return 조회된 파일 정보 목록 (List<FileMeta>)
     */
    public List<FileMeta> findAllByPostId(Long postId) {
        return jpaRepository.findAllByPostId(postId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
