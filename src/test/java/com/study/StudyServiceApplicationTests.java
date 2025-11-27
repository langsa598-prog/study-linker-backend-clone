package com.study;


import org.junit.jupiter.api.Test; //Junit5를 사용함

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StudyServiceApplicationTests {

    @Test
    public void contextLoads() {
        // 테스트하고자 하는 대상에 대한 테스트 로직 작성
    }


//        F.I.R.S.T 원칙 : Fast(빠르게), Independent(독립적으로), Repeatable(반복가능하도록), Self-validating(셀프검증되도록). Timely(시기적절하게),
//        given : 테스트를 위한 준비과정을 명시 , 테스트에 필요한 전제조건들이 포함됨, 테스트대상에 전달되는 입력값(테스트데이터) 역시 Given에 포함됨
//        when: 테스트를 할 동작(대상)을 지정함 , 단위테스트에서는 일반적으로 메서드호출을 통해 테스트를 진행하므로 한두줄정도로 끝남

}



