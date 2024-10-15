package yukinarisoftware.spring.data.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionOperations;
import yukinarisoftware.spring.data.jpa.entity.Category;
import yukinarisoftware.spring.data.jpa.entity.Product;
import yukinarisoftware.spring.data.jpa.model.ProductPrice;
import yukinarisoftware.spring.data.jpa.model.SimpleProduct;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionOperations transactionOperations;

    @Test
    void testInsert() {

        Category category = new Category();
        category.setName("software");

        categoryRepository.save(category);

        assertNotNull(category.getId());
    }

    @Test
    void testUpdate() {
        Category category = categoryRepository.findById(1L).orElse(null);

        category.setName("operating system");
        categoryRepository.save(category);

        category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);
        assertEquals("operating system", category.getName());
    }

    @Test
    void testQueryMethod() {
        Category category = categoryRepository.findFirstByNameEquals("operating system").orElse(null);
        assertNotNull(category);
        assertEquals("operating system", category.getName());

        List<Category> categoryList = categoryRepository.findAllByNameLike("%operating system%");
        assertNotNull(categoryList);
        assertEquals("operating system", categoryList.get(0).getName());
    }


    @Test
    void createProduct() {
        Category category = categoryRepository.findById(1L).orElse(null);
        assertNotNull(category);

        //create product
        {
            Product product = new Product();
            product.setName("windows 10");
            product.setPrice(3_000_000L);
            product.setCategory(category);
            productRepository.save(product);

        }

        {
            Product product = new Product();
            product.setName("windows 11");
            product.setPrice(4_000_000L);
            product.setCategory(category);
            productRepository.save(product);

        }
    }

    @Test
    void testFindProduct() {
        List<Product> products = productRepository.findAllByCategory_Name("operating system");
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("windows 10", products.get(0).getName());
        assertEquals("windows 11", products.get(1).getName());
    }

    @Test
    void testFindProductSort() {
        Sort sort = Sort.by(Sort.Order.desc("id"));

        List<Product> products = productRepository.findAllByCategory_Name("operating system", sort);
        assertNotNull(products);
        assertEquals(2, products.size());

        assertEquals("windows 10", products.get(1).getName());
        assertEquals("windows 11", products.get(0).getName());
    }

    @Test
    void pagingTest() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));
        Page<Product> products = productRepository.findAllByCategory_Name("operating system", pageable);

        assertNotNull(products);
        assertEquals(1, products.getContent().size());
        assertEquals(0, products.getNumber());
        assertEquals(2, products.getTotalPages());
        assertEquals(2, products.getTotalElements());

    }

    @Test
    void countTest() {
        Long count = productRepository.count();
        assertEquals(2L, count);

        count = productRepository.countByCategory_Name("operating system");
        assertEquals(2L, count);
    }

    @Test
    void existTest() {
        boolean exist = productRepository.existsByName("windows 10");
        assertTrue(exist);

        exist = productRepository.existsByName("ubuntu linux");
        assertFalse(exist);
    }

    @Test
    void testDelete() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            int delete = productRepository.deleteByName("ubuntu linux");
            assertEquals(1, delete);
        });
    }

    @Test
    void testQueryAnnotation() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Order.desc("id")));

        Page<Product> products = productRepository.searchProduct("%windows%", pageable);
        assertEquals(1, products.getContent().size());

        Page<Product> category = productRepository.searchProduct("%operating system%", pageable);
        assertEquals(1, category.getContent().size());
    }

    @Test
    void modifyingTest() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            int total = productRepository.updateProductPriceToZero(1L);
            assertEquals(1, total);

            Product product = productRepository.findById(1L).orElse(null);
            assertNotNull(product);
            assertEquals(0L, product.getPrice());
        });
    }

    @Test
    void modifyingTest2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);

            Product product = new Product();
            product.setName("macos");
            product.setPrice(20_000_000L);
            product.setCategory(category);
            productRepository.save(product);

            int deleted = productRepository.deleteProductUsingName("macos");
            assertEquals(1, deleted);
        });
    }

    @Test
    void streamTest() {
        transactionOperations.executeWithoutResult(transactionStatus -> {
            Category category = categoryRepository.findById(1L).orElse(null);
            assertNotNull(category);

            Stream<Product> productStream = productRepository.streamAllByCategory(category);
            productStream.forEach(result -> System.out.println(result.getId() + " : " + result.getName()));
        });
    }

    @Test
    void testSlice() {
        Pageable pageable = PageRequest.of(0, 1);

        Category category = categoryRepository.findById(1L).orElse(null);

        Slice<Product> products = productRepository.findAllByCategory(category, pageable);
        //do anything here

        while (products.hasNext()) {
            products = productRepository.findAllByCategory(category, products.nextPageable());
            //do anything here
        }
    }

    @Test
    void lockExample1() {
        transactionOperations.executeWithoutResult(transactionStatus -> {

            try {
                Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
                assertNotNull(product);
                product.setPrice(2000L);
                Thread.sleep(20_000L);
                productRepository.save(product);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
    }

    @Test
    void lockExample2() {
        transactionOperations.executeWithoutResult(transactionStatus -> {

            Product product = productRepository.findFirstByIdEquals(1L).orElse(null);
            assertNotNull(product);
            product.setPrice(2_000_000L);
            productRepository.save(product);

        });
    }

    @Test
    void addAuditingDateTime() {
        Category category = new Category();
        category.setName("software");
        categoryRepository.save(category);

        assertNotNull(category);
        assertNotNull(category.getCreatedDate());
        assertNotNull(category.getLastModifiedDate());
    }

    //using query data jpa by Example
    @Test
    void example1() {
        Category category = new Category();
        category.setName("operating system");

        Example<Category> example = Example.of(category);
        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void example2() {
        Category category = new Category();
        category.setId(1L);
        Example<Category> example = Example.of(category);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1 ,categories.size());
    }

    @Test
    void exampleMatcher() {
        Category category = new Category();
        category.setName("operating system");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreCase();
        Example<Category> example = Example.of(category, exampleMatcher);

        List<Category> categories = categoryRepository.findAll(example);
        assertEquals(1, categories.size());
    }

    @Test
    void specificationTest() {
        Specification<Product> specification = (root, criteria, builder) -> {
            return criteria.where(
                    builder.or(
                            builder.equal(root.get("name"), "windows 10"),
                            builder.equal(root.get("name"), "windows 11")
                    )
            ).getRestriction();


        };

        List<Product> products = productRepository.findAll(specification);
        assertEquals(2, products.size());
    }


    @Test
    void testProjection() {

        List<SimpleProduct> products = productRepository.findAllByNameLike("%windows%", SimpleProduct.class);
        assertEquals(2, products.size());

        List<ProductPrice> productPrices = productRepository.findAllByNameLike("%windows%", ProductPrice.class);
        assertEquals(2, productPrices.size());
    }
}
