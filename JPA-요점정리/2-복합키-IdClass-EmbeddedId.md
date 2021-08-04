# 2. 복합키 - IdClass / EmbeddedId

오늘은 일단 IdClass만 정리 시작

<br>

## 참고자료

- [자바 ORM 표준 JPA 프로그래밍 - 7.3. 복합 키와 식별관계 매핑](http://www.yes24.com/Product/Goods/19040233)
- [복합 키와 식별관계 매핑](https://webcoding-start.tistory.com/25)
- [JPA 퀵스타트](http://www.yes24.com/Product/Goods/92287236)

<br>

## 개념 요약

`@Id` 는 하나의 엔티티 클래스에 하나만 있어야 하는데, 복합키를 사용하는 경우는  `@Id` 클래스가 두 개 이상 있어야 한다. 이런 경우에 `@IdClass` 또는 `@EmbeddedId` 를 사용한다.<br>

하하ㅏㅎ.... 내일정리해야겠다. 오늘 힘들당...<br>

<br>

## @IdClass

### 개념 SUMMARY



### 예제

#### Employee.java

```java
@Data
@Entity
@SequenceGenerator(
    name = "employee_sequence",
    schema = "public", sequenceName = "EMP_SEQ",
    initialValue = 1, allocationSize = 1
)
@Table(name = "EMP", schema = "public")
@IdClass(EmployeeId.class)
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_sequence")
    @Column(name = "EMPLOYEE_ID")
    private Long id;

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "EMPLOYEE_NAME")
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPT_ID")
    private Department dept;

    public Employee(){}

    public Employee(String name, Department dept){
        this.name = name;
        this.dept = dept;
    }
}
```

<br>

#### EmployeeId.java

```java
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class EmployeeId implements Serializable {
	private Long id;
	private String email;

	public EmployeeId(){}
}
```

<br>

#### IdClassTest.java

```java
@SpringBootTest
@Transactional
@Commit
public class IdClassTest {

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("식별자_테스트테스트")
	public void 식별자_테스트테스트() throws Exception{
		Employee employee = new Employee();
		employee.setEmail("helloworld@naver.com");
		employee.setName("안뇽");

		Department dept = new Department("소방서");
		employee.setDept(dept);

		em.persist(dept);
		em.persist(employee);

		EmployeeId empId = EmployeeId.builder().id(1L).email("helloworld@naver.com").build();
		em.flush();

		Employee employee1 = em.find(Employee.class, empId);
		System.out.println(employee1);
	}
}
```



#### 출력결과

```plain
Hibernate: 
    select
        employee0_.email as email1_1_0_,
        employee0_.employee_id as employee2_1_0_,
        employee0_.dept_id as dept_id4_1_0_,
        employee0_.employee_name as employee3_1_0_ 
    from
        public.emp employee0_ 
    where
        employee0_.email=? 
        and employee0_.employee_id=?
Hibernate: 
    select
        department0_.dept_id as dept_id1_0_0_,
        department0_.dept_name as dept_nam2_0_0_ 
    from
        public.dept department0_ 
    where
        department0_.dept_id=?
Employee(id=1, email=helloworld@naver.com, name=안뇽, dept=Department(id=1, deptName=소방서))
```

<br>

## @EmbeddedId















