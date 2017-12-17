package jch.personal.train.hibernate5.jph2.ch03.simple.model;

import jch.personal.train.hibernate5.jph2.constants.ModelConstants;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue(generator = ModelConstants.ID_GENERATOR)
    protected Long id;

    public Long getId() {
        return id;
    }

    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
