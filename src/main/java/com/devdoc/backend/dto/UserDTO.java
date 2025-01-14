package com.devdoc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// UserDTO: 사용자 DTO

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String id; // 사용자 고유 ID
	private String token; // JWT 토큰
	private String username; // 사용자 이름
	private String password; // 비밀번호
	private String email; // 이메일


}
