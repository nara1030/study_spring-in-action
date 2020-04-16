스프링 속으로
=====
* [실습 코드](https://github.com/nara1030/study_spring-in-action/tree/master/src/my/spring_in_action)
* [교재 코드](https://github.com/nara1030/study_spring-in-action/tree/master/src/sol/SpringiA4_SourceCode)
- - -
## 목차
1. [차례](#차례)
	1. [자바 개발 간소화](#자바-개발-간소화)
	2. [빈을 담는 그릇으로의 컨테이너](#빈을-담는-그릇으로의-컨테이너)
	3. [스프링 현황](#스프링-현황)
	4. [스프링의 새로운 기능](#스프링의-새로운-기능)
	5. [요약](#요약)
2. [참고](#참고)

## 차례
### 자바 개발 간소화
스프링은 로드 존슨의 책[1]을 통해 처음 소개된 오픈 소스 프레임워크로서 EJB로만 할 수 있었던 작업을 평범한 자바빈을 사용해 가능케 한다. 스프링을 사용하는 모든 자바 애플리케이션은 간소함, 테스트 용이성, 낮은 결합도라는 이득을 얻었다. 자바 복잡도 간소화를 지원하기 위해 스프링은 네 가지 주요 전략을 사용한다.

1. [POJO를 이용한 가볍고(lightweight) 비침투적(non-invasive)인 개발](#POJO를-이용한-가볍고-비침투적인-개발)
2. [DI와 인터페이스 지향(interface orientation)을 통한 느슨한 결합도(loose coupling)](#DI와-인터페이스-지향을-통한-느슨한-결합도)
3. [애스펙트와 공통 규약을 통한 선언적(declarative) 프로그래밍](#애스펙트와-공통-규약을-통한-선언적-프로그래밍)
4. [애스펙트와 템플릿(template)을 통한 반복적인 코드 제거](#애스펙트와-템플릿을-통한-반복적인-코드-제거)

#### POJO를 이용한 가볍고 비침투적인 개발
스프링은 스프링에 특화된 인터페이스 구현이나 스프링 자체에 의존성이 높은 클래스 확장을 거의 요구하지 않는다. 스프링 기반 애플리케이션의 클래스에는 스프링이 사용한다는 표시도 거의 없다. 최악의 경우, 클래스에 스프링의 애너테이션(annotation)이 붙지만 그렇지 않은 경우에는 POJO`[2]다. 즉, 스프링의 비침투적 프로그래밍[3] 모델에서, 클래스는 스프링 애플리케이션 외에도 잘 동작한다.

이러한 간단한 형태에도 불구하고 POJO는 매우 강력한데, 스프링이 POJO에 힘을 불어넣는 방법 중 하나가 바로 DI를 활용한 조립이다.

##### [목차로 이동](#목차)

#### DI와 인터페이스 지향을 통한 느슨한 결합도
애플리케이션에서는 두 개 이상의 클래스가 서로 협력하여 비즈니스 로직(logic)을 수행한다. 이때 각 객체는 협력하는 객체에 대한 레퍼런스(reference, 즉 종속객체)를 얻을 책임이 있다. 하지만 그 결과, 결합도가 높아지고 테스트하기 힘든 코드가 만들어지기 쉽다.

```java
public class DamselRescuingKnight implements Knight {
    private RescueDamselQuest quest;

    public DamselRescuingKnight() {
        this.quest = new RescueDamselQuest();   // RescueDamselQuest에 강하게 결합
    }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }
}
```

위 코드에서 DamselRescuingKnight는 생성자 안에 RescueDamselQuest를 생성한다. 이것은 DamselRescuingKnight가 RescueDamselQuest에 강하게 결합되도록 하며, DamselRescuingKnight에 대한 단위 테스트를 작성하기도 몹시 어렵다. 이와 같이 결합도가 높은 코드는 테스트와 재활용이 어렵지만, DI(Dependency Injection)를 이용하면 코드가 간단해질 뿐 아니라 테스트하기도 쉬워진다.

```java
public class BraveKnight implements Knight {
    private Quest quest;

    public BraveKnight(Quest quest) {  // Quest 주입
        this.quest = quest;
    }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }
}
```

위에선 DamselRescuingKnight와는 달리 BraveKnight는 자신의 원정(Quest)을 생성하지 않는 대신, 생성 시점에 생성자 인자에 원정이 부여(constructor injection)된다. 요점은 BraveKnight가 Quest의 특정 구현체에 결합되지 않는다는 사실이다. Quest 인터페이스를 구현하기만 하면 기사에게 어떤 종류의 원정을 떠나도록 요청하든 문제가 되지 않는다. 이것이 바로 DI의 주요 이점인 느슨한 결합도(loose coupling)이다. 즉 임의의 객체가 자신이 필요로 하는 종속객체를 인터페이스를 통해서만 알고 있는(구현 클래스나 인스턴스화 방법이 아니라) 것이다. 이전에는 강한 결합도(tight coupling)로 인해 DamselRescuingKnight를 적절히 테스트할 수 없었지만 이제는 Quest의 모의(mock) 구현체[4]를 제공하여 BraveKnight를 쉽게 테스트할 수 있다.

```java
public class BraveKnightTest {
    @Test
    public void knightShouldEmbarkOnQuest() {
        Quest mockQuest = mock(Quest.class);    	// 모의 Quest 생성
        BraveKnight knight = new BraveKnight(mockQuest);    // 모의 Quest 주입
        knight.embarkOnQuest();
        verify(mockQuest, times(1)).embark();
    }
}
```

한편 이제 중요한 것은 *어떻게 BraveKnight에게 SlayDragonQuest를 줄 수 있는가?* 그리고 *어떻게 SlayDragonQuest에게 PrintStream을 줄 수 있는가?* 하는 것이다. 이처럼 애플리케이션 컴포넌트 간의 관계를 정하는 것을 와이어링(wiring)이라고 하며 스프링에서는 XML 혹은 자바 설정 방법이 있으나 현재 스프링부트 환경이므로 자바 기반 설정을 살펴본다.

```java
@Configuration
public class KnightConfig {
    @Bean
    public Knight knight() {
        return new BraveKnight(quest());
    }

    @Bean
    public Quest quest() {
        return new SlayDragonQuest(System.out);
    }
}
```

핵심은 BraveKnight가 Quest에 의존적이긴 하지만, 어떤 타입의 Quest가 주어질지 또는 그 Quest가 어디에서부터 올지는 모른다는 것이다. 마찬가지로 SlayDragonQuest이 PrintStream에 의존적이기는 하지만, 오직 스프링만이 모든 조각이 어떻게 합쳐지는지 설정을 통해 아는 것이다. 이것을 통해 종속된 클래스를 수정하지 않으면서 종속성 수정이 가능하다. 이제 이 설정을 로드하여 애플리케이션을 구동해볼 차례다.

```java
public class KnightMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(KnightConfig.class);
        Knight knight = context.getBean(Knight.class);
        knight.embarkOnQuest();
    }
}
```

위처럼 스프링 애플리케이션에서 애플리케이션 컨텍스트(application context)는 빈에 관한 정의들을 바탕으로 빈들을 엮어준다(여기서는 스프링부트를 사용했기에 ClassPathXmlApplicationContext가 아닌 AnnotationConfigApplicationContext를 사용).

##### [목차로 이동](#목차)

#### 애스펙트와 공통 규약을 통한 선언적 프로그래밍
시스템은 보통 특정한 기능을 책임지는 여러 개의 컴포넌트로 구성된다. 그러나 각 컴포넌트는 대체로 본연의 특정한 기능 외에 로깅(logging)이나 트랜잭션 관리, 보안 등의 시스템 서비스도 수행해야 하는 경우가 많다. 이러한 시스템 서비스는 시스템의 여러 컴포넌트에 관련되는 경향이 있으므로 횡단 관심사(cross-cutting concerns)라고 한다. AOP는 이러한 시스템 서비스를 모듈화해서 컴포넌트에 *선언적*으로 적용한다.

다시 말해 AOP를 이용하면 시스템 서비스에 대해서는 전혀 알지 못하지만, 응집도가 높고 본연의 관심사에 집중하는 컴포넌트를 만든다. 즉 애스펙트는 POJO를 단순화한다. 핵심 기능을 구현하는 모듈에는 아무런 변화도 가하지 않고 추가적인 기능을 *선언적*으로 적용하기 때문이다. 아래와 같은 로깅 시스템을 가정해보자.

콛.

##### [목차로 이동](#목차)

#### 애스펙트와 템플릿을 통한 반복적인 코드 제거


##### [목차로 이동](#목차)

- - -
* [1]: Expert One-on-One: J2EE Design and Development
* `[2]: POJO(plain-old-java-object)
	* [What is the difference a JavaBean and a POJO?](https://stackoverflow.com/questions/1394265/what-is-the-difference-between-a-javabean-and-a-pojo)
* [3]
	* 비침투적 개발이란 바탕이 되는 기술을 사용하는 클래스, 인터페이스, API 등을 코드에 직접 나타내지 않는 방법이다. 복잡함을 분리할 수 있다.
* [4]
	* 여기서는 Quest 인터페이스의 모의 구현체를 만들기 위해 Mockito로 알려진 모의 객체 프레임워크를 사용


##### [목차로 이동](#목차)

### 빈을 담는 그릇으로의 컨테이너


##### [목차로 이동](#목차)

### 스프링 현황


##### [목차로 이동](#목차)

### 스프링의 새로운 기능


##### [목차로 이동](#목차)

### 요약


##### [목차로 이동](#목차)

## 참고



##### [목차로 이동](#목차)