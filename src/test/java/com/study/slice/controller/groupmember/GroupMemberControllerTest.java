package com.study.slice.controller.groupmember;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest         // (1)
@AutoConfigureMockMvc   // (2)
public class GroupMemberControllerTest {
    // (3)
    @Autowired
    private MockMvc mockMvc;

    // (4)
    @Test
    public void postGroupMemberTest() {
        // given (5) 테스트용 request body 생성

        // when (6) MockMvc 객체로 테스트 대상 Controller 호출

        // then (7) Controller 핸들러 메서드에서 응답으로 수신한 HTTP Status 및 response body 검증
    }
}

/**

 package com.springboot.slice.controller.member;

 import com.springboot.member.dto.MemberDto;
 import com.google.gson.Gson;
 import org.junit.jupiter.api.Test;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.http.MediaType;
 import org.springframework.test.web.servlet.MockMvc;
 import org.springframework.test.web.servlet.ResultActions;
 import org.springframework.transaction.annotation.Transactional;

 import static org.hamcrest.Matchers.is;
 import static org.hamcrest.Matchers.startsWith;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

 //    @WebMvcTest(controllers = MemberController.class)
 //    @MockBean(JpaMetamodelMappingContext.class)
 @Transactional
 @SpringBootTest
 @AutoConfigureMockMvc
 class MemberControllerTest {
 @Autowired
 private MockMvc mockMvc;

 @Autowired
 private Gson gson;

 //    @MockBean
 //    private MemberService memberService;

 //    @MockBean
 //    private MemberMapper mapper;

 @Test
 void postMemberTest() throws Exception {
 // given
 MemberDto.Post post = new MemberDto.Post("hgd@gmail.com",
 "홍길동",
 "010-1234-5678");
 String content = gson.toJson(post);


 // when
 ResultActions actions =
 mockMvc.perform(
 post("/v11/members")
 .accept(MediaType.APPLICATION_JSON)
 .contentType(MediaType.APPLICATION_JSON)
 .content(content)
 );

 // then
 actions
 .andExpect(status().isCreated())
 .andExpect(header().string("Location", is(startsWith("/v11/members/"))));
 }

 @Test
 void getMemberTest() throws Exception {
 // given: MemberController의 getMember()를 테스트하기 위해서 postMember()를 이용해 테스트 데이터를 생성 후, DB에 저장
 MemberDto.Post post = new MemberDto.Post("hgd@gmail.com","홍길동","010-1111-1111");
 String postContent = gson.toJson(post);

 ResultActions postActions =
 mockMvc.perform(
 post("/v11/members")
 .accept(MediaType.APPLICATION_JSON)
 .contentType(MediaType.APPLICATION_JSON)
 .content(postContent)
 );

 String location = postActions.andReturn().getResponse().getHeader("Location"); // "/v11/members/1"

 // when / then
 mockMvc.perform(
 get(location)
 .accept(MediaType.APPLICATION_JSON)
 )
 .andExpect(status().isOk())
 .andExpect(jsonPath("$.data.email").value(post.getEmail()))
 .andExpect(jsonPath("$.data.name").value(post.getName()))
 .andExpect(jsonPath("$.data.phone").value(post.getPhone()));
 }
 }
 * **/
