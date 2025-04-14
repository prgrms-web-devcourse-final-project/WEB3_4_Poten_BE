package com.beanSpot.WEB3_4_Poten_BE.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository <Member, Long>{
	Optional<Member> findByEmail(String email);
	Optional<Member> findByoAuthId(String oAuthId);
	Optional<Member> findByEmailAndMemberType(String email, Member.MemberType memberType);

	List<Member> findByMemberType(Member.MemberType memberType);
}
