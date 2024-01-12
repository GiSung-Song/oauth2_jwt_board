# OAuth2.0 + JWT Board

## OAuth2.0 과 JWT를 이용한 Rest Api Board 만들기

1. User 관련 class 생성

→ dto, entity, repository, service, controller 생성

추후 : ExceptionHandler → 예외 처리해야함

2. JWT 서비스 및 필터 생성

## 따로 만들면서 공부한 점

JPA 연관관계 

→ 즉시로딩(@ManyToOne(fetch = FetchType.EAGER))

→ 지연로딩(@MantyToOne(fetch = FetchType.LAZY))

즉시로딩 : 하나의 엔티티를 조회할 때 연관된 엔티티를 모두 가져옴

지연로딩 : 연관된 엔티티를 실제로 사용하는 시점에서 JPA가 SQL을 호출하여 조회함

---

- 지연 로딩 : 연관된 엔티티를 프록시로 조회하고, 프록시를 실제 사용할 때 프록시를 초기화하면서 데이터베이스를 조회한다.
- 즉시 로딩 : 연관된 엔티티를 즉시 조회한다, 하이버네이트는 가능하면 SQL 조인을 사용해서 한 번에 조회한다.

- `@NoArgsConstructor(access = AccessLevel.PUBLIC)`
    - 기본 생성자를 이용하여, 값을 주입하는 방식을 최대한 방지하기 위해서 사용을 권장하지 않는다.
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
    - 위, 아래와 같은 프록시 객체의 생성과 객체에 대한 접근 범위 문제를 해결하기 위해서 사용한다.
- `@NoArgsConstructor(access = AccessLevel.PRIVATE)`프록시 객체 생성시 문제가 생기기 때문에 사용을 권장하지 않는다.

---

### 참조
https://velog.io/@kevin_

https://ksh-coding.tistory.com/