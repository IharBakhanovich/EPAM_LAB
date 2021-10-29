package com.epam.esm.converter;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderToOrderDtoConverter implements Converter<Order, OrderDto> {

    @Override
    public OrderDto convert(Order order) {
        OrderDto orderDto = new OrderDto();
        BigDecimal costOfAllCertificates = BigDecimal.valueOf(0);
        for (GiftCertificate giftCertificate: order.getCertificates()) {
            costOfAllCertificates = costOfAllCertificates.add(giftCertificate.getPrice());
        }
        orderDto.setCost(costOfAllCertificates);
        orderDto.setPurchaseDate(order.getCreateDate());
        return orderDto;
    }
}
