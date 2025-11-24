package com.study.service.user.service;

import com.study.service.user.domain.User;
import com.study.service.user.domain.Role;
import com.study.service.user.dto.LocationUpdateRequest;
import com.study.service.user.dto.UserGroupResponse;
import com.study.service.user.dto.UserRequest;
import com.study.service.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 🔹 비밀번호 암호화용

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================
    // 전체 조회
    // ============================
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ============================
    // ID로 단일 조회
    // ============================
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
    }

    // ============================
    // username으로 조회 (profile용)
    // ============================
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // ============================
    // 사용자 생성 (회원가입)
    // ============================
    @Transactional
    public User save(UserRequest request) {

        // 🔹 1. 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 🔹 2. User 생성 + 비밀번호 암호화
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ 암호화
        user.setName(request.getName());

        // ✅ role 매핑 (null 또는 빈 값이면 USER 기본)
        String roleStr = request.getRole();
        if (roleStr == null || roleStr.isBlank()) {
            user.setRole(Role.USER);   // 🔁 User.Role → Role
        } else {
            user.setRole(Role.valueOf(roleStr.toUpperCase())); // 🔁 User.Role → Role
        }


        user.setInterestTags(request.getInterestTags());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        return userRepository.save(user);
    }

    // ============================
    // 사용자 수정 (PUT)
    // ============================
    @Transactional
    public User update(Long userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("수정할 사용자를 찾을 수 없습니다. ID: " + userId));

        user.setUsername(request.getUsername());

        // 🔹 비밀번호도 요청에 있으면 암호화해서 반영
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // ✅ role 매핑 (update에서도 동일 로직)
        String roleStr = request.getRole();
        if (roleStr != null && !roleStr.isBlank()) {
            user.setRole(Role.valueOf(roleStr.toUpperCase())); // 🔁 User.Role → Role
        }
        // null/빈 값이면 기존 role 유지

        user.setInterestTags(request.getInterestTags());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        return userRepository.save(user);
    }

    // ============================
    // 비밀번호 변경
    // ============================
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("비밀번호를 변경할 사용자를 찾을 수 없습니다. ID: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword)); // ✅ 암호화

        userRepository.save(user);
    }

    // ============================
    // 위치(lat/lon) 갱신
    // ============================
    @Transactional
    public User updateLocation(Long userId, LocationUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("위치를 변경할 사용자를 찾을 수 없습니다. ID: " + userId));

        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        return userRepository.save(user);
    }

    // ============================
    // 사용자 삭제
    // ============================
    @Transactional
    public void deleteById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 사용자를 찾을 수 없습니다. ID: " + userId));

        userRepository.delete(user);
    }

    // ============================
    // 사용자가 가입한 스터디 그룹 목록 조회
    // ============================
    public List<UserGroupResponse> findGroupsByUserId(Long userId) {
        return Collections.emptyList(); // TODO: 구현 예정
    }
}