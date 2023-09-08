package com.xiaofan.flink.bean;

/**
 * @author twan
 * @version 1.0
 * @description cdc测试
 * @date 2023-09-02 10:53:05
 */
public class Student901 {
    private Integer id;
    private String name;
    private Integer age;
    private Long eventTime;

    public Student901() {
    }

    public Student901(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Student901(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        return "Student901{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", eventTime=" + eventTime +
                '}';
    }
}
