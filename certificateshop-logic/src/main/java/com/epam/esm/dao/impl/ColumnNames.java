package com.epam.esm.dao.impl;

/**
 * Stores names of the database tables columns, which are used by {@link GiftCertificateExtractor}
 * {@link CertificateTagMapper} and {@link GiftCertificateMapper} to fetch data from a ResultSet.
 */
public class ColumnNames {
    private ColumnNames() {
    }

    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_ID = "certificateId";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_NAME = "certificateName";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_DESCRIPTION = "certificateDescription";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_PRICE = "certificatePrice";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_DURATION = "certificateDuration";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_CREATE_DATE = "certificateCreateDate";
    public static final String TABLE_GIFT_CERTIFICATE_COLUMN_LAST_UPDATE_DATE = "certificateLastUpdateDate";
    public static final String TABLE_TAG_COLUMN_ID = "tagId";
    public static final String TABLE_TAG_COLUMN_NAME = "tagName";
    public static final String TABLE_USER_COLUMN_ID = "userId";
    public static final String TABLE_USER_COLUMN_NICKNAME = "userNickName";
    public static final String TABLE_USERORDER_CERTIFICATE_COLUMN_CERTIFICATEINJSON = "orderCertificate";
    public static final String TABLE_USERORDER_COLUMN_ID = "orderId";
    public static final String TABLE_USERORDER_COLUMN_CREATE_DATE = "orderCreateDate";
    public static final String TABLE_USERORDER_COLUMN_NAME = "orderName";
}
