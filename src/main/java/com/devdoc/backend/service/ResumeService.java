package com.devdoc.backend.service;

import com.devdoc.backend.dto.AwardDTO;
import com.devdoc.backend.dto.LanguageDTO;
import com.devdoc.backend.dto.ResumeDTO;
import com.devdoc.backend.model.Award;
import com.devdoc.backend.model.Language;
import com.devdoc.backend.model.Resume;
import com.devdoc.backend.model.UserEntity;
import com.devdoc.backend.repository.AwardRepository;
import com.devdoc.backend.repository.LanguageRepository;
import com.devdoc.backend.repository.ResumeRepository;
import com.devdoc.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private AwardRepository awardRepository;

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

            List<Award> awards = resumeDTO.getAwards().stream()
                    .map(awardDTO -> new Award(awardDTO.getId(), awardDTO.getAwardName(), awardDTO.getAwardingInstitution(), awardDTO.getDate(), awardDTO.getDescription(), resume))
                    .collect(Collectors.toList());
            resume.setAwards(awards);

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

            List<AwardDTO> awardDTOs = resume.getAwards().stream()
                    .map(award -> new AwardDTO(award.getId(), award.getAwardName(), award.getAwardingInstitution(), award.getDate(), award.getDescription()))
                    .collect(Collectors.toList());

            return new ResumeDTO(resume.getId(), resume.getTitle(), languageDTOs, awardDTOs);
        }
        return null;
    }

    public List<ResumeDTO> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return resumes.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ResumeDTO convertToDTO(Resume resume) {
        List<LanguageDTO> languageDTOs = languageRepository.findByResumeId(resume.getId())
                .stream()
                .map(language -> new LanguageDTO(language.getId(), language.getLanguage(), language.getTestName(), language.getScore(), language.getDate()))
                .collect(Collectors.toList());

        List<AwardDTO> awardDTOs = awardRepository.findByResumeId(resume.getId())
                .stream()
                .map(award -> new AwardDTO(award.getId(), award.getAwardName(), award.getAwardingInstitution(), award.getDate(), award.getDescription()))
                .collect(Collectors.toList());

        return new ResumeDTO(resume.getId(), resume.getTitle(), languageDTOs, awardDTOs);
    }

    public List<ResumeDTO> getAllResumesByUser(String userId) {
        List<Resume> resumes = resumeRepository.findByUserId(userId);
        return resumes.stream().map(resume -> new ResumeDTO(resume.getId(), resume.getTitle(), null, null)).collect(Collectors.toList());
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
            return new ResumeDTO(resume.getId(), resume.getTitle(), null, null);
        }
        return null;
    }

    // Language 관련 메서드들
    @Transactional
    public LanguageDTO saveOrUpdateLanguage(int resumeId, LanguageDTO languageDTO) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            Language language = languageRepository.findByIdAndResumeId(languageDTO.getId(), resumeId)
                    .orElse(new Language());

            boolean isNew = (language.getId() == null);

            language.setLanguage(languageDTO.getLanguage());
            language.setTestName(languageDTO.getTestName());
            language.setScore(languageDTO.getScore());
            language.setDate(languageDTO.getDate());
            language.setResume(resume);

            Language savedLanguage = languageRepository.save(language);

            return new LanguageDTO(savedLanguage.getId(), savedLanguage.getLanguage(), savedLanguage.getTestName(), savedLanguage.getScore(), savedLanguage.getDate());
        }
        throw new RuntimeException("Resume not found");
    }

    @Transactional
    public void deleteLanguage(int resumeId, int languageId) {
        Optional<Language> language = languageRepository.findByIdAndResumeId(languageId, resumeId);
        language.ifPresent(languageRepository::delete);
    }

    // Award 관련 메서드들
    @Transactional
    public AwardDTO saveOrUpdateAward(int resumeId, AwardDTO awardDTO) {
        Optional<Resume> optionalResume = resumeRepository.findById(resumeId);
        if (optionalResume.isPresent()) {
            Resume resume = optionalResume.get();
            Award award = awardRepository.findByIdAndResumeId(awardDTO.getId(), resumeId)
                    .orElse(new Award());

            boolean isNew = (award.getId() == null);

            award.setAwardName(awardDTO.getAwardName());
            award.setAwardingInstitution(awardDTO.getAwardingInstitution());
            award.setDate(awardDTO.getDate());
            award.setDescription(awardDTO.getDescription());
            award.setResume(resume);

            Award savedAward = awardRepository.save(award);

            return new AwardDTO(savedAward.getId(), savedAward.getAwardName(), savedAward.getAwardingInstitution(), savedAward.getDate(), savedAward.getDescription());
        }
        throw new RuntimeException("Resume not found");
    }

    @Transactional
    public void deleteAward(int resumeId, int awardId) {
        Optional<Award> award = awardRepository.findByIdAndResumeId(awardId, resumeId);
        award.ifPresent(awardRepository::delete);
    }
}
