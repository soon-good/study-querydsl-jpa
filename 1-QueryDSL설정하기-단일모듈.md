# 1. QueryDsl 설정하기 (단일모듈)

강의를 보고 따라한 적은 있었고, 어드민 프로젝트를 하면서는 전임자가 해놓았던 querydsl 설정을 그대로 사용만 했었다. 그런데 프로젝트를 처음부터 다시하는 입장이 되니, gradle 설정을 알아야 겠다는 생각이... 들었다.(설정방식이 바뀐것도 많다는 것도 알게되었다.) 그래서 정리를 시작.<br>

혼자서 적용해볼 때는 querydsl 이 Intellij 2020 이후 버전에서도 적용되도록 수정했다. 2020 이후 버전에서 예전버전의 querydsl 스크립트가 동작하지 않았던 원인은 아마도 intellij 2020 이후 버전에서는 인텔리제이 내장 gradle 플러그인이 6.x 이상을 사용하도록 되어 있어서인 듯 해보인다.<br>

<br>

## 참고자료

- [Spring Boot Data Jpa 프로젝트에 QueryDsl 적용하기](https://jojoldu.tistory.com/372)
  - 이 자료를 천천히 쭈욱 읽다 보니 아래 자료(허니몬님 블로그)에 최신 버전으로 설정하는 방법에 대한 상세한 설명이 있다고 안내받음
- [그레이들 Annotation processor 와 Querydsl](http://honeymon.io/tech/2020/07/09/gradle-annotation-processor-with-querydsl.html)
  - 바뀐 점은 아래와 같다.
    - 인텔리제이 2019.x 사용시 : 그레이들 플러그인 `com.ewerk.gradle.plugins.querydsl` 사용
    - 인텔리제이 2020.x 사용시 : 그레이들 `annotationProcessor` 사용

<br>

## 그레이들, 스프링부트 버전

스프링 부트 2.3 부터는 Gradle 6.3 이상의 버전이 필요하다. 이런 문제로 Gradle 6.3 이상으로 업그레이드 했을 때 `com.ewerk.gradle.plugins.querydsl` 을 사용할 경우 에러를 낸다.

gradle 4.6 부터는 “Convenient declaration of annotation processor dependencies” 가 추가되었다. 무엇인가 하면 롬복에서도 자주 사용되는 어노테이션 프로세서 의존성의 선언 편의성이다. 어노테이션 기반 코드들에 대한 의존성들을 정의하기 편하게 해주는 기능인가 보다. 뭔지 자세히 알려고 하면 골치아프다. 그런것 같다.<br>

인텔리제이 2019 또는 Gradle 4.6 이하 버전 대에서는 querydsl 에 필요한 'Q클래스'를 생성할 때 `그레이들 플러그인` 을 사용했다. 이 그레이들 플러그인은 `com.ewerk.gradle.plugins.querydsl` 라는 플러그인 이다.  `com.ewerk.gradle.plugins.querydsl` 플러그인은 Q클래스를 생성할 때 `JPAAnotationProcessor`를 사용했는데, 이것이 인텔리제이 `2020.x 버전 or Gradle 6.x`에서 사용이 안된다. <br>

> 그레이들을 JPA 개발시 사용하는 이유는, Q클래스를 생성하는 스크립트의 경우 groovy 기반의 스크립트 기반 빌드방식인 gradle 기반 빌드가 유연하고, 빌드와 배포에 용이함을 제공하기에 사용한다. 메이븐으로 하기에는 불편해서 사용...ㅋㅋ

<br>

추후 정리 시작... 시간이 너무 없다.



## build.gradle (1)

아래 build.gradle 파일의 내용은 [개발자 이동욱님 블로그](https://jojoldu.tistory.com/372)에서 설명하고 있는 build.gradle 설정이다.

```
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
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
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

이 외에도 honeymon.io 에서 설명하고 있는 방식 역시도 파악을 해보려 하는데, 해당 내용은 아래에 정리할 예정이다.

## 두가지 방법들 (honeymon.io)

- Annotation Processor 방식 설정
- com.ewerk.gradle.plugins.querydsl 플러그인 스크립트 변경
  - 단점은... 해당 플러그인을 제공하든 오픈 소스가 지금 업데이트되고 있지 않다고 한다.

<br>

### Annotation Processor 방식 설정

annotation processor 는 애노테이션들이 적용된 클래스들을 별도의 프로세서에서 처리하도록 해서 성능에 이점을 제공했다.(주의. 하드웨어의 프로세서를 의미하는 것은 아님.)<br>

무슨 내용인지는 조금 더 공부해서 정리해봐야 할 듯 하다.

```groovy
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

**!TODO**<br>

<br>



### gradle plugin "com.ewerk.gradle.plugins.querydsl" 사용 설정 방식

잘 알려진 빌드스크립트였고, 나도 많이 복사해서 썼던 스크립트였다. 이 방식 역시 6.x 에서 동작하게끔 하는 방법이 있다. 아래 코드는 6.x 에서 동작하도록 수정한 코드이다. 

**그레이들 5.x 이전까지 흔히 사용하던 `com.ewerk.gradle.plugins.querydsl` 플러그인 사용 방식.(수정된 버전)**

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

<br>

그레이들 5.x 가 출시된 후에는 정상적으로 동작하지 않아 아래 부분이 추가되었다. querydsl-apt 라는 라이브러리 내의 AnnotationProcessor 의 경로(annotationProcessorPath) 를 수동으로 변경해줘야 한다.

```groovy
compileQuerydsl {
    options.annotationProcessorPath = configurations.querydsl
}
```

<br>

**위의 방식을 6.x 에서 작동되도록 하기**<br>

위의 방식을 6.x 에서 잘 동작하게끔 하려면 아래의 내용을 추가해주면 된다고 한다.

```groovy
configurations {
    /**
     * 손권남님이 공유해주신 팁 
     * 아래를 지정하지 않으면, compile 로 걸린 JPA 의존성에 접근하지 못한다.
     */
    querydsl.extendsFrom compileClasspath
}
```



