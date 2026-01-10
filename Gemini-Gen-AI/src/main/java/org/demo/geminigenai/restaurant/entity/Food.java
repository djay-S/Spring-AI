package org.demo.geminigenai.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FOOD")
public class Food {
    @Id
    @Column(name = "FOOD_ID")
    private int foodId;

    @Column(name = "FOOD_NAME")
    private String foodName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE")
    private double price;
}
