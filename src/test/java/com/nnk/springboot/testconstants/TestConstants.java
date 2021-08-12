package com.nnk.springboot.testconstants;

public class TestConstants {
    /* bidList */
    public static final Integer NEW_BID_LIST_ID = 2021;
    public static final String NEW_BID_LIST_ACCOUNT = "Account Test";
    public static final String NEW_BID_LIST_TYPE = "Type Test";
    public static final Double NEW_BID_LIST_BID_QUANTITY = 10D;
    public static final String NEW_BID_LIST_TYPE_WITH_TOO_LONG_SIZE = "Type with a too long size generate error";

    public static final Integer EXISTING_BID_LIST_ID = 1;
    public static final String EXISTING_BID_LIST_ACCOUNT = "Account for existing bid";
    public static final String EXISTING_BID_LIST_TYPE = "Type for existing bid";
    public static final Double EXISTING_BID_LIST_BID_QUANTITY = 100D;

    public static final Integer UNKNOWN_BID_LIST_ID = 666;

    /* curvePoint */
    public static final Integer NEW_CURVE_POINT_ID = 2021;
    public static final Integer NEW_CURVE_POINT_CURVE_ID = 2021;
    public static final Double NEW_CURVE_POINT_TERM = 10D;
    public static final Double NEW_CURVE_POINT_VALUE = 20D;

    public static final Integer EXISTING_CURVE_POINT_ID = 1;
    public static final Integer EXISTING_CURVE_POINT_CURVE_ID = 1;
    public static final Double EXISTING_CURVE_POINT_TERM = 100D;
    public static final Double EXISTING_CURVE_POINT_VALUE = 200D;

    public static final Integer UNKNOWN_CURVE_POINT_ID = 666;

    /* rating */
    public static final Integer NEW_RATING_ID = 2021;
    public static final String NEW_RATING_MOODYS_RATING = "New Moody's rating";
    public static final String NEW_RATING_SANDP_RATING = "New Standard and Poor's rating";
    public static final String NEW_RATING_FITCH_RATING = "New Fitch's rating";
    public static final Integer NEW_RATING_ORDER_NUMBER = 2021;

    public static final Integer EXISTING_RATING_ID = 1;
    public static final String EXISTING_RATING_MOODYS_RATING = "Existing Moody's rating";
    public static final String EXISTING_RATING_SANDP_RATING = "Existing Standard and Poor's rating";
    public static final String EXISTING_RATING_FITCH = "Existing Fitch's rating";
    public static final Integer EXISTING_RATING_ORDER_NUMBER = 1;

    public static final Integer UNKNOWN_RATING_ID = 666;

    /* ruleName */
    public static final Integer NEW_RULE_NAME_ID = 2021;
    public static final String NEW_RULE_NAME_NAME = "New RuleName name";
    public static final String NEW_RULE_NAME_DESCRIPTION = "New RuleName description";
    public static final String NEW_RULE_NAME_JSON = "New RuleName JSon";
    public static final String NEW_RULE_NAME_TEMPLATE = "New template";
    public static final String NEW_RULE_NAME_SQLSTR = "New sqlStr";
    public static final String NEW_RULE_NAME_SQLPART = "New sqlPart";

    public static final Integer EXISTING_RULE_NAME_ID = 1;
    public static final String EXISTING_RULE_NAME_NAME = "Existing RuleName name";
    public static final String EXISTING_RULE_NAME_DESCRIPTION = "Existing RuleName description";
    public static final String EXISTING_RULE_NAME_JSON = "Existing RuleName JSon";
    public static final String EXISTING_RULE_NAME_TEMPLATE = "Existing template";
    public static final String EXISTING_RULE_NAME_SQLSTR = "Existing sqlStr";
    public static final String EXISTING_RULE_NAME_SQLPART = "Existing sqlPart";

    public static final Integer UNKNOWN_RULE_NAME_ID = 666;

    /* user */
    public static final Integer EXISTING_USER_ID = 1;
    public static final String EXISTING_USER_USERNAME = "myfirst";
    public static final String EXISTING_USER_PASSWORD = "password";
    public static final String EXISTING_USER_FULLNAME = "my first user";
    public static final String EXISTING_USER_ROLE_USER = "USER";
    public static final String EXISTING_USER_ROLE_ADMIN = "ADMIN";

    public static final String UNKNOWN_USERNAME = "JohnDoe";
}
