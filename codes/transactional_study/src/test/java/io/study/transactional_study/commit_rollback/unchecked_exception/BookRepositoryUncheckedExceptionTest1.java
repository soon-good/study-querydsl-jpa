package io.study.transactional_study.commit_rollback.unchecked_exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootTest
public class BookRepositoryUncheckedExceptionTest1 {

    @Autowired
    BookService bookService;

    @Test
    public void 언체크드_예외_발생시_트랜잭션은_롤백된다(){
        Assertions.assertThatThrownBy(() -> bookService.throwUncheckedException())
                .isInstanceOf(RuntimeException.class);
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
        public void throwUncheckedException(){
            log.info("언체크드 익셉션(RuntimeException) throw 하겠음");
            throw new RuntimeException();
        }
    }
}
