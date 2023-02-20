# Spring Boot集成Elasticsearch8.6.x

## IK分词器的安装与使用

### 安装

下载地址：https://github.com/medcl/elasticsearch-analysis-ik

注意：**版本必须与es版本对应**

下载完后在plugins文件夹下新建文件夹ik，将下载到的安装包解压到此处

### 配置

ik分词器目录下config文件夹中配置文件`IKAnalyzer.cfg.xml`如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
	<comment>IK Analyzer 扩展配置</comment>
	<!--用户可以在这里配置自己的扩展字典 -->
	<entry key="ext_dict">my.dic</entry>
	 <!--用户可以在这里配置自己的扩展停止词字典-->
	<entry key="ext_stopwords"></entry>
	<!--用户可以在这里配置远程扩展字典 -->
	<!-- <entry key="remote_ext_dict">words_location</entry> -->
	<!--用户可以在这里配置远程扩展停止词字典-->
	<!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>
```

config 文件中可以自定义xxx.dic字典，添加词组，配置到`<entry key="ext_dict">xxx.dic</entry>`标签中

**es重启后生效**

### 使用

es文档映射中为属性添加“analyzer”属性

ik分词器的analyzer分为`ik_master`和`ik_max_word`，区别如下：

- `ik_smart` 模式是智能分词，它能够根据上下文自动进行分词，准确性比较高，适用于**搜索引擎、信息检索**等场景。
- `ik_max_word` 模式是细粒度切分，会将文本尽可能多地切分成单词，适用于对精确度要求较高的场景，如**自然语言处理、文本分类**等。

## SpringBoot配置并使用

### Maven依赖

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### yml配置

```yml
spring:
  elasticsearch:
    uris: http://10.181.33.239:9200
```

### 使用

官方文档：https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/

官方提供了`Elasticsearch Operations`与`Elasticsearch Repositories`两种操作es的方法

#### Elasticsearch Object Mapping

```java
@Data
@Document(indexName="books")	//文档index
@NoArgsConstructor
public class Book {
    @Id
    private String id;	//文档ID

    @Field(type = FieldType.Text, analyzer = "ik_smart")	//文档属性，可以在此指定分词器
    private String name;

    @Field(type = FieldType.Integer)
    private Integer price;

    public Book(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}
```

实现的映射如下：

```json
{
  "books": {
    "mappings": {
      "properties": {
        "_class": {
          "type": "keyword",
          "index": false,
          "doc_values": false
        },
        "name": {
          "type": "text",
          "analyzer": "ik_smart"
        },
        "price": {
          "type": "integer"
        }
      }
    }
  }
}
```



#### Elasticsearch Operations

```java
class Tests {
    /**
     * ElasticsearchOperations使用
     */
    @Autowired
    ElasticsearchOperations operations;

    @Test
    void testOperations() {
        Book book = new Book();
        book.setName("平凡的世界");
        book.setPrice(150);
        Book save = operations.save(book);
        System.out.println(save);    //返回保存的文档
    }

    @Test
    void testOperationsQuery() {
        Criteria criteria = new Criteria("price").is(50);
        Query query = new CriteriaQuery(criteria);    //构造查询条件Query
        SearchHits<Book> hits = operations.search(query, Book.class);
        for (SearchHit<Book> hit : hits) {
            System.out.println(hit);
        }
    }
}
```



#### Elasticsearch Repositories

- 需要先定义并继承ElasticsearchRepository接口，定义的接口本身具有基本的CRUD方法，更多复杂的查询方法可以直接写在此接口中，规则参考文档https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.query-methods

```java
@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

  //查询方法可以直接写在此接口中
    List<Book> findByNameAndPrice(String name, Integer price);

    List<Book> findByNameContaining(String name);

    List<Book> findByPriceLessThanEqual(Integer price);

}
```

- 使用、查询的时候直接调用接口中定义的方法即可

```java
class Tests {
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
      //查找的时候会根据设置的analyzer搜索
        List<Book> books = repository.findByNameAndPrice("活着", 50);	
        List<Book> books = repository.findByNameContaining("世");

        List<Book> books = repository.findByPriceLessThanEqual(100);

        System.out.println(books);
    }
}

```

