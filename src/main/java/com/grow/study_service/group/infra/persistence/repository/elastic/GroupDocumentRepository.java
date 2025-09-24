package com.grow.study_service.group.infra.persistence.repository.elastic;

import com.grow.study_service.group.domain.document.GroupDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GroupDocumentRepository extends ElasticsearchRepository<GroupDocument, String> {
}