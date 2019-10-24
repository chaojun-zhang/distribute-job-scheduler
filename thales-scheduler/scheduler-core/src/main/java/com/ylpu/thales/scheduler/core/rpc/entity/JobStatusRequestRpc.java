// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: worker.proto

package com.ylpu.thales.scheduler.core.rpc.entity;

/**
 * Protobuf type {@code JobStatusRequestRpc}
 */
public  final class JobStatusRequestRpc extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:JobStatusRequestRpc)
    JobStatusRequestRpcOrBuilder {
  // Use JobStatusRequestRpc.newBuilder() to construct.
  private JobStatusRequestRpc(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private JobStatusRequestRpc() {
    requestId_ = "";
    taskState_ = 0;
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private JobStatusRequestRpc(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            requestId_ = s;
            break;
          }
          case 24: {

            taskState_ = input.readInt32();
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.ylpu.thales.scheduler.core.rpc.entity.WorkerGrpc.internal_static_JobStatusRequestRpc_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.ylpu.thales.scheduler.core.rpc.entity.WorkerGrpc.internal_static_JobStatusRequestRpc_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.class, com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.Builder.class);
  }

  public static final int REQUESTID_FIELD_NUMBER = 1;
  private volatile java.lang.Object requestId_;
  /**
   * <code>string requestId = 1;</code>
   */
  public java.lang.String getRequestId() {
    java.lang.Object ref = requestId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      requestId_ = s;
      return s;
    }
  }
  /**
   * <code>string requestId = 1;</code>
   */
  public com.google.protobuf.ByteString
      getRequestIdBytes() {
    java.lang.Object ref = requestId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      requestId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TASKSTATE_FIELD_NUMBER = 3;
  private int taskState_;
  /**
   * <code>int32 taskState = 3;</code>
   */
  public int getTaskState() {
    return taskState_;
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getRequestIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, requestId_);
    }
    if (taskState_ != 0) {
      output.writeInt32(3, taskState_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getRequestIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, requestId_);
    }
    if (taskState_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, taskState_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc)) {
      return super.equals(obj);
    }
    com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc other = (com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc) obj;

    boolean result = true;
    result = result && getRequestId()
        .equals(other.getRequestId());
    result = result && (getTaskState()
        == other.getTaskState());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + REQUESTID_FIELD_NUMBER;
    hash = (53 * hash) + getRequestId().hashCode();
    hash = (37 * hash) + TASKSTATE_FIELD_NUMBER;
    hash = (53 * hash) + getTaskState();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code JobStatusRequestRpc}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:JobStatusRequestRpc)
      com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpcOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ylpu.thales.scheduler.core.rpc.entity.WorkerGrpc.internal_static_JobStatusRequestRpc_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ylpu.thales.scheduler.core.rpc.entity.WorkerGrpc.internal_static_JobStatusRequestRpc_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.class, com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.Builder.class);
    }

    // Construct using com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      requestId_ = "";

      taskState_ = 0;

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.ylpu.thales.scheduler.core.rpc.entity.WorkerGrpc.internal_static_JobStatusRequestRpc_descriptor;
    }

    public com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc getDefaultInstanceForType() {
      return com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.getDefaultInstance();
    }

    public com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc build() {
      com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc buildPartial() {
      com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc result = new com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc(this);
      result.requestId_ = requestId_;
      result.taskState_ = taskState_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc) {
        return mergeFrom((com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc other) {
      if (other == com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc.getDefaultInstance()) return this;
      if (!other.getRequestId().isEmpty()) {
        requestId_ = other.requestId_;
        onChanged();
      }
      if (other.getTaskState() != 0) {
        setTaskState(other.getTaskState());
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object requestId_ = "";
    /**
     * <code>string requestId = 1;</code>
     */
    public java.lang.String getRequestId() {
      java.lang.Object ref = requestId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        requestId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string requestId = 1;</code>
     */
    public com.google.protobuf.ByteString
        getRequestIdBytes() {
      java.lang.Object ref = requestId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        requestId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string requestId = 1;</code>
     */
    public Builder setRequestId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      requestId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string requestId = 1;</code>
     */
    public Builder clearRequestId() {
      
      requestId_ = getDefaultInstance().getRequestId();
      onChanged();
      return this;
    }
    /**
     * <code>string requestId = 1;</code>
     */
    public Builder setRequestIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      requestId_ = value;
      onChanged();
      return this;
    }

    private int taskState_ ;
    /**
     * <code>int32 taskState = 3;</code>
     */
    public int getTaskState() {
      return taskState_;
    }
    /**
     * <code>int32 taskState = 3;</code>
     */
    public Builder setTaskState(int value) {
      
      taskState_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 taskState = 3;</code>
     */
    public Builder clearTaskState() {
      
      taskState_ = 0;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:JobStatusRequestRpc)
  }

  // @@protoc_insertion_point(class_scope:JobStatusRequestRpc)
  private static final com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc();
  }

  public static com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<JobStatusRequestRpc>
      PARSER = new com.google.protobuf.AbstractParser<JobStatusRequestRpc>() {
    public JobStatusRequestRpc parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new JobStatusRequestRpc(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<JobStatusRequestRpc> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<JobStatusRequestRpc> getParserForType() {
    return PARSER;
  }

  public com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
