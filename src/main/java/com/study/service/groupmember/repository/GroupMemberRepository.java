package com.study.service.groupmember.repository;

import com.study.service.groupmember.domain.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // 모든 멤버 (groupId 기준)
    List<GroupMember> findByGroupGroupId(Long groupId);

    // 특정 유저
    Optional<GroupMember> findByGroupGroupIdAndUserUserId(Long groupId, Long userId);

    // 리더 조회 (Role enum은 GroupMember 안에 있는 중첩 enum)
    Optional<GroupMember> findByGroupGroupIdAndRole(Long groupId, GroupMember.Role role);

    // 이미 신청/가입 했는지 체크
    boolean existsByGroupGroupIdAndUserUserId(Long groupId, Long userId);

    // 🔽 GroupMemberService에서 사용하는 언더스코어 버전들 추가
    List<GroupMember> findByGroup_GroupId(Long groupId);

    Optional<GroupMember> findByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);
}
