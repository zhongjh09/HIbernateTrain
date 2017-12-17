package jch.personal.train.hibernate5.jph2.ch03.simple.model;

import jch.personal.train.hibernate5.jph2.constants.ModelConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Bid {

    @Id
    @GeneratedValue(generator = ModelConstants.ID_GENERATOR)
    protected Long id;

    public Long getId() {
        return id;
    }

    /**
     * Hibernate不能调用有参构造方法，因此必须要有一个无参构造方法供Hibernate调用
     */
    public Bid() {

    }

    @NotNull
    protected BigDecimal amount;

    /**
     * 构造方法，强制实施关系完整性。
     * @param amount
     * @param item
     */
    public Bid(BigDecimal amount, Item item) {
        this.amount = amount;
        this.item = item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * item属性允许从一个Bid导航到相关Item。是一个具有多对一的多样性的关联。
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    protected Item item;

    public Bid(Item item) {
        this.item = item;
        item.getBids().add(this);       // 双向关联
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
