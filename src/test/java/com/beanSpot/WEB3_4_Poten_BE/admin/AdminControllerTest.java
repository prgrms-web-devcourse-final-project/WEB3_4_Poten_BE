package com.beanSpot.WEB3_4_Poten_BE.admin;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller.AdminController;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.AdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private MemberRepository memberRepository;

	@MockBean
	private PasswordEncoder passwordEncoder;

	@MockBean
	private AdminService adminService;

	private Member mockAdmin;
	private String mockToken;
	private String mockRefreshToken;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.build();

		// Mock admin user
		mockAdmin = Member.builder()
			.id(1L)
			.email("admin@example.com")
			.password("encodedPassword")
			.name("Admin User")
			.memberType(Member.MemberType.ADMIN)
			.build();

		// Mock tokens
		mockToken = "mock-jwt-token";
		mockRefreshToken = "mock-refresh-token";
	}

	@Test
	@DisplayName("Admin Login - Success")
	void testAdminLoginSuccess() throws Exception {
		// Given
		AdminLoginDto loginDto = new AdminLoginDto("admin@example.com", "password123");

		when(memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN))
			.thenReturn(Optional.of(mockAdmin));
		when(passwordEncoder.matches(loginDto.password(), mockAdmin.getPassword()))
			.thenReturn(true);
		when(jwtService.generateToken(mockAdmin)).thenReturn(mockToken);
		when(jwtService.generateRefreshToken(mockAdmin)).thenReturn(mockRefreshToken);

		// When & Then
		mockMvc.perform(post("/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDto)))
			.andExpect(status().isOk())
			.andExpect(header().string("Authorization", "Bearer " + mockToken))
			.andExpect(header().string("RefreshToken", mockRefreshToken))
			.andExpect(content().string("관리자 로그인 성공"));
	}

	@Test
	@DisplayName("Admin Login - User Not Found")
	void testAdminLoginUserNotFound() throws Exception {
		// Given
		AdminLoginDto loginDto = new AdminLoginDto("unknown@example.com", "password123");

		when(memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN))
			.thenReturn(Optional.empty());

		// When & Then
		mockMvc.perform(post("/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDto)))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("로그인 실패 - 계정을 찾을 수 없음"));
	}

	@Test
	@DisplayName("Admin Login - Incorrect Password")
	void testAdminLoginIncorrectPassword() throws Exception {
		// Given
		AdminLoginDto loginDto = new AdminLoginDto("admin@example.com", "wrongPassword");

		when(memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN))
			.thenReturn(Optional.of(mockAdmin));
		when(passwordEncoder.matches(loginDto.password(), mockAdmin.getPassword()))
			.thenReturn(false);

		// When & Then
		mockMvc.perform(post("/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginDto)))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string("로그인 실패 - 비밀번호 불일치"));
	}

	@Test
	@DisplayName("Admin Logout - Success")
	void testAdminLogoutSuccess() throws Exception {
		// When & Then
		mockMvc.perform(post("/admin/logout")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isOk())
			.andExpect(content().string("관리자 로그아웃 성공. 클라이언트에서 토큰을 삭제해주세요."));
	}

	@Test
	@DisplayName("Get Pending Applications - Success")
	void testGetPendingApplicationsSuccess() throws Exception {
		// Given
		List<ApplicationRes> mockApplications = Arrays.asList(
			new ApplicationRes(1L, "Cafe 1", "Address 1", "123-456-7890", "PENDING"),
			new ApplicationRes(2L, "Cafe 2", "Address 2", "098-765-4321", "PENDING")
		);

		when(adminService.getPendingApplications()).thenReturn(mockApplications);

		// When & Then
		mockMvc.perform(get("/admin/applications/pending")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockApplications)));
	}

	@Test
	@DisplayName("Approve Application - Success")
	void testApproveApplicationSuccess() throws Exception {
		// Given
		ApplicationApprovedRes mockResponse = new ApplicationApprovedRes(1L, "Cafe 1", "Address 1", "123-456-7890");

		when(adminService.approveApplication(1L)).thenReturn(mockResponse);

		// When & Then
		mockMvc.perform(post("/admin/applications/1/approve")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}

	@Test
	@DisplayName("Approve Application - Not Found")
	void testApproveApplicationNotFound() throws Exception {
		// Given
		when(adminService.approveApplication(99L))
			.thenThrow(new ServiceException(400, "카페 신청을 찾을 수 없습니다."));

		// When & Then
		mockMvc.perform(post("/admin/applications/99/approve")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Reject Application - Success")
	void testRejectApplicationSuccess() throws Exception {
		// Given
		ApplicationRes mockResponse = new ApplicationRes(1L, "Cafe 1", "Address 1", "123-456-7890", "REJECTED");

		when(adminService.rejectApplication(1L)).thenReturn(mockResponse);

		// When & Then
		mockMvc.perform(post("/admin/applications/1/reject")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
	}

	@Test
	@DisplayName("Reject Application - Not Found")
	void testRejectApplicationNotFound() throws Exception {
		// Given
		when(adminService.rejectApplication(99L))
			.thenThrow(new ServiceException(400, "카페 신청을 찾을 수 없습니다."));

		// When & Then
		mockMvc.perform(post("/admin/applications/99/reject")
				.header("Authorization", "Bearer " + mockToken))
			.andExpect(status().isNotFound());
	}
}
