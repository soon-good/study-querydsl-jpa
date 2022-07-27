package io.study.transactional_study.commit_rollback.exception_translation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

@SpringBootTest
public class BookRepositoryExceptionTranslationTest1 {

    @Autowired
    BookRepository bookRepository;

    @Test
    public void 예외번역_테스트1_체크드예외를_언체크드예외로_번역하더라도_커밋이_필요하면_dontRollbackOn을_사용한다(){
        Assertions.assertThatThrownBy(()->bookRepository.exceptionTranslationWithCommit())
                .isInstanceOf(MyUncheckedException.class);
    }

    @Test
    public void 예외번역_테스트2_체크드예외를_언체크드예외로_번역하고_언체크드_예외로_번역되므로_rollback이_발생한다(){
        Assertions.assertThatThrownBy(()->bookRepository.exceptionTranslationWithRollback_WithAllException())
                .isInstanceOf(MyUncheckedException.class);
    }


    @TestConfiguration
    static class InlineConfiguration{
        @Bean
        BookRepository bookService(){
            return new BookRepository();
        }
    }

    @Slf4j
    static class BookRepository {
        @Transactional
        public void throwCheckedException() throws Exception{
            log.info("체크드 예외 발생~~~");
            throw new Exception();
        }

        @Transactional(dontRollbackOn = MyUncheckedException.class)
        public void exceptionTranslationWithCommit(){
            try{
                throw new Exception();
            } catch(Exception e){
                throw new MyUncheckedException(BookPaymentErrorCode.NOT_ENOUGH_MONEY, e);
            }
        }

        // Unchecked, Checked 익셉션에 상관없이 롤백을 진행
        @Transactional
        public void exceptionTranslationWithRollback_WithAllException(){
            try{
                throw new Exception();
            } catch(Exception e){
                throw new MyUncheckedException(BookPaymentErrorCode.NOT_ENOUGH_MONEY, e);
            }
        }
    }

    static class MyUncheckedException extends IllegalArgumentException{
        public MyUncheckedException(){}

        public MyUncheckedException(BookPaymentErrorCode errorCode){
            super(errorCode.getMessage());
        }

        public MyUncheckedException(BookPaymentErrorCode errorCode, Throwable cause){
            super(errorCode.getMessage(), cause);
        }

        public MyUncheckedException(Throwable cause){
            super(cause);
        }
    }

    @Getter
    enum BookPaymentErrorCode{
        NOT_ENOUGH_MONEY("잔고부족");

        private final String message;

        BookPaymentErrorCode(String message){
            this.message = message;
        }
    }
}
