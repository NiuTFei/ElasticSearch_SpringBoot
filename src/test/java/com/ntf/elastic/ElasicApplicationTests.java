package com.ntf.elastic;

import com.ntf.elastic.entity.Book;
import com.ntf.elastic.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class ElasicApplicationTests {

    /**
     * ElasticsearchOperations使用
     */
    @Autowired
    ElasticsearchOperations operations;

    @Test
    void testOperations() {
        Book book = new Book();
//        book.setId("4");
        book.setName("平凡的世界");
        book.setPrice(150);
        Book save = operations.save(book);
        System.out.println(save);
    }

    @Test
    void testOperationsQuery() {
        Criteria criteria = new Criteria("price").is(50);
        Query query = new CriteriaQuery(criteria);
        SearchHits<Book> hits = operations.search(query, Book.class);
        for (SearchHit<Book> hit : hits) {
            System.out.println(hit);
        }

    }

    /**
     * ElasticsearchRepository使用
     */
    @Autowired
    BookRepository repository;

    @Test
    void contextLoads() {
        Book book = new Book();
        book.setName("活着");
        book.setPrice(30);
        repository.save(book);

        List<Book> books = new ArrayList<>();
        books.add(new Book("追风筝的人",35));
        books.add(new Book("红楼梦",99));
        books.add(new Book("相对论",200));
        repository.saveAll(books);
    }

    @Test
    void testQuery(){
//        List<Book> books = repository.findByNameAndPrice("活着", 50);
//        List<Book> books = repository.findByNameContaining("世");

        List<Book> books = repository.findByPriceLessThanEqual(100);

        System.out.println(books);
    }



}
