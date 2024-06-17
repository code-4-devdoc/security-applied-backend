// ResumeDTO.java
package com.devdoc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private Integer id;
    private String title;
    private List<LanguageDTO> languages;
    private List<AwardDTO> awards;
}
