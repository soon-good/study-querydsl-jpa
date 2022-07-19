# 스프링의 트랜잭션 AOP 및 커밋,롤백 원칙

> 아직도 전직장에서 내 깃헙을 몰래 훔쳐보는 듯한 트라우마가 남아있긴 하지만, 나도 어쩔 수 없이 내 할일을 해야 해서 일단 정리를 시작. 인권 가스라이팅 수준으로 불결한 경험을 줬던 사람들인 것 같다... 거의 트라우마 수준으로 남았다. 글을 새로 쓸때마다 이렇게 불안하게 만들정도면 말 다했지 않나 싶다.<br>
>
> 불특정 다수가 내 깃헙을 보는것은 하나도 불쾌하지 않다. 그 중에 매일 노력하고, 선의를 가지고 내 깃헙을 보는 분들도 있을 듯 하다. 그런데 그런사람들이 있는지도 나도 잘 모르겠다. 그런 분들이 있다면 나도 싫지 않다. 나도 내 깃헙이 그냥 하루 하루 노력하는 결과를 저장하는 저장소여서 별 신경을 안쓴다. 하지만 전 직장 사람들이 내 깃헙을 보는 건 좀... 아니지 않나 싶다. 양심이 있다면 그러면 안되는 사람들이지 않나 싶다. <br>

<br>

# 참고자료

- [스프링 DB 2편 - 데이터 접근 활용 기술 - 인프런 | 강의](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-db-2)

오늘 정리하는 자료는 위의 자료를 요약한 내용이다. 강사님이 설명해주는 것에 비해 내가 요약한 내용은 턱없이 부족할 수 있다. 혹시라도 이 문서를 보는 누군가가 계신다면 가급적 강의를 들어보는 것을 추천. <br>

내 경우는 보통 왠만한 강의를 들을 때 대부분 2배속으로 자주 듣는 편인데, 김영한님은 2배속으로 들어도 발음이 모두 잘 들려서 충분히 빠르게 훑어볼수 있다. 강의도 비싼 편은 아니니 꼭 한번 들어보는 것을 추천<br>

<br>

# 리마인드 - 스프링의 트랜잭션

## PlatformTransactionManager

Java 는 모든 DBMS에 대해서 jdbc 라이브러리를 제공해주고 있다. 그리고 이 jdbc 라이브러리를 추상화한 데이터 라이브러리가 있다. 이 데이터 라이브러리들은 각각 트랜잭션을 시작/커밋/롤백 하기 위해 사용해야 하는 Java 코드가 제 각각이다. 즉, 데이터 라이브러리 별로 트랜잭션 관련 코드들이 제각각이다.<br>

스프링에서는 JdbcTemplate, JPA, Mybatis 등의 jdbc 구현체 라이브러리들의 트랜잭션 시작, 커밋,롤백을 위한 코드들을 구현해둔 클래스가 있다. 즉, 데이터 라이브러리마다 사용할 수 있는 TransactionManager 들을 모두 지원하고 있다. 각각의 TransactionManager 클래스 들은 아래와 같다.<br>

<br>

JDBCTemplate

- [DataSourceTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceTransactionManager.html)
- [JDBCTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/support/JdbcTransactionManager.html)

<br>

JPA

- [JpaTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html)
- [HibernateTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/hibernate5/HibernateTransactionManager.html)

<br>

Etc

- [JmsTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jms/connection/JmsTransactionManager.html)
- [WebLogicJtaTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/jta/WebLogicJtaTransactionManager.html)
- …

<br>

이렇게 스프링에서 기본으로 제공하고 있는 OOOTransactionManager 는 `PlatformTransactionManager` 인터페이스 타입을 구현하고 있다. 즉 각각의 구현체의 추상타입은 [PlatformTransactionManager](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/PlatformTransactionManager.html) 이다.<br>

각각의 OOOTransactionManager 클래스의 인스턴스는 직접 생성해도 되지만, 스프링에서는 빈으로 등록해서 사용할 수 있다. 이렇게 Bean 으로 등록할때, 구체 타입을 지정하는 것이 나쁜 것은 아니지만, PlatformTransactionManager 라는 추상타입으로 Bean 으로 등록하고, 의존성주입을 받을 때도 PlatformTransactionManager 타입으로 주입받아 사용할 수 있다.<br>

<br>

## 선언형 트랜잭션 적용 vs 프로그래밍 방식 트랜잭션 적용

트랜잭션을 적용하는 것은 선언적인 방식, 프로그래밍 방식 이렇게 두가지 방식으로 모두 적용하는 것이 가능하다.<br>

<br>

선언형 트랜잭션 적용

- ex) `@Transactional` 어노테이션
- 스프링프레임워크에서는 클래스 내에 메서드 중 하나라도 `@Transactional` 어노테이션이 적용되어 있거나, 클래스 레벨에 `@Transactional` 이 적용되어 있으면, 트랜잭션 프록시 객체가 되어서, 해당 메서드 수행시 트랜잭션의 시작/커밋/롤백 코드를 스프링 컨테이너에서 수행한다.
- 과거에는 xml 설정방식을 통해서 xml 설정파일에 어떤 클래스의 어떤 메서드에 트랜잭션을 사용하겠다고 명시하기도 했다.

<br>

프로그래밍 방식 트랜잭션

- 트랜잭션 매니저, 트랜잭션 템플릿을 사용해서 직접 트랜잭션 코드를 구현하는 방식
- 기술계층의 코드와 애플리케이션 계층의 코드가 혼재하게 되어 테스트의 경계도 모호해지고, 유지보수 역시 쉽지 않아진다는 단점이 있다.

<br>

## 트랜잭션 프록시 코드를 직접 작성할 경우의 예제

> 잠시 스킵

`@Transactional` 이 어떻게 동작하는지 단순한 아이디어만 짚어보기 위해 엄청나게 단순한 수준의 예제를 정리<br>

<br>

# 스프링의 트랜잭션 AOP

## 트랜잭션 프록시 적용 주요 원칙

- 클래스레벨에 @Transactional 
  - 클래스레벨에 @Transactional 이 적용되면 모든 public 메서드에 @Transactional 자동 적용
- @Transactional 덮어쓰기
  - 최 하위레벨에서 덮어쓴 선언이 최종 @Transactional 선언이 된다.
- 인터페이스에 @Transactional
  - 인터페이스에도 @Transactional 이 적용될 수 있다.
- Transactional 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 메서드 내부 호출
  - 이 부분은 아래에서 더 자세히 다룰 예정이다.

<br>

**클래스레벨에 적용되면 모든 public 메서드에 @Transactional 자동 적용**<br>

@Transactional 이 클래스에 적용되어 있으면, 그 클래스 내의 모든 public 메서드는 @Transactional 이 적용된다. public 이 적용되지 않은 곳에 `@Transactional` 이 적용되어 있으면 예외가 발생하지는 않고 트랜잭션 적용만 무시된다.<br>

클래스 레벨에 트랜잭션을 적용하면 모든 public 메서드에 트랜잭션이 걸릴 수 있다. 이런 경우 트랜잭션이 의도하지 않은 곳까지도 과도하게 적용된다.<br>

<br>

**@Transactional 덮어쓰기, 최 하위레벨에서 덮어쓴 선언이 최종 @Transactional 선언이 된다.**

@Transactional 이 적용되는 것은 우선순위가 있다. 가장 작은 단위에 적용된 @Tranactional 이 그 윗레벨에 적용된 @Transactional 을 모두 덮어쓰게 된다. 예를 들면, readOnly = true , readOnly = false 같은 옵션이 적용된 @Transactional 이 호출스택 곳곳에 산재해있는 아래의 경우를 예로 들수 있다.<br>

아래 예제를 보자.<br>

```java
@Slf4j
@Transactional(readOnly = true)
public class BookService{
	// ...
	@Transactional(readOnly = false)
	public void txSaveBook(){
		printTxInfo();
	}

	// ...
	public void printTxInfo(){
		boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
		log.info("tx active = {}", txActive);
		boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		log.info("tx readOnly = {}", readOnly);
	}
}
```

<br>

출력결과

```java
tx active = true
tx readOnly = true
```

<br>

**인터페이스에도 @Transactional 이 적용될 수 있다.**

> 인터페이스에 @Transactional 을 사용하는 것은 스프링 공식 매뉴얼에서는 권장하지 않는 방법이다. 간혹 스프링 버전에 따라서 인터페이스에 @Transactional 이 적용하는 방식이 지원되지 않는 경우도 있다. (스프링 5.0 부터는 가능하지만, 가급적 사용하지 않는 것을 추천)<br>

인터페이스에도 @Transactional 이 적용되어 있다면, 이 경우 @Transactional 이 적용되는 우선순위를 높은 순서로 나열해보면 아래와 같다.

- 1 ) 클래스의 메서드 (가장 하위 레벨. 최종적으로 덮어쓰기 됨)
- 2 ) 클래스 레벨 (클래스 Type 레벨)
- 3 ) 인터페이스의 메서드
- 4 ) 인터페이스 레벨 (인터페이스 Type 레벨)

<br>

## 스프링의 @Transactional 에 대한 프록시 객체 생성/등록 원리

- @Transactional 이 적용된 트랜잭션 프록시 객체는 실제 객체를 부모로 해서 상속받은 가짜 객체다.
- 그리고 이 트랜잭션 프록시 객체 내에는 트랜잭션의 시작/커밋/롤백 처리를 후처리 할 수 있는 내부 로직이 있다.
- 이런 처리를 하는 기준은 트랜잭션 프록시 객체내에 @Transactional 어노테이션이 있는가이다.
  - (커스텀 어노테이션을 스프링에 등록해서 사용하는 예제를 떠올려보면 이해가 쉽게 된다.)
- 실제 객체에는 아무리 @Transactional 이 있더라도 실제 객체의 메서드를 바로 직접 호출하는 경우눈, @Transactional에 합당한 동작은 안하고, 메서드 몸체만 단순 실행하게 된다.

> 조금 더 명확하게 정리. 내 목표는 더 명확하게 정리/요약하는게 목적이다.<br>

<br>



![1](./img/TRANSACTIONAL-PROXY-OVERVIEW-1.png)

스프링은 `@Transactional` 이 적용된 클래스 또는 클래스내의 public 메서드 들 중 단 하나의 메서드 라도 `@Transactional` 이 적용된 클래스가 있으면, 해당 클래스를 상속받은 가짜클래스타입의 객체를 생성한다. 이렇게 생성된 가짜 객체는 Proxy 역할을 수행하기 위해 스프링 컨테이너에 의해 생성된 객체다.<br>

이 Proxy 객체는 트랜잭션의 시작/커밋/롤백 코드 수행을 담당하게 된다. 이렇게 스프링이 생성한 Proxy 객체는 보통 디버깅화면이나 로그 화면에서 보면 `$$CGLIB` 이라는 접미사가 붙어있는 것을 볼 수 있다.<br>

> 예를 들어 BookApiController 라는 클래스와 BookApiControllerTest 라는 클래스가 있다고 해보자. BookApiController, BookApiControllerTest 둘 중 어느 것으로 예로 들 수 있겠지만, 개념정리를 위해서는 역시 단순한 케이스가 더 낫기 때문에, BookApiControllerTest 내에서 @Transactional 이 적용된 메서드를 호출하는 경우를 예제로 정리하기로 했다.

<br>

**BookService - txSaveBook(), notTxSaveBook()**

BookApiController, BookApiControllerTest 클래스는 각각 BookService 객체 내의 `txSaveBook()` , `notTxSaveBook()` 메서드를 호출한다. txSaveBook() 메서드는 @Transactional 이 적용된 메서드이고, notTxSaveBook() 메서드는 @Transactional 어노테이션을 적용하지 않은 메서드다.<br>

<br>

**BookApiControllerTest 클래스 내에서 txSaveBook() 메서드를 호출할 때**

txSaveBook() 을 호출할 때 사용하는 객체는 스프링 컨테이너가 BookService 를 상속받는 타입으로 생성한 프록시 객체인 bookService$$CGLIB1 이다.<br>

(스프링은 클래스의 범위내에 하나라도 @Transactional 이 존재하면 프록시 객체를 생성해서 Bean으로 등록해두기에, 실제 객체가 아닌 프록시 객체가 사용된다.)<br>

이 bookService$$CGLIB1 이라는 프록시 객체 내의 txSaveBook() 메서드가 호출될 때 txSaveBook() 메서드의 실행 전후에는 트랜잭션 시작/커밋/롤백이 적용되어 호출된다. 실제 txSaveBook 로직은 프록시 객체 입장에서 부모 클래스인 실제 bookService 객체의 구현부를 그대로 호출한다.<br>

<br>

**BookApiControllerTest 클래스 내에서 notTxSaveBook() 메서드를 호출할 때**

notTxSaveBook() 을 호출할 때 사용하는 객체 역시 스프링 컨테이너가 BookService 를 상속받는 타입으로 생성한 프록시 객체인 bookService$$CGLIB1 이다. @Transactional 이 적용되지 않은 메서드를 호출했음에도, 프록시 객체를 통해서 `notTxSaveBook()` 메서드가 호출된다.<br>

(스프링은 클래스의 범위내에 하나라도 @Transactional 이 존재하면 프록시 객체를 생성해서 Bean으로 등록해두기에, 실제 객체가 아닌 프록시 객체가 사용된다.)<br>

이 bookService$$CGLIB1 이라는 프록시 객체 내의 notTxSaveBook() 메서드가 호출될 때 notTxSaveBook() 메서드의 실행 전후에는 트랜잭션 시작/커밋/롤백이 적용되지 않은 순수한 메서드가 호출된다. 프록시 객체내의 트랜잭션 시작/커밋/롤백을 적용하지 않고 실객체의 notTxSaveBook() 메서드를 호출하게 되기 때문이다.<br>

<br>

## Transactional 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 메서드 내부 호출하는 경우

여기부터는 내일 새벽 내지, 내일 중으로 정리 예정. 