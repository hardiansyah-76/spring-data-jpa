package yukinarisoftware.spring.data.jpa.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;


    @Test
    void testSuccessThrowableTransaction() {
       assertThrows(RuntimeException.class, () -> {
            categoryService.create();
       });
    }


}