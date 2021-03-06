package com.fursys.chatbot.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberDto {
	private Long id;
    private String email;
    private String password;
}
