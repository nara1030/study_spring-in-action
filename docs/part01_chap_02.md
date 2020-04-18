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

```txt
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
* [1]: 베이스 패키지를 명시적으로 설정하는 두 가지 방법
	1. basePackages
		* 베이스 패키지 설정이 String 값으로 표시되어 타입 세이프하지 않음  
		(패키지 이름 변경 시 에러 가능성)
	2. basePackageClasses
		* String 이름을 사용하여 패키지를 지정하는 대신, 클래스 배열로 지정  
		(패키지 내의 클래스나 인터페이스 사용 가능, ~~즉 컴파일러 체크 가능?~~)
		* 컴포넌트 클래스보다는 스캔될 패키지 안의 비어 있는 *마커 인터페이스* 생성 고려  
		(마커 인터페이스를 사용하면 리팩토링이 쉽도록 인터페이스에 대한 레퍼런스만 가지게 되고, ~~컴포넌트 스캔하고자 했던 클래스의 밖으로 옮겨질 수 있는~~ 실제 애플리케이션 코드에 대한 레퍼런스를 미사용 가능)

##### [목차로 이동](#목차)

#### 오토와이어링되는 빈의 애너테이션
한편 오토와이어링은 스프링이 빈의 요구 사항과 매칭되는 애플리케이션 컨텍스트상에서 다른 빈을 찾아 빈 간의 의존성을 자동으로 만족시키도록 하는 수단이다. 오토와이어링 수행을 하도록 지정하기 위해서는 스프링의 @Autowired 애너테이션[1]을 사용한다.

@Autowired 애너테이션의 사용은 생성자로 한정되지 않는다. 생성자나 세터 메소드를 포함한 어떤 메소드이든 스프링은 *메소드 파라미터에 의존성*[2]을 가진다. 한 개의 빈이 일치하면 그 빈은 와이어링되지만, 매칭되는 빈이 없다면 스프링은 애플리케이션 컨텍스트가 생성될 때 예외를 발생시킨다. 

- - -
* [1]
	* [생성자 주입을 필드 주입보다 권장하는 이유](https://madplay.github.io/post/why-constructor-injection-is-better-than-field-injection)
		1. 순환 참조 방지
		2. 테스트 코드 작성 용이
		3. 나쁜 냄새 제거
		4. Immutable
* [2]  
	```java
	@Bean
	public CDPlayer cdPlayer(CompactDisc compactDisc) {
		return new CDPlayer(compactDisc);
	}
	```
	
##### [목차로 이동](#목차)

### 자바로 빈 와이어링하기
대부분 컴포넌트 스캐닝과 오토 와이어링을 사용한 자동 스프링 설정을 선호하지만 스프링을 명시적으로 설정해야 하는 경우가 있다. 예를 들어 타사 라이브러리의 컴포넌트를 애플리케이션으로 와이어하고자 한다면 그 라이브러리의 소스 코드를 가지고 있지 않으므로 클래스를 @Component와 @Autowired를 사용하여 애너테이트할 수 없다. 이 경우 XML 혹은 JavaConfig를 통한 명시적 설정을 해야 하는데, 이 절에서는 JavaConfig 사용법에 대해 살펴본다. 먼저 특징은 다음과 같다.

1. JavaConfig는 타입 세이프하며 리팩토링 친화적으로 명시적 설정을 위해 선호하는 옵션
2. 다른 자바 코드와는 다른 설정용 코드로 어떠한 비즈니스 로직도 미포함, 미영향  
(필수 사항은 아니나 JavaConfig는 애플리케이션 로직 중 비즈니스 로직 외의 다른 부분과는 분리된 패키지)

이미 만들었던 CDPlayerConfig를 통해 JavaConfig를 살펴보자.

```java
@Configuration
public class CDPlayerConfig {
}
```

JavaConfig 클래스 만들기의 핵심은 @Configuration으로 애너테이트하는 것이다. @Configuration 애너테이션은 이를 설정 클래스로서 식별하고, 스프링 애플리케이션 컨텍스트에서 만들어진 빈의 자세한 내용이 포함될 수 있다는 것을 나타낸다.

한편 이 절에서는 명시적인 설정에 집중하기 위해 @ComponentScan 애너테이션을 제거하였다(컴포넌트 스캐닝과 명시적인 설정을 사용할 수 없는 이유가 있는 것은 아님). 즉 @ComponentScan이 제거되었으므로 CDPlayerConfig 클래스는 별 효과가 없다. 만약 CDPlayerTest를 실행한다면 BeanCreaionException이 발생하며, 테스트는 실패한다. 그렇다면 어떻게 다시 테스트를 성공시킬 수 있을까?

JavaConfig에서 빈을 선언하기 위해서 원하는 타입의 인스턴스를 만드는 메소드를 만들고, @Bean으로 애너테이트[1]한다.

```java
@Bean
public CompactDisc sgtPeppers() {
	return new SgtPeppers();
}
```

선언된 CompactDisc 빈은 간단하며 의존성을 가지지 않는다. 하지만 CompactDisc에 의존성을 가진 CDPlayer 빈을 선언해야 한다. JavaConfig에서 어떻게 와이어링할까?

```java
@Bean
public CDPlayer cdPlayer() {
	return new CDPlayer(sgtPeppers());
}
```

cdPlayer() 메소드의 몸체는 sgtPeppers() 메소드 몸체와 미묘하게 다르다. 구체적으로 CompactDisc는 sgtPeppers를 호출해서 생성되는 것처럼 보이지만, 항상 그렇진 않다. sgtPeppers() 메소드는 @Bean으로 애너테이트되므로 스프링은 콜을 중간에 인터셉트하고, 메소드에 의해 만들어진 빈은 다시 만들어지지 않고 이미 만들어진 것을 리턴해주는 것을 보장한다. 만약 sgtPeppers()에 대한 호출이 일반 자바 메소드의 호출처럼 처리된다면 각 CDPlayer에는 각각의 SgtPeppers의 인스턴스가 주어질 것이다. 반면 기본적으로 스프링의 모든 빈은 싱글톤(singletons)이고, 중복 인스턴스를 생성할 필요가 없다. 다만 메소드를 호출하여 빈을 참조하는 방법은 혼동의 여지가 있으므로 아래와 같은 방법을 사용할 수 있다.

```java
@Bean
public CDPlayer cdPlayer(CompactDisc compactDisc) {
	return new CDPlayer(compactDisc);
}
```

cdPlayer() 메소드는 파라미터로 CompactDisc를 사용한다(→ 스프링은 메소드 파라미터에 의존성 가짐). 스프링이 CDPlayer 빈을 만들기 위해 cdPlayer()를 호출하였을 때, CompactDisc를 설정 메소드로 오토와이어링한다. 즉 cdPlayer() 메소드는 CompactDisc의 @Bean 메소드를 명시적으로 참조하지 않고서도, CompactDisc를 CDPlayer 생성자에 주입한다.

- - -
* [1]
	* @Bean 애너테이션은 이 메소드가 스프링 애플리케이션 컨텍스트에서 빈으로 등록된 객체를 반환해야 함을 의미
	* 기본적으로 빈은 @Bean으로 애너테이트된 메소드와 동일한 ID를 받음  
	(다른 ID 갖고자 한다면 name 애트리뷰트 사용)

##### [목차로 이동](#목차)

### 빈을 XML로 와이어링하기


##### [목차로 이동](#목차)

### 설정 가져오기와 믹싱하기


##### [목차로 이동](#목차)

### 요약


##### [목차로 이동](#목차)

## 참고
* 마커 인터페이스
	1. [What is Marker interfaces in Java and why required?](https://javarevisited.blogspot.com/2012/01/what-is-marker-interfaces-in-java-and.html)
	2. [Marker interfaces in Java - Baeldung](https://www.baeldung.com/java-marker-interfaces)
	3. [Marker interfaces in Java - GeeksforGeeks](https://www.geeksforgeeks.org/marker-interface-java/)
	4. [Java Marker Interface - javapaper](https://javapapers.com/core-java/abstract-and-interface-core-java-2/what-is-a-java-marker-interface/)
	5. [마커 인터페이스](https://woovictory.github.io/2019/01/04/Java-What-is-Marker-interface/)

##### [목차로 이동](#목차)
