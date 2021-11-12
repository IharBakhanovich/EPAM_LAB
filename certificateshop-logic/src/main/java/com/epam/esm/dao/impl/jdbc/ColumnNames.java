package com.epam.esm.dao.impl.jdbc;

import java.util.*;

/**
 * Stores names of the database tables columns, which are used by {@link GiftCertificateExtractor}
 * {@link CertificateTagMapper} and {@link GiftCertificateMapper} to fetch data from a ResultSet.
 */
public class ColumnNames {
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
    public static final String PAGE_NUMBER_PARAM_NAME = "pageNumber";
    public static final String AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME = "amountOfEntitiesOnThePage";
    public static final Map<String, String> DEFAULT_PARAMS = new HashMap<String, String>() {{
        put(PAGE_NUMBER_PARAM_NAME, "0");
        put(AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
    }};
    public static final Map<String, List<String>> DEFAULT_PARAMS_FOR_FIND_ALL = new HashMap<String, List<String>>() {{
        put(PAGE_NUMBER_PARAM_NAME, new ArrayList<String>() {
            {
                add("0");
            }
        });
        put(AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, new ArrayList<String>() {
            {
                add(DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
            }
        });
    }};

    private ColumnNames() {
    }

    /**
     * To use in controllers.
     *
     * @param entities
     * @param pageNumber
     * @param amountOfEntitesOnThePage
     * @return
     */
    public static <T> Map<String, String> createNextParameters(List<T> entities, int pageNumber, int amountOfEntitesOnThePage) {
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

    public static <T> Map<String, String> createPrevParameters(List<T> certificates, int pageNumber, int amountOfEntitiesOnThePage) {
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

    /**
     * To use in controllers.
     *
     * @param entities
     * @param pageNumber
     * @param amountOfEntitesOnThePage
     * @return
     */
    public static <T> Map<String, List<String>> createNextParametersForFindAll(List<T> entities,
                                                                               int pageNumber, int amountOfEntitesOnThePage) {
        int nextPageNumber = 0;
        Map<String, List<String>> paramsNext = new HashMap<>();
        if (amountOfEntitesOnThePage == entities.size()) {
            nextPageNumber = pageNumber + 1;
        } else {
            nextPageNumber = pageNumber;
        }
        int finalNextPageNumber = nextPageNumber;
        paramsNext.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, new ArrayList<String>() {
            {
                add(String.valueOf(finalNextPageNumber));
            }
        });
        setLimitForFindAll(amountOfEntitesOnThePage, paramsNext);
        return paramsNext;
    }

    public static <T> Map<String, List<String>> createPrevParametersForFindAll(List<T> certificates,
                                                                               int pageNumber, int amountOfEntitiesOnThePage) {
        Map<String, List<String>> paramsPrev = new HashMap<>();
        int prevPageNumber = 0;
        if (pageNumber > 0) {
            prevPageNumber = pageNumber - 1;
        } else {
            prevPageNumber = pageNumber;
        }
        int finalPrevPageNumber = prevPageNumber;
        paramsPrev.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, new ArrayList<String>() {
            {
                add(String.valueOf(finalPrevPageNumber));
            }
        });
        setLimitForFindAll(amountOfEntitiesOnThePage, paramsPrev);
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

    private static void setLimitForFindAll(int amountOfEntitiesOnThePage, Map<String, List<String>> paramsNext) {
        if (amountOfEntitiesOnThePage != 0) {
            paramsNext.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME,
                    new ArrayList<String>() {
                        {
                            add(String.valueOf(amountOfEntitiesOnThePage));
                        }
                    });
        } else {
            paramsNext.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME,
                    new ArrayList<String>() {
                        {
                            add(DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
                        }
                    });
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
        if (parameters.size() == 0 || !parameters.containsKey(ColumnNames.PAGE_NUMBER_PARAM_NAME)
                || !parameters.containsKey(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME)) {
            parameters.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "0");
            parameters.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, defaultAmountEntitiesOnThePage);
        }
        return parameters;
    }

    public static Map<String, List<String>> validateParametersForCertificates(
            Map<String, List<String>> parameters, String defaultAmountEntitiesOnThePage) {
        if (parameters.size() == 0 || !parameters.containsKey(ColumnNames.PAGE_NUMBER_PARAM_NAME)
                || !parameters.containsKey(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME)) {
            List<String> listForPageNumber = new ArrayList<>();
            listForPageNumber.add("0");
            List<String> listForAmountEntities = new ArrayList<>();
            listForPageNumber.add(defaultAmountEntitiesOnThePage);
            parameters.put(ColumnNames.PAGE_NUMBER_PARAM_NAME, listForPageNumber);
            parameters.put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, listForAmountEntities);
        }
        return parameters;
    }

    public static String createQuery(Map<String, String> parameters) {

        String part1 = "select c.id as certificateId, c.name as certificateName, c.description as certificateDescription," +
                " c.duration as certificateDuration, c.create_date as certificateCreateDate," +
                " c.price as certificatePrice, c.last_update_date as certificateLastUpdateDate, t.id as tagId," +
                " t.name as tagName" +
                " from gift_certificate as c" +
                " LEFT OUTER JOIN (has_tag as h LEFT OUTER JOIN tag as t ON t.id = h.tagId) ON c.id = h.certificateId" +
                " WHERE c.id IN (select * from (select id from (select cq.id, tq.name, COUNT(tq.name) as amount" +
                " from (gift_certificate as cq" +
                " LEFT OUTER JOIN (has_tag as hq LEFT OUTER JOIN tag as tq ON tq.id = hq.tagId) ON cq.id = hq.certificateId)";
        String part2_1 = " where cq.name like ";
        String part2_2 = "'%%'";
        String part3_1 = " and cq.description like ";
        String part3_2 = "'%%'";
        String part4_1 = " and tq.name in (%s)";
        String part4_2 = "";
        String part4_3 = " group by cq.id";
        String part4_4 = " having amount = ";
        String part4_5 = "";
        String part5 = " order by cq.id) as query2";
        String part6 = " LIMIT ?, ?) as query1);";

        Set<Map.Entry<String, String>> set = parameters.entrySet();
        for (Map.Entry<String, String> entry : set) {
            if (entry.getKey().equals("part_cert_name")) {
                part2_2 = "'%" + parameters.get("part_cert_name") + "%'";
            }
            if (entry.getKey().equals("part_descr_name")) {
                part3_2 = "'%" + parameters.get("part_descr_name") + "%'";
            }
            if (entry.getKey().equals("tag_name")) {
                List<String> values = Arrays.asList(parameters.get("tag_name").split(","));
                part4_5 = String.valueOf(values.size());
                for (String value : values) {
                    if (part4_2.equals("")) {
                        part4_2 = part4_2.concat("'").concat(value).concat("'");
                    } else {
                        part4_2 = part4_2.concat(", ").concat("'").concat(value).concat("'");
                    }
                }
            }
        }
        String findAllQuery = part1.concat(part2_1);
        if (part4_2.equals("")) {
            findAllQuery = findAllQuery + part2_2 + part3_1 + part3_2 + part4_3 + part5 + part6;
        } else {
            String part4 = String.format(part4_1, part4_2);
            findAllQuery = findAllQuery + part2_2 + part3_1 + part3_2
                    + String.format(part4_1, part4_2) + part4_3 + part4_4 + part4_5 + part5 + part6;
        }
        return findAllQuery;
    }
}
