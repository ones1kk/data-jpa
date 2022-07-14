package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void test1() throws Exception {
        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void test2() throws Exception {
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());

    }

    @Test
    @DisplayName("Basic CRUD")
    public void test3() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        //then
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

        Member member3 = new Member("member3");
        memberRepository.save(member3);

        Member findMember3 = memberRepository.findById(member3.getId()).get();
        findMember3.setUsername("update");

        Member updateMember = memberRepository.findById(findMember3.getId()).get();
        assertThat(updateMember.getUsername()).isEqualTo("update");
    }

    @Test
    void test4() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Named Query")
    void test5() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void test6() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Find username List")
    void test7() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void test8() {
        Team teamA = new Team("teamA");

        Member m1 = new Member("AAA", 10);
        m1.setTeam(teamA);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();

        assertThat(result.get(0).getUsername()).isEqualTo(m1.getUsername());
        assertThat(result.get(0).getTeamName()).isEqualTo(teamA.getName());

    }

    @Test
    void test9() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        assertThat(result.size()).isEqualTo(2);

    }

    @Test
    void test10() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optional = memberRepository.findOptionalByUsername("AAA");

    }

    @Test
    @DisplayName("Paging")
    void test11() {
        // given
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("AAA", 10));
        memberRepository.save(new Member("AAA", 10));

        // when
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    void test12() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 11));
        memberRepository.save(new Member("member3", 12));
        memberRepository.save(new Member("member4", 13));
        memberRepository.save(new Member("member5", 14));

        // when
        int resultCount = memberRepository.bulkAgePlus(12);
//        em.flush();
//        em.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);

        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);

    }

    @Test
    @DisplayName("Problem of N + 1, then resolve using by fetch join & @EntityGraph")
    public void test13() throws Exception {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when N + 1
        // select Member 1
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        List<Member> fetchJoinMembers = memberRepository.findMemberFetchJoin();

        for (Member member : fetchJoinMembers) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
        //then

    }

    @Test
    @DisplayName("Query hint")
    public void test14() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        Member readOnlyMember = memberRepository.findReadOnlyByUsername("member1");
        readOnlyMember.setUsername("member2");
        em.flush();

        System.out.println("member1 = " + member1);
        System.out.println("readOnlyMember = " + readOnlyMember);

        System.out.println("===============================");
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//        em.flush();
//
//        System.out.println("member1 = " + member1);
//        System.out.println("findMember = " + findMember);

    }

    @Test
    @DisplayName("Lock")
    public void test15() throws Exception {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

        //then
    }

    @Test
    @DisplayName("Specification basic")
    public void test16() throws Exception {
        // given
        Team teamA = new Team("teamAA");

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> specification = MemberSpecification.username("m1").and(MemberSpecification.teamName("teamA"));
        List<Member> result = memberRepository.findAll(specification);

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Query by example")
    public void test17() throws Exception {
        // given
        Team teamA = new Team("teamAA");

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Member member = new Member("m1");
        member.setTeam(teamA);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        //then
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    @DisplayName("Projections")
    public void test18() throws Exception {
        // given
        Team teamA = new Team("teamAA");

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        List<UsernameOnly> result1 = memberRepository.findProjectionsV1ByUsername("m1");

        //then
        for (UsernameOnly usernameOnly : result1) {
            System.out.println("username = " + usernameOnly.getUsername());
        }

        List<UsernameOnlyDto> result2 = memberRepository.findProjectionsV2ByUsername("m1");
        for (UsernameOnlyDto usernameOnlyDto : result2) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }

        List<UsernameOnlyDto> result3 = memberRepository.findProjectionsV3ByUsername("m1", UsernameOnlyDto.class);
        for (UsernameOnlyDto usernameOnlyDto : result3) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }

        List<NestedClosedProjections> result4 = memberRepository.findProjectionsV3ByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result4) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections);
        }
    }

    @Test
    @DisplayName("Native query")
    public void test19() throws Exception {
        // given
        Team teamA = new Team("teamAA");

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Member result1 = memberRepository.findByNativeQuery("m1");
        System.out.println("result1 = " + result1);

        Page<MemberProjection> result2 = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> contents = result2.getContent();

        for (MemberProjection content : contents) {
            System.out.println("content.getId() = " + content.getId());
            System.out.println("content.getUsername() = " + content.getUsername());
            System.out.println("content.getTeamName() = " + content.getTeamName());
        }

        //then
    }

}