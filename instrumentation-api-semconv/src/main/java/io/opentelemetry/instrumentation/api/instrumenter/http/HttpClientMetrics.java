/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.instrumenter.http;

import com.google.auto.value.AutoValue;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.instrumentation.api.instrumenter.OperationListener;
import io.opentelemetry.instrumentation.api.instrumenter.OperationMetrics;
import io.opentelemetry.instrumentation.api.instrumenter.operation.OperationMetricsView;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static io.opentelemetry.instrumentation.api.instrumenter.http.TemporaryMetricsView.applyClientDurationAndSizeView;
import static java.util.logging.Level.FINE;

/**
 * {@link OperationListener} which keeps track of <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/semantic_conventions/http-metrics.md#http-client">HTTP
 * client metrics</a>.
 */
public final class HttpClientMetrics implements OperationListener {

  private static final double NANOS_PER_MS = TimeUnit.MILLISECONDS.toNanos(1);

  private static final ContextKey<State> HTTP_CLIENT_REQUEST_METRICS_STATE =
      ContextKey.named("http-client-request-metrics-state");

  private static final Logger logger = Logger.getLogger(HttpClientMetrics.class.getName());

  /**
   * Returns a {@link OperationMetrics} which can be used to enable recording of {@link
   * HttpClientMetrics} on an {@link
   * io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder}.
   */
  public static OperationMetrics get() {
    return HttpClientMetrics::new;
  }

  private final LongCounter requestsTotal;
  private final DoubleHistogram requestDuration;
  private final DoubleHistogram duration;
  private final LongHistogram requestSize;
  private final LongHistogram responseSize;

  private HttpClientMetrics(Meter meter) {
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
    duration =
        meter
            .histogramBuilder("http.client.duration")
            .setUnit("ms")
            .setDescription("The duration of the outbound HTTP request")
            .build();
    requestSize =
        meter
            .histogramBuilder("http.client.request.size")
            .setUnit("By")
            .setDescription("The size of HTTP request messages")
            .ofLongs()
            .build();
    responseSize =
        meter
            .histogramBuilder("http.client.response.size")
            .setUnit("By")
            .setDescription("The size of HTTP response messages")
            .ofLongs()
            .build();
  }

  @Override
  public Context onStart(Context context, Attributes startAttributes, long startNanos) {
    return context.with(
        HTTP_CLIENT_REQUEST_METRICS_STATE,
        new AutoValue_HttpClientMetrics_State(startAttributes, startNanos));
  }

  @Override
  public void onEnd(Context context, Attributes endAttributes, long endNanos) {
    State state = context.get(HTTP_CLIENT_REQUEST_METRICS_STATE);
    if (state == null) {
      logger.log(
          FINE,
          "No state present when ending context {0}. Cannot record HTTP request metrics.",
          context);
      return;
    }

    double durationTime = (endNanos - state.startTimeNanos()) / NANOS_PER_MS;

    // duration
    Attributes durationAndSizeAttributes =
        applyClientDurationAndSizeView(state.startAttributes(), endAttributes);
    duration.record(durationTime, durationAndSizeAttributes, context);

    // operation metrics
    Attributes operationAttributes =
        OperationMetricsView.applyClientView(state.startAttributes(), endAttributes);
    requestsTotal.add(1, operationAttributes, context);
    requestDuration.record(durationTime, operationAttributes, context);

    // request and response size
    Long requestLength =
        getAttribute(
            SemanticAttributes.HTTP_REQUEST_CONTENT_LENGTH, endAttributes, state.startAttributes());
    if (requestLength != null) {
      requestSize.record(requestLength, durationAndSizeAttributes);
    }
    Long responseLength =
        getAttribute(
            SemanticAttributes.HTTP_RESPONSE_CONTENT_LENGTH,
            endAttributes,
            state.startAttributes());
    if (responseLength != null) {
      responseSize.record(responseLength, durationAndSizeAttributes);
    }
  }

  @Nullable
  private static <T> T getAttribute(AttributeKey<T> key, Attributes... attributesList) {
    for (Attributes attributes : attributesList) {
      T value = attributes.get(key);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  @AutoValue
  abstract static class State {

    abstract Attributes startAttributes();

    abstract long startTimeNanos();
  }
}
