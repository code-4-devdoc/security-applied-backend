// ResumeController.java
package com.devdoc.backend.service;

import com.devdoc.backend.dto.LanguageDTO;
import com.devdoc.backend.dto.ResumeDTO;
import com.devdoc.backend.dto.SkillDTO;
import com.devdoc.backend.model.Language;
import com.devdoc.backend.model.Resume;
import com.devdoc.backend.model.Skill;
import com.devdoc.backend.model.UserEntity;
import com.devdoc.backend.repository.LanguageRepository;
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
    private LanguageRepository languageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveResume(int resumeId, ResumeDTO resumeDTO) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            resume.setTitle(resumeDTO.getTitle());

            List<Language> languages = resumeDTO.getLanguages().stream()
                    .map(languageDTO -> new Language(languageDTO.getId(), languageDTO.getLanguage(), languageDTO.getTestName(), languageDTO.getScore(), languageDTO.getDate(), resume))
                    .collect(Collectors.toList());
            resume.setLanguages(languages);

            resumeRepository.save(resume);
        }
    }

    public ResumeDTO getResumeByResumeId(int resumeId) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();

            List<LanguageDTO> languageDTOs = resume.getLanguages().stream()
                    .map(language -> new LanguageDTO(language.getId(), language.getLanguage(), language.getTestName(), language.getScore(), language.getDate()))
                    .collect(Collectors.toList());

            return new ResumeDTO(resume.getId(), resume.getTitle(), languageDTOs);
        }
        return null;
    }

    public List<ResumeDTO> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return resumes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ResumeDTO convertToDTO(Resume resume) {
        List<LanguageDTO> languages = languageRepository.findByResumeId(resume.getId())
                .stream()
                .map(language -> new LanguageDTO(language.getId(), language.getLanguage(), language.getTestName(), language.getScore(), language.getDate()))
                .collect(Collectors.toList());

        return new ResumeDTO(resume.getId(), resume.getTitle(), languages);
    }

    public List<ResumeDTO> getAllResumesByUser(String userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return resumes.stream().map(resume -> new ResumeDTO(resume.getId(), resume.getTitle(), null)).collect(Collectors.toList());
    }

    @Transactional
    public Resume createResume(String title, String userId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Resume resume = new Resume(title, user);
            resume = resumeRepository.save(resume);
            return resume;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Transactional
    public void deleteResumeByResumeId(int resumeId) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        optionalResume.ifPresent(resumeRepository::delete);
    }

    @Transactional
    public ResumeDTO saveResumeTitleByResumeId(int resumeId, String newTitle) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            resume.setTitle(newTitle);
            resumeRepository.save(resume);
            return new ResumeDTO(resume.getId(), resume.getTitle(), null);
        }
        return null;
    }
}
