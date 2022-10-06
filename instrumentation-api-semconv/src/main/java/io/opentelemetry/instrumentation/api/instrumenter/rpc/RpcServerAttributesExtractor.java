/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.rpc;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.operation.OperationSemanticAttributes;
import io.opentelemetry.instrumentation.api.internal.SpanKey;
import io.opentelemetry.instrumentation.api.internal.SpanKeyProvider;

import javax.annotation.Nullable;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;

/**
 * Extractor of <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/rpc.md">RPC
 * server attributes</a>.
 *
 * <p>This class delegates to a type-specific {@link RpcAttributesGetter} for individual attribute
 * extraction from request/response objects.
 */
public final class RpcServerAttributesExtractor<REQUEST, RESPONSE>
    extends RpcCommonAttributesExtractor<REQUEST, RESPONSE> implements SpanKeyProvider {

  /** Creates the RPC server attributes extractor. */
  public static <REQUEST, RESPONSE> RpcServerAttributesExtractor<REQUEST, RESPONSE> create(
      RpcAttributesGetter<REQUEST> getter) {
    return new RpcServerAttributesExtractor<>(getter);
  }

  private RpcServerAttributesExtractor(RpcAttributesGetter<REQUEST> getter) {
    super(getter);
  }

  @Override
  public final void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {
    internalSet(
        attributes,
        OperationSemanticAttributes.SPAN_KIND,
        OperationSemanticAttributes.SpanKindValues.SERVER);
    internalSet(
        attributes,
        OperationSemanticAttributes.REQUEST_PROTOCOL,
        OperationSemanticAttributes.RequestProtocolValues.RPC);
  }

  @Override
  public final void onEnd(
      AttributesBuilder attributes,
      Context context,
      REQUEST request,
      @Nullable RESPONSE response,
      @Nullable Throwable error) {
    // No response attributes
  }

  /**
   * This method is internal and is hence not for public use. Its API is unstable and can change at
   * any time.
   */
  @Override
  public SpanKey internalGetSpanKey() {
    return SpanKey.RPC_SERVER;
  }
}
