package com.epam.esm.dto;

import com.epam.esm.model.DatabaseEntity;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The DataTransferObject for the {@link Order} entity to transfer data outside the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
        private BigDecimal cost;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private LocalDateTime purchaseDate;
}
