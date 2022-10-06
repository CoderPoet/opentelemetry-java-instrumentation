/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.rpc;

import com.google.auto.value.AutoValue;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.instrumentation.api.instrumenter.OperationListener;
import io.opentelemetry.instrumentation.api.instrumenter.OperationMetrics;
import io.opentelemetry.instrumentation.api.instrumenter.operation.OperationMetricsView;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.opentelemetry.instrumentation.api.instrumenter.rpc.MetricsView.applyServerView;
import static java.util.logging.Level.FINE;

/**
 * {@link OperationListener} which keeps track of <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/semantic_conventions/rpc.md#rpc-server">RPC
 * server metrics</a>.
 */
public final class RpcServerMetrics implements OperationListener {

  private static final double NANOS_PER_MS = TimeUnit.MILLISECONDS.toNanos(1);

  private static final ContextKey<RpcServerMetrics.State> RPC_SERVER_REQUEST_METRICS_STATE =
      ContextKey.named("rpc-server-request-metrics-state");

  private static final Logger logger = Logger.getLogger(RpcServerMetrics.class.getName());

  private final LongCounter requestsTotal;
  private final DoubleHistogram requestDuration;
  private final DoubleHistogram serverDurationHistogram;

  private RpcServerMetrics(Meter meter) {
    requestsTotal =
        meter
            .counterBuilder("aos_requests_total")
            .setDescription("This is a COUNTER incremented for every request handled")
            .build();
    requestDuration =
        meter
            .histogramBuilder("aos_request_duration_milliseconds")
            .setDescription("This is a DISTRIBUTION which measures the duration of requests")
            .build();
    serverDurationHistogram =
        meter
            .histogramBuilder("rpc.server.duration")
            .setDescription("The duration of an inbound RPC invocation")
            .setUnit("ms")
            .build();
  }

  /**
   * Returns a {@link OperationMetrics} which can be used to enable recording of {@link
   * RpcServerMetrics} on an {@link
   * io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder}.
   */
  public static OperationMetrics get() {
    return RpcServerMetrics::new;
  }

  @Override
  public Context onStart(Context context, Attributes startAttributes, long startNanos) {
    return context.with(
        RPC_SERVER_REQUEST_METRICS_STATE,
        new AutoValue_RpcServerMetrics_State(startAttributes, startNanos));
  }

  @Override
  public void onEnd(Context context, Attributes endAttributes, long endNanos) {
    State state = context.get(RPC_SERVER_REQUEST_METRICS_STATE);
    if (state == null) {
      logger.log(
          FINE,
          "No state present when ending context {0}. Cannot record RPC request metrics.",
          context);
      return;
    }
    double durationTime = (endNanos - state.startTimeNanos()) / NANOS_PER_MS;

    serverDurationHistogram.record(
        durationTime, applyServerView(state.startAttributes(), endAttributes), context);

    // operation metrics
    Attributes operationAttributes =
        OperationMetricsView.applyClientView(state.startAttributes(), endAttributes);
    requestsTotal.add(1, operationAttributes, context);
    requestDuration.record(durationTime, operationAttributes, context);
  }

  @AutoValue
  abstract static class State {

    abstract Attributes startAttributes();

    abstract long startTimeNanos();
  }
}
