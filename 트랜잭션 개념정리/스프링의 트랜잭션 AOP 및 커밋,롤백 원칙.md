# 스프링의 트랜잭션 AOP 및 커밋,롤백 원칙

# 참고자료

- [스프링 DB 2편 - 데이터 접근 활용 기술 - 인프런 | 강의](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-db-2)

오늘 정리하는 자료는 위의 자료를 요약한 내용이다. 강사님이 설명해주는 것에 비해 내가 요약한 내용은 턱없이 부족할 수 있다. 혹시라도 이 문서를 보는 누군가가 계신다면 가급적 강의를 들어보는 것을 추천. <br>

2배속으로 들어도 발음이 모두 잘 들려서 충분히 빠르게 훑어볼수 있다. 강의도 비싼 편은 아니니 꼭 한번 들어보는 것을 추천쓰<br>

<br>

으하하하하하하하 정리가 힘들다 ㅠㅠ 코테준비하면서 짬내서 하는게 쬐끔 힘들다 흑... 이번주 안에 정리 되겠지?? 가자!!<br>

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
- 예외상황) Transactional 이 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 적용된 메서드 내부 호출
  - 이 경우, 아래와 같은 방식으로 수행된다.
  - 가짜 객체의 일반 메서드 A 호출 -> 가짜객체는 실제 객체의 일반메서드 A 호출 -> 실제 객체의 일반 메서드A에서 @Transactional 메서드 AA 호출 -> @Transactional 적용 안됨.

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

**요약**

- @Transactional 이 적용된 트랜잭션 프록시 객체는 실제 객체를 부모로 해서 상속받은 가짜 객체다.
- 이 가짜 객체는 스프링 컨테이너가 로딩되면서, 스프링 컨테이너가 프록시 라이브러리를 통해 실제 객체를 상속받은 가짜 객체를 프록시 방식으로 생성해놓은 객체다. @Transactional 이 선언되있는 클래스는 내부적으로는 스프링의 컨테이너가 스프링 컨테이너 로딩시에 프록시 객체를 생성해서 빈으로 등록해준다.
- 그리고 이 트랜잭션 프록시 객체 내에는 트랜잭션의 시작/커밋/롤백 처리를 후처리 할 수 있는 내부 로직이 있다.
- 커스텀 어노테이션을 생성했을 때 스프링에서 사용할 수 있도록 하는 예제를 만들어봤다면, 이해가 쉽울듯.

<br>

**@Transactional 이 적용되지 않을수도 있는 예외 케이스**<br>

- (예외상황) Transactional 이 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 적용된 메서드 내부 호출
  - 이 경우, 아래와 같은 방식으로 수행된다.
  - 가짜 객체의 일반 메서드 A 호출 -> 가짜객체는 실제 객체의 일반메서드 A 호출 -> 실제 객체의 일반 메서드A에서 @Transactional 메서드 AA 호출 -> @Transactional 적용 안됨.

<br>

**ex) BookApiController, BookService**<br>

예를 들어 BookApiController, BookService 클래스가 있다고 해보자. 이 경우 트랜잭션 프록시는 아래와 같은 흐름으로 호출된다.

![1](./img/TRANSACTIONAL-PROXY-OVERVIEW-1.png)

<br>

**(1) 트랜잭션 프록시 객체 빈 등록**<br>

스프링은 트랜잭션 프록시를 적용해야 할 Bean이 보인다면, 컨테이너 로딩시에 해당 객체를 프록시 객체로 만들어서 해당객체의 Bean에 프록시 객체를 등록해둔다. 이때 스프링이 트랜잭션 프록시 객체 생성을 할 타입을 찾는 조건은 아래와 같다.<br>

- `@Transactional` 이 클래스 레벨에 선언된 클래스
- 클래스내의 public 메서드 들 중 단 하나의 메서드 라도 `@Transactional` 이 적용된 클래스
- implements 하고 있는 interface에 @Transactional 이 적용되어 있을 경우

이렇게 생성된 가짜 객체는 Proxy 역할을 수행하기 위해 스프링 컨테이너에 의해 생성된 객체다. 이 Proxy 객체는 트랜잭션의 시작/커밋/롤백 코드 수행을 담당하게 된다. (이렇게 스프링이 생성한 Proxy 객체는 보통 디버깅화면이나 로그 화면에서 보면 `$$CGLIB` 이라는 접미사가 붙어있는 것을 볼 수 있다.)<br>

<br>

**(2) BookApiController/BookApiControllerTest -> bookService.txSaveBook() or bokService.notTxSaveBook() 호출**

> **bookService.txSaveBook() 호출**<br>

BookApiController 에서 bookService 의 txSaveBook() 메서드를 호출하는 경우를 생각해보자. txSaveBook() 메서드는 메서드 정의시 메서드의 구현부 바로 위에`@Transactional` 어노테이션을 적용해둔 상태다. 이 경우, @Transactional 이 호출할 메서드 바디의 실행전/실행 후에 트랜잭션의 시작/커밋/롤백 처리가 적용된다.<br>

트랜잭션 프록시가 메서드 바디의 실행 전/후에 실행되는 절차는 아래와 같다.

- 메서드의 바디를 트랜잭션 프록시가 잡고 있고, 
- 메서드 시작전) 트랜잭션 프록시가 트랜잭션 시작로직을 호출한다. 
- 메서드 구현부 종료후) 트랜잭션 커밋/롤백 호출하는 방식으로 수행된다.

<br>

> **bookService.notTxSaveBook() 호출**<br>

BookApiController 에서 bookService 의 notTxSaveBook() 메서드를 호출하는 경우를 생각해보자. notTxSaveBook() 메서드는 메서드 정의시 메서드의 구현부에 @Transactional 이 적용되어있지 않은 상태다.<br>

이 경우, 같은 클래스 내의 txSaveBook( ) 메서드가 @Transactional 이 적용되어 있기에, 트랜잭션 프록시 객체가 소유한 notTxSaveBook() 메서드가 호출된다. 하지만 notTxSaveBook() 메서드에는 `@Transactioanl` 이 적용되어 있지 않은 상태이기 때문에, 트랜잭션 시작/커밋/롤백 처리는 적용되지 않는다.<br>

<br>

와... 머릿속에 있는 것을 다시 볼때 알아들을 수 있는 언어로 정리하는 게 이렇게 힘들줄이야... ㄷㄷㄷ하다.<br>

<br>

## Transactional 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 메서드 내부 호출하는 경우

> 조금 단순하게 예를 들어보면, 이런 경우다.<br>
>
> Transactional 이 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 적용된 메서드 내부 호출
>
> - 이 경우, 아래와 같은 방식으로 수행된다.
> - 가짜 객체의 일반 메서드 A 호출 -> 가짜객체는 실제 객체의 일반메서드 A 호출 -> 실제 객체의 일반 메서드A에서 @Transactional 메서드 AA 호출 -> @Transactional 적용 안됨.

<br>

**!!!!!!!!!!!!!!!!!!!! 아래에서부터는 노션에서 일단 조금씩 정리하면서 가져온건데, 오늘 윗 부분의 글의 요약을 다시한 것처럼 아래 글의 노션에서 가져온 부분은 내일 또 다듬기 작업이 핅요함!!!(까먹지 말자 좀 ㅠㅠ)**<br>

<br>

**예제 시나리오**

- BookService 객체에서 `@Transactional` 이 적용되지 않은 notTxSaveBook 메서드가 있다.
- 이 notTxSaveBook() 메서드 내에서는 txSaveBook() 메서드를 호출하고 있다.
- txSaveBook()메서드는 @Transactional 이 적용된 메서드다.
- 그리고 BookServiceTest 내에서 bookService 객체를 빈으로 주입받아서 notTxSaveBook() 메서드를 호출한다고 해보자.
- 이때, notTxSaveBook() 메서드를 호출할때 스프링의 트랜잭션이 적용될까?

쉽게 설명하면 아래와 같은 코드 호출 구조다.

<br>

```java
class BookService{
	// ...
	public void notTxSaveBook(){
		txSaveBook();
	}

	@Transactional
	public void txSaveBook(){
		// some logic
	}

}
```

<br>

호출 구조를 그림으로 표현해보면 아래와 같은 구조다.<br>

![1](./img/TRANSACTIONAL-PROXY-NOT-TX-EXAMPLE.png)

<br>

검은색으로 표시한 `bookService$$CGLIB` 객체는 트랜잭션 프록시가 적용된 객체다. 프록시가 적용된 객체는 보통 뒤에 `CGLIB` 이라는 접미사가 붙는다.<br>

파란색으로 표시된 `bookService` 객체는 실제 객체다.<br>

<br>

**트랜잭션이 적용되지 않는 이유**<br>

프록시 객체내에서의 notTxSaveBook() 메서드가 프록시 객체내의 txSaveBook 을 호출했다면 트랜잭션이 적용되었을 것이다. 하지만, 프록시 객체내의 notTxSaveBook() 은 `@Transactional` 의 영향권 밖이다.<br>

따라서 별도의 트랜잭션 처리 없이 실제 bookService 객체 내의 notTxSaveBook() 메서드를 호출하고, 실제 객체 내의 txSaveBook() 메서드를 호출한다. 현재 상황에서는 실제 객체 bookService 객체는 트랜잭션 프록시가 적용되어 있지 않기에 실제 객체 내의 txSaveBook()메서드에 적용된 `@Transactional` 어노테이션은 그냥 알파벳으로만 존재하는 문자일 뿐이다. 따라서 트랜잭션이 적용되지 않는다.<br>

<br>

지금까지의 예를 테스트해보기 위한 예제는 아래와 같다.

```jsx
@Slf4j
@SpringBootTest
public class BookServiceTest {

	@Autowired
	BookService bookService;

	@Test
	public void printProxy(){
		log.info("bookService class = {}", bookService.getClass());
	}

	@Test
	public void txMethodCall(){
		bookService.txSaveBook();
	}

	@Test
	public void notTxMethodCall(){
		bookService.notTxSaveBook();
	}

	@TestConfiguration
	static class InlineConfig {
		@Bean
		BookService bookService(){
			return new BookService();
		}
	}

	@Slf4j
	static class BookService{

		public void notTxSaveBook(){
			log.info("Not 트랜잭셔널 메서드 호출");
			printTxInfo();
			txSaveBook();
		}

		@Transactional
		public void txSaveBook(){
			log.info("트랜잭셔널 메서드 호출");
			printTxInfo();
		}

		// 트랜잭션 적용여부 로깅용도 메서드
		private void printTxInfo(){
			boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
			log.info("tx active ?? {}", txActive);
		}

	}

}
```

<br>

**해결방법 - notTxSaveBook() 메서드에도 트랜잭션이 적용되게끔 하려면?**

아래 코드처럼 별도의 클래스로 분리해준다. 내부호출이 되지 않게끔하는 것이 목적이기에 다른 클래스의 메서드로 분리해뒀다. 이 외에도 실무에서 다양한 문제에 부딪히는데 이것과 관련해서는 글이 길어질것 같아 다른 문서에서 정리 예정이다.

```jsx
class BookService{
	// ...
	// 아래 코드를 별도의 클래스로 옮겨서
	public void notTxSaveBook(){
		txBookService.txSaveBook();
	}
	// ...
}

class TxBookService{
	@Transactional
	public void txSaveBook(){
		// some logic
	}
}
```