// SkillRepository.java
package com.devdoc.backend.repository;

import com.devdoc.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {

    // 관리 페이지(/record) 관련. 저장된 모든 이력서 조회 (테스트용)
    List<Skill> findByResumeId(Integer resumeId);
}