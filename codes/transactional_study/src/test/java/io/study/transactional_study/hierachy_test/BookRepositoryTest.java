package io.study.transactional_study.hierachy_test;

import org.junit.jupiter.api.Test;

public class BookRepositoryTest {

    @Test
    public void 자식객체에서_간접적으로_부모객체의_메서드를_호출하는_예(){
        ExtendedBookService s1 = new ExtendedBookService();
        s1.somethingTodo();
    }
}
