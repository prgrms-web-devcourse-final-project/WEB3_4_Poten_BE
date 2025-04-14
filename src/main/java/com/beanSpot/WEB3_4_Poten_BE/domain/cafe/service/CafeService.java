package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.res.AdminCafeListRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeDetailRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeInfoRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception.CafeNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Service;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeService {

	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;
	private final ApplicationRepository applicationRepository;
	private final S3Service s3Service;

	//이 부분 관리자가 신청서 승인시 신청서 바탕으로 카페를 생성해주고 owner는 수정만 하는지 아니면 관리자는 신청만 수락하는지 알려주세요
	@Transactional
	public CafeInfoRes createCafe(CafeCreateReq request, Long ownerId) {
		Member owner = memberRepository.findById(ownerId)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다. ID: " + ownerId));

		// MemberType이 OWNER가 아니면 OWNER로 변경
		if (owner.getMemberType() != Member.MemberType.OWNER) {
			owner.changeRoleToOwner();
		}

		//승인된 신청이 있어야만 카페 생성 가능.
		Application approvedApplication = applicationRepository.findByMemberIdAndStatus(ownerId, Status.APPROVED)
			.orElseThrow(() -> new IllegalStateException("승인된 신청이 없습니다."));

		Cafe cafe = Cafe.builder()
			.owner(owner)
			.application(approvedApplication)
			.name(request.name())
			.address(request.address())
			.latitude(0.0) //추후수정
			.longitude(0.0) //추후 수정
			.phone(request.phone())
			.description(request.description())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.imageFilename(request.imageFilename())
			.capacity(0) //추후 수정
			.disabled(false)
			.build();

		cafeRepository.save(cafe);

		String imageUrl = s3Service.getFileUrl(cafe.getImageFilename());
		return CafeInfoRes.fromEntity(cafe, imageUrl);
	}

	@Transactional
	public Page<CafeInfoRes> getCafeList(Pageable pageable) {
		return cafeRepository.findAllByDisabledFalse(pageable)
			.map(cafe -> CafeInfoRes.fromEntity(cafe, s3Service.getFileUrl(cafe.getImageFilename())));
	}

	@Transactional
	public CafeDetailRes getCafeDetail(Long cafeId) {
		Cafe cafe = cafeRepository.findBycafeIdAndDisabledFalse(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		String imageUrl = s3Service.getFileUrl(cafe.getImageFilename());
		return CafeDetailRes.fromEntity(cafe, imageUrl);
	}

	@Transactional
	public CafeInfoRes updateCafe(Long cafeId, Long userId, CafeUpdateReq request) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		if (!cafe.getOwner().getId().equals(userId)) {
			throw new ServiceException(403, "본인이 등록한 카페만 수정할 수 있습니다.");
		}

		cafe.update(request);
		String imageUrl = s3Service.getFileUrl(cafe.getImageFilename());
		return CafeInfoRes.fromEntity(cafe, imageUrl);
	}

	@Transactional
	public List<CafeInfoRes> searchCafe(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new IllegalStateException("검색 키워드는 비워둘 수 없습니다.");
		}

		List<Cafe> cafes = cafeRepository.searchByKeywordAndDisabledFalse(keyword);

		return cafes.stream()
			.map(cafe -> CafeInfoRes.fromEntity(cafe, s3Service.getFileUrl(cafe.getImageFilename())))
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteCafe(Long cafeId, Long userId) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		if (!cafe.getOwner().getId().equals(userId)) {
			throw new ServiceException(403, "본인이 등록한 카페만 삭제할 수 있습니다.");
		}

		applicationRepository.delete(cafe.getApplication());
		cafe.disable();
	}

	@Transactional(readOnly = true)
	public Page<AdminCafeListRes> getAdminCafeList(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		Page<Cafe> cafePage = cafeRepository.findAllByDisabledFalseWithOwner(pageable);
		return cafePage.map(AdminCafeListRes::fromEntity);
	}

	@Transactional(readOnly = true)
	public Page<CafeInfoRes> getCafesByOwner(Long ownerId, Pageable pageable) {
		Member owner = memberRepository.findById(ownerId)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다. ID: " + ownerId));

		Page<Cafe> cafes = cafeRepository.findByOwnerAndDisabledFalse(owner, pageable);

		return cafes.map(cafe -> CafeInfoRes.fromEntity(cafe, s3Service.getFileUrl(cafe.getImageFilename())));
	}
}