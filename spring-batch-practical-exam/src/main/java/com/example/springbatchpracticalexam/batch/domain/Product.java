package com.example.springbatchpracticalexam.batch.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    /*
     ! JPA 에서 id 를 문맥적으로 자동으로 생성하겠다고 하고, id 를 set 할 경우 다음 같은 에러를 만나게 된다.
     * PersistentObjectException : detached entity passed to persist

     - @GeneratedValue 를 사용하고, id 를 set 할 경우 persist() 할 경우 위와 같은 에러를 발생시킨다.
     - 위의 상황을 하려면, merge() 를 사용해야 정상 동작하지만 persist() 보다 비효율적으로 동작하니 추전하지 않는다.
     */
//    @GeneratedValue
    private Long id;

    private String name;
    private Long price;
    private String type;

    @Builder
    private Product(Long id, String name, Long price, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }

}
