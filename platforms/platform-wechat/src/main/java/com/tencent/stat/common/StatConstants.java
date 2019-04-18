package com.tencent.stat.common;

import java.io.File;

/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

public class StatConstants {
    public static final String VERSION = "3.4.7";
    public static final String MTA_SERVER_HOST = "pingma.qq.com";
    public static final int MTA_SERVER_PORT = 80;
    public static final String MTA_SERVER = "pingma.qq.com:80";
    public static final String MTA_STAT_URL = "/mstat/report";
    public static final String MTA_REPORT_FULL_URL = "http://pingma.qq.com:80/mstat/report";
    public static final String MTA_FEEDBACK_REPORT_URL = "http://mta.qq.com/mta/api/ctr_feedback";
    public static final String FB_KEY = "nDkb9nMIizcj2RDehplOjn+Q";
    public static final String MTA_DB2SP_TAG = "tencent_mta_sp_";
    public static final String MTA_COOPERATION_TAG = "";
    public static final String MTA_STORAGE_PRE_TAG = "tencent.mta" + File.separator + "data" + "";
    public static final int STAT_DB_VERSION = 3;
    public static final int SDK_ONLINE_CONFIG_TYPE = 1;
    public static final int USER_ONLINE_CONFIG_TYPE = 2;
    public static String DATABASE_NAME = "tencent_analysis.db";
    public static final String LOG_TAG = "MtaSDK";
    public static final int XG_PRO_VERSION = 1;
    public static final int MAX_INPUT_LENGTH = 61440;
    public static final int MAX_CRASH_EVENT_LENGTH = 1048576;
    public static final int OK = 0;
    public static final int ERROR_SERVICE_DISABLE = -1;
    public static final int ERROR_ARGUMENT_INVALID = 1000;
    public static final int ERROR_INPUT_LENGTH_LIMIT = 1001;
}
