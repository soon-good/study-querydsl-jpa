# 스프링의 트랜잭션 AOP 및 커밋,롤백 원칙

# 참고자료

- [스프링 DB 2편 - 데이터 접근 활용 기술 - 인프런 | 강의](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-db-2)

<br>

오늘 정리하는 자료는 위의 자료를 요약한 내용이다. 강사님이 설명해주는 것에 비해 내가 요약한 내용은 턱없이 부족할 수 있다. 혹시라도 이 문서를 보는 누군가가 계신다면 가급적 강의를 들어보는 것을 추천. <br>

2배속으로 들어도 발음이 모두 잘 들려서 충분히 빠르게 훑어볼수 있다. 강의도 비싼 편은 아니니 꼭 한번 들어보는 것을 추천쓰<br>

<br>

2022.07.25

- 스프링의 커밋/롤백 을 정리하기 시작

- 스프링에서의 커밋/롤백을 실제로 테스트해볼때 테스트 코드에서는 확인하기가 쉽지 않았었다. 그 이유는 @Transactional 의 test 코드에서의 커밋/롤백 때문이었다. 테스트코드에서의 커밋/롤백 정책은 테스트에 대한 개념이 있어야 이해가 가능할 듯 하다.
- 그때 Web Application 내에서 테스트를 진행한 후 커밋 롤백을 DB에 저장이 되었는지를 기준으로 테스트했던 것으로 기억한다.
- 물론 테스트 코드도 만들어서 테스트 했지만, 마음에 썩 들지는 않았었다.
- 그 이후로 몇주뒤였던가 싶을때 위 강의와 강의 프린트를 보고 어떻게 테스트할지 감을 잡았던 것 같다. 
  - (일도 꽤 힘들고, 시간이 없는 와중에도 이때는 스터디를 꽤 열심히 했다.)
- 강의 프린트에서도 늘 언급을 하는 것이 트랜잭션 적용원칙이나, 내부메서드 중첩호출로 인한 트랜잭션 적용원칙, 커밋/롤백 원칙 등은 실무에서도 혼동하는 사람이 꽤 많고, 실무에서도 중요한 주제인 것으로 보였다.

- 혼자서 테스트해볼때는 나름 막연한 것이 많았는데, 위의 자료를 보고 스터디에 큰 도움을 얻었다. 위의 강의를 만드실때 스프링 내부 코드를 직접 확인해보고, TransactionSynchronizationManager 클래스내의 여러 메서드들의 API를 직접 확인해보느라 시간과 고생을 많이 들이셨을 것 같다는 생각이 들었다.

<br>

# 리마인드 - 스프링의 트랜잭션

- [리마인드 - 스프링의 트랜잭션](https://github.com/soon-good/study-querydsl-jpa/blob/develop/%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%20%EA%B0%9C%EB%85%90%EC%A0%95%EB%A6%AC/%EB%A6%AC%EB%A7%88%EC%9D%B8%EB%93%9C-%EC%8A%A4%ED%94%84%EB%A7%81%EC%9D%98%20%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98.md)

<br>

# 스프링의 트랜잭션 프록시

스프링은 트랜잭션을 AOP로 제공한다. 이번 문서에서 AOP 에 대한 개념을 정리할 여건은 되지 않아서 AOP 개념정리는 스킵.<br>

대신 스프링에서 트랜잭션을 어떻게 프록시로 제공하고, 여기에 적용되는 우선순위 규칙 및 프록시가 적용되지 않는 예외 케이스에 대해서 정리하기로 했다.<br>

스프링의 트랜잭션 프록시는 아래의 순서로 개념을 정리하기로 했다.

- 트랜잭션 프록시 적용 주요 원칙
- 트랜잭션 프록시 객체 생성/등록 원리
- 예외케이스) 같은 클래스 내 non-tx 메서드 -> tx 메서드 호출하는 경우

<br>

## 트랜잭션 프록시 적용 주요 원칙

- 클래스레벨에 @Transactional 
  - 클래스레벨에 @Transactional 이 적용되면 모든 public 메서드에 @Transactional 자동 적용
- @Transactional 덮어쓰기
  - 최 하위레벨에서 덮어쓴 선언이 최종 @Transactional 선언이 된다.
- 인터페이스에 @Transactional
  - 인터페이스에도 @Transactional 이 적용될 수 있다.
- 예외케이스) 같은 클래스 내 non-tx 메서드 -> tx 메서드 호출하는 경우 
  - Transactional 이 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 적용된 메서드 내부 호출하는 경우다.
  - ex) 가짜 객체의 일반 메서드 A 호출 -> 가짜객체는 실제 객체의 일반메서드 A 호출 -> 실제 객체의 일반 메서드A에서 @Transactional 메서드 AA 호출 -> @Transactional 적용 안됨.


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

<br>

인터페이스에도 @Transactional 이 적용되어 있다면, 이 경우 @Transactional 이 적용되는 우선순위를 높은 순서로 나열해보면 아래와 같다.

- 1 ) 클래스의 메서드 (가장 하위 레벨. 최종적으로 덮어쓰기 됨)
- 2 ) 클래스 레벨 (클래스 Type 레벨)
- 3 ) 인터페이스의 메서드
- 4 ) 인터페이스 레벨 (인터페이스 Type 레벨)

<br>

**예외상황) 같은 클래스 내 non-tx 메서드 -> tx 메서드 호출하는 경우**<br>

- 뒤에서 정리할 예정

<br>

## 트랜잭션 프록시 객체 생성/등록 원리

> 스프링에서 Bean 등록시 @Transactional 이 붙은 클래스에 대해 프록시 객체로 생성/등록 원리에 대해서 정리

<br>

목차

- 요약
- @Transactional 이 적용되지 않을수도 있는 예외 케이스
- ex) BookApiController, BookService
- (1) 트랜잭션 프록시 객체 빈 등록
- (2) BookApiController/BookApiControllerTest -> bookService.txSaveBook() or bokService.notTxSaveBook() 호출

<br>

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

## 예외케이스) 같은 클래스 내 non-tx 메서드 -> tx 메서드 호출하는 경우

> 조금 단순하게 예를 들어보면, 이런 경우다.<br>
>
> Transactional 이 적용되지 않은 메서드에서 같은 클래스내의 @Transactional 적용된 메서드 내부 호출하는 경우
>
> - 이 경우, 아래와 같은 방식으로 수행된다.
> - 가짜 객체의 일반 메서드 A 호출 -> 가짜객체는 실제 객체의 일반메서드 A 호출 -> 실제 객체의 일반 메서드A에서 @Transactional 메서드 AA 호출 -> @Transactional 적용 안됨.

<br>

예제로 사용할 BookService 클래스를 보자.<br>

> 마크다운의 표 편집 기능에 익숙치 않아서 UML 표기법에 맞춰서 작성하지는 못했다. (단순 표로 정리함)

| **BookService**  (클래스)  |                                |
| -------------------------- | ------------------------------ |
| **notTxSaveBook() 메서드** | 일반 메서드                    |
| **txSaveBook()**           | @Transactional 어노테이션 선언 |
| …                          |                                |

<br>

 BookService 클래스는 아래와 같이 `notTxSaveBook()` , `txSaveBok()` 이렇게 두개의 메서드를 가지고 있다. txSaveBook() 메서드의 경우 메서드의 선언시 @Transactional 어노테이션을 붙여둔 상태다.<br>

이때 notTxSaveBook() 메서드 내에서 txSaveBook() 메서드를 호출하고 있다고 해보자. 예를 들면 아래와 같이 코드를 작성해둔 상태다.<br>

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

이때, notTxSaveBook() 메서드를 호출할 때 스프링의 트랜잭션이 적용될까? 결론은 `No` 다.<br>

호출 구조를 그림으로 표현해보면 아래와 같은 구조다.<br>

![1](./img/TRANSACTIONAL-PROXY-NOT-TX-EXAMPLE.png)

<br>

검은색으로 표시한 `bookService$$CGLIB` 객체는 트랜잭션 프록시가 적용된 객체다. 프록시가 적용된 객체는 보통 뒤에 `CGLIB` 이라는 접미사가 붙는다. 파란색으로 표시된 `bookService` 객체는 실제 객체다. 프록시 객체는 스프링이 빈으로 등록해둔 객체이고, bookService의 메서드들을 호출할때는 이 프록시 객체를 경유해서 실제 객체의 로직들을 사용하게 된다. (자세한 내용은 위에서 정리해두었기에, 여기서는 패스.)<br>

<br>

**트랜잭션이 적용되지 않는 이유**<br>

프록시 객체는 실제 객체를 상속받아 만들어진 가짜객체다. 만들어진 목적은 실제 객체의 메서드를 호출 전/후에 트랜잭션의 시작/커밋/롤백을 처리하기 위한 것이 목적이다. 즉, 프록시 객체가 실제 객체의 구현부를 직접 가지고 있는 것이 아니다. 부모객체의 로직은 상속받은 메서드를 그대로 사용하고 처리의 전/후에 트랜잭션의 커밋/롤백 등을 적용하는 것이다.<br>

상속 시에 자식객체는 부모객체의 메서드를 물려받는데, 상속받은 메서드를 자식객체에서 오버라이드 하지 않으면, 부모객체의 메서드를 기본적으로 호출한다. 또 말로 설명해서 이상하다. 그냥 예제로 정리하면 간단해질것 같다.<br>

아래는 BaseBookService 클래스다. 부모 클래스다.

```java
package io.study.transactional_study.hierachy_test;

public class BaseBookService {
    public void printMessage(){
        System.out.println("실제객체에요~~~");
    }
}
```

<br>

아래는 자식 클래스다. 클래스 명은 ExtendedBookService 클래스다.

```java
package io.study.transactional_study.hierachy_test;

public class ExtendedBookService extends BaseBookService{
    
    public void bookServiceStart(){
        System.out.println("bookService 시작");
    }

    public void bookServiceEnd(){
        System.out.println("bookService 종료");
    }

    public void somethingTodo(){
        bookServiceStart();
        printMessage(); // 부모객체의 메서드
        bookServiceEnd();
    }

}
```

<br>

이제 이걸 테스트한번 해보자. 자식 클래스인 ExtendedBookService 클래스의 somethingTodo() 메서드를 호출할때 printMessage()가 부모객체에 선언한 그대로의 메서드가 호출되는지 보자. 아직까지는 오버라이딩을 하지 않았기 때문에 부모객체의 메서드가 호출되어야 한다.

```java
package io.study.transactional_study.hierachy_test;

import org.junit.jupiter.api.Test;

public class BookServiceTest {

    @Test
    public void 자식객체에서_간접적으로_부모객체의_메서드를_호출하는_예(){
        ExtendedBookService s1 = new ExtendedBookService();
        s1.somethingTodo();
    }
}
```

<br>

출력결과

```plain
bookService 시작
실제객체에요~~~
bookService 종료
```

<br>

부모객체의 메서드에서 출력하는 메시지인 "실제객체에요~~~" 라는 문구가 출력됐다. 이유는 자식 클래스에서 오버라이딩하지 않았기 때문이다.<br>

이 예제를 통해 알 수 있는 것은 트랜잭션 프록시 객체가 부모객체인 실제 객체의 메서드를 호출할 때, @Transactional 이 적용되지 않은 메서드는 별도의 처리를 하지 않았기에 실제 객체의 메서드를 호출한다. 이때 호출하는 실제 객체의 메서드 내에서 @Transactional 메서드를 호출하면 트랜잭션 프록시의 메서드가 호출되는 것이 아니라 실제 객체의 @Transactional 메서드가 호출될 뿐이다<br>

이때 실제 객체에는 아무런 트랜잭션 처리가 되어 있지 않기 때문에 @Transactional 어노테이션은 단순한 문자로서의 역할, 마커인터페이스일 뿐이다. (실제 객체 내에 존재하는 이 마커인터페이스를 찾아서 바인딩해주는 주체가 따로 없다.)<br>

<br>

**non-tx 메서드 -> tx 메서드 호출 케이스 검증**<br>

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

출력결과

위의 테스트 케이스 들 중에서 `notTxMethodCall()` 메서드를 호출했다고 해보자.

```plain
Not 트랜잭셔널 메서드 호출
tx active ?? false
트랜잭셔널 메서드 호출
tx active ?? false
```

<br>

**해결방법 - notTxSaveBook() 메서드에도 트랜잭션이 적용되게끔 하려면?**

아래 코드처럼 별도의 클래스로 분리해준다. 내부호출이 되지 않게끔하는 것이 목적이기에 다른 클래스의 메서드로 분리해뒀다. 이 외에도 실무에서 다양한 문제에 부딪히는데 이것과 관련해서는 글이 길어질것 같아 다른 문서에서 정리 예정이다.

```jsx
class BookService{
	// ...
	public void notTxSaveBook(){
		txBookService.txSaveBook();
	}
	// ...
}

class TxBookService{
    // txSaveBook 을 별도의 클래스로 분리해줬다.
	@Transactional
	public void txSaveBook(){
		// some logic
	}
}
```

<br>

## 트랜잭션 AOP 가 초기화되는 시점

- 애플리케이션 초기화 시점에 DB에서 어떤 데이터를 미리 로딩해와서 트랜잭셔널한 작업을 처리해야 하는 경우가 있다.
- 물론 내가 직접 경험한 케이스는 아니지만, 실무에서 충분히 경험할 수 있는 요구사항인 것 같다.
- 스프링에서 애플리케이션의 초기화 완료이벤트로 생각할 수 잇는 애플리케이션 이벤트로는 ApplicationReadyEvent, @PostConstruct 가 있다.
- 이 중 @PostConstruct 는 스프링 컨테이너의 로딩이 완료된 시점이 아니라, 의존성 주입이 완료된 후에 호출된다.
  - (참고: [PostConstruct (Java(TM) EE 7 Specification APIs)](https://docs.oracle.com/javaee/7/api/javax/annotation/PostConstruct.html))
- @ApplicationReadyEvent 는 스프링 컨테이너가 로딩된 시점에 호출된다.
  - (애플리케이션이 리퀘스트를 받을 준비가 되었을 때 ApplicationReadyEvent 가 발생한다. (참고: [ApplicationReadyEvent (Spring Boot 2.7.2 API)](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/event/ApplicationReadyEvent.html) ))

애플리케이션 로딩시에 트랜잭셔널한 작업을 할때에는, 가급적 `@ApplicationReadyEvent` 를 사용하는 것이 낫다.

<br>

**@PostConstruct 를 적용할 경우**

@PostConstruct 가 적용된 메서드는 스프링 컨테이너가 완전히 모두 로딩되지 않은 시점에 먼저 호출된다. 따라서 트랜잭션이 적용되지 않은 시점에 @PostConstruct 적용된 메서드가 호출된다.

```java
@SpringBootTest
public class SpringInitTest1{

	@PostConstruct
	@Transactional
	public void initTest(){
		boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
		log.info("Init Complete, txActive ?? {}", isActive);
	}
}
```

<br>

이 경우 로그를 보면 아래와 같이 출력된다.

```plain
Init Complete, txActive ?? false
```



**ApplicationReadyEvent 를 사용할 경우**

ApplicationReadyEvent 시점에는 트랜잭션이 초기화되는 것을 확인할 수 있다. ApplicationReadyEvent는 스프링 컨테이너가 완전히 로딩된 시점에 호출된다. 따라서 트랜잭션이 적용된 시점에 호출된다.

```jsx
@SpringBootTest
public class SpringInitTest2{

	@EventListener(value = ApplicationReadyEvent.class)
	@Transactional
	public void initTest(){
		boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
		log.info("Init Complete, txActive ?? {}", isActive);
	}
}
```

이 경우 로그를 보면 아래와 같이 출력된다.

```jsx
Init Complete, txActive ?? true
```

<br>

# Exception 발생시 스프링의 트랜잭션 커밋/롤백정책

드디어 대망의 트랜잭션 커밋/롤백 정책이다.<br>

<br>

## UncheckedException, CheckedException

JAVA의 Exception 은 아래의 두가지 종류가 있다.

- 언체크 예외(Unchecked Exception)
  - [RuntimeException (Java SE 9 & JDK 9 )](https://docs.oracle.com/javase/9/docs/api/java/lang/RuntimeException.html)
- 체크 예외(Checked Exception)
  - [Exception (Java Platform SE 8 )](https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/Exception.html)

<br>

스프링에서는 Unchecked Exception, Checked Exception 을 처리하는 나름의 기준을 세워두고 있다. <br>

<br>

## 시스템 예외, 비즈니스 예외

여러가지 기준이 있을 수 있겠지만, 시스템예외, 비즈니스 예외라는 프레임으로 예외들을 구분해보자.

- 시스템 예외
  - 네트워크 에러 와 같은 예외상황이 발생하는 경우를 의미한다.
- 비즈니스 예외
  - 예를 들면 주문시에 잔고가 부족해 결제에 실패하면 주문 데이터를 저장하고 결제 상태를 대기 상태로 표시하는 경우가 있다.
  - 이 경우 고객에게는 잔고 부족을 알리고, 결제를 다시 하도록 안내하는 등의 화면을 표시하고 알림 메일을 발송한다.

<br>

직접 트랜잭션 관련 코드를 유지보수하는 것이 아닌, 처음부터 개발을 시작할 경우 예외 코드를 작성할 때 

- 비즈니스 예외는 체크예외로 처리하는게 맞을까? 
- 시스템 예외는 언체크 예외로 두어 처리해야 할까? 
- 어떤 규칙으로 처리하는게 맞을까? 

이런 궁금증에 직면하게 될 것 같다.

<br>

이 경우 보통 아래의 기준으로 적용하게 된다.(물론 예외 케이스도 있을수 있다.)<br>

<br>

비즈니스 예외

- 비즈니스 예외의 의미가 있을 때 사용
- 체크예외로 취급
- 비즈니스 적으로 예외가 발생한 것은 Checked 되어야 한다는 의미인것 같다.
- 비즈니스 적으로 예외가 발생하는 것은 어떤 상태에서 어떤 이유로 예외가 발생되었는지 기록이 되어야 하기에 예외가 발생하더라도 커밋이 되는 Checked Exception 을 사용한다. (=체크를 한다는 의미)

시스템 예외

- 복구할 수 없는 예외는 커밋이 되어야 하지 말아야 한다.
- 예를 들면 네트워크 유실 등의 예외가 발생하면, 커밋이 발생하지 않는다.
- 언체크드 예외로 취급한다.

<br>

오늘은 이 부분들에 대해 알아보기 위해 테스트 코드를 기반으로 해당 내용들을 확인해본다.<br>

강사님은 테스트 코드까지 직접 작성할 수 있도록 떠먹여주시고 계신다. 실제 강의를 볼 수 있다면 꼭 보는 것을 추천.<br>

<br>

## 스프링 트랜잭션 AOP 객체의 커밋/롤백 원칙

언체크, 체크 예외에 대한 스프링의 커밋/롤백 원칙은 아래와 같다.

- 언체크 예외에 대한 커밋/롤백 원칙
  - 예외 발생시 트랜잭션을 롤백한다.
  - 복구가 불가능한 예외로 여긴다.
  - 복구가 불가능한 상황은 커밋이 될수 없어야 하기에 트랜잭션을 롤백한다.
  - ex) RuntimeException, Error
- 체크 예외에 대한 커밋/롤백 원칙
  - 예외 발생시 트랜잭션을 커밋한다.
  - 복구 가능한 예외로 여긴다.
  - 예외가 발생했을 때의 원인과, 당시의 상황을 기록할 수 있다.
  - ex) Exception.class

단, 체크예외를 사용하더라도 `@Transactional` 에 rollbackFor 옵션에 원하는 예외 클래스를 지정하면, 예외가 발생하면 롤백을 한다. (오버라이딩하는 느낌?)

<br>

## 테스트 시작) 로깅을 위해 적용하는 jpa 옵션

- 아래에서부터는 가급적이면 직접 테스트코드를 작성해봐야 한다. 

테스트 시에 트랜잭션의 롤백을 확인할때 아래의 로깅옵션을 통해서 롤백이 되었는지 아닌지를 확인할 수 있다.<br>

`application.properties`

```jsx
logging.level.org.springframework.transaction.interceptor=TRACE
logging.level.org.springframework.jdbc.datasource.DataSourceTransactionManager=DEBUG

# JPA 로그
logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG
# Hibernate 의 트랜잭션 관련 로그
logging.level.org.hibernate.resource.transaction=DEBUG
# SQL
logging.level.org.hibernate.SQL=DEBUG
```

<br>

그냥 문자만 멍하니 보다가 복붙하면, 각각의 속성을 왜 붙였는지 이유를 궁금해지지 않는다. 이 말을 적어두는 이유는, 나 역시도 제일 처음 프린트를 읽으면서 스터디를 할때 이 부분에 대해서 너무나 스무스하게 지나쳤었다. 아무 생각없이 글자만 읽고 지나쳤었다.<br>

<br>

- logging.level.org.springframework.transaction.interceptor
  - AOP가 적용된 스프링의 트랜잭션 프록시는 가급적이면 이 옵션에 대해 TRACE 로 해두면 동작을 확인하기에 유용하다.
- logging.level.org.springframework.jdbc.datasource.DataSourceTransactionManager
  - DataSourceTransactionManager 클래스는 기본적으로 JDBC 가 사용하는 트랜잭션 매니저 구현체 클래스다.  
    - 참고: [DataSourceTransactionManager (Spring Framework 5.3.22 API)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/datasource/DataSourceTransactionManager.html) 
  - 즉, JDBC의 트랜잭션 매니저를 로그에 찍히도록 켜둔 것.
- JpaTransactionManager
  - logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG
  - 스프링에서의 Jpa의 트랜잭션 매니저인 JpaTransactionManager 를 DEBUG 레벨로 켜둔다.
- hibernate
  - logging.level.org.hibernate.resource.transaction=DEBUG
  - logging.level.org.hibernate.SQL
  - hibernate 레벨의 로그를 켜두었다. 유의할 점은 transaction 역시도 켜두었다는 것.
  - 로그 레벨을 켜둘 패키지를 찾는 것도 꽤 번거로운 작업이었겠다 하고 생각했다.

<br>

## 예제 1) 언체크드 예외(Unchecked Exception) 의 커밋/롤백 확인 예제

```java
@SpringBootTest
public class RollbackTest1{

	@Autowired
	BookService bookService;

    // 
	@Test
	public void 언체크드_예외_테스트(){
		assertThatThrownBy(() -> bookService.throwUncheckedException())
			.isInstanceOf(RuntimeException.class);
	}

	@TestConfiguration
	static class InlineConfiguration(

		@Bean
		BookService bookService(){
			return new BookService();
		}
	}

	@Slf4j
	static class BookService{
		@Transactional
		public void throwUncheckedException(){
			log.info("런타임 예외 호출");
			throw new RuntimeException();
		}
	}
}
```

<br>

위의 예제에서 "언체크드\_예외\_테스트" 라고 적힌 테스트 케이스를 실행하는 결과는 아래와 같다. bookService 객체의 `throwUncheckedException()` 메서드를 호출하면, 롤백을 수행하는 것을 로그를 통해 확인할 수 있다.

```plain
...
런타임 예외 호출
...
Initiating transaction rollback
Rolling back JPA transaction on EntityManager
```

<br>

## 예제 2) 체크드(Checked) 예외의 커밋/롤백 확인 예제

```java
@SpringBootTest
public class RollbackTest2{

	@Autowired
	BookService bookService;

	@Test
	public void 체크드_예외_테스트(){
		assertThatThrownBy(() -> bookService.throwCheckedException())
			.isInstanceOf(Exception.class);
	}

	@TestConfiguration
	static class InlineConfiguration(

		@Bean
		BookService bookService(){
			return new BookService();
		}
	}

	@Slf4j
	static class BookService{
		@Transactional
		public void throwCheckedException(){
			log.info("체크드 예외 호출");
			throw new Exception();
		}
	}
}
```

<br>

위의 예제를 실행하는 결과는 아래와 같다. "체크드\_예외\_테스트" 라고 적힌 테스트 케이스를 실행하는 결과는 아래와 같다. 체크드 예외가 발생할 때에는 커밋이 그대로 수행되는 것을 확인할 수 있다.

```java
...
체크드 예외 호출
...
Initiating transaction commit
Committing JPA transaction on EntityManager
```

<br>

## 예제3) 체크드 예외여도 rollbackFor에 예외를 지정하면 롤백되는지 확인

```jsx
@SpringBootTest
public class RollbackTest3{

	@Autowired
	BookService bookService;

	@Test
	public void 롤백_테스트(){
		assertThatThrownBy(() -> bookService.rollbackForMethod())
			.isInstanceOf(JustException.class);
	}

	@TestConfiguration
	static class InlineConfiguration(

		@Bean
		BookService bookService(){
			return new BookService();
		}
	}

	static class JustException extends Exception{
	}

	@Slf4j
	static class BookService{
		@Transactional(rollbackFor = JustException.class)
		public void rollbackForMethod(){
			log.info("rollbackForMethod 메서드 호출");
			throw new Exception();
		}
	}
}
```

<br>

예제를 실행한 결과는 아래와 같다. 롤백을 잘 수행하는 것을 볼 수 있다.

```jsx
...
rollbackForMethod 메서드 호출
...
Initiating transaction rollback
Rolling back JPA transaction on EntityManager
```

<br>

## 예제4) 비즈니스 예외

도서 정보를 저장하는 메서드인 `saveBook(Book book)` 메서드를 등록하는 테스트를 통해서 비즈니스 예외를 정의하고 어떻게 처리 되는지 확인해보기 위한 예제다.<br>

<br>

**체크드 익셉션 정의**

```jsx
public class NoPriceInformationException extends Exception {
	public NoPriceInformationException(String message){
		super(message);
	}
}
```

<br>

**BookRegisterService**

```jsx
@Slf4j
@Service
@RequiredArgsConstructor
public class BookRegisterService{
	private final BookRepository bookRepository;

	@Transactional
	public void saveBook(Book book) throws NotEnoughMoneyException {
		log.info("saveBook 메서드");
		bookRepository.saveBook(book);

		log.info("도서 정보 저장 프로세스 진입");
		if(book.getPrice() == null){
			log.info("가격 정보가 없습니다.");
			book.setRegisterStatus("등록대기");
			throw new NoPriceInformationException();
		}
		if(book.getRegisterStatus("네트워크에러"){
			log.info("네트워크 에러"); 
			throw new RuntimeException("네트워크 에러");
		}
		// 그 외의 경우는 정상 처리
		log.info("정상");
		book.setRegisterStatus("정상등록 완료");

		log.info("도서 정보 저장 프로세스 완료");
	}
}
```

<br>

**BookServiceTest**

```jsx
@Slf4j
@SpringBootTest
public class BookServiceTest{
	@Autowired 
	BookRegisterService bookService;
	
	@Autowired
	BookRepository bookRepository;

	@Test
	public void 정상수행_테스트(){
		Book book = new Book();
		book.setPrice(BigDecimal.valueOf(21000));
		bookService.saveBook(book);

		Book savedBook = bookRepository.findById(book.getId()).get();
		assertThat(savedBook.getRegisterStatus().isEqualTo("정상등록 완료"));
	}

	@Test
	public void 가격정보_없을_경우_등록상태는_저장되어야하고_NoPriceInformationException_이_발생해야한다(){
		Book book = new Book("박지성", "멈추지 않는 도전");
		bookService.saveBook(book);

		Book savedBook = bookRepository.findById(book.getId()).get();
		assertThat(savedBook.getRegisterStatus().isEqualTo("등록대기");
	}

	@Test
	public void 네트워크에러일때는_RuntimeException이_발생해야하고_데이터저장프로세스는_롤백되어야한다(){
		Book book = new Book("박지성", "멈추지 않는 도전");
		book.setRegisterStatus("네트워크 에러");
		bookService.saveBook(book);

		Assertions.assertThatThrownBy(() -> bookService.saveBook(book))
			.isInstanceOf(RuntimeException.class);

		Optional<Book> savedBook = bookRepository.findById(book.getId()).get();
		assertThat(savedBook.isEmpty()).isTrue();
	}
}
```

<br>