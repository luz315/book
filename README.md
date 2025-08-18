## 실행 방법
- 터미널에 ./gradlew clean build 입력
- 터미널에 docker-compose up -d 입력
- http://localhost:8080/doc 실행
  (빠른 테스트를 위해 모든 코드는 공개 처리)
- 테스트 커버리지는 build-jacocoReport-test-html-index.html 참고해주세요

p.s 테스트 데이터 내용물은 src-main-resources-seed에서 확인 가능합니다

<br>

## API 명세
| 기능           | 메서드 | 경로                       | 파라미터                                                                                 | 비고                                               |
| ------------ | --- | ------------------------ | ------------------------------------------------------------------------------------ | ------------------------------------------------ |
| 도서 검색        | GET | `/api/v1/books`          | `keyword` (query, optional) · `page` (query, default=1) · `size` (query, default=20) | PostgreSQL FTS 기반 검색 (`SIMPLE`/`OR`/`NOT` 자동 판별) |
| 도서 상세 조회     | GET | `/api/v1/books/{id}`     | `id` (path, UUID)                                                                    | 단일 도서 상세                                         |
| 인기 검색어 TOP N | GET | `/api/v1/books/trending` | 없음                                                                                   | Redis ZSET 기반, 월별 상위 키워드                         |


## 핵심 구현 포인트 

### 1) Postgres Full-Text Search로 빠르고 정확한 검색

- title, author, publisher 3개 컬럼을 합쳐 to_tsvector 로 인덱스 가능 형태 구현

  - 단순 검색: plainto_tsquery(:tsQuery)
  - OR/NOT 검색: to_tsquery(:tsQuery)

**사용 이유**
- plainto_tsquery 는 사용자가 “그냥 단어”를 입력했을 때 안전하게 공백을 AND로 적용
- to_tsquery 는 A | B, A & !B 같은 논리 연산을 직접 다루기 적합

**트러블 슈팅**
- Hibernate HQL은 Postgres의 @@ 연산자를 토큰으로 인식하지 못해 파싱 에러 발생
- JPA QueryDSL → Native SQL(EntityManager) 로 전환

### 2) 전략 자동 판별 + 안전한 질의 정규화

- SearchStrategy.fromQuery(keyword) 로 전략 자동 판별
  - A|B 가 들어오면 OR_OPERATION
  - A-B 가 들어오면 NOT_OPERATION
  - 이외는 SIMPLE

**사용 이유**
- UI/클라이언트가 별도 파라미터 없이 키워드만으로 고급 검색을 쓸 수 있습니다.
- 공백/빈 토큰 제거하고 잘못된 패턴은 INVALID_SEARCH_QUERY 로 즉시 차단

### 3) Redis Sorted Set 으로 “월별” 인기 검색어 집계

- ZINCRBY trending_keywords:YYYY-MM 로 카운트 증가 (만료 40일)

**사용 이유**
- ZSET으로 상위 N 추출이 O(logN + N) 이라 요청 시 가벼움
- 실시간 누적 집계에는 인메모리 Redis ZSET이 DB보다 적합
- 월별키와 TTL로 최신성 자동 유지


### 4) 일관된 API 응답 래퍼 + 상세 예외 설계

- ApiResult<T> 를 통해 success/data/error 스키마로 통일
- GlobalExceptionHandler 로 공통 처리
- CustomException을 사용하여 오류 메시지와 상태 코드를 설정하고 에러 응답 통일

### 5) 포트/어댑터 구조로 유연한 확장성

- 저장소나 캐시 교체가 쉬워지고 도메인 의존성 방향이 안정적(DIP)
  - IN 포트: BookService, BookTrendService
  - OUT 포트: BookRepository, BookTrendCache
  - 어댑터: JpaBookRepositoryAdapter, BookTrendCacheImpl(Redis), BookQueryDslRepository(Native SQL)

### 6) 실행시간 측정 및 커스텀 페이지네이션 응답 포맷 사용

**실행시간 측정**
- JDK 기본 제공 System.nanoTime()으로 시작/종료 시점 캡처 → 모노토닉 클록 기반이라 NTP 보정·서버 시간 변경에도 안전
- 결과는 ms 단위로 변환해 응답의 searchMetadata.executionTime에 포함 → 클라이언트 텔레메트리/회귀 모니터링이 쉬움
**페이지 변환**
- Pagination<T>.map으로 엔티티 → DTO 무손실 변환
- 프레임워크 종속 타입(Page<T>) 대신 가벼운 순수 클래스로 유지 → 레이어 간 결합도 감소
- 전송 계층은 PageResponse<T>로 표준 스키마 고정

<br>

## 검색 패턴 종류

시드 데이터: /resources/seed/books_seed.csv (OpenCSV) — 최초 기동 시 한 번만 적재 (idempotent)
| 입력 패턴          | 의미(논리)      | 내부 tsquery 규칙(일반형)                                   | 사용 함수              |         |              |
| -------------- |-------------| ---------------------------------------------------- | ------------------ | ------- | ------------ |
| 단어(단일 토큰)      | 해당 단어 포함    | `plainto_tsquery('<term>')`                          | `plainto_tsquery`  |         |              |
| 공백 AND(두 단어 이상) | 모든 단어 포함(AND) | `plainto_tsquery('<A> <B> ...')` ≈ `<A> & <B> & ...` | `plainto_tsquery`  |         |              |
| OR (`\|`)            | A 또는 B                                               | \`to\_tsquery('<A> | <B>')\` | `to_tsquery` |
| NOT (`-`)      | A 이면서 B 제외  | `to_tsquery('<A> & !<B>')`                           | `to_tsquery`       |         |              |

### 예시 응답

- Design → Design Patterns, Domain-Driven Design … (4건)
- Head → Head First Java, Head First Python … (4건)
- Head Design → Head First Design Patterns (1건)
- Head|Design → Head 계열 4권 + Design 계열 3권 = (중복 제거 후) 7건
- Head-Design → Head는 포함하지만 Design은 없는 3권 (Head First Java, Head First HTML and CSS, Head First Python)