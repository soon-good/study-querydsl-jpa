package io.study.transactional_study.commit_rollback.checked_exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootTest
public class BookRepositoryCheckedExceptionTest1 {

    @Autowired
    BookService bookService;

    @Test
    public void 체크드_예외_메서드_호출시_커밋되는지_테스트(){
        Assertions.assertThatThrownBy(()->bookService.throwCheckedException())
                .isInstanceOf(Exception.class);
    }


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
            log.info("체크드 예외 호출");
            throw new Exception();
        }
    }
}
