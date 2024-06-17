// ResumeController.java
package com.devdoc.backend.controller;

import com.devdoc.backend.dto.ResumeDTO;
import com.devdoc.backend.dto.SkillDTO;
import com.devdoc.backend.model.Resume;
import com.devdoc.backend.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @DeleteMapping("/{resumeId}/languages/{languageId}")
    public ResponseEntity<Void> deleteLanguage(@PathVariable int resumeId, @PathVariable int languageId) {
        try {
            resumeService.deleteLanguage(resumeId, languageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/record")
    public ResponseEntity<List<ResumeDTO>> getAllResumes() {
        List<ResumeDTO> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    @PostMapping("/{resumeId}/save")
    public ResponseEntity<?> saveResume(@PathVariable int resumeId, @RequestBody ResumeDTO resumeDTO) {
        try {
            resumeService.saveResume(resumeId, resumeDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDTO> getResumeByResumeId(@PathVariable int resumeId) {
        try {
            ResumeDTO resumeDTO = resumeService.getResumeByResumeId(resumeId);
            if (resumeDTO != null) {
                return new ResponseEntity<>(resumeDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<ResumeDTO>> getAllResumesByUser(@AuthenticationPrincipal String userId) {
        try {
            List<ResumeDTO> resumes = resumeService.getAllResumesByUser(userId);
            return new ResponseEntity<>(resumes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Resume> createResume(@RequestBody String title, @AuthenticationPrincipal String userId) {
        try {
            Resume createdResume = resumeService.createResume(title, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdResume);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResumeByResumeId(@PathVariable int resumeId) {
        try {
            resumeService.deleteResumeByResumeId(resumeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{resumeId}/title")
    public ResponseEntity<ResumeDTO> saveResumeTitleByResumeId(
            @PathVariable int resumeId,
            @RequestBody String newTitle) {
        try {
            ResumeDTO updatedResume = resumeService.saveResumeTitleByResumeId(resumeId, newTitle);
            if (updatedResume != null) {
                return ResponseEntity.ok(updatedResume);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
