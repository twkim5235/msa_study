package dev.practice.order.domain.item;

import com.google.common.collect.Lists;
import dev.practice.order.common.exception.InvalidParamException;
import dev.practice.order.common.util.TokenGenerator;
import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.item.optiongroup.ItemOptionGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "items")
public class Item extends AbstractEntity {
    private static final String PREFIX_ITEM = "itm_";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemToken;
    private Long partnerId;
    private String itemName;
    private Long itemPrice;

    // Item : ItemOptionGroup = 1 : N
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "item")
    private List<ItemOptionGroup> itemOptionGroupList = Lists.newArrayList();

    @Enumerated(EnumType.STRING)
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public enum Status {
        PREPARE("판매 준비중"),
        ON_SALES("판매 중"),
        END_OF_SALES("판매 종료");

        private final String description;
    }

    @Builder
    public Item(Long partnerId, String itemName, Long itemPrice) {
        if(partnerId == null) throw new InvalidParamException();
        if(StringUtils.isEmpty(itemName)) throw new InvalidParamException();
        if(itemPrice == null) throw new InvalidParamException();

        this.itemToken = TokenGenerator.randomCharacterWithPrefix(PREFIX_ITEM);
        this.partnerId = partnerId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.status = Status.PREPARE;
    }

    public void changePrepare() {
        this.status = Status.PREPARE;
    }

    public void changeOnSale() {
        if(this.status == Status.END_OF_SALES) throw new InvalidParamException();
        this.status = Status.ON_SALES;
    }

    public void changeEndOfSale() {
        this.status = Status.END_OF_SALES;
    }

}
