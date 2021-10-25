package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The POJO that describes the CertificateTag.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateTag implements DatabaseEntity {
    private long id;
    private String name;
}
