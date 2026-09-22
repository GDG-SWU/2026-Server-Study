package com.example.server_study_2026;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;

    public void test() {
        memberRepository.save(new Member(1L, "A"));


        Optional<Member> optional = memberRepository.findById(1L);
        Member member = optional.orElseThrow();


        List<Member> members = memberRepository.findAll();

        memberRepository.deleteById(1L);
    }

}
