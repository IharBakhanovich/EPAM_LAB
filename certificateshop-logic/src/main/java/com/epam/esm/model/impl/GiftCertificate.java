package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The POJO that describes the {@link GiftCertificate} entity in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCertificate implements DatabaseEntity {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private long duration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastUpdateDate;
    private List<CertificateTag> tags;
}
