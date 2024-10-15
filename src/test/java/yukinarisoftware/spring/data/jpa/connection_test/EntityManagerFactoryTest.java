package yukinarisoftware.spring.data.jpa.connection_test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EntityManagerFactoryTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void test() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Assertions.assertNotNull(entityManager);
    }
}
