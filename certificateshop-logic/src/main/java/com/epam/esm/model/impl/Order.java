package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The POJO that describes the {@link Order} entity in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements DatabaseEntity {
    private long id;
    private User user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createDate;
    private String name;
    private List<GiftCertificate> certificates;
}
