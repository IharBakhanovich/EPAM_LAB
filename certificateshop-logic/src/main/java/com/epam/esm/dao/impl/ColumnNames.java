package com.epam.esm.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String TABLE_USERORDER_COLUMN_ID = "userOrderId";
    public static final String TABLE_USERORDER_COLUMN_CREATE_DATE = "orderCreateDate";
    public static final String TABLE_USERORDER_COLUMN_NAME = "orderName";
    // default value in the system. Sets how many records on the page will be shown.
    public static final String DEFAULT_ENTITIES_ON_THE_PAGE = "5";
    public static final Map<String, String> DEFAULT_PARAMS = new HashMap<String, String>(){{
        put("offset", "0");
        put("limit", DEFAULT_ENTITIES_ON_THE_PAGE);
    }};
    public static final String OFFSET_PARAM_NAME = "offset";
    public static final String LIMIT_PARAM_NAME = "limit";



    /**
     * To use in controllers.
     *
     * @param certificates
     * @param offset
     * @param limit
     * @return
     */
    public static<T> Map<String, String> createNextParameters(List<T> certificates, long offset, long limit) {
        long nextOffset = 0;
        Map<String, String> paramsNext = new HashMap<>();
        if (limit == certificates.size()) {
            nextOffset = offset + limit;
        }
        paramsNext.put("offset", String.valueOf(nextOffset));
        setLimit(limit, paramsNext);
        return paramsNext;
    }

    public static<T> Map<String, String> createPrevParameters(List<T> certificates, long offset, long limit) {
        Map<String, String> paramsPrev = new HashMap<>();
        long prevOffset = 0;
        if (offset - 2*limit >= 0) {
            prevOffset = offset - limit;
        }
        paramsPrev.put("offset", String.valueOf(prevOffset));
        setLimit(limit, paramsPrev);
        return paramsPrev;
    }

    private static void setLimit(long limit, Map<String, String> paramsNext) {
        if (limit != 0) {
            paramsNext.put("limit", String.valueOf(limit));
        } else {
            paramsNext.put("limit", DEFAULT_ENTITIES_ON_THE_PAGE);
        }
    }

    /**
     * Sets 'offset' and 'limit' query parameters if they are not sett.
     *
     * @param parameters the map to validate.
     * @return parameters of the query.
     */
    public static Map<String, String> validateParameters(Map<String, String> parameters, String defaultLimit) {
        if(parameters.size() == 0) {
            parameters.put("offset", "0");
            parameters.put("limit", defaultLimit);
        }
        return parameters;
    }
}
