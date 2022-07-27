package io.study.transactional_study.commit_rollback.exception_translation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootTest
public class BookServiceExceptionTranslationTest1 {

    @Autowired
    BookService bookService;

    @Test
    public void TEST1_별도메서드에서_처리할때(){
        Assertions.assertThatThrownBy(()->TEST1())
                .isInstanceOf(IllegalArgumentException.class);
    }

    public void TEST1(){
        try{
            bookService.throwCheckedException();
        } catch(Exception e){
            throw new IllegalArgumentException();
        }
    }

    @Test
    public void TEST2_

    @TestConfiguration
    static class InlineConfiguration{
        @Bean
        BookService bookService(){
            return new BookService();
        }
    }

    @Slf4j
    static class BookService{
        @Transactional
        public void throwCheckedException() throws Exception{
            log.info("체크드 예외 발생~~~");
            throw new Exception();
        }
    }
}
