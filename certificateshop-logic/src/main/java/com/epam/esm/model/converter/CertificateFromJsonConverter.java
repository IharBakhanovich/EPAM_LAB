package com.epam.esm.model.converter;

import com.epam.esm.model.impl.GiftCertificate;
import com.google.gson.Gson;

import javax.persistence.AttributeConverter;

public class CertificateFromJsonConverter implements AttributeConverter<GiftCertificate, String> {
    @Override
    public String convertToDatabaseColumn(GiftCertificate giftCertificate) {
        Gson gson = new Gson();
        return gson.toJson(giftCertificate);
    }

    @Override
    public GiftCertificate convertToEntityAttribute(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, GiftCertificate.class);
    }
}
