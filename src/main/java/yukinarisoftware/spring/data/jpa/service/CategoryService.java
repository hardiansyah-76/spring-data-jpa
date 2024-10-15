package yukinarisoftware.spring.data.jpa.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yukinarisoftware.spring.data.jpa.entity.Category;
import yukinarisoftware.spring.data.jpa.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    //example using transactional
    @Transactional
    public void create (){
        for (int i = 0; i < 10; i++) {
            Category category = new Category();
            category.setName("example transactional");
            categoryRepository.save(category);
        }
        throw new RuntimeException("ups file is rollback");
    }

}
