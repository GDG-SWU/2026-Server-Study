# 7주차 개인 저장소 CI 실습 기록

- 저장소: [sua710/server-cicd](https://github.com/sua710/server-cicd)
- 실습 PR: [#1 CI 성공·실패·복구](https://github.com/sua710/server-cicd/pull/1)
- 개인 CI 실습은 personal 원격에서 수행했습니다. 동아리 제출 대상은 origin의 `week-07/sua`이며 main에는 병합하지 않습니다.

| 단계 | 실제 실행 기록 | 결과 |
| --- | --- | --- |
| 첫 main push | [Actions 실행](https://github.com/sua710/server-cicd/actions/runs/45350007233) | build 성공, deploy 건너뜀 |
| 최초 PR | [성공 실행](https://github.com/sua710/server-cicd/actions/runs/45350200282) | build 성공, deploy 건너뜀 |
| 의도적인 실패 | [실패 실행](https://github.com/sua710/server-cicd/actions/runs/45350456323) | 전체 31개 중 health 테스트 1개 실패 |
| 복구 | [복구 성공 실행](https://github.com/sua710/server-cicd/actions/runs/45350769074) | 기대값 OK v2로 복구, 31개 통과·실패 0개 |

실패 실습은 API 동작을 바꾸지 않고 JwtSecurityTest의 health 기대값만
`OK v2` → `INTENTIONAL_CI_FAILURE`로 변경했습니다.
실패 로그에서 `healthIsPublicAndUsesCommonResponse()`의 AssertionError를 확인했습니다.
복구 커밋에서는 기대값을 `OK v2`로 되돌렸습니다.

API에서 받은 실행 상태·커밋·작업별 결과는 `docs/evidence/week-07/`의 JSON에,
실제 오류 로그 발췌는 `pr-failure.log`에 보관했습니다.
이 기록은 GitHub 화면 캡처를 대신하지 않습니다.
제출용으로 위 성공·실패·복구 실행 페이지를 각각 열어 캡처하면 됩니다.

## 아직 필요한 AWS 작업

AWS 인증과 배포 환경 정보가 준비되지 않아 ENABLE_DEPLOY를 켜지 않았습니다.
PR도 병합하지 않고 검토할 수 있도록 열어 둡니다.
개인 저장소 Secrets/Variables 설정, 기존 배포의 Postman 캡처,
main 병합 후 배포 결과 확인 절차는 [과제 안내](week-07-guide.md)를 따릅니다.
배포 성공, Beanstalk 버전, Postman 응답 전후 캡처는 아직 완료되지 않았습니다.
