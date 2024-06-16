// ResumeController.java
package com.devdoc.backend.service;

import com.devdoc.backend.dto.ResumeDTO;
import com.devdoc.backend.dto.SkillDTO;
import com.devdoc.backend.model.Resume;
import com.devdoc.backend.model.Skill;
import com.devdoc.backend.model.UserEntity;
import com.devdoc.backend.repository.ResumeRepository;
import com.devdoc.backend.repository.SkillRepository;
import com.devdoc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository; // UserRepository 추가

    // User 의 Resume 목록 조회 : Id와 Title만
    public List<ResumeDTO> getAllResumesByUser(String userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        List<ResumeDTO> resumeDTOs = new ArrayList<>();
        for (Resume resume : resumes) {
            ResumeDTO resumeDTO = new ResumeDTO(resume.getId(), resume.getTitle());
            resumeDTOs.add(resumeDTO);
        }
        return resumeDTOs;
    }

    // ResumeId 조회 : 모든 테이블
    public ResumeDTO getResumeByResumeIdTest(int resumeId) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            List<SkillDTO> skillDTOs = resume.getSkills().stream()
                    .map(skill -> new SkillDTO(skill.getId(), skill.getResume().getId(), skill.getStatus(), skill.getContent()))
                    .collect(Collectors.toList());
            return new ResumeDTO(resume.getId(), resume.getTitle(), skillDTOs);
        }
        return null;
    }

    // ResumeId 조회 : Status = T 인 모든 테이블
    public ResumeDTO getResumeByResumeId(int resumeId) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            List<Skill> skills = resume.getSkills().stream()
                    .filter(Skill::getStatus)
                    .collect(Collectors.toList());
            List<SkillDTO> skillDTOs = new ArrayList<>();
            for (Skill skill : skills) {
                SkillDTO skillDTO = new SkillDTO(skill.getId(), skill.getResume().getId(), skill.getStatus(), skill.getContent());
                skillDTOs.add(skillDTO);
            }
            return new ResumeDTO(resume.getId(), resume.getTitle(), skillDTOs);
        }
        return null;
    }

    // ResumeId 생성 : SkillId x3 생성
    @Transactional
    public Resume createResume(String title, String userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Resume resume = new Resume(title, user);
            resume = resumeRepository.save(resume);

            for (int i = 0; i < 3; i++) {
                Skill skill = new Skill();
                skill.setResume(resume);                // resumeId
                skillRepository.save(skill);
            }

            return resume;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    // ResumeId 삭제
    @Transactional
    public void deleteResumeByResumeId(int resumeId) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        optionalResume.ifPresent(resume -> resumeRepository.delete(resume));
    }

    // ResumeId 업데이트 : Title만
    @Transactional
    public ResumeDTO saveResumeTitleByResumeId(int resumeId, String newTitle) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);

        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            resume.setTitle(newTitle);
            resumeRepository.save(resume);
            return new ResumeDTO(resume.getId(), resume.getTitle());
        }
        return null;
    }

    // SkillId 업데이트
    @Transactional
    public SkillDTO saveSkillBySkillId(int skillId, String content) {
        Optional<Skill> optionalSkill = skillRepository.findById(skillId);
        if (optionalSkill.isPresent()) {
            Skill skill = optionalSkill.get();

            if (content != null && !content.equalsIgnoreCase("null")) {
                skill.setContent(content);
                skill.setStatus(true);
            } else {
                skill.setContent(null);
                skill.setStatus(false);         // status : T? F?
            }

            skillRepository.save(skill);
            return new SkillDTO(skill.getId(), skill.getResume().getId(), skill.getStatus(), skill.getContent());
        }
        return null;
    }
}
