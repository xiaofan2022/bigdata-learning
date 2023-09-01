package com.xiaofan.flink.bean;

import lombok.Data;

/**
 * @author: twan
 * @date: 2023/9/1 13:14
 * @description:
 */
@Data
public class Book {
    private Long id;
    private String title;
    private String authors;
    private Integer year;

    public Book(Long id, String title, String authors, Integer year) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.year = year;
    }
}
