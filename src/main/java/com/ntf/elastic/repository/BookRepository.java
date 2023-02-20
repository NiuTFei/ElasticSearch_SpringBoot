package com.ntf.elastic.repository;

import com.ntf.elastic.entity.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

    List<Book> findByNameAndPrice(String name, Integer price);

    List<Book> findByNameContaining(String name);

    List<Book> findByPriceLessThanEqual(Integer price);

}

