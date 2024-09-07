import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

import java.time.Duration


/**
 * @description ${description}
 * @author twan
 * @date 2024-03-27 17:49:16
 * @version 1.0
 */
object MiningDataBaseCDCTest {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration
    configuration.setInteger("rest.port", 8081)
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration)
    //env.getCheckpointConfig().setCheckpointStorage( new Path("file:///D:\\qidian\\data\\checkpoint\\cdc_test9e1ac7efab10479b6d2f2b7b81304e6d\\chk-1"));
    env.enableCheckpointing(Duration.ofMinutes(1).toMillis())
    env.disableOperatorChaining()
    val tableEnv: StreamTableEnvironment = StreamTableEnvironment.create(env)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_source_oracle (
        |ID INT,
        |NAME STRING
        |//CREATE_TIME TIMESTAMP,
        |//UPDATE_TIME TIMESTAMP
        |)
        |WITH (
        |	'connector' = 'oracle-cdc',
        |	'hostname' = 'hdp02',
        |	'port' = '1521',
        |	'username' = 'FLINKUSER',
        |	'password' = 'flinkpw',
        |	'database-name' = 'p19c',
        |	'schema-name' = 'FLINKUSER',
        |	'table-name' = 'TEST01',
        |	'debezium.mining.database.hostname' = 'hdp02',
        |	'debezium.mining.database.dbname' = 'logmnr',
        |	'debezium.mining.database.port' = '1521',
        |	'debezium.mining.database.user' = 'system',
        |	'debezium.mining.database.password' = '123',
        |	'debezium.mining.directory.path' = '/hadoop/u01/app/oradata',
        |	'debezium.mining.directory.name'='dictionary.ora',
        |	'debezium.log.mining.strategy' = 'offline_catalog',
        |	'debezium.decimal.handling.mode'='STRING',
        |	//'debezium.database.archivelog_path_convert'='/opt/oracle/fast_recovery_area/ORACDB2:/opt/oracle/cv_fast_recovery_area/ORACDB2',
        |	//'debezium.database.redo_path_convert'='/opt/oracle/oradata/ORACDB2:/opt/oracle/cv',
        |	'debezium.internal.log.mining.transaction.snapshot.boundary.mode'='skip',  //跳过正在进行的事务
        |	'debezium.log.mining.continuous.mine' = 'false' )
        |""".stripMargin)
    tableEnv.executeSql(
      """
        |CREATE TABLE table_customers_kafka (
        |ID INT,
        |NAME STRING
        |) WITH (
        |    'connector' = 'kafka',
        |    'topic' = 'flink_standby_test01',
        |    'properties.bootstrap.servers' = 'hdp04:6667',
        |    'format' = 'debezium-json',
        |    'scan.startup.mode' = 'earliest-offset'
        |);
        |""".stripMargin)
    tableEnv.executeSql("insert into table_customers_kafka select * from table_source_oracle  ")

    //env.execute()
  }

}
