// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: worker.proto

package com.ylpu.thales.scheduler.core.rpc.entity;

public interface WorkerRequestRpcOrBuilder extends
        // @@protoc_insertion_point(interface_extends:WorkerRequestRpc)
        com.google.protobuf.MessageOrBuilder {

    /**
     * <code>int32 id = 1;</code>
     */
    int getId();

    /**
     * <code>int32 workerType = 2;</code>
     */
    int getWorkerType();

    /**
     * <code>string host = 3;</code>
     */
    java.lang.String getHost();

    /**
     * <code>string host = 3;</code>
     */
    com.google.protobuf.ByteString getHostBytes();

    /**
     * <code>int32 port = 4;</code>
     */
    int getPort();

    /**
     * <code>string workerGroup = 5;</code>
     */
    java.lang.String getWorkerGroup();

    /**
     * <code>string workerGroup = 5;</code>
     */
    com.google.protobuf.ByteString getWorkerGroupBytes();

    /**
     * <code>string zkdirectory = 6;</code>
     */
    java.lang.String getZkdirectory();

    /**
     * <code>string zkdirectory = 6;</code>
     */
    com.google.protobuf.ByteString getZkdirectoryBytes();

    /**
     * <code>double cpuUsage = 7;</code>
     */
    double getCpuUsage();

    /**
     * <code>double memoryUsage = 8;</code>
     */
    double getMemoryUsage();

    /**
     * <code>.google.protobuf.Timestamp lastHeartbeatTime = 9;</code>
     */
    boolean hasLastHeartbeatTime();

    /**
     * <code>.google.protobuf.Timestamp lastHeartbeatTime = 9;</code>
     */
    com.google.protobuf.Timestamp getLastHeartbeatTime();

    /**
     * <code>.google.protobuf.Timestamp lastHeartbeatTime = 9;</code>
     */
    com.google.protobuf.TimestampOrBuilder getLastHeartbeatTimeOrBuilder();

    /**
     * <code>int32 workerStatus = 10;</code>
     */
    int getWorkerStatus();
}
