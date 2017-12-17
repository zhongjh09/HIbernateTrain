package jch.personal.train.hibernate5.jph2.ch03.simple;

import jch.personal.train.hibernate5.jph2.ch03.simple.model.Bid;
import jch.personal.train.hibernate5.jph2.ch03.simple.model.Item;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ModelOperations {

    @Test
    public void linkBidAndItem() {
        Item anItem = new Item();
        Bid aBid = new Bid();

        anItem.getBids().add(aBid);
        aBid.setItem(anItem);

        assertEquals(anItem.getBids().size(), 1);
        assertTrue(anItem.getBids().contains(aBid));
        assertEquals(aBid.getItem(), anItem);

        Bid secondBid = new Bid();
        anItem.addBid(secondBid);

        assertEquals(2, anItem.getBids().size());
        assertTrue(anItem.getBids().contains(secondBid));
        assertEquals(anItem, secondBid.getItem());
    }


    @Test
    public void validateItem() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setName("Some Item");
        item.setAuctionEnd(new Date());

        Set<ConstraintViolation<Item>> violations = validator.validate(item);

        // 一条验证错误消息，拍卖结束日期不是未来的时间点。
        assertEquals(1, violations.size());

        ConstraintViolation<Item> violation = violations.iterator().next();
        String failedPropertyName = violation.getPropertyPath().iterator().next().getName();

        assertEquals(failedPropertyName, "auctionEnd");

        if (Locale.getDefault().getLanguage().equals("en")) {
            assertEquals(violation.getMessage(), "must be in the future");
        }
    }


}
