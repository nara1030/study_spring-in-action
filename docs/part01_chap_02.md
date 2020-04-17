빈 와이어링
=====
* [실습 코드](https://github.com/nara1030/study_spring-in-action/tree/master/src/my/spring_in_action)
* [교재 코드](https://github.com/nara1030/study_spring-in-action/tree/master/src/sol/SpringiA4_SourceCode)
- - -
## 목차
1. [차례](#차례)
	1. [스프링 설정 옵션 알아보기](#스프링-설정-옵션-알아보기)
	2. [자동으로 빈 와이어링하기](#자동으로-빈-와이어링하기)
	3. [자바로 빈 와이어링하기](#자바로-빈-와이어링하기)
	4. [빈을 XML로 와이어링하기](#빈을-XML로-와이어링하기)
	5. [설정 가져오기와 믹싱하기](#설정-가져오기와-믹싱하기)
	6. [요약](#요약)
2. [참고](#참고)

## 차례
### 스프링 설정 옵션 알아보기
1장에서 살펴보았듯 스프링을 사용하는 애플리케이션에서는 각 객체가 자신의 일을 하기 위해 필요한 다른 객체를 직접 찾거나 생성할 필요가 없다. 컨테이너가 협업할 객체에 대한 레퍼런스를 주기 때문이다. 애플리케이션 객체 간의 이러한 연관관계 형성 작업이 바로 종속객체 주입(DI) 개념의 핵심이며, 이를 보통 와이어링(wiring)이라고 한다.

스프링에서는 빈을 엮는 많은 방법이 있으며, 여기서는 스프링 컨테이너를 설정하기 위한 가장 흔한 세 가지 접근에 대해 알아본다.

1. XML에서의 명시적 설정
2. 자바에서의 명시적 설정
3. 내재되어 있는 빈을 찾아 자동으로 와이어링하기

어떤 방법을 선택하든 프로젝트에 적절하게 사용할 수 있지만 할 수만 있다면 자동 설정을 추천한다. 명시적인 설정이 적을수록 좋기 때문[1]이다. 명시적인 빈 설정을 해야 할 때는, 타입 세이프(type-safe)를 보장하고 더욱 강력한 JavaConfig를 XML보다 선호한다. 마지막으로 사용하고자 하는 XML 네임스페이스의 기능이 JavaConfig에 없을 경우 XML을 사용한다.


- - -
* [1]
	* 사용의 용이성 측면에서 스프링 자동 설정보다 나은 것이 없음

##### [목차로 이동](#목차)

### 자동으로 빈 와이어링하기
스프링은 두 가지 방법으로 오토와이어링을 수행한다.

1. [컴포넌트 스캐닝](#발견-가능한-빈-만들기)
	* 스프링은 애플리케이션 컨텍스트에서 생성되는 빈을 자동으로 발견
2. [오토와이어링](#오토와이어링되는-빈의-애너테이션)
	* 스프링은 자동으로 빈 의존성을 충족

컴포넌트 스캐닝과 오토와이어링을 모두 사용하면 명시적 설정을 최소한으로 유지하면서 스프링이 시작 시에 클래스를 발견, 빈으로 생성하고 주입한다.

#### 발견 가능한 빈 만들기
CD 개념을 만들어보기 위해 CD를 정의하는 인터페이스인 CompactDisc를 정의한다.

```java
public interface CompactDisc {
    void play();
}
```

이를 구현하는 SgtPeppers는 아래와 같다.

```java
@Component
public class SgtPeppers implements CompactDisc {
    private static final Logger LOGGER = LoggerFactory.getLogger(SgtPeppers.class);

    private String title = "Sgt. Pepper's Lonely Hearts Club Band";
    private String artist = "The Beatles";

    @Override
    public void play() {
        LOGGER.info("Playing {} by {}", title, artist);
    }
}
```

이때 SgtPeppers가 @Component를 가지고 애너테이트됨을 주목해야 하는데, 이는 클래스가 컴포넌트 클래스임을 나타내며 클래스를 빈으로 만들어야 함을 스프링에 단서로 제공한다(∴ 1장에서와 같이 @Configuration 및 @Bean을 이용, 빈을 명시적으로 설정할 필요 없음). 하지만 컴포넌트 스캐닝이 기본적으로 켜 있지 않기 때문에 스프링을 통해 @Component로 애너테이트된 클래스를 찾기 위해 명시적인 설정을 작성할 필요가 있다. 다음과 같다.

```java
@Configuration
@ComponentScan
public class CDPlayerConfig {
}
```

CDPlayerConfig는 명시적으로 어떤 빈도 정의하지 않지만 스프링으로 컴포넌트 스캐닝을 가능케 하기 위해 @ComponentScan으로 애너테이트된다. 이때 스캐닝은 클래스가 속해 있는 패키지와 하위 패키지를 스캔하고, @Component로 애너테이트된 클래스를 찾는다. 따라서 패키지 구조는 아래와 같아야 한다(CDPlayerConfig와 SgtPeppers가 동일 패키지).

```txt
└── src
    ├── main
    │   ├── java
    │   │   └── hje
    │   │       └── study
    │   │           └── spring_in_action
    │   │               └── soundsystem
    │   │                   └── cd
    │   │                       ├── CompactDisc.java
    │   │                       ├── SgtPeppers.java
    │   │                       └── CDPlayerConfig.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── hje
                └── study
                    └── spring_in_action
                        └── soundsystem
                            └── CDPlayerTest.java
```

만약 config를 따로 분리해서 SgtPeppers(Component로 애너테이트된 클래스)와 동일 패키지가 아니라면 아래 테스트 시 @Autowired 부분에서 에러가 발생한다.

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CDPlayerConfig.class)
public class CDPlayerTest {
    @Autowired
    private CompactDisc cd;	// Could not autowire. No beans of 'CompactDisc' type found. 

    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }
}
```

따라서 config를 애플리케이션 코드와 분리해주기 위해 @ComponentScan 안에 애트리뷰트를 설정해서 베이스 패키지를 명시적으로 설정[1]할 수 있다. 아래와 같다.

```java
@Configuration
@ComponentScan(basePackages = "hje.study.spring_in_action.ch02.soundsystem")
public class CDPlayerConfig {
}
```

이 경우 config를 분리할 수 있기 때문에 패키지 구조를 아래와 같이 구성할 수 있다.

└── src
    ├── main
    │   ├── java
    │   │   └── hje
    │   │       └── study
    │   │           └── spring_in_action
    │   │               └── soundsystem
    │   │                   ├── cd
    │   │                   │   ├── CompactDisc.java
    │   │                   │   └── SgtPeppers.java
    │   │                   └── config
    │   │                       └── CDPlayerConfig.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── hje
                └── study
                    └── spring_in_action
                        └── soundsystem
                            └── CDPlayerTest.java
```

- - -
* [1]

##### [목차로 이동](#목차)

#### 오토와이어링되는 빈의 애너테이션
한편 오토와이어링은 스프링이 빈의 요구 사항과 매칭되는 애플리케이션 컨텍스트상에서 다른 빈을 찾아 빈 간의 의존성을 자동으로 만족시키도록 하는 수단이다. 오토와이어링 수행을 하도록 지정하기 위해서는 스프링의 @Autowired 애너테이션[1]을 사용한다.



- - -
* [1]
	* [생성자 주입을 필드 주입보다 권장하는 이유](https://madplay.github.io/post/why-constructor-injection-is-better-than-field-injection)

##### [목차로 이동](#목차)

### 자바로 빈 와이어링하기


##### [목차로 이동](#목차)

### 빈을 XML로 와이어링하기


##### [목차로 이동](#목차)

### 설정 가져오기와 믹싱하기


##### [목차로 이동](#목차)

### 요약


##### [목차로 이동](#목차)

## 참고


##### [목차로 이동](#목차)
