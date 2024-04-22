package com.aliyun.sql.hudi;

import java.util.TimeZone;

/**
 * @author twan
 * @version 1.0
 * @description
 * @date 2024-04-16 12:53:47
 */
public class TestTimeStamp {
    public static void main(String[] args) {
//        Object value=1591765524000000L;
//        Timestamp timestamp = (Timestamp) value;
//        System.out.println("args = " +timestamp);
        //org.apache.spark.sql.catalyst.util.DateTimeUtils.fromJavaTimestamp();
        System.out.println("args = " + TimeZone.getDefault());
    }
}
