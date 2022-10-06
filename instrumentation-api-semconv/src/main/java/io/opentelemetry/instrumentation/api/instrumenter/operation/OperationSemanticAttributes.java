/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.operation;

import io.opentelemetry.api.common.AttributeKey;

public class OperationSemanticAttributes {
  private OperationSemanticAttributes() {}

  public static final AttributeKey<String> DESTINATION_CANONICAL_SERVICE =
      AttributeKey.stringKey("destination_canonical_service");
  public static final AttributeKey<String> SOURCE_CANONICAL_SERVICE =
      AttributeKey.stringKey("source_canonical_service");
  public static final AttributeKey<String> CANONICAL_SERVICE =
      AttributeKey.stringKey("canonical_service");
  public static final AttributeKey<String> OPERATION = AttributeKey.stringKey("operation");
  public static final AttributeKey<String> REQUEST_PROTOCOL =
      AttributeKey.stringKey("request_protocol");
  public static final AttributeKey<String> SPAN_KIND = AttributeKey.stringKey("span_kind");
  public static final AttributeKey<String> STATUS_CODE = AttributeKey.stringKey("status_code");
  public static final AttributeKey<Long> RESPONSE_CODE = AttributeKey.longKey("response_code");

  public static final class RequestProtocolValues {
    public static final String HTTP = "http";
    public static final String RPC = "rpc";

    private RequestProtocolValues() {}
  }

  public static final class SpanKindValues {
    public static final String CLIENT = "client";
    public static final String SERVER = "server";

    private SpanKindValues() {}
  }
}
