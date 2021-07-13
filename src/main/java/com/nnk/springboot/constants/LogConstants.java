package com.nnk.springboot.constants;

public class LogConstants {

    /* Services */
    public static final String USER_LOAD_CALL = "Call to loadUserByUsername with (username): ";

    public static final String CREATE_BID_LIST_CALL = "Call to createBidList with (BidList): ";
    public static final String CREATE_BID_LIST_OK = "Bid list created with (ID): ";
    public static final String CREATE_BID_LIST_ERROR = "Error when saving bid list: ";

    public static final String FIND_BID_LIST_ALL_CALL = "Call to findAllBidList";
    public static final String FIND_BID_LIST_ALL_OK = "List of bidList retrieved with () values: ";

    public static final String FIND_BID_LIST_BY_ID_CALL = "Call to findBidListById";
    public static final String FIND_BID_LIST_BY_ID_OK = "BidList retrieved for id: ";

    public static final String UPDATE_BID_LIST_CALL = "Call to updateBidList with (BidList): ";
    public static final String UPDATE_BID_LIST_OK ="Bid list updated for (ID): ";
    public static final String UPDATE_BID_LIST_ERROR = "Error when updating bid list: ";

    /* Controllers */
    public static final String CURRENT_USER_UNKNOWN = "Current user unknown";

    public static final String BIDLIST_CREATION_FORM_REQUEST_RECEIVED = "GET request on endpoint /bidList/add received";
    public static final String BIDLIST_CREATION_REQUEST_RECEIVED = "POST request on endpoint /bidList/validate received for (BidList): ";
    public static final String BIDLIST_CREATION_REQUEST_NOT_VALID = "Bid list information not valid";
    public static final String BIDLIST_CREATION_REQUEST_KO = "New bid list has not been added";
    public static final String BIDLIST_CREATION_REQUEST_OK = "New bid list has been added with id: ";

    public static final String BIDLIST_LIST_REQUEST_RECEIVED = "Request on endpoint /bidList/list received";

    public static final String BIDLIST_UPDATE_FORM_REQUEST_RECEIVED = "GET request on endpoint /bidList/update/{id} received for id:";
    public static final String BIDLIST_UPDATE_REQUEST_RECEIVED = "POST request on endpoint /bidList/update/{id} received for (BidList): ";
    public static final String BIDLIST_UPDATE_REQUEST_NOT_VALID = "Bid list information not valid";
    public static final String BIDLIST_UPDATE_REQUEST_OK = "Bid list has been updated for id: ";
    public static final String BIDLIST_UPDATE_REQUEST_KO = "Bid list has not been updated for id: ";
}
