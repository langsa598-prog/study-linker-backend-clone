package com.study.service.studygroup.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.service.groupmember.domain.GroupMember;
import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.groupmember.repository.GroupMemberRepository;
import com.study.service.studyschedule.domain.StudySchedule;
import com.study.service.studyschedule.domain.StudyScheduleStatus;   // ⭐ 추가
import com.study.service.studyschedule.dto.StudyScheduleRequest;
import com.study.service.studyschedule.dto.StudyScheduleResponse;
import com.study.service.studyschedule.repository.StudyScheduleRepository;
import com.study.service.studygroup.domain.StudyGroup;
import com.study.service.studygroup.dto.RecommendedGroupDto;
import com.study.service.studygroup.dto.StudyGroupRequest;
import com.study.service.studygroup.repository.StudyGroupRepository;
import com.study.service.user.domain.User;
import com.study.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final StudyScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public StudyGroupService(
            StudyGroupRepository groupRepository,
            GroupMemberRepository memberRepository,
            StudyScheduleRepository scheduleRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper
    ) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // ============================
    // 스터디 그룹 전체 조회
    // ============================
    public List<StudyGroup> findAll() {
        return groupRepository.findAll();
    }

    // ============================
    // 단건 조회
    // ============================
    public StudyGroup findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));
    }

    // ============================
    // 그룹 생성 (요청자가 리더)
    // ============================
    @Transactional
    public StudyGroup createGroup(StudyGroupRequest request, Long leaderId) {

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: " + leaderId));

        StudyGroup group = new StudyGroup();
        group.setLeader(leader);
        group.setTitle(request.getTitle());
        group.setDescription(request.getDescription());
        group.setCategory(request.getCategory());
        group.setMaxMembers(request.getMaxMembers());

        // ✅ Double -> BigDecimal 변환
        if (request.getLatitude() != null) {
            group.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        } else {
            group.setLatitude(null);
        }

        if (request.getLongitude() != null) {
            group.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        } else {
            group.setLongitude(null);
        }

        StudyGroup saved = groupRepository.save(group);

        // 리더를 GroupMember에도 자동 등록
        GroupMember leaderMember = new GroupMember();
        leaderMember.setGroup(saved);
        leaderMember.setUser(leader);
        leaderMember.setRole(GroupMember.Role.LEADER);
        leaderMember.setStatus(GroupMember.Status.APPROVED);
        memberRepository.save(leaderMember);

        return saved;
    }

    // ============================
    // 그룹 삭제 (리더만 가능)
    // ============================
    @Transactional
    public void deleteById(Long groupId, Long requesterId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(requesterId)) {
            throw new SecurityException("해당 그룹의 리더만 삭제할 수 있습니다.");
        }

        groupRepository.delete(group);
    }


    // ============================
    // 멤버 목록 조회
    // ============================
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        return memberRepository.findByGroupGroupId(groupId)
                .stream()
                .map(GroupMemberResponse::fromEntity)
                .toList();
    }

    // ============================
    // 특정 멤버 조회
    // ============================
    public GroupMemberResponse getGroupMember(Long groupId, Long userId) {
        GroupMember member = memberRepository.findByGroupGroupIdAndUserUserId(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        return GroupMemberResponse.fromEntity(member);
    }

    // ============================
    // 리더 조회
    // ============================
    public GroupMemberResponse getGroupLeader(Long groupId) {
        GroupMember leader = memberRepository.findByGroupGroupIdAndRole(groupId, GroupMember.Role.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("리더가 존재하지 않습니다."));
        return GroupMemberResponse.fromEntity(leader);
    }

    // ============================
    // 가입 신청
    // ============================
    @Transactional
    public GroupMemberResponse requestJoinGroup(Long groupId, Long userId) {

        StudyGroup group = findById(groupId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 중복 신청 방지
        if (memberRepository.existsByGroupGroupIdAndUserUserId(groupId, userId)) {
            throw new IllegalArgumentException("이미 신청했거나 가입된 유저입니다.");
        }

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupMember.Role.MEMBER);
        member.setStatus(GroupMember.Status.PENDING);

        return GroupMemberResponse.fromEntity(memberRepository.save(member));
    }

    // ============================
    // 가입 승인
    // ============================
    @Transactional
    public void approveMember(Long groupId, Long targetUserId, Long leaderId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 승인할 수 있습니다.");
        }

        GroupMember member = memberRepository
                .findByGroupGroupIdAndUserUserId(groupId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));

        member.setStatus(GroupMember.Status.APPROVED);
    }

    // ============================
    // 가입 거절
    // ============================
    @Transactional
    public void rejectMember(Long groupId, Long targetUserId, Long leaderId) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 거절할 수 있습니다.");
        }

        GroupMember member = memberRepository
                .findByGroupGroupIdAndUserUserId(groupId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));

        member.setStatus(GroupMember.Status.REJECTED);
    }

    // ============================
    // 스케줄 목록 조회
    // ============================
    public List<StudyScheduleResponse> getGroupSchedules(Long groupId) {
        return scheduleRepository.findByGroupGroupId(groupId)
                .stream()
                .map(StudyScheduleResponse::fromEntity)
                .toList();
    }

    // ============================
    // 스케줄 생성 (리더만)
    // ============================
    @Transactional
    public StudyScheduleResponse createSchedule(Long groupId, Long leaderId, StudyScheduleRequest request) {

        StudyGroup group = findById(groupId);

        if (!group.getLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("리더만 일정 등록이 가능합니다.");
        }

        StudySchedule schedule = new StudySchedule();
        schedule.setGroup(group);
        schedule.setTitle(request.getTitle());
        schedule.setDescription(request.getDescription());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setLocation(request.getLocation());

        // ✅ StudyScheduleStatus 사용 (엔티티 status 필드 타입에 맞게)
        schedule.setStatus(StudyScheduleStatus.SCHEDULED);

        return StudyScheduleResponse.fromEntity(scheduleRepository.save(schedule));
    }

    // ============================
    // 추천 그룹 조회
    // ============================
    public List<RecommendedGroupDto> findRecommendedGroups(
            Double userLat,
            Double userLon,
            List<String> tags,
            Double distanceKm
    ) {

        try {
            String tagsJson = objectMapper.writeValueAsString(tags);

            List<Object[]> results = groupRepository
                    .findRecommendedGroups(userLat, userLon, tagsJson, distanceKm);

            return results.stream()
                    .map(row -> new RecommendedGroupDto(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            row[3] != null ? ((Number) row[3]).doubleValue() : null,
                            row[4] != null ? ((Number) row[4]).doubleValue() : null,
                            row[5] != null ? ((Number) row[5]).doubleValue() : null
                    ))
                    .toList();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("태그 JSON 처리 오류", e);
        }
    }
}