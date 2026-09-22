# 데이터베이스 · JPA 교재 실습

## 실행

PowerShell에서 프로젝트 폴더로 이동한 뒤 실행합니다.

```powershell
cd D:\IdeaProjects\2026-Server_Study
$env:JAVA_HOME = 'D:\Java\temurin-17\jdk-17.0.20.1+1'
.\gradlew.bat test --console=plain
```

IntelliJ에서는 테스트 클래스 또는 메서드 옆의 실행 버튼을 눌러도 됩니다.
Gradle JVM은 JDK 17을 사용합니다.

## 실습 파일

- src/test/java/com/example/server_study_2026/SqlPracticeTest.java: SELECT, WHERE 조건, INSERT, DELETE, UPDATE를 H2에서 실행하는 5개 테스트
- src/test/java/com/example/server_study_2026/MemberRepositoryTest.java: 조회, 쿼리 메서드, 저장, 여러 건 저장, 삭제, 변경 감지, 1차 캐시, 엔티티 생명주기를 확인하는 10개 테스트
- src/test/resources/insert-customers.sql: SQL 실습용 손님 데이터
- src/test/resources/insert-members.sql: JPA 조회·수정·삭제 실습용 회원 데이터
- src/test/resources/application.properties: 테스트 시 운영 소스의 data.sql 자동 실행 방지

테스트 보고서: build/reports/tests/test/index.html

## 교재에서 보완한 부분

- SQL과 Java 예제의 오타를 수정했습니다.
- 한글 SQL 스크립트는 UTF-8로 읽도록 지정했습니다.
- 자동 생성 ID는 새 엔티티에 직접 지정하지 않고 save() 반환값에서 확인합니다.
- 수정·삭제·저장 검증은 flush()와 clear() 후 재조회해 DB 반영까지 확인합니다.
- detach()한 객체를 바로 remove()하지 않고 관리 중인 엔티티를 다시 조회한 뒤 삭제합니다.
- @DataJpaTest의 기본 롤백을 활용하므로 @AfterEach의 deleteAll()은 필요하지 않습니다.
- SQL 실습은 독립된 H2 테스트 DB에서 실행합니다. H2의 IDENTITY 문법을 사용합니다.
