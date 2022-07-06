package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("v1/{id}")
    String findMember(@PathVariable Long id) {
        Member member = memberRepository.findById(id).get();

        return member.getUsername();
    }

    // Not Recommended
    @GetMapping("v2/{id}")
    String findMember2(@PathVariable Member member) {
        return member.getUsername();
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("member1"));
        memberRepository.save(new Member("member2"));
        memberRepository.save(new Member("member3"));
    }

}
