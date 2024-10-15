package yukinarisoftware.spring.data.jpa.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import yukinarisoftware.spring.data.jpa.entity.Category;
import yukinarisoftware.spring.data.jpa.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    //projection feature
    // List<SimpleProduct> findAllByNameLike(String name); // old version
    // this is new version, so as not to create many classes
    <T> List<T> findAllByNameLike(String name, Class<T> tClass);

    //locking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findFirstByIdEquals (Long id);

    //slice, for return information if next exist and previous page exist
    Slice<Product> findAllByCategory (Category category, Pageable pageable);

    //find all product using stream, stream better than List<T>, because not Out of memory
    Stream<Product> streamAllByCategory (Category category);

    //modifying annotation, for to tell the system that this is not a select query
    @Modifying
    @Query(value = "update Product p set p.price = 0 where p.id = :id")
    int updateProductPriceToZero (@Param("id") Long id);

    @Modifying
    @Query(value = "delete from Product p where p.name = :name")
    int deleteProductUsingName (@Param("name") String name);

    //query annotation
    @Query(
            value = "select p from Product p where p.name like :name or p.category.name like :name"
    )
    Page<Product> searchProduct(@Param("name") String name, Pageable pageable);

    //delete query
    int deleteByName (String name);

    /*query relations
     * find all by category name*/
    List<Product> findAllByCategory_Name (String name);

    //sorting
    List<Product> findAllByCategory_Name (String name, Sort sort);

    //paging
    Page<Product> findAllByCategory_Name (String name, Pageable pageable);

    //count query
    Long countByCategory_Name (String name);

    //check existBy
    boolean existsByName (String name);

}
