package com.xiaofan.flink.cdc;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

/**
 * @author twan
 * @version 1.0
 * @description 自定义cdc反序列化类
 * @date 2024-04-22 18:59:02
 */
public class MyJsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
    @Override
    public void deserialize(SourceRecord record, Collector<String> out) throws Exception {
        //1.创建 JSON 对象用于存储最终数据
        JSONObject result = new JSONObject();
        //2.获取库名、表名放入 source
        JSONObject databaseTableJson = getDabaseTable(record);
        //3.获取"before"数据
        JSONObject beforeJson = getDataJson(record, "before");
        //4.获取"after"数据
        JSONObject afterJson = getDataJson(record, "after");
        //5.获取操作类型 CREATE UPDATE DELETE 进行符合 Debezium-op 的字母
        String type = getOP(record);
        //6.将字段写入 JSON 对象
        result.put("source", databaseTableJson);
        result.put("before", beforeJson);
        result.put("after", afterJson);
        result.put("op", type);
        //7.输出数据
        out.collect(result.toJSONString());
    }

    public String getOP(SourceRecord sourceRecord) {
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
//        String type = operation.toString().toLowerCase();
        String type = operation.code();
        return type;
    }

    private JSONObject getDabaseTable(SourceRecord sourceRecord) {
        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String database = fields[1];
        String tableName = fields[2];
        JSONObject ret = new JSONObject();
        ret.put("database", database);
        ret.put("table", tableName);
        return ret;
    }

    public JSONObject getDataJson(SourceRecord sourceRecord, String fieldName) {
        Struct value = (Struct) sourceRecord.value();
        Struct struct = value.getStruct(fieldName);
        JSONObject ret = new JSONObject();
        if (struct != null) {
            Schema schema = struct.schema();
            List<Field> fields = schema.fields();
            for (Field field : fields) {
                Object obj = struct.get(field);
                ret.put(field.name(), obj);
            }
        }
        return ret;
    }

    @Override
    public TypeInformation<String> getProducedType() {
        // 表示返回String类型
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
