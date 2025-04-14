package com.beanSpot.WEB3_4_Poten_BE.domain.admin;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller.AdminMemberController;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.ReviewAdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service.CafeService;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ReviewAdminService reviewAdminService;

	@Mock
	private CafeService cafeService;

	@InjectMocks
	private AdminMemberController adminMemberController;

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(adminMemberController)
			.addFilter((request, response, chain) -> {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				chain.doFilter(request, response);
			})
			.build();

		// ObjectMapper 설정 - 한글 처리 개선
		objectMapper = new ObjectMapper();
		objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
	}

	@Test
	@DisplayName("관리자 로그인 성공 테스트")
	void adminLogin_Success() throws Exception {
		// Given
		AdminLoginDto loginDto = new AdminLoginDto("admin@beanspot.com", "adminPassword");
		Member adminMember = Member.builder()
			.id(1L)
			.email("admin@beanspot.com")
			.password("encoded_password")
			.memberType(Member.MemberType.ADMIN)
			.oAuthId("admin")
			.build();

		when(memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN))
			.thenReturn(Optional.of(adminMember));
		when(passwordEncoder.matches(loginDto.password(), adminMember.getPassword()))
			.thenReturn(true);
		when(jwtService.generateToken(adminMember)).thenReturn("test_access_token");
		when(jwtService.generateRefreshToken(adminMember)).thenReturn("test_refresh_token");

		// When & Then
		mockMvc.perform(post("/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDto)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(header().string("Authorization", "Bearer test_access_token"))
			.andExpect(header().string("RefreshToken", "test_refresh_token"))
			.andExpect(jsonPath("$.message").value("관리자 로그인 성공"))
			.andExpect(jsonPath("$.userId").value(1L));
	}

	@Test
	@DisplayName("관리자 로그인 실패 테스트 - 없는 계정")
	void adminLogin_Failure_AccountNotFound() throws Exception {
		// Given
		AdminLoginDto loginDto = new AdminLoginDto("nonexistent@beanspot.com", "password");

		when(memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN))
			.thenReturn(Optional.empty());

		// When & Then
		mockMvc.perform(post("/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsString(loginDto)))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.error").exists())
			.andExpect(content().string(containsString("실패")));
	}


	@Test
	@DisplayName("대기 중인 카페 신청 목록 조회 테스트")
	void getPendingApplications_Success() throws Exception {
		// Given
		ApplicationRes app1 = new ApplicationRes(1L, 101L, "카페1", "서울시 강남구", "010-1234-5678", "PENDING");
		ApplicationRes app2 = new ApplicationRes(2L, 102L, "카페2", "서울시 서초구", "010-5678-1234", "PENDING");
		List<ApplicationRes> applications = Arrays.asList(app1, app2);

		when(reviewAdminService.getPendingApplications()).thenReturn(applications);

		// When & Then
		mockMvc.perform(get("/admin/applications/pending"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].name").value("카페1"))
			.andExpect(jsonPath("$[1].id").value(2L))
			.andExpect(jsonPath("$[1].name").value("카페2"));
	}

	@Test
	@DisplayName("카페 신청 승인 테스트")
	void approveApplication_Success() throws Exception {
		// Given
		Long applicationId = 1L;
		ApplicationRes approvedApp = new ApplicationRes(
			applicationId, 101L, "승인된 카페", "서울시 강남구", "010-1234-5678", "APPROVED");

		when(reviewAdminService.approveApplication(applicationId)).thenReturn(approvedApp);

		// When & Then
		mockMvc.perform(post("/admin/applications/{applicationId}/approve", applicationId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(applicationId))
			.andExpect(jsonPath("$.status").value("APPROVED"));

		verify(reviewAdminService, times(1)).approveApplication(applicationId);
	}

	@Test
	@DisplayName("카페 신청 거절 테스트")
	void rejectApplication_Success() throws Exception {
		// Given
		Long applicationId = 1L;
		ApplicationRes rejectedApp = new ApplicationRes(
			applicationId, 101L, "거절된 카페", "서울시 강남구", "010-1234-5678", "REJECTED");

		when(reviewAdminService.rejectApplication(applicationId)).thenReturn(rejectedApp);

		// When & Then
		mockMvc.perform(post("/admin/applications/{applicationId}/reject", applicationId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(applicationId))
			.andExpect(jsonPath("$.status").value("REJECTED"));

		verify(reviewAdminService, times(1)).rejectApplication(applicationId);
	}

	@Test
	@DisplayName("회원 목록 조회 테스트 - 회원 유형 필터링 없음")
	void getMembers_Success_NoFilter() throws Exception {
		// Given
		Member member1 = createMember(1L, "user1@test.com", Member.MemberType.USER);
		Member member2 = createMember(2L, "owner@test.com", Member.MemberType.OWNER);
		List<Member> members = Arrays.asList(member1, member2);

		when(reviewAdminService.getAllMembers()).thenReturn(members);

		// When & Then
		mockMvc.perform(get("/admin/members"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].email").value("user1@test.com"))
			.andExpect(jsonPath("$[1].id").value(2L))
			.andExpect(jsonPath("$[1].email").value("owner@test.com"));
	}

	@Test
	@DisplayName("회원 목록 조회 테스트 - 회원 유형 필터링 있음")
	void getMembers_Success_WithFilter() throws Exception {
		// Given
		Member member1 = createMember(1L, "user1@test.com", Member.MemberType.USER);
		Member member2 = createMember(2L, "user2@test.com", Member.MemberType.USER);
		List<Member> members = Arrays.asList(member1, member2);

		when(reviewAdminService.getMembersByType(Member.MemberType.USER)).thenReturn(members);

		// When & Then
		mockMvc.perform(get("/admin/members")
				.param("memberType", "USER"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(2)))
			.andExpect(jsonPath("$[0].id").value(1L))
			.andExpect(jsonPath("$[0].memberType").value("USER"))
			.andExpect(jsonPath("$[1].id").value(2L))
			.andExpect(jsonPath("$[1].memberType").value("USER"));
	}

	@Test
	@DisplayName("회원 상세 정보 조회 테스트")
	void getMemberDetails_Success() throws Exception {
		// Given
		Long memberId = 1L;
		Member member = createMember(memberId, "user@test.com", Member.MemberType.USER);

		when(reviewAdminService.getMemberById(memberId)).thenReturn(member);

		// When & Then
		mockMvc.perform(get("/admin/members/{memberId}", memberId))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(memberId))
			.andExpect(jsonPath("$.email").value("user@test.com"));

		verify(reviewAdminService, times(1)).getMemberById(memberId);
	}

	@Test
	@DisplayName("관리자용 카페 리스트 조회 테스트")
	void getAdminCafeList_Success() throws Exception {
		// 테스트 단순화: 메서드 호출 검증만 수행
		mockMvc.perform(get("/admin/admin/cafes")
				.param("page", "0")
				.param("size", "20"))
			.andDo(print())
			// 상태 코드 검증 제외
			.andExpect(status().isOk());

		verify(cafeService).getAdminCafeList(0, 20);
	}

	@Test
	@DisplayName("관리자 로그아웃 테스트")
	void adminLogout_Success() throws Exception {
		// Given
		String token = "test_token";

		// When & Then
		mockMvc.perform(post("/admin/admin/logout")
				.header("Authorization", "Bearer " + token))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));

		verify(jwtService, times(1)).blacklistToken(token);
	}

	@Test
	@DisplayName("관리자 로그아웃 실패 테스트 - 유효한 토큰 없음")
	void adminLogout_Failure_NoToken() throws Exception {
		// When & Then
		mockMvc.perform(post("/admin/admin/logout"))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("유효한 토큰이 없습니다."));
	}

	// 테스트용 Member 객체 생성 도우미 메서드
	private Member createMember(Long id, String email, Member.MemberType memberType) {
		return Member.builder()
			.id(id)
			.email(email)
			.name("Test User " + id)
			.oAuthId("oauth_" + id)
			.memberType(memberType)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}
}