package com.aliyun.utils;

import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author twan
 * @version 1.0
 * @description yarn任务工具
 * @date 2024-04-25 09:10:23
 */
public class RunningTasksFetcher {
    private static final Logger LOG = LoggerFactory.getLogger(RunningTasksFetcher.class);

    private static final Configuration conf = new Configuration();
    private final YarnClient yarnClient;
    private String dev;

    public RunningTasksFetcher(String dev) {
        this.dev = dev;
        this.yarnClient = initYarnClient();
    }


    private YarnClient initYarnClient() {
        YarnClient client = YarnClient.createYarnClient();
        if ("dev_ambari".equals(dev)) {
            conf.addResource("dev_ambari/yarn-site.xml");
        }
        client.init(conf);
        client.start();
        return client;
    }

    public List<ApplicationReport> getApplications() {
        try {
            return yarnClient.getApplications();
        } catch (Exception e) {
            LOG.error("getApplications error:{}", e);
        }
        return Lists.newArrayList();
    }

    public List<ApplicationReport> getApplicationsByState(YarnApplicationState applicationState) {
        List<ApplicationReport> list = Lists.newArrayList();
        for (ApplicationReport application : getApplications()) {
            if (application.getYarnApplicationState() == applicationState) {
                list.add(application);
            }
        }
        return list;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }
}
