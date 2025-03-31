package com.beanSpot.WEB3_4_Poten_BE.domain.member.repository;

import java.nio.channels.FileChannel;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository <Member, Long>{
	Optional<Member> findByEmail(String email);

	Optional<Member> findByOAuthId(String oAuthId);
}