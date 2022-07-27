package io.study.transactional_study.commit_rollback.rollbackfor_test;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootTest
public class BookRepositoryCheckedExceptionRollbackOnTest1 {

    @Autowired
    BookService bookService;

    @Test
    public void 체크드_예외_메서드_호출시_rollbackOn에_등록한_타입에_대해서는_체크드예외_이더라도_트랜잭션_롤백을_해야한다(){
        Assertions.assertThatThrownBy(() -> bookService.rollbackOnMethod())
                .isInstanceOf(JustException.class);
    }

    @TestConfiguration
    static class InlineConfiguration{
        @Bean
        public BookService bookService(){
            return new BookService();
        }
    }

    static class JustException extends Exception{

    }

    @Slf4j
    static class BookService{
        @Transactional(rollbackOn = JustException.class)
        public void rollbackOnMethod() throws Exception{
            log.info("rollbackOnMethod 메서드 호출");
            throw new JustException();
        }
    }
}
