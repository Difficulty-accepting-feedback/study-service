package com.grow.study_service.group.domain.document;

import com.grow.study_service.group.domain.enums.Category;
import com.grow.study_service.group.domain.enums.PersonalityTag;
import com.grow.study_service.group.domain.enums.SkillTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Document(indexName = "groups")
@Setting(settingPath = "/elasticsearch/group-settings.json")
@Getter
@AllArgsConstructor
public class GroupDocument {

    @Id
    private String id; // 엘라스틱 서치 기본 ID 값은 String 타입으로 저장됨

    // 그룹 이름 -> 유연한 검색 가능, 자동 완성이 가능하게끔 해야 함
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "groups_name_analyzer"), // 한+영 검색 허용
            otherFields = {
                    @InnerField(suffix = "auto_complete", type = FieldType.Search_As_You_Type, analyzer = "nori") // 자동 완성은 한글 검색 허용으로
            }
    )
    private String name;

    @Field(type = FieldType.Text, analyzer = "groups_description_analyzer")
    private String description;

    // 단어 입력 -> [그룹 이름 + 설명 + 카테고리 + 스킬 태그]로 검색 가능 (이 중에 하나에만 속해도 검색이 가능할 수 있도록)
    // 유연한 타입 + 카테고리로만 필터링 할 때를 위해서 정확한 타입 또한 필요
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "groups_category_analyzer"),
            otherFields = {
                    @InnerField(suffix = "raw", type = FieldType.Keyword)
            }
    )
    private String category;

    @Field(type = FieldType.Date)
    private LocalDate startAt;

    @Field(type = FieldType.Date)
    private LocalDate endAt;

    @Field(type = FieldType.Integer)
    private int amount;

    @Field(type = FieldType.Long)
    private int viewCount;

    @Field(type = FieldType.Keyword)
    private PersonalityTag personalityTag; // 특성 태그 -> Null 가능, List 가능으로 변경 필요함

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "groups_skillTag_analyzer"),
            otherFields = {
                    @InnerField(suffix = "raw", type = FieldType.Keyword)
            }
    )
    private String skillTag;

    // 하이라이팅 하기 위함
    public void updateName(String newName) {
        this.name = newName;
    }
}
