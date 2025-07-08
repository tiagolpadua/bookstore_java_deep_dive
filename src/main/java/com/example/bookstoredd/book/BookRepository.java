package com.example.bookstoredd.book;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

  /**
   * Find books by title containing the given text (case-insensitive)
   *
   * @param title the title to search for
   * @return list of books with matching titles
   */
  @Query(
      "SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) ORDER BY b.id")
  List<Book> findByTitleContainingIgnoreCase(@Param("title") String title);

  /**
   * Find books by author containing the given text (case-insensitive)
   *
   * @param author the author to search for
   * @return list of books with matching authors
   */
  @Query(
      "SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')) ORDER BY b.id")
  List<Book> findByAuthorContainingIgnoreCase(@Param("author") String author);

  /**
   * Find all books ordered by ID
   *
   * @return list of all books ordered by ID
   */
  @Query("SELECT b FROM Book b ORDER BY b.id")
  List<Book> findAllOrderedById();
}
