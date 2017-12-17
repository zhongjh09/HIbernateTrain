package jch.personal.train.hibernate5.jph2.ch02.helloworld.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity     // 每个持久化类都必须具有@Entity注解。Hibernate会将这个类映射到名为MESSAGE的数据库表。
public class Message {

    @Id     // 每个持久化实体类都必须具有一个用@Id注解的标识符属性，Hibernate会将这一属性映射到名为ID的列。
    @GeneratedValue     // 必须生成标识符：启用自动ID生成机制。
    private Long id;

    // 映射属性
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
