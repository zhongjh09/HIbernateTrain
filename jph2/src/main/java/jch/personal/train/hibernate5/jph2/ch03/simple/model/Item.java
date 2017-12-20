package jch.personal.train.hibernate5.jph2.ch03.simple.model;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Item {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    protected Long id;

    public Long getId() {
        return id;
    }

    @Version
    protected long version;

    @NotNull
    @Size(
            min = 2,
            max = 255,
            message = "Name is required, maxium 255 characters."
    )
    protected String name;

    @Future
    protected Date auctionEnd;

    protected BigDecimal buyNowPrice;

    /**
     * 同一个商品用户可以多次出价，因此也是一个具有一对多的多样性关联。
     */
    @Transient
    protected Set<Bid> bids = new HashSet<>();

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAuctionEnd() {
        return auctionEnd;
    }

    public void setAuctionEnd(Date auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    public BigDecimal getBuyNowPrice() {
        return buyNowPrice;
    }

    public void setBuyNowPrice(BigDecimal buyNowPrice) {
        this.buyNowPrice = buyNowPrice;
    }

    public Set<Bid> getBids() {
        return bids;
    }

    public void setBids(Set<Bid> bids) {
        this.bids = bids;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    protected Category category;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addBid(Bid bid) {
        // 一个便利方法会简化关系管理，且具有保护意识
        if (bid == null) {
            throw new NullPointerException("不可添加一个空的Bid");
        }
        if (bid.getItem() != null) {
            throw new IllegalStateException("Bid已经指定了一个Item");
        }
        getBids().add(bid);
        bid.setItem(this);
    }

    public Bid placeBid(Bid currentHighestBid, BigDecimal bidAmount) {
        if (currentHighestBid == null ||
                bidAmount.compareTo(currentHighestBid.getAmount()) > 0) {
            return new Bid(bidAmount, this);
        }
        return null;
    }
}
