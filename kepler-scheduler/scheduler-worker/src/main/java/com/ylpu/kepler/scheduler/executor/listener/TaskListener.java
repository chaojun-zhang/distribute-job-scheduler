package com.ylpu.kepler.scheduler.executor.listener;

import com.ylpu.kepler.scheduler.core.config.Configuration;
import com.ylpu.kepler.scheduler.enums.MetricAlert;

import java.util.Properties;

public class TaskListener implements ITaskListener{

    @Override
    public void onFailure(Event event) {
        
        IMetricProducer producer = null;
        String metricAlert = Configuration.getString(Configuration.getConfig(),
                "kepler.metric.alert",MetricAlert.KAFKA.toString()); 
        if(MetricAlert.getAlertMetric(metricAlert) == MetricAlert.KAFKA) {
            Properties props = Configuration.getConfig("kafka_metric.properties");
            producer = new KafkaMetricProducer(props);
            producer.send(event);
        }else if(MetricAlert.getAlertMetric(metricAlert) == MetricAlert.JDBC) {
            Properties props = Configuration.getConfig("jdbc_metric.properties");
            producer = new JdbcMetricProducer(props);
            producer.send(event);
        }
    } 
}