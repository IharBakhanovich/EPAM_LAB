package com.epam.esm.dao.impl.jdbc;

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
    public static final String DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE = "5";
    public static final Map<String, String> DEFAULT_PARAMS = new HashMap<String, String>(){{
        put(PAGE_NUMBER_PARAM_NAME, "0");
        put(AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
    }};
    public static final String PAGE_NUMBER_PARAM_NAME = "pageNumber";
    public static final String AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME = "amountOfEntitiesOnThePage";



    /**
     * To use in controllers.
     *
     * @param entities
     * @param pageNumber
     * @param amountOfEntitesOnThePage
     * @return
     */
    public static<T> Map<String, String> createNextParameters(List<T> entities, int pageNumber, int amountOfEntitesOnThePage) {
        int nextPageNumber = 0;
        Map<String, String> paramsNext = new HashMap<>();
        if (amountOfEntitesOnThePage == entities.size()) {
            nextPageNumber = pageNumber + 1;
        } else {
            nextPageNumber = pageNumber;
        }
        paramsNext.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, String.valueOf(nextPageNumber));
        setLimit(amountOfEntitesOnThePage, paramsNext);
        return paramsNext;
    }

    public static<T> Map<String, String> createPrevParameters(List<T> certificates, int pageNumber, int amountOfEntitiesOnThePage) {
        Map<String, String> paramsPrev = new HashMap<>();
        int prevPageNumber = 0;
        if (pageNumber > 0) {
            prevPageNumber = pageNumber - 1;
        } else {
            prevPageNumber = pageNumber;
        }
        paramsPrev.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, String.valueOf(prevPageNumber));
        setLimit(amountOfEntitiesOnThePage, paramsPrev);
        return paramsPrev;
    }

    private static void setLimit(int amountOfEntitiesOnThePage, Map<String, String> paramsNext) {
        if (amountOfEntitiesOnThePage != 0) {
            paramsNext.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME,
                    String.valueOf(amountOfEntitiesOnThePage));
        } else {
            paramsNext.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        }
    }

    /**
     * Sets 'offset' and 'limit' query parameters if they are not set.
     *
     * @param parameters the map to validate.
     * @return parameters of the query.
     */
    public static Map<String, String> validateParameters(Map<String, String> parameters,
                                                         String defaultAmountEntitiesOnThePage) {
        if(parameters.size() == 0) {
            parameters.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "0");
            parameters.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, defaultAmountEntitiesOnThePage);
        }
        return parameters;
    }
}
