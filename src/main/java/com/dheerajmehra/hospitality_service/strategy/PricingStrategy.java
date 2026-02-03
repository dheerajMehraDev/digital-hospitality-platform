package com.dheerajmehra.hospitality_service.strategy;



import com.dheerajmehra.hospitality_service.entity.Inventory;

import java.math.BigDecimal;
public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
