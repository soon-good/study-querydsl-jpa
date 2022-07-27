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
