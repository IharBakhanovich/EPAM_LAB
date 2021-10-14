package com.epam.esm.service.handler;

import com.epam.esm.model.impl.GiftCertificate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum HandlerType {
    BY_TAG_NAME("tag_name") {
        @Override
        public List<GiftCertificate> handle(List<GiftCertificate> certificates, String value) {
            return certificates.stream()
                    .filter(certificate -> certificate.getTags().stream()
                            .anyMatch(tag -> tag.getName().equals(value)))
                    .collect(Collectors.toList());
        }
    },
    BY_PART_NAME("part_cert_name") {
        @Override
        public List<GiftCertificate> handle(List<GiftCertificate> certificates, String value) {
            return certificates.stream()
                    .filter(certificate -> certificate.getName().contains(value))
                    .collect(Collectors.toList());
        }
    },
    BY_PART_DESCRIPTION("part_descr_name") {
        @Override
        public List<GiftCertificate> handle(List<GiftCertificate> certificates, String value) {
            return certificates.stream()
                    .filter(certificate -> certificate.getDescription().contains(value))
                    .collect(Collectors.toList());
        }
    },
    SORT_BY_NAME("sortByName") {
        @Override
        public List<GiftCertificate> handle(List<GiftCertificate> certificates, String value) {
            if (value.equals("asc")) {
                certificates.sort(Comparator.comparing(GiftCertificate::getName));
            }

            if (value.equals("desc")){
                certificates.sort(Comparator.comparing(GiftCertificate::getName).reversed());
            }
            return certificates;
        }
    },
    SORT_BY_DATE("sortByDate") {
        @Override
        public List<GiftCertificate> handle(List<GiftCertificate> certificates, String value) {
            if (value.equals("asc")) {
                certificates.sort(Comparator.comparing(GiftCertificate::getCreateDate));
            }

            if(value.equals("desc")) {
                certificates.sort(Comparator.comparing(GiftCertificate::getCreateDate).reversed());
            }
            return certificates;
        }
    };

    private String parameterName;

    HandlerType(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public abstract List<GiftCertificate> handle(List<GiftCertificate> certificates, String value);
}