# 1. QueryDsl 설정하기 (단일모듈)

많은 개발자분들이 맥북 교체시기가 되거나 이직을 하면서 그레이들 버전을 6.x 로 설치하면서 Querydsl 설정을 할 때 멘붕을 겪으시는 듯 하다. 여기저기 자료를 찾다보니 [허니몬님 블로그](http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html), [이동욱님 블로그](https://jojoldu.tistory.com/372)의 글이 보여서 글을 차례로 읽으면서 차이점이 뭔지 정리했다. 이 글의 초기 버전은 굉장히 산만했었는데, 몇달이 지나고 나니 철이 들어서인지는 모르겠지만, 굉장히 요약 스타일로 바꾸게 되었다. 여윽시 무슨 일을 하든 요약을 잘하는 게 중요한 것 같다.<br>

<br>

## 참고자료

- [Spring Boot Data Jpa 프로젝트에 QueryDsl 적용하기](https://jojoldu.tistory.com/372)
  - 이 자료를 천천히 쭈욱 읽다 보니 아래 자료(허니몬님 블로그)에 최신 버전으로 설정하는 방법에 대한 상세한 설명이 있다고 안내받음
- [그레이들 Annotation processor 와 Querydsl](http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html)
  - 바뀐 점은 아래와 같다.
    - 인텔리제이 2019.x 사용시 : 그레이들 플러그인 `com.ewerk.gradle.plugins.querydsl` 사용
    - 인텔리제이 2020.x 사용시 : 그레이들 `annotationProcessor` 사용

‌<br>

## 그레이들, 스프링부트 버전

스프링 부트 2.3 부터는 Gradle 6.3 이상의 버전이 필요하다. 이런 문제로 Gradle 6.3 이상으로 업그레이드 했을 때 `com.ewerk.gradle.plugins.querydsl` 을 사용할 경우 에러를 낸다.<br>

gradle 4.6 부터는 “Convenient declaration of annotation processor dependencies” 가 추가되었다. 무엇인가 하면 롬복에서도 자주 사용되는 어노테이션 프로세서 의존성의 선언 편의성이다. 어노테이션 기반 코드들에 대한 의존성들을 정의하기 편하게 해주는 기능인가 보다. 뭔지 자세히 알려고 하면 골치아프다. 그런것 같다.<br>

인텔리제이 2019 또는 Gradle 4.6 이하 버전 대에서는 querydsl 에 필요한 'Q클래스'를 생성할 때 `그레이들 플러그인`을 사용했다. 이 그레이들 플러그인은 `com.ewerk.gradle.plugins.querydsl` 라는 플러그인 이다. `com.ewerk.gradle.plugins.querydsl` 플러그인은 Q클래스를 생성할 때 `JPAAnotationProcessor`를 사용했는데, 이것이 인텔리제이 `2020.x 버전 or Gradle 6.x`에서 사용이 안된다. <br>

> 그레이들을 JPA 개발시 사용하는 이유
>
> - Q클래스를 생성하는 스크립트의 경우 groovy 기반의 스크립트 기반 빌드방식인 gradle 기반 빌드가 유연하고, 빌드와 배포에 용이함을 제공하기에 사용한다.<br>
> - 메이븐으로도 역시 Querydsl 개발을 할 수 있다. 다만 groovy 기반 gradle 스크립트 기반 빌드 방식이 유연하다는 장점이 있다.<br>

<br>

## 방법 (1) 

> 참고자료: [개발자 이동욱님 블로그](https://jojoldu.tistory.com/372)

위의 링크를 참고해서 고쳐본 build.gradle 스크립트의 전체 내용은 아래와 같다. (참고 : [build.gradle 소스 전문](https://github.com/gosgjung/study-querydsl-jpa/blob/develop/codes/Querydsl/querydsl-v1/build.gradle))

```groovy
plugins {
	id 'org.springframework.boot' version '2.5.1'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}


group = 'io.study.qdsl'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'


repositories {
	mavenCentral()
}


dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.postgresql:postgresql:42.2.21'
	implementation 'mysql:mysql-connector-java:8.0.24'


	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'


	// querydsl
	implementation 'com.querydsl:querydsl-core'
	implementation 'com.querydsl:querydsl-jpa'
	annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa" // querydsl JPAAnnotationProcessor 사용 지정
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")


	// test
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	runtimeOnly 'com.h2database:h2:1.4.200'


}


test {
	useJUnitPlatform()
}


def generated='src/main/generated'
sourceSets {
	main.java.srcDirs += [generated]
}


tasks.withType(JavaCompile){
	options.annotationProcessorGeneratedSourcesDirectory = file(generated)
}


clean.doLast {
	file(generated).deleteDir()
}
```

<br>

## 방법 (2)

> gradle 5.x 까지 사용하는 방식(=com.ewerk.gradle.plugins.querydsl)이 있고 <br>gradle 6.x에서는 새롭게 설정해줘야 하는 설정이 있다.<br>

- Annotation Processor 방식 설정 (gradle 6.x)
- com.ewerk.gradle.plugins.querydsl 플러그인 스크립트 변경 (gradle 5.x)
  - 단점은... 해당 플러그인을 제공하든 오픈 소스가 지금 업데이트되고 있지 않다고 한다.



### Annotation Processor 방식 설정

annotation processor 는 애노테이션들이 적용된 클래스들을 별도의 프로세서에서 처리하도록 해서 성능에 이점을 제공했다.(주의. 하드웨어의 프로세서를 의미하는 것은 아님. =라고 gradle 공식 영문 문서에서 농담을하고 있다.ㅋㅋ)<br>

무슨 내용인지는 조금 더 공부해서 정리해봐야 할 듯 하다.

```
configure(querydslProjects) {
    apply plugin: "io.spring.dependency-management"


    dependencies {
        compile("com.querydsl:querydsl-core")
        compile("com.querydsl:querydsl-jpa")


        annotationProcessor("com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jpa") // querydsl JPAAnnotationProcessor 사용 지정
        annotationProcessor("jakarta.persistence:jakarta.persistence-api") // java.lang.NoClassDefFoundError(javax.annotation.Entity) 발생 대응 
        annotationProcessor("jakarta.annotation:jakarta.annotation-api") // java.lang.NoClassDefFoundError (javax.annotation.Generated) 발생 대응 
    }


    // clean 태스크와 cleanGeneatedDir 태스크 중 취향에 따라서 선택하세요.
	/** clean 태스크 실행시 QClass 삭제 */
    clean {
        delete file('src/main/generated') // 인텔리제이 Annotation processor 생성물 생성위치
    }


	/**
     * 인텔리제이 Annotation processor 에 생성되는 'src/main/generated' 디렉터리 삭제
     */
    task cleanGeneatedDir(type: Delete) { // 인텔리제이 annotation processor 가 생성한 Q클래스가 clean 태스크로 삭제되는 게 불편하다면 둘 중에 하나를 선택 
        delete file('src/main/generated')
    }
}
```

<br>

### gradle plugin "com.ewerk.gradle.plugins.querydsl" 사용 설정 방식

잘 알려진 빌드스크립트였고, 나도 많이 복사해서 썼던 스크립트였다. 이 방식 역시 6.x 에서 동작하게끔 하는 방법이 있다. 아래 코드는 이 "com.ewerk.gradle.plugins.querydsl" 을 사용해서 6.x 에서 동작하도록 수정한 코드이다.<br>

**ex)**<br>

**그레이들 5.x 이전까지 흔히 사용하던** `**com.ewerk.gradle.plugins.querydsl**` **플러그인 사용 방식**

```groovy
apply plugin: "com.ewerk.gradle.plugins.querydsl"


def queryDslDir = "src/main/generated"
querydsl {
    library = "com.querydsl:querydsl-apt:4.2.2" // 사용할 AnnotationProcesoor 정의
    jpa = true
    querydslSourcesDir = queryDslDir
}
sourceSets {
    main {
        java {
            srcDir queryDslDir
        }
    }
}


compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}


configurations {
    /**
     * 손권남님이 공유해주신 팁 
     * 아래를 지정하지 않으면, compile 로 걸린 JPA 의존성에 접근하지 못한다.
     */
    querydsl.extendsFrom compileClasspath
}
```



그레이들 5.x 가 출시된 후에는 정상적으로 동작하지 않아 아래 부분이 추가되었다. querydsl-apt 라는 라이브러리 내의 AnnotationProcessor 의 경로(annotationProcessorPath) 를 수동으로 변경해줘야 한다.

```groovy
compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```
