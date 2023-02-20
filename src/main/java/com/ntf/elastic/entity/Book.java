package com.ntf.elastic.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName="books")
@NoArgsConstructor
public class Book {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer price;

    public Book(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
