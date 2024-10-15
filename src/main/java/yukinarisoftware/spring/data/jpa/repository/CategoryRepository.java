package yukinarisoftware.spring.data.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import yukinarisoftware.spring.data.jpa.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    //find first by name equals
    Optional<Category> findFirstByNameEquals(String name);

    //find all
    List<Category> findAllByNameLike (String name);


}
