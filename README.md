# OAuth2.0 + JWT Board

## OAuth2.0 과 JWT를 이용한 Rest Api Board 만들기

1. User 관련 class 생성

→ dto, entity, repository, service, controller 생성

2. JWT 서비스 및 필터 생성

-> 토큰 발급 및 유효성 검사 필터 등 생성

3. 로그인 Json 요청 변환

- CustomJsonUsernamePasswordAuthenticationFilter

-> CustomJsonUsernamePasswordAuthenticationFilter 필터에서 인증 대상 객체를 UsernamePasswordAuthenticationToken 으로 설정

-> UsernamePasswordAuthenticationToken 는 클라이언트 Request의 ID(username : email), PW(비밀번호)를 가지고 있음

-> 이 객체를 ProviderManager에게 전달

-> ProviderManager는 UsernamePasswordAuthenticationToken 객체를 ProviderManager의 구현체인 DaoAuthenticationProvider로 전달

-> DaoAuthenticationProvider는 UserDetailsService의 loadUserByUsername(String username)을 호출하여 UserDetails 객체를 받음.

-> 여기서는 LoginService가 UserDetailsService의 구현체임

-> 반환 받은 UserDetails 객체의 password를 PasswordEncoder에서 검증

-> 일치한다면, UsernamePasswordAuthenticationToken에 userDetails와 Authorities를 담아서 반환

-> ProviderManager에서 반환된 UserDetails 객체와 Authorities가 담긴 UsernamePasswordAuthenticationToken으로 인증 객체를 생성하여 인증 성공 처리


- LoginSuccessHandler

-> 로그인 성공 시 handler (CustomJsonUsernamePasswordAuthenticationFilter를 정상적으로 통과)

-> AccessToken과 RefreshToken을 생성하여 발급해주고 RefreshToken을 User DB에 저장

- LoginFailureHandler

-> 로그인 실패 시 400 Error

4. Post 관련 클래스 생성

- Service Test 중 getPrincipal (인증 정보 객체)가 null -> 해결 중 (2024.01.17)

## 공부한 점

---

JPA 연관관계 

→ 즉시로딩(@ManyToOne(fetch = FetchType.EAGER))

→ 지연로딩(@MantyToOne(fetch = FetchType.LAZY))

즉시로딩 : 하나의 엔티티를 조회할 때 연관된 엔티티를 모두 가져옴

지연로딩 : 연관된 엔티티를 실제로 사용하는 시점에서 JPA가 SQL을 호출하여 조회함

- 지연 로딩 : 연관된 엔티티를 프록시로 조회하고, 프록시를 실제 사용할 때 프록시를 초기화하면서 데이터베이스를 조회한다.

- 즉시 로딩 : 연관된 엔티티를 즉시 조회한다, 하이버네이트는 가능하면 SQL 조인을 사용해서 한 번에 조회한다.


- `@NoArgsConstructor(access = AccessLevel.PUBLIC)` - 기본 생성자를 이용하여, 값을 주입하는 방식을 최대한 방지하기 위해서 사용을 권장하지 않는다.
 
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` - 위, 아래와 같은 프록시 객체의 생성과 객체에 대한 접근 범위 문제를 해결하기 위해서 사용한다.

- `@NoArgsConstructor(access = AccessLevel.PRIVATE)` - 프록시 객체 생성시 문제가 생기기 때문에 사용을 권장하지 않는다.

---

@NoArgsConstructor(access = AccessLevel.PROTECTED)

- 접근 권한을 Protected로 한 이유는 Proxy 조회 때문
- 즉시 로딩일 경우 실제 엔티티를 생성하므로 문제가 되지 않음
- 지연 로딩 시 프록시 객체를 통해서 조회를 하기 때문에 private이면 프록시 객체를 생성 할 수 없음

---

### 참조
https://velog.io/@kevin_

https://ksh-coding.tistory.com