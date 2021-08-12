package com.nnk.springboot.constants;

public class LogConstants {

    /* Services */
    public static final String USER_LOAD_CALL = "Call to loadUserByUsername with username: ";

    public static final String CREATE_BID_LIST_CALL = "Call to createBidList with BidList: ";
    public static final String CREATE_BID_LIST_OK = "BidList created with id: ";
    public static final String CREATE_BID_LIST_ERROR = "Error when saving bidList: ";

    public static final String FIND_BID_LIST_ALL_CALL = "Call to findAllBidList";
    public static final String FIND_BID_LIST_ALL_OK = "List of bidList retrieved with {} values \n";

    public static final String FIND_BID_LIST_BY_ID_CALL = "Call to findBidListById";
    public static final String FIND_BID_LIST_BY_ID_OK = "BidList retrieved for id: ";

    public static final String UPDATE_BID_LIST_CALL = "Call to updateBidList with BidList: ";
    public static final String UPDATE_BID_LIST_OK = "BidList updated for id: ";
    public static final String UPDATE_BID_LIST_ERROR = "Error when updating bidList: ";

    public static final String DELETE_BID_LIST_CALL = "Call to deleteBidList for id: ";
    public static final String DELETE_BID_LIST_OK = "BidList deleted for id: ";
    public static final String DELETE_BID_LIST_ERROR = "Error when deleting bidList id: ";

    public static final String CREATE_CURVE_POINT_CALL = "Call to createCurvePoint with CurvePoint: ";
    public static final String CREATE_CURVE_POINT_OK = "CurvePoint created with id: ";
    public static final String CREATE_CURVE_POINT_ERROR = "Error when saving curvePoint: ";

    public static final String FIND_CURVE_POINT_ALL_CALL = "Call to findAllCurvePoint";
    public static final String FIND_CURVE_POINT_ALL_OK = "List of curvePoint retrieved with {} values \n";

    public static final String FIND_CURVE_POINT_BY_ID_CALL = "Call to findCurvePointById";
    public static final String FIND_CURVE_POINT_BY_ID_OK = "CurvePoint retrieved for id: ";

    public static final String UPDATE_CURVE_POINT_CALL = "Call to updateCurvePoint with CurvePoint: ";
    public static final String UPDATE_CURVE_POINT_OK = "CurvePoint updated for id: ";
    public static final String UPDATE_CURVE_POINT_ERROR = "Error when updating curvePoint: ";

    public static final String DELETE_CURVE_POINT_CALL = "Call to deleteCurvePoint for id: ";
    public static final String DELETE_CURVE_POINT_OK = "CurvePoint deleted for id: ";
    public static final String DELETE_CURVE_POINT_ERROR = "Error when deleting curvePoint id: ";

    public static final String CREATE_RATING_CALL = "Call to createRating with Rating: ";
    public static final String CREATE_RATING_OK = "Rating created with id: ";
    public static final String CREATE_RATING_ERROR = "Error when saving rating: ";

    public static final String FIND_RATING_ALL_CALL = "Call to findAllRating";
    public static final String FIND_RATING_ALL_OK = "List of rating retrieved with {} values \n";

    public static final String FIND_RATING_BY_ID_CALL = "Call to findRatingById";
    public static final String FIND_RATING_BY_ID_OK = "Rating retrieved for id: ";

    public static final String UPDATE_RATING_CALL = "Call to updateRating with Rating: ";
    public static final String UPDATE_RATING_OK = "Rating updated for id: ";
    public static final String UPDATE_RATING_ERROR = "Error when updating rating: ";

    public static final String DELETE_RATING_CALL = "Call to deleteRating for id: ";
    public static final String DELETE_RATING_OK = "Rating deleted for id: ";
    public static final String DELETE_RATING_ERROR = "Error when deleting rating id: ";

    public static final String CREATE_RULE_NAME_CALL = "Call to createRuleName with RuleName: ";
    public static final String CREATE_RULE_NAME_OK = "RuleName created with id: ";
    public static final String CREATE_RULE_NAME_ERROR = "Error when saving rating: ";

    public static final String FIND_RULE_NAME_ALL_CALL = "Call to findAllRuleName";
    public static final String FIND_RULE_NAME_ALL_OK = "List of rating retrieved with {} values \n";

    public static final String FIND_RULE_NAME_BY_ID_CALL = "Call to findRuleNameById";
    public static final String FIND_RULE_NAME_BY_ID_OK = "RuleName retrieved for id: ";

    public static final String UPDATE_RULE_NAME_CALL = "Call to updateRuleName with RuleName: ";
    public static final String UPDATE_RULE_NAME_OK = "RuleName updated for id: ";
    public static final String UPDATE_RULE_NAME_ERROR = "Error when updating rating: ";

    public static final String DELETE_RULE_NAME_CALL = "Call to deleteRuleName for id: ";
    public static final String DELETE_RULE_NAME_OK = "RuleName deleted for id: ";
    public static final String DELETE_RULE_NAME_ERROR = "Error when deleting rating id: ";

    public static final String CREATE_TRADE_CALL = "Call to createTrade with Trade: ";
    public static final String CREATE_TRADE_OK = "Trade created with id: ";
    public static final String CREATE_TRADE_ERROR = "Error when saving trade: ";

    public static final String FIND_TRADE_ALL_CALL = "Call to findAllTrade";
    public static final String FIND_TRADE_ALL_OK = "List of trade retrieved with {} values \n";

    public static final String FIND_TRADE_BY_ID_CALL = "Call to findTradeById";
    public static final String FIND_TRADE_BY_ID_OK = "Trade retrieved for id: ";

    public static final String UPDATE_TRADE_CALL = "Call to updateTrade with Trade: ";
    public static final String UPDATE_TRADE_OK = "Trade updated for id: ";
    public static final String UPDATE_TRADE_ERROR = "Error when updating trade: ";

    public static final String DELETE_TRADE_CALL = "Call to deleteTrade for id: ";
    public static final String DELETE_TRADE_OK = "Trade deleted for id: ";
    public static final String DELETE_TRADE_ERROR = "Error when deleting trade id: ";

    /* Controllers */
    public static final String CURRENT_USER_UNKNOWN = "Current user unknown";

    public static final String BIDLIST_CREATION_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /bidList/add received for user: {} \n";
    public static final String BIDLIST_CREATION_REQUEST_RECEIVED =
            "POST request on endpoint /bidList/validate received for BidList: ";
    public static final String BIDLIST_CREATION_REQUEST_NOT_VALID =
            "BidList information not valid";
    public static final String BIDLIST_CREATION_REQUEST_KO =
            "New bidList has not been added";
    public static final String BIDLIST_CREATION_REQUEST_OK =
            "New bidList has been added with id {} by user: {} \n";

    public static final String BIDLIST_LIST_REQUEST_RECEIVED =
            "Request on endpoint /bidList/list received for user: {}";

    public static final String BIDLIST_UPDATE_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /bidList/update/{} received for user: {}";
    public static final String BIDLIST_UPDATE_REQUEST_RECEIVED =
            "POST request on endpoint /bidList/update/{} received with BidList: {}, for user: {}";
    public static final String BIDLIST_UPDATE_REQUEST_NOT_VALID =
            "BidList information not valid";
    public static final String BIDLIST_UPDATE_REQUEST_OK =
            "BidList id {} has been updated by user: {} \n";
    public static final String BIDLIST_UPDATE_REQUEST_KO =
            "BidList id {} has not been updated : {} \n";

    public static final String BIDLIST_DELETE_REQUEST_RECEIVED =
            "GET request on endpoint /bidList/delete/{} received for user: {}";
    public static final String BIDLIST_DELETE_REQUEST_OK =
            "BidList id {} has been deleted by user: {} \n";
    public static final String BIDLIST_DELETE_REQUEST_KO =
            "BidList id {} has not been deleted: {} \n";

    public static final String CURVEPOINT_CREATION_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /curvePoint/add received for user: {} \n";
    public static final String CURVEPOINT_CREATION_REQUEST_RECEIVED =
            "POST request on endpoint /curvePoint/validate received for CurvePoint: ";
    public static final String CURVEPOINT_CREATION_REQUEST_NOT_VALID =
            "CurvePoint information not valid";
    public static final String CURVEPOINT_CREATION_REQUEST_KO =
            "New curvePoint has not been added";
    public static final String CURVEPOINT_CREATION_REQUEST_OK =
            "New curvePoint has been added with id {} by user: {} \n";

    public static final String CURVEPOINT_LIST_REQUEST_RECEIVED =
            "Request on endpoint /curvePoint/list received for user: {}";

    public static final String CURVEPOINT_UPDATE_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /curvePoint/update/{} received for user: {}";
    public static final String CURVEPOINT_UPDATE_REQUEST_RECEIVED =
            "POST request on endpoint /curvePoint/update/{} received with CurvePoint: {}, for user: {}";
    public static final String CURVEPOINT_UPDATE_REQUEST_NOT_VALID =
            "CurvePoint information not valid";
    public static final String CURVEPOINT_UPDATE_REQUEST_OK =
            "CurvePoint id {} has been updated by user: {} \n";
    public static final String CURVEPOINT_UPDATE_REQUEST_KO =
            "CurvePoint id {} has not been updated : {} \n";

    public static final String CURVEPOINT_DELETE_REQUEST_RECEIVED =
            "GET request on endpoint /curvePoint/delete/{} received for user: {}";
    public static final String CURVEPOINT_DELETE_REQUEST_OK =
            "CurvePoint id {} has been deleted by user: {} \n";
    public static final String CURVEPOINT_DELETE_REQUEST_KO =
            "CurvePoint id {} has not been deleted: {} \n";

    public static final String RATING_CREATION_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /rating/add received for user: {} \n";
    public static final String RATING_CREATION_REQUEST_RECEIVED =
            "POST request on endpoint /rating/validate received for Rating: ";
    public static final String RATING_CREATION_REQUEST_NOT_VALID =
            "Rating information not valid";
    public static final String RATING_CREATION_REQUEST_KO =
            "New rating has not been added";
    public static final String RATING_CREATION_REQUEST_OK =
            "New rating has been added with id {} by user: {} \n";

    public static final String RATING_LIST_REQUEST_RECEIVED =
            "Request on endpoint /rating/list received for user: {}";

    public static final String RATING_UPDATE_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /rating/update/{} received for user: {}";
    public static final String RATING_UPDATE_REQUEST_RECEIVED =
            "POST request on endpoint /rating/update/{} received with Rating: {}, for user: {}";
    public static final String RATING_UPDATE_REQUEST_NOT_VALID =
            "Rating information not valid";
    public static final String RATING_UPDATE_REQUEST_OK =
            "Rating id {} has been updated by user: {} \n";
    public static final String RATING_UPDATE_REQUEST_KO =
            "Rating id {} has not been updated : {} \n";

    public static final String RATING_DELETE_REQUEST_RECEIVED =
            "GET request on endpoint /rating/delete/{} received for user: {}";
    public static final String RATING_DELETE_REQUEST_OK =
            "Rating id {} has been deleted by user: {} \n";
    public static final String RATING_DELETE_REQUEST_KO =
            "Rating id {} has not been deleted: {} \n";

    public static final String RULE_NAME_CREATION_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /ruleName/add received for user: {} \n";
    public static final String RULE_NAME_CREATION_REQUEST_RECEIVED =
            "POST request on endpoint /ruleName/validate received for RuleName: ";
    public static final String RULE_NAME_CREATION_REQUEST_NOT_VALID =
            "RuleName information not valid";
    public static final String RULE_NAME_CREATION_REQUEST_KO =
            "New ruleName has not been added";
    public static final String RULE_NAME_CREATION_REQUEST_OK =
            "New ruleName has been added with id {} by user: {} \n";

    public static final String RULE_NAME_LIST_REQUEST_RECEIVED =
            "Request on endpoint /ruleName/list received for user: {}";

    public static final String RULE_NAME_UPDATE_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /ruleName/update/{} received for user: {}";
    public static final String RULE_NAME_UPDATE_REQUEST_RECEIVED =
            "POST request on endpoint /ruleName/update/{} received with RuleName: {}, for user: {}";
    public static final String RULE_NAME_UPDATE_REQUEST_NOT_VALID =
            "RuleName information not valid";
    public static final String RULE_NAME_UPDATE_REQUEST_OK =
            "RuleName id {} has been updated by user: {} \n";
    public static final String RULE_NAME_UPDATE_REQUEST_KO =
            "RuleName id {} has not been updated : {} \n";

    public static final String RULE_NAME_DELETE_REQUEST_RECEIVED =
            "GET request on endpoint /ruleName/delete/{} received for user: {}";
    public static final String RULE_NAME_DELETE_REQUEST_OK =
            "RuleName id {} has been deleted by user: {} \n";
    public static final String RULE_NAME_DELETE_REQUEST_KO =
            "RuleName id {} has not been deleted: {} \n";
//
    public static final String TRADE_CREATION_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /trade/add received for user: {} \n";
    public static final String TRADE_CREATION_REQUEST_RECEIVED =
            "POST request on endpoint /trade/validate received for Trade: ";
    public static final String TRADE_CREATION_REQUEST_NOT_VALID =
            "Trade information not valid";
    public static final String TRADE_CREATION_REQUEST_KO =
            "New trade has not been added";
    public static final String TRADE_CREATION_REQUEST_OK =
            "New trade has been added with id {} by user: {} \n";

    public static final String TRADE_LIST_REQUEST_RECEIVED =
            "Request on endpoint /trade/list received for user: {}";

    public static final String TRADE_UPDATE_FORM_REQUEST_RECEIVED =
            "GET request on endpoint /trade/update/{} received for user: {}";
    public static final String TRADE_UPDATE_REQUEST_RECEIVED =
            "POST request on endpoint /trade/update/{} received with Trade: {}, for user: {}";
    public static final String TRADE_UPDATE_REQUEST_NOT_VALID =
            "Trade information not valid";
    public static final String TRADE_UPDATE_REQUEST_OK =
            "Trade id {} has been updated by user: {} \n";
    public static final String TRADE_UPDATE_REQUEST_KO =
            "Trade id {} has not been updated : {} \n";

    public static final String TRADE_DELETE_REQUEST_RECEIVED =
            "GET request on endpoint /trade/delete/{} received for user: {}";
    public static final String TRADE_DELETE_REQUEST_OK =
            "Trade id {} has been deleted by user: {} \n";
    public static final String TRADE_DELETE_REQUEST_KO =
            "Trade id {} has not been deleted: {} \n";
}
