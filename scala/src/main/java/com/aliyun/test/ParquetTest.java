package com.aliyun.test;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.schema.Type;

import java.io.IOException;
import java.util.List;

/**
 * @author twan
 * @version 1.0
 * @description
 * @date 2024-04-14 17:55:31
 */
public class ParquetTest {

    public static void main(String[] args) throws Exception {

//        parquetWriter("test\\parquet-out2","input.txt");
        parquetReaderV2("D:\\data\\warehouse\\hudi\\hudi_constomer_utc\\dt=2020-06-10\\57b5b414-188b-497d-b0fe-a4609d6dfd7a_1-8-0_20240415101042173.parquet");
    }


    static void parquetReaderV2(String inPath) throws Exception {
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(inPath));
        ParquetReader<Group> build = reader.build();
        Group line = null;
        while ((line = build.read()) != null) {
            List<Type> fields = line.getType().getFields();
            for (int i = 5; i < fields.size(); i++) {
                Group valueGroup = line.getGroup(fields.get(i).getName(), i);
                System.out.println("type" + valueGroup.getType() + ",field" + fields.get(i) + ",value:" + valueGroup);
            }


//通过下标和字段名称都可以获取
/*System.out.println(line.getString(0, 0)+"\t"+
　　　　　　　　line.getString(1, 0)+"\t"+
　　　　　　　　time.getInteger(0, 0)+"\t"+
　　　　　　　　time.getString(1, 0)+"\t");*/

//System.out.println(line.toString());

        }
        System.out.println("读取结束");
    }

    //新版本中new ParquetReader()所有构造方法好像都弃用了,用上面的builder去构造对象
    static void parquetReader(String inPath) throws Exception {
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader<Group> reader = new ParquetReader<Group>(new Path(inPath), readSupport);
        Group line = null;
        while ((line = reader.read()) != null) {
            System.out.println(line);
        }
        System.out.println("读取结束");

    }
    /**
     *
     * @param outPath　　输出Parquet格式
     * @param inPath  输入普通文本文件
     * @throws IOException
     */
/*    static void parquetWriter(String outPath,String inPath) throws IOException {
        MessageType schema = MessageTypeParser.parseMessageType("message Pair {\n" +
                        " required binary city (UTF8);\n" +
                        " required binary ip (UTF8);\n" +
                        " repeated group time {\n"+
                　　" required int32 ttl;\n"+
                　　 " required binary ttl2;\n"+
                        "}\n"+
                        "}");
        GroupFactory factory = new SimpleGroupFactory(schema);
        Path path = new Path(outPath);
        Configuration configuration = new Configuration();
        GroupWriteSupport writeSupport = new GroupWriteSupport();
        writeSupport.setSchema(schema,configuration);
        ParquetWriter<Group> writer = new ParquetWriter<Group>(path,configuration,writeSupport);
　　　　//把本地文件读取进去，用来生成parquet格式文件
        BufferedReader br =new BufferedReader(new FileReader(new File(inPath)));
        String line="";
        Random r=new Random();
        while((line=br.readLine())!=null){
            String[] strs=line.split("\\s+");
            if(strs.length==2) {
                Group group = factory.newGroup()
                        .append("city",strs[0])
                        .append("ip",strs[1]);
                Group tmpG =group.addGroup("time");
                tmpG.append("ttl", r.nextInt(9)+1);
                tmpG.append("ttl2", r.nextInt(9)+"_a");
                writer.write(group);
            }
        }
        System.out.println("write end");
        writer.close();
    }*/
}
