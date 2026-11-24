# 7주차 CI/CD 과제 — 준비한 내용과 남은 작업

## 로컬에서 준비한 내용

- 작업 브랜치: `week-07/sua`.
- Java 17 빌드·테스트 → 실행 JAR와 Procfile을 ZIP으로 패키징 → artifact 전달 → AWS 배포.
- `GET /health`: 기존 `data: "ok"`에서 `data: "OK v2"`로 변경. 기존 공개 API 회귀 테스트도 변경.
- `scripts/ci-practice.ps1`: 기존 health 테스트의 기대값만 바꿔 의도적인 실패와 복구를 재현.
- `docs/postman/week-07-cicd.postman_collection.json`: 변경 전·후 응답 확인 요청.
- 기존 `compose.yaml` 변경과 이전 주차 파일은 이번 작업에 포함하지 않음.

워크플로우는 PR과 main push에서 빌드합니다. 배포는 개인 저장소 main push이고
`ENABLE_DEPLOY`가 정확히 `true`일 때만 실행합니다. 동아리 저장소
`GDG-SWU/2026-Server-Study`에서는 배포하지 않습니다.
처음 개인 main을 만들 때는 ENABLE_DEPLOY를 설정하지 않아 CI만 실행되도록 하세요.

## 1. 개인 저장소 연결 완료

새 비공개 저장소 [sua710/server-cicd](https://github.com/sua710/server-cicd)를 생성하고
`personal` 원격으로 연결했습니다. 프로필 저장소 sua710/sua710은 사용하지 않았습니다.
`origin`은 기존 동아리 저장소 그대로입니다. 원격 push는 아직 실행하지 않았습니다.

```powershell
git remote -v
```

| 원격 이름 | URL |
| --- | --- |
| origin | https://github.com/GDG-SWU/2026-Server-Study.git |
| personal | https://github.com/sua710/server-cicd.git |

같은 이름의 원격을 다시 추가할 필요는 없습니다.

## 2. 로컬 검증 및 첫 CI 실행

IntelliJ의 Project SDK와 Gradle JVM은 Java 17을 사용합니다.
Terminal은 프로젝트 루트에서 실행합니다.

로컬에서 Java 17 `clean test bootJar`와 복구 후 전체 테스트가 성공했습니다.
테스트 31개, 실패 0개입니다. health 기대값을 일부러 바꿔 해당 테스트가 실패하는 것도
확인했고 정상 기대값 OK v2로 복구했습니다. 배포 ZIP의 항목과 손상 여부,
워크플로우 YAML 및 Bash 문법, Postman JSON도 확인했습니다.
이는 로컬 검증 결과이며 GitHub Actions 또는 AWS 배포 성공을 의미하지는 않습니다.

다시 빌드할 때:

```powershell
.\gradlew.bat clean test bootJar
python scripts/package-deploy.py
```

이번 과제 파일만 로컬 커밋으로 정리하고 기존 사용자 변경은 제외합니다.

사진의 동아리 제출은 `git push origin week-07/sua`까지이며,
동아리 저장소 main 병합이나 PR은 요구하지 않습니다.
프로젝트 AGENTS.md에 따라 원격 push는 사용자의 명시적인 요청을 받은 뒤 실행합니다.

개인 저장소 첫 main 생성:

```powershell
git push personal week-07/sua:main
```

개인 저장소 Actions에서 build 성공을 확인합니다. deploy는 건너뜁니다.
이 시점에 로컬 코드는 OK v2이지만, 기존 AWS 서버는 아직 변경하지 않았습니다.
기존 배포 주소 /health의 `data: "ok"` 응답을 Postman에서 먼저 캡처하세요.
사진의 원래 값은 OK지만 이 프로젝트의 실제 기존 값은 소문자 ok입니다.

## 3. 개인 PR에서 성공 → 실패 → 복구 캡처

```powershell
git switch -c codex/ci-test
```

이 문서 끝의 실습 메모 한 줄을 수정하고 저장한 뒤:

```powershell
git add docs/week-07-guide.md
git commit -m "docs: 개인 PR CI 실행 확인"
git push -u personal codex/ci-test
```

개인 GitHub에서 base main ← compare codex/ci-test PR을 생성합니다.
build 성공 화면을 캡처하고 아직 병합하지 않습니다.

의도적으로 health 응답 테스트를 실패시킵니다. 실제 API 코드는 바꾸지 않습니다.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/ci-practice.ps1 -Mode Fail
git add src/test/java/com/example/server_study_2026/JwtSecurityTest.java
git commit -m "test: 의도적인 CI 실패 확인"
git push personal codex/ci-test
```

같은 PR의 빨간 build와 실패한 테스트 로그를 캡처합니다. 복구:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/ci-practice.ps1 -Mode Restore
git add src/test/java/com/example/server_study_2026/JwtSecurityTest.java
git commit -m "test: CI 실패 복구"
git push personal codex/ci-test
```

같은 PR에서 다시 초록색 build를 캡처합니다.
AWS 준비 전에는 ENABLE_DEPLOY를 켜지 마세요.

## 4. AWS 및 GitHub 설정

기존 Beanstalk 환경이 정상 실행되어 있어야 합니다.
플랫폼은 Java SE / Corretto 17, 앱 포트는 5000입니다.
실행 명령은 ZIP 최상위 Procfile에 포함되어 있습니다.

사진의 IAM 사용자 방식:
IAM → 사용자 생성 → github-actions-deployer → 콘솔 로그인 사용 안 함 →
직접 정책 연결 → AdministratorAccess-AWSElasticBeanstalk → 생성 →
보안 자격 증명 → 액세스 키 만들기 → AWS 외부 애플리케이션 용도.
이 정책은 실습용으로 범위가 넓습니다. 이름이 비슷하다고 임의의 다른 정책을 고르지 마세요.
키는 채팅이나 저장소 파일에 붙여 넣지 말고 개인 저장소 Secrets에 직접 등록합니다.

개인 저장소 Settings → Secrets and variables → Actions:

| 구분 | 이름 | 값 |
| --- | --- | --- |
| Secret | AWS_ACCESS_KEY_ID | 배포 전용 사용자의 키 ID |
| Secret | AWS_SECRET_ACCESS_KEY | 같은 사용자의 비밀 키 |
| Variable | AWS_REGION | 실제 리전, 예: ap-northeast-2 |
| Variable | EB_APPLICATION_NAME | 기존 Beanstalk 애플리케이션 이름 |
| Variable | EB_ENVIRONMENT_NAME | 기존 Beanstalk 환경 이름 |
| Variable | EB_S3_BUCKET | 같은 리전의 기존 Beanstalk 소스 번들 버킷 |
| Variable | EB_URL | 도메인만 입력, http:// 및 끝의 / 제외 |
| Variable | EB_SCHEME | http 또는 https, 생략 시 http |
| Variable | ENABLE_DEPLOY | 준비가 끝났을 때만 true |

사용하는 S3 버킷에 배포용 사용자가 업로드할 수 있어야 하고 Beanstalk도 파일을 읽을 수 있어야 합니다.
Beanstalk 환경에는 DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET이 필요합니다.
DB 네트워크·보안 그룹과 Java 17 플랫폼도 확인하세요. CI는 이 운영 비밀값을 사용하지 않습니다.

AWS 계정이 IAM 사용자나 액세스 키 생성을 제한한다면 사진 안내대로 CI를 완료하고
CD 실습 대체 여부를 담당자와 상의합니다.

## 5. main 병합 후 자동 배포 및 캡처

기존 서버의 변경 전 Postman 캡처를 저장한 뒤 ENABLE_DEPLOY=true를 설정합니다.
복구가 완료된 개인 PR을 main에 병합합니다.
main push에서 build → deploy가 실행됩니다. PR 실행의 deploy는 항상 건너뜁니다.

1. Actions에서 deploy와 마지막 Health check 성공을 캡처합니다.
2. Beanstalk 애플리케이션 버전에서 gha-번호-해시-재실행번호를 캡처합니다.
3. 환경의 현재 배포 버전도 해당 라벨인지 확인합니다.
4. Postman에서 변경 전과 같은 서비스 주소 /health를 호출합니다.
5. data가 OK v2로 바뀐 응답을 캡처합니다.

배포 ZIP은 build/deploy.zip이며 최상위에 app.jar와 Procfile이 들어갑니다.
CI에서 생성한 ZIP을 그대로 배포하며 배포 작업에서 다시 빌드하지 않습니다.
Health check는 HTTP 성공뿐 아니라 JSON의 success=true, data=OK v2까지 확인합니다.
/health는 애플리케이션 응답 확인이며 DB의 모든 기능까지 검사하는 API는 아닙니다.

## 제출 체크리스트

- [ ] 개인 PR build 성공
- [ ] 의도적으로 실패한 build와 오류 로그
- [ ] 수정 후 같은 PR에서 build 성공
- [ ] main 병합 후 deploy 성공
- [ ] Beanstalk gha-... 버전 라벨
- [ ] 배포 전 Postman data: ok
- [ ] 배포 후 같은 주소의 Postman data: OK v2

최소 7장으로 준비합니다. 키·도메인은 가리고 경로 /health, 상태 코드, 응답은 보이게 합니다.
캡처를 만들기 위해 성공 결과를 조작하지 말고 실제 GitHub/AWS 실행 화면을 사용합니다.
최종 제출 위치와 형식은 수업 공지를 따릅니다.

## 오류 확인

- build 실패: Actions의 Build and test 로그와 test-reports artifact 확인.
- deploy 건너뜀: PR인지, main push인지, ENABLE_DEPLOY가 정확히 true인지 확인.
- Missing deployment setting: 오류에 표시된 이름을 Secrets 또는 Variables에 등록.
- AccessDenied: 실패한 AWS 명령에 대한 IAM 및 S3 권한 확인.
- 배포 시작 후 실패: Beanstalk Events 및 애플리케이션 로그, 운영 환경변수 확인.
- Health check 실패: 포트 5000, DB 연결로 인한 시작 실패, EB_URL/EB_SCHEME, 실제 배포 버전 확인.

공식 참고:
- https://github.com/actions/setup-java
- https://github.com/actions/upload-artifact
- https://github.com/actions/download-artifact
- https://github.com/aws-actions/configure-aws-credentials
- https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-se-procfile.html

실습 메모: 개인 PR을 만들 때 이 줄에 본인의 확인 내용을 추가하세요.
