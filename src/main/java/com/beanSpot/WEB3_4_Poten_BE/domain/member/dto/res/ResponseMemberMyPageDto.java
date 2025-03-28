package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseMemberMyPageDto {
	private Long id;
	private String oAuthId;
	private String name;
	private String email;
	private Member.MemberType memberType;
	private Member.SnsType snsType;
	private String profileImg;
	private String phoneNumber;
	private String updatedAt;  // 정보 업데이트 시간 추가

	public ResponseMemberMyPageDto(Member entity) {
		this.id = entity.getId();
		this.oAuthId = entity.getOAuthId();
		this.name = entity.getName();
		this.email = entity.getEmail();
		this.memberType = entity.getMemberType();
		this.snsType = entity.getSnsType();
		this.profileImg = entity.getProfileImg();
		this.phoneNumber = entity.getPhoneNumber();
		this.updatedAt = java.time.LocalDateTime.now().toString(); // 현재 시간 설정
	}
}

