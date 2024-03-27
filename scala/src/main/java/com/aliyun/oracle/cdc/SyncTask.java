/*
package com.aliyun.oracle.cdc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

*/
/**
 * @author twan
 * @version 1.0
 * @description SyncTask
 * @date 2024-03-24 14:06:15
 *
 * <p>方法名称: createDictionary|描述: 调用logminer生成数据字典文件</p>
 * @param sourceConn 源数据库连接
 * @throws java.lang.Exception 异常信息
 *
 * <p>方法名称: startLogmur|描述:启动logminer分析 </p>
 * @throws java.lang.Exception
 *//*

public class SyncTask {

    */
/**
 * <p>方法名称: createDictionary|描述: 调用logminer生成数据字典文件</p>
 * @param sourceConn 源数据库连接
 * @throws java.lang.Exception 异常信息
 *//*

    public void createDictionary(Connection sourceConn) throws Exception{
        String createDictSql = "BEGIN dbms_logmnr_d.build(dictionary_filename => 'dictionary.ora', dictionary_location =>'"+Constants.DATA_DICTIONARY+"'); END;";
        CallableStatement callableStatement = sourceConn.prepareCall(createDictSql);
        callableStatement.execute();
    }

    */
/**
 * <p>方法名称: startLogmur|描述:启动logminer分析 </p>
 * @throws java.lang.Exception
 *//*

    public void startLogmur() throws Exception{

        Connection sourceConn = null;
        Connection targetConn = null;
        try {
            ResultSet resultSet = null;

            // 获取源数据库连接
            sourceConn = DataBase.getSourceDataBase();
            Statement statement = sourceConn.createStatement();

            // 添加所有日志文件，本代码仅分析联机日志
            StringBuffer sbSQL = new StringBuffer();
            sbSQL.append(" BEGIN");
            sbSQL.append(" dbms_logmnr.add_logfile(logfilename=>'"+Constants.LOG_PATH+"\\REDO01.LOG', options=>dbms_logmnr.NEW);");
            sbSQL.append(" dbms_logmnr.add_logfile(logfilename=>'"+Constants.LOG_PATH+"\\REDO02.LOG', options=>dbms_logmnr.ADDFILE);");
            sbSQL.append(" dbms_logmnr.add_logfile(logfilename=>'"+Constants.LOG_PATH+"\\REDO03.LOG', options=>dbms_logmnr.ADDFILE);");
            sbSQL.append(" END;");
            CallableStatement callableStatement = sourceConn.prepareCall(sbSQL+"");
            callableStatement.execute();

            // 打印获分析日志文件信息
            resultSet = statement.executeQuery("SELECT db_name, thread_sqn, filename FROM v$logmnr_logs");
            while(resultSet.next()){
                System.out.println("已添加日志文件==>"+resultSet.getObject(3));
            }

            System.out.println("开始分析日志文件,起始scn号:"+Constants.LAST_SCN);
            callableStatement = sourceConn.prepareCall("BEGIN dbms_logmnr.start_logmnr(startScn=>'"+Constants.LAST_SCN+"',dictfilename=>'"+Constants.DATA_DICTIONARY+"\\dictionary.ora',OPTIONS =>DBMS_LOGMNR.COMMITTED_DATA_ONLY+dbms_logmnr.NO_ROWID_IN_STMT);END;");
            callableStatement.execute();
            System.out.println("完成分析日志文件");

            // 查询获取分析结果
            System.out.println("查询分析结果");
            resultSet = statement.executeQuery("SELECT scn,operation,timestamp,status,sql_redo FROM v$logmnr_contents WHERE seg_owner='"+Constants.SOURCE_CLIENT_USERNAME+"' AND seg_type_name='TABLE' AND operation !='SELECT_FOR_UPDATE'");

            // 连接到目标数据库，在目标数据库执行redo语句
            targetConn = DataBase.getTargetDataBase();
            Statement targetStatement = targetConn.createStatement();

            String lastScn = Constants.LAST_SCN;
            String operation = null;
            String sql = null;
            boolean isCreateDictionary = false;
            while(resultSet.next()){
                lastScn = resultSet.getObject(1)+"";
                if( lastScn.equals(Constants.LAST_SCN) ){
                    continue;
                }

                operation = resultSet.getObject(2)+"";
                if( "DDL".equalsIgnoreCase(operation) ){
                    isCreateDictionary = true;
                }

                sql = resultSet.getObject(5)+"";

                // 替换用户
                sql = sql.replace("\""+Constants.SOURCE_CLIENT_USERNAME+"\".", "");
                System.out.println("scn="+lastScn+",自动执行sql=="+sql+"");

                try {
                    targetStatement.executeUpdate(sql.substring(0, sql.length()-1));
                } catch (Exception e) {
                    System.out.println("测试一下,已经执行过了");
                }
            }

            // 更新scn
            Constants.LAST_SCN = (Integer.parseInt(lastScn))+"";

            // DDL发生变化，更新数据字典
            if( isCreateDictionary ){
                System.out.println("DDL发生变化，更新数据字典");
                createDictionary(sourceConn);
                System.out.println("完成更新数据字典");
                isCreateDictionary = false;
            }

            System.out.println("完成一个工作单元");

        }
        finally{
            if( null != sourceConn ){
                sourceConn.close();
            }
            if( null != targetConn ){
                targetConn.close();
            }

            sourceConn = null;
            targetConn = null;
        }
    }

}
*/
