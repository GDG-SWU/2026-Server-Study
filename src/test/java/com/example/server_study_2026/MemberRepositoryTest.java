package com.example.server_study_2026;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Sql("/insert-members.sql")
    void getAllMembers() {
        assertThat(memberRepository.findAll())
                .extracting(Member::getName)
                .containsExactlyInAnyOrder("A", "B", "C");
    }

    @Test
    @Sql("/insert-members.sql")
    void getMemberById() {
        assertThat(memberRepository.findById(2L).orElseThrow().getName())
                .isEqualTo("B");
    }

    @Test
    @Sql("/insert-members.sql")
    void getMemberByName() {
        assertThat(memberRepository.findByName("C").orElseThrow().getId())
                .isEqualTo(3L);
        assertThat(memberRepository.findByName("missing")).isEmpty();
    }

    @Test
    void saveMember() {
        Member saved = memberRepository.save(new Member("A"));
        Long id = saved.getId();

        em.flush();
        em.clear();

        assertThat(id).isNotNull();
        assertThat(memberRepository.findById(id).orElseThrow().getName())
                .isEqualTo("A");
    }

    @Test
    void saveMembers() {
        memberRepository.saveAll(List.of(new Member("B"), new Member("C")));

        em.flush();
        em.clear();

        assertThat(memberRepository.findAll())
                .extracting(Member::getName)
                .containsExactlyInAnyOrder("B", "C");
    }

    @Test
    @Sql("/insert-members.sql")
    void deleteMemberById() {
        memberRepository.deleteById(2L);

        em.flush();
        em.clear();

        assertThat(memberRepository.findById(2L)).isEmpty();
        assertThat(memberRepository.findAll())
                .extracting(Member::getName)
                .containsExactlyInAnyOrder("A", "C");
    }

    @Test
    @Sql("/insert-members.sql")
    void deleteAllMembers() {
        memberRepository.deleteAll();

        em.flush();
        em.clear();

        assertThat(memberRepository.findAll()).isEmpty();
    }

    @Test
    @Sql("/insert-members.sql")
    void updateMember() {
        Member member = memberRepository.findById(2L).orElseThrow();
        member.changeName("BC");

        // Verify the database value instead of the object in the first-level cache.
        em.flush();
        em.clear();

        assertThat(memberRepository.findById(2L).orElseThrow().getName())
                .isEqualTo("BC");
    }

    @Test
    @Sql("/insert-members.sql")
    void firstLevelCacheReturnsSameInstance() {
        Member first = em.find(Member.class, 1L);
        Member second = em.find(Member.class, 1L);

        assertThat(second).isSameAs(first);
    }

    @Test
    void entityLifecycle() {
        Member member = new Member("new");
        assertThat(em.contains(member)).isFalse();

        em.persist(member);
        assertThat(em.contains(member)).isTrue();
        em.flush();

        Long id = member.getId();
        em.detach(member);
        assertThat(em.contains(member)).isFalse();

        member.changeName("detached change");
        em.flush();
        Member managed = em.find(Member.class, id);
        assertThat(managed.getName()).isEqualTo("new");

        // remove() needs a managed entity, not the detached instance.
        em.remove(managed);
        em.flush();
        em.clear();

        assertThat(em.find(Member.class, id)).isNull();
    }
}
