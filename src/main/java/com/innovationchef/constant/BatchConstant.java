package com.innovationchef.constant;

public final class BatchConstant {
    private BatchConstant() {}

    public static final String JOB_ID = "JOB_ID";
    public static final String INPUT_FILE = "inputFilePath";
    public static final String ARCHIVE_FILE = "archiveFilePath";
    public static final String FILE_EXTENSION = ".csv";
    public static final String DELIMITER = ",";
    public static final int SKIP_HEADER_COUNT = 1;
    public static final String EXIT_STATUS_END = "END";

    // Batch Job Types
    public static final String JOB_TYPE = "job-type";
    public static final String PAYMENT_JOB = "payment-job";
    public static final String CUSTOMER_JOB = "customer-job";

    // Bulk Payment Job Constants
    public static final String PAY_JOB_NAME = "bulk-pay-process";
    public static final String PAY_STEP_NAME = "pay-request";

    // Customer Management Job Constants
    public static final String CUST_JOB_NAME = "cust-mgmt-process";
    public static final String CUST_STEP_NAME = "cust-request";

}
