# Transactional 의 각 옵션들

@Transactional 어노테이션 코드는 아래와 같다.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	@AliasFor("transactionManager")
	String value() default "";

	@AliasFor("value")
	String transactionManager() default "";

	String[] label() default {};

	Propagation propagation() default Propagation.REQUIRED;

	Isolation isolation() default Isolation.DEFAULT;

	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	String timeoutString() default "";

	boolean readOnly() default false;

	Class<? extends Throwable>[] rollbackFor() default {};

	String[] rollbackForClassName() default {};

	Class<? extends Throwable>[] noRollbackFor() default {};

	String[] noRollbackForClassName() default {};
}
```

<br>

## 트랜잭션 매니저 별도 지정

Bean에 기본적으로 등록한 트랜잭션 매니저를 사용할 경우 이 옵션을 설정하는 것을 건너뛰어도 된다.<br>

만약 JPA, JDBC, Mybatis 등 여러가지 데이터 소스를 사용하고, 트랜잭션 매니저도 각각 다르다면, 트랜잭션 매니저를 별도로 지정해줘야 한다.<br>

value, transactionManager 필드에 트랜잭션 매니저의 빈 이름을 지정해줘야 한다.<br>

아래는 간단한 예제다.

<br>

```java
@Component
public class SomeComponent{
    @Transactional("bookTransactionManager")
    public void saveBook(){
        // ...
    }
    
    @Transactional(transactionManager = "commentTransactionManager")
    public void saveComment(){
        // ...
    }
}
```

<br>

## 롤백

스프링은 언체크 예외 발생시 트랜잭션 롤백을 한다. 체크 예외 발생시에는 트랜잭션을 롤백하지 않고 커밋한다.<br>

그런데 이 원칙에 대해 예외를 둘수도 있다. 예를 들면 체크드 익셉션에 대해 rollback 을 하게 하거나, 언체크드 익셉션에 대해 롤백을 하지 않도록 하는 등의 옵션을 지정하는 경우가 있다.

<br>

### rollbackFor - 특정 예외타입(클래스)에 대해 rollback 하도록 지정

- 체크드 익셉션에 대해 롤백을 하려면 아래와 같이 `rollbackFor` 속성에 예외타입을 지정해준다.

```java
@Transactional(rollbackFor = Exception.class)
```

<br>

### noRollbackFor - 특정 예외타입(클래스)에 대해 rollback 하지 않도록 지정

- `MyHelloPrintException` 이라는 클래스를 RutimeException 클래스를 상속해 만들어두었다고 해보자.
- 이 경우 `MyHelloPrintException` 클래스는 언체크드 익셉션이다.
- 이 `MyHelloPrintException` 예외가 발생했을 때 트랜잭션 롤백이 되게끔 하려면 noRollbackFor 에 `MyHelloPrintException` 타입을 지정해주면 된다.

```java
@Transactional(noRallbackFor = MyHelloPrintException.class)
```

<br>

## isolation

트랜잭션 격리수준이다. 기본값은 DBMS에서 채택하는 기본적인 트랜잭션 격리수준(`DEFAULT`)을 사용하도록 되어있다. 경험상 @Transactional 에 isolation 레벨을 직접 지정해서 수정하는 코드는 설계자체가 잘못됐던 코드였다.<br>

자세한 설명은 어느 문서에 정리를 해두었는데, 해당 문서의 링크를 찾으면 여기에 링크로 대체해둘 예정.

- `DEFAULT` : 
  - 데이터베이스에서 설정하는 격리수준에 맞춰서 따라감 
  - DBMS에 설정된 기본 설정값 그대로 그냥 따라감
- `READ_UNCOMMITTED` 
- `READ_COMMITTED`
- `REPEATABLE_READ`
- `SERIALIZABLE` 

<br>

## timeout

트랜잭션 수행 시간에 대한 타임아웃을 지정. 특정 시간동안 트랜잭션에 지정한 작업이 완료되지 않으면 종료되도록 지정.<br>

운영환경에 따라 동작하는 경우도 있고 그렇지 않은 경우도 있다. 따라서 직접 확인후 사용필요.<br>

<br>

## readOnly

@Transactional 선언시 `readOnly = true` 을 주면 읽기 전용 트랜잭션 내에서 해당 메서드가 실행된다.<br>

이렇게 선언하면 읽기 전용이 되기에 수정/삭제가 되지 않는다. 읽기 기능만 동작한다.<br>

드라이버, 데이터베이스에 따라 정상동작하지 않는 경우 역시 존재.<br>

<br>

