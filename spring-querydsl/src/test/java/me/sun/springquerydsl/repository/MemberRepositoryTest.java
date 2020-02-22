package me.sun.springquerydsl.repository;

import me.sun.springquerydsl.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Stranger on 2020/02/22
 */
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;


    @Test
    void basicTest() throws Exception{
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        //when
        List<Member> findMembers = memberRepository.findByUsername(member.getUsername());

        //then
        assertThat(findMembers.size()).isEqualTo(1);
        assertThat(findMembers).extracting("username").containsOnly("member1");
    }
}