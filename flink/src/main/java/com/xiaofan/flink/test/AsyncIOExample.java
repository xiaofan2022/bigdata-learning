/*
package com.xiaofan.flink.test;

import com.xiaofan.flink.AsyncMysqlSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.streaming.api.functions.source.datagen.DataGeneratorSource;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

*/
/**
 * Example to illustrate how to use {@link AsyncFunction}.  An example of {@link AsyncFunction} using an async client to query an external service.
 *//*

public class AsyncIOExample {

    */
/** An example of {@link AsyncFunction} using an async client to query an external service. *//*

    private static class SampleAsyncFunction extends RichAsyncFunction<Integer, String> {
        private static final long serialVersionUID = 1L;

        private transient AsyncMysqlSource.MysqlClient client;

        @Override
        public void open(Configuration parameters) {
            client = new AsyncMysqlSource.MysqlClient();
        }

        @Override
        public void asyncInvoke(final Integer input, final ResultFuture<String> resultFuture) {
            client.queryStudent(input);
        }
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);

        final String mode;
        final long timeout;

        try {
            mode = params.get("waitMode", "ordered");
            timeout = params.getLong("timeout", 10000L);
        } catch (Exception e) {
            System.out.println(
                    "To customize example, use: AsyncIOExample [--waitMode <ordered or unordered>]");
            throw e;
        }

        // obtain execution environment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataGeneratorSource<Integer> generatorSource =
                new DataGeneratorSource<>(
                        Long::intValue,
                        Integer.MAX_VALUE,
                        RateLimiterStrategy.perSecond(100),
                        Types.INT);

        // create input stream of a single integer
        DataStream<Integer> inputStream =
                env.fromSource(
                        generatorSource,
                        WatermarkStrategy.noWatermarks(),
                        "Integers-generating Source");

        AsyncFunction<Integer, String> function = new SampleAsyncFunction();

        // add async operator to streaming job
        DataStream<String> result;
        switch (mode.toUpperCase()) {
            case "ORDERED":
                result =
                        AsyncDataStream.orderedWait(
                                inputStream, function, timeout, TimeUnit.MILLISECONDS, 20);
                break;
            case "UNORDERED":
                result =
                        AsyncDataStream.unorderedWait(
                                inputStream, function, timeout, TimeUnit.MILLISECONDS, 20);
                break;
            default:
                throw new IllegalStateException("Unknown mode: " + mode);
        }

        result.print();

        // execute the program
        env.execute("Async IO Example: " + mode);
    }
}*/
