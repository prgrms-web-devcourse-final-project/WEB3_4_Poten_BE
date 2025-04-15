package com.beanSpot.WEB3_4_Poten_BE.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository.FavoriteRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Profile({"dev", "test"})
@Slf4j
public class InitializerService implements ApplicationRunner {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final CafeRepository cafeRepository;
	private final ApplicationRepository applicationRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteRepository favoriteRepository;
	private final ReservationRepository reservationRepository;
	private final PlatformTransactionManager transactionManager;

	@Override
	public void run(ApplicationArguments args) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

		// Check if we already have data
		if (isInitialized()) {
			log.info("데이터가 이미 초기화되어 있습니다. 초기화 작업을 건너뜁니다.");
			generateTestUserTokens(); // Still generate tokens for convenience
			return;
		}

		try {
			// Initialize admin account in its own transaction
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						initAdminAccount();
					} catch (Exception e) {
						log.error("관리자 계정 초기화 중 오류 발생: {}", e.getMessage(), e);
						status.setRollbackOnly();
					}
				}
			});

			// Initialize test users in its own transaction
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						initTestUsers();
					} catch (Exception e) {
						log.error("테스트 사용자 초기화 중 오류 발생: {}", e.getMessage(), e);
						status.setRollbackOnly();
					}
				}
			});

			// Create test cafes in its own transaction
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						createTestCafes();
					} catch (Exception e) {
						log.error("테스트 카페 생성 중 오류 발생: {}", e.getMessage(), e);
						status.setRollbackOnly();
					}
				}
			});

			// Setup test user roles in its own transaction
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						setupTestUserRoles();
					} catch (Exception e) {
						log.error("테스트 사용자 역할 설정 중 오류 발생: {}", e.getMessage(), e);
						status.setRollbackOnly();
					}
				}
			});

			// Create additional test data in its own transaction
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						createAdditionalTestData();
					} catch (Exception e) {
						log.error("추가 테스트 데이터 생성 중 오류 발생: {}", e.getMessage(), e);
						status.setRollbackOnly();
					}
				}
			});

			// Generate tokens (no transaction needed)
			generateTestUserTokens();

		} catch (Exception e) {
			log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
		}
	}

	/**
	 * 이미 초기화가 완료되었는지 확인
	 */
	private boolean isInitialized() {
		// 관리자 계정, 테스트 사용자, 카페 등이 이미 있는지 확인
		boolean hasAdmin = memberRepository.findByEmailAndMemberType("admin@beanspot.com", Member.MemberType.ADMIN).isPresent();
		boolean hasTestUsers = memberRepository.findByEmail("test-kakao@example.com").isPresent() &&
			memberRepository.findByEmail("test-naver@example.com").isPresent() &&
			memberRepository.findByEmail("test-google@example.com").isPresent();
		boolean hasCafes = cafeRepository.count() > 0;

		return hasAdmin && hasTestUsers && hasCafes;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void initAdminAccount() {
		if (memberRepository.findByEmailAndMemberType("admin@beanspot.com", Member.MemberType.ADMIN).isPresent()) {
			log.info("관리자 계정이 이미 존재합니다.");
			resetAdminPassword();
			return;
		}

		Member admin = Member.builder()
			.email("admin@beanspot.com")
			.name("관리자")
			.password(passwordEncoder.encode("adminPassword"))
			.oAuthId("admin")
			.memberType(Member.MemberType.ADMIN)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		memberRepository.save(admin);
		log.info("기본 관리자 계정이 생성되었습니다.");
	}

	// 기존 관리자 비밀번호 초기화 메서드
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void resetAdminPassword() {
		memberRepository.findByEmailAndMemberType("admin@beanspot.com", Member.MemberType.ADMIN)
			.ifPresent(admin -> {
				admin.setPassword(passwordEncoder.encode("adminPassword"));
				memberRepository.save(admin);
				log.info("관리자 비밀번호가 초기화되었습니다.");
			});
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void initTestUsers() {
		// 기존 사용자 유지
		createUserIfNotExists("카카오테스트사용자", "test-kakao@example.com", "oauth-kakao-id-12345",
			Member.MemberType.USER, Member.SnsType.KAKAO);

		createUserIfNotExists("네이버테스트사용자", "test-naver@example.com", "oauth-naver-id-12345",
			Member.MemberType.USER, Member.SnsType.NAVER);

		createUserIfNotExists("구글테스트사용자", "test-google@example.com", "oauth-google-id-12345",
			Member.MemberType.USER, Member.SnsType.GOOGLE);

		// 추가 테스트 사용자들
		createUserIfNotExists("소유주사용자1", "owner1@example.com", "oauth-owner-id-1",
			Member.MemberType.OWNER, Member.SnsType.KAKAO);

		createUserIfNotExists("소유주사용자2", "owner2@example.com", "oauth-owner-id-2",
			Member.MemberType.OWNER, Member.SnsType.NAVER);

		createUserIfNotExists("일반사용자1", "user1@example.com", "oauth-user-id-1",
			Member.MemberType.USER, Member.SnsType.GOOGLE);

		createUserIfNotExists("예약사용자1", "reservation1@example.com", "oauth-reservation-id-1",
			Member.MemberType.USER, Member.SnsType.KAKAO);

		createUserIfNotExists("예약사용자2", "reservation2@example.com", "oauth-reservation-id-2",
			Member.MemberType.USER, Member.SnsType.NAVER);

		createUserIfNotExists("리뷰예약사용자", "review-reservation@example.com", "oauth-review-reservation-id",
			Member.MemberType.USER, Member.SnsType.GOOGLE);

		createUserIfNotExists("신청사용자", "applicant@example.com", "oauth-applicant-id",
			Member.MemberType.USER, Member.SnsType.KAKAO);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected Member createUserIfNotExists(String name, String email, String oAuthId,
		Member.MemberType memberType, Member.SnsType snsType) {
		Optional<Member> existingMember = memberRepository.findByEmail(email);
		if (existingMember.isPresent()) {
			log.info("사용자가 이미 존재합니다: {}", email);
			return existingMember.get();
		}

		Member member = Member.builder()
			.name(name)
			.email(email)
			.oAuthId(oAuthId)
			.memberType(memberType)
			.snsType(snsType)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		Member savedMember = memberRepository.save(member);
		log.info("새 사용자가 생성되었습니다. ID: {}, Email: {}", savedMember.getId(), email);
		return savedMember;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void createTestCafes() {
		// 기존 카페가 있는지 확인
		if (cafeRepository.count() > 0) {
			log.info("테스트 카페가 이미 존재합니다.");
			return;
		}

		try {
			// 구글 사용자(첫 번째 소유자)
			Member owner1 = memberRepository.findByEmail("test-google@example.com")
				.orElseThrow(() -> new RuntimeException("첫 번째 소유자를 찾을 수 없습니다."));

			// 추가 소유자들
			Member owner2 = memberRepository.findByEmail("owner1@example.com")
				.orElseThrow(() -> new RuntimeException("두 번째 소유자를 찾을 수 없습니다."));

			Member owner3 = memberRepository.findByEmail("owner2@example.com")
				.orElseThrow(() -> new RuntimeException("세 번째 소유자를 찾을 수 없습니다."));

			// 첫 번째 소유자의 신청서 생성
			Application application1 = createOrGetApplication(owner1, "구글 테스트 카페", "서울시 강남구 테헤란로 123", 20);

			// 두 번째 소유자의 신청서 생성
			Application application2 = createOrGetApplication(owner2, "코딩 카페", "서울시 서초구 반포대로 111", 30);

			// 세 번째 소유자의 신청서 생성
			Application application3 = createOrGetApplication(owner3, "북스타 카페", "서울시 강남구 도산대로 222", 25);

			// 첫 번째 카페 생성
			Cafe cafe1 = createTestCafe("비앤브레드 카페", "서울시 강남구 역삼동 123-45", "02-1234-5678",
				"조용한 분위기의 베이커리 카페입니다. 직접 구운 빵과 커피를 제공합니다.", 37.5012, 127.0396, 30, owner1, application1);

			// 두 번째 카페 생성
			Cafe cafe2 = createTestCafe("코딩 카페", "서울시 서초구 반포대로 111", "02-2345-6789",
				"프로그래머를 위한 카페입니다. 넓은 테이블과 전원 콘센트가 있습니다.", 37.4832, 127.0245, 40, owner2, application2);

			// 세 번째 카페 생성
			Cafe cafe3 = createTestCafe("북스타 카페", "서울시 강남구 도산대로 222", "02-3456-7890",
				"책을 읽기 좋은 분위기의 북카페입니다. 다양한 책과 음료를 제공합니다.", 37.5225, 127.0385, 25, owner3, application3);

			log.info("테스트 카페 3개가 생성되었습니다.");
		} catch (Exception e) {
			log.error("테스트 카페 생성 중 오류 발생: {}", e.getMessage());
			throw e; // Propagate for transaction rollback
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected Application createOrGetApplication(Member owner, String name, String address, int capacity) {
		// 해당 멤버의 기존 Application이 있는지 확인
		Optional<Application> existingApplication = applicationRepository.findByMemberIdAndStatus(owner.getId(), Status.APPROVED);
		if (existingApplication.isPresent()) {
			log.info("소유자에게 이미 승인된 신청서가 존재합니다. ID: {}", existingApplication.get().getId());
			return existingApplication.get();
		}

		// 신규 신청서 생성
		Application application = Application.builder()
			.name(name)
			.address(address)
			.phone("02-" + (1000 + owner.getId()) + "-" + (5000 + owner.getId()))
			.status(Status.APPROVED)  // 이미 승인된 상태로 생성
			.capacity(capacity)  // 추가된 필드
			.member(owner)
			.createdAt(LocalDateTime.now())
			.build();

		return applicationRepository.save(application);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected Cafe createTestCafe(String name, String address, String phone, String description,
		double latitude, double longitude, int capacity, Member owner, Application application) {

		// 이미 존재하는 카페인지 확인
		boolean cafeExists = cafeRepository.existsByNameAndAddress(name, address);
		if (cafeExists) {
			log.info("카페가 이미 존재합니다: {}, {}", name, address);
			// 기존 카페를 반환하기 위해 조회
			List<Cafe> existingCafes = cafeRepository.searchByKeywordAndDisabledFalse(name);
			for (Cafe cafe : existingCafes) {
				if (cafe.getName().equals(name) && cafe.getAddress().equals(address)) {
					return cafe;
				}
			}
		}

		// 카페 생성
		Cafe cafe = Cafe.builder()
			.owner(owner)
			.application(application)
			.name(name)
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.phone(phone)
			.description(description)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.image("default-cafe-image.jpg")
			.capacity(capacity)
			.disabled(false)
			.build();

		// 양방향 관계 설정을 위해 owner의 ownedCafes에 카페 추가
		if (owner.getOwnedCafes() == null) {
			owner.setOwnedCafes(new ArrayList<>());
		}
		owner.getOwnedCafes().add(cafe);
		memberRepository.save(owner); // 변경된 owner 저장

		return cafeRepository.save(cafe);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void setupTestUserRoles() {
		try {
			// 구글 사용자를 카페 소유자로 변경 (기존 로직)
			Member googleUser = memberRepository.findByEmail("test-google@example.com")
				.orElseThrow(() -> new RuntimeException("구글 테스트 사용자를 찾을 수 없습니다."));

			googleUser.setMemberType(Member.MemberType.OWNER);
			memberRepository.save(googleUser);
			log.info("구글 테스트 사용자가 카페 소유자로 변경되었습니다.");

			// 네이버 사용자에게 리뷰와 즐겨찾기 추가 (기존 로직)
			Member naverUser = memberRepository.findByEmail("test-naver@example.com")
				.orElseThrow(() -> new RuntimeException("네이버 테스트 사용자를 찾을 수 없습니다."));

			// 모든 카페 가져오기
			List<Cafe> cafes = cafeRepository.findAll();
			if (!cafes.isEmpty()) {
				// 첫 번째 카페에 리뷰 추가
				Cafe firstCafe = cafes.get(0);
				addReviewIfNotExists(naverUser, firstCafe, 5, "훌륭한 커피와 분위기가 좋은 카페입니다. 직원들도 친절합니다.");
				addFavoriteIfNotExists(naverUser, firstCafe);
			}
		} catch (Exception e) {
			log.error("테스트 사용자 역할 설정 중 오류 발생: {}", e.getMessage());
			throw e; // Propagate for transaction rollback
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void addReviewIfNotExists(Member member, Cafe cafe, int rating, String comment) {
		// 기존 리뷰가 있는지 확인
		boolean hasExistingReview = reviewRepository.existsByCafeAndMember(cafe, member);

		if (!hasExistingReview) {
			Review review = Review.builder()
				.member(member)
				.cafe(cafe)
				.rating(rating)
				.comment(comment)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

			reviewRepository.save(review);
			log.info("사용자(ID: {})에게 카페(ID: {})에 대한 리뷰가 추가되었습니다.", member.getId(), cafe.getCafeId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void addFavoriteIfNotExists(Member member, Cafe cafe) {
		try {
			// 이미 즐겨찾기에 있는지 확인
			boolean favoriteExists = favoriteRepository.findByMemberAndCafe(member, cafe).isPresent();

			if (!favoriteExists) {
				Favorite favorite = new Favorite(member, cafe);
				favoriteRepository.save(favorite);
				log.info("사용자(ID: {})에게 카페(ID: {})에 대한 즐겨찾기가 추가되었습니다.", member.getId(), cafe.getCafeId());
			}
		} catch (Exception e) {
			log.error("즐겨찾기 추가 중 오류 발생: {}", e.getMessage());
			throw e; // Propagate for transaction rollback
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void createAdditionalTestData() {
		try {
			List<Cafe> cafes = cafeRepository.findAll();
			if (cafes.isEmpty()) {
				log.warn("추가 테스트 데이터 생성 실패: 카페가 존재하지 않습니다.");
				return;
			}

			// // 신청 대기 중인 사용자 설정
			// Member applicantUser = memberRepository.findByEmail("applicant@example.com")
			// 	.orElseThrow(() -> new RuntimeException("신청 사용자를 찾을 수 없습니다."));
			//
			// // 해당 사용자의 기존 Application이 있는지 확인
			// Optional<Application> existingApplication = applicationRepository.findByMemberId(applicantUser.getId());
			//
			// if (existingApplication.isPresent()) {
			// 	log.info("신청 사용자에게 이미 신청서가 존재합니다. ID: {}", existingApplication.get().getId());
			// 	log.info("기존 신청서의 상태: {}", existingApplication.get().getStatus());
			// 	// 이미 신청서가 있으므로 새로 생성하지 않음
			// } else {
			// 	// 신규 신청서 생성
			// 	Application pendingApplication = Application.builder()
			// 		.name("신청 대기 중인 카페")
			// 		.address("서울시 마포구 홍대입구역 123")
			// 		.phone("02-9876-5432")
			// 		.status(Status.PENDING)
			// 		.capacity(15)  // 추가된 필드
			// 		.member(applicantUser)
			// 		.createdAt(LocalDateTime.now())
			// 		.build();
			//
			// 	applicationRepository.save(pendingApplication);
			// 	log.info("신청 대기 중인 Application이 생성되었습니다. ID: {}", pendingApplication.getId());
			// }

			// 예약 데이터 생성
			createReservationTestData(cafes);

		} catch (Exception e) {
			log.error("추가 테스트 데이터 생성 중 오류 발생: {}", e.getMessage());
			throw e; // Propagate for transaction rollback
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected void createReservationTestData(List<Cafe> cafes) {
		// 예약 데이터 생성
		Member reservationUser1 = memberRepository.findByEmail("reservation1@example.com")
			.orElseThrow(() -> new RuntimeException("예약 사용자 1을 찾을 수 없습니다."));

		Member reservationUser2 = memberRepository.findByEmail("reservation2@example.com")
			.orElseThrow(() -> new RuntimeException("예약 사용자 2를 찾을 수 없습니다."));

		Member reviewReservationUser = memberRepository.findByEmail("review-reservation@example.com")
			.orElseThrow(() -> new RuntimeException("리뷰+예약 사용자를 찾을 수 없습니다."));

		// 현재 날짜 기준으로 예약 생성
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		LocalDate dayAfterTomorrow = today.plusDays(2);
		LocalDate yesterday = today.minusDays(1);

		// 첫 번째 카페에 예약 추가
		Cafe cafe1 = cafes.get(0);

		// 예약 사용자 1의 예약
		createReservation(reservationUser1, cafe1, today,
			LocalTime.of(10, 0), LocalTime.of(12, 0), 2, ReservationStatus.CONFIRMED);

		createReservation(reservationUser1, cafe1, tomorrow,
			LocalTime.of(14, 0), LocalTime.of(16, 0), 3, ReservationStatus.CONFIRMED);

		// 예약 사용자 2의 예약
		createReservation(reservationUser2, cafe1, today,
			LocalTime.of(13, 0), LocalTime.of(15, 0), 1, ReservationStatus.CONFIRMED);

		createReservation(reservationUser2, cafe1, dayAfterTomorrow,
			LocalTime.of(9, 0), LocalTime.of(11, 0), 4, ReservationStatus.CONFIRMED);

		// 두 번째 카페가 있다면
		if (cafes.size() > 1) {
			Cafe cafe2 = cafes.get(1);

			// 리뷰+예약 사용자의 예약
			createReservation(reviewReservationUser, cafe2, today,
				LocalTime.of(15, 0), LocalTime.of(17, 0), 2, ReservationStatus.CONFIRMED);

			// 리뷰 추가
			addReviewIfNotExists(reviewReservationUser, cafe2, 4, "좋은 분위기에서 일하기 좋은 카페입니다. 다음에 또 방문할 예정입니다.");

			// 즐겨찾기 추가
			addFavoriteIfNotExists(reviewReservationUser, cafe2);
		}

		// 세 번째 카페가 있다면
		if (cafes.size() > 2) {
			Cafe cafe3 = cafes.get(2);

			// 취소된 예약 추가
			createReservation(reservationUser1, cafe3, yesterday,
				LocalTime.of(10, 0), LocalTime.of(12, 0), 2, ReservationStatus.CANCELLED);

			// 노쇼 예약 추가
			createReservation(reservationUser2, cafe3, yesterday,
				LocalTime.of(14, 0), LocalTime.of(16, 0), 1, ReservationStatus.NO_SHOW);
		}

		log.info("예약 테스트 데이터가 성공적으로 생성되었습니다.");
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected Reservation createReservation(Member member, Cafe cafe, LocalDate date,
		LocalTime startTime, LocalTime endTime,
		int partySize, ReservationStatus status) {

		LocalDateTime start = LocalDateTime.of(date, startTime);
		LocalDateTime end = LocalDateTime.of(date, endTime);

		// 이미 같은 예약이 있는지 확인 - 시간 겹침 확인
		List<Reservation> existingReservations = reservationRepository.getOverlappingReservations(
			cafe.getCafeId(), start, end);

		// 같은 사용자의 같은 시간대 예약이 있는지 확인
		for (Reservation existingReservation : existingReservations) {
			if (existingReservation.getMember().getId().equals(member.getId()) &&
				existingReservation.getStartTime().equals(start) &&
				existingReservation.getEndTime().equals(end) &&
				existingReservation.getStatus() == status) {
				log.info("동일한 예약이 이미 존재합니다. ID: {}", existingReservation.getId());
				return existingReservation;
			}
		}

		Reservation reservation = Reservation.builder()
			.member(member)
			.cafe(cafe)
			.startTime(start)
			.endTime(end)
			.partySize(partySize)
			.status(status)
			.valid(status.isValid())
			.createdAt(LocalDateTime.now().minusHours(24)) // 하루 전에 예약했다고 가정
			.updatedAt(LocalDateTime.now().minusHours(24))
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);
		log.info("예약이 생성되었습니다. ID: {}, 사용자: {}, 카페: {}, 날짜: {}",
			savedReservation.getId(), member.getName(), cafe.getName(), date);

		return savedReservation;
	}

	protected void generateTestUserTokens() {
		// 기존 테스트 사용자 토큰
		generateTokenForUser("test-kakao@example.com", "카카오 테스트 사용자");
		generateTokenForUser("test-naver@example.com", "네이버 테스트 사용자");
		generateTokenForUser("test-google@example.com", "구글 테스트 사용자");

		// 추가 테스트 사용자 토큰
		generateTokenForUser("owner1@example.com", "소유주 사용자 1");
		generateTokenForUser("owner2@example.com", "소유주 사용자 2");
		generateTokenForUser("user1@example.com", "일반 사용자 1");
		generateTokenForUser("reservation1@example.com", "예약 사용자 1");
		generateTokenForUser("reservation2@example.com", "예약 사용자 2");
		generateTokenForUser("review-reservation@example.com", "리뷰+예약 사용자");
		generateTokenForUser("applicant@example.com", "신청 사용자");
	}

	private void generateTokenForUser(String email, String userDescription) {
		try {
			Member user = memberRepository.findByEmail(email).orElse(null);
			if (user != null) {
				String accessToken = jwtService.generateToken(user);
				log.info("{} 액세스 토큰: {}", userDescription, accessToken);
			} else {
				log.warn("토큰 생성 실패: 사용자를 찾을 수 없음 - {}", email);
			}
		} catch (Exception e) {
			log.error("토큰 생성 중 오류 발생 ({}) - {}", userDescription, e.getMessage());
		}
	}
}