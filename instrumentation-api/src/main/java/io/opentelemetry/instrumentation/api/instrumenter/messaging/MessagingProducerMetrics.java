package io.opentelemetry.instrumentation.api.instrumenter.messaging;

import com.google.auto.value.AutoValue;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.ContextKey;
import io.opentelemetry.instrumentation.api.annotations.UnstableApi;
import io.opentelemetry.instrumentation.api.instrumenter.RequestListener;
import io.opentelemetry.instrumentation.api.instrumenter.RequestMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

import static io.opentelemetry.instrumentation.api.instrumenter.messaging.MetricsView.applyProducerDurationView;

public class MessagingProducerMetrics implements RequestListener {

  private static final double NANOS_PER_MS = TimeUnit.MILLISECONDS.toNanos(1);

  private static final ContextKey<State> KAFKA_PRODUCER_REQUEST_METRICS_STATE =
      ContextKey.named("kafka-producer-request-metrics-state");

  private static final Logger logger = LoggerFactory.getLogger(MessagingProducerMetrics.class);

  private final DoubleHistogram duration;

  public MessagingProducerMetrics(Meter meter) {
    duration =
        meter
            .histogramBuilder("messaging.io.duration")
            .setUnit("ms")
            .setDescription("The duration of the message produce")
            .build();
  }

  @UnstableApi
  public static RequestMetrics get() {
    return MessagingProducerMetrics::new;
  }

  @Override
  public Context start(Context context, Attributes startAttributes, long startNanos) {
    return context.with(
        KAFKA_PRODUCER_REQUEST_METRICS_STATE,
        new AutoValue_MessagingProducerMetrics_State(startAttributes, startNanos));
  }

  @Override
  public void end(Context context, Attributes endAttributes, long endNanos) {
    State state = context.get(KAFKA_PRODUCER_REQUEST_METRICS_STATE);
    if (state == null) {
      logger.debug(
          "No state present when ending context {}. Cannot record message produce metrics.",
          context);
      return;
    }
    duration.record(
        (endNanos - state.startTimeNanos()) / NANOS_PER_MS,
        applyProducerDurationView(state.startAttributes(), endAttributes),
        context);
  }

  @AutoValue
  abstract static class State {

    abstract Attributes startAttributes();

    abstract long startTimeNanos();
  }
}
