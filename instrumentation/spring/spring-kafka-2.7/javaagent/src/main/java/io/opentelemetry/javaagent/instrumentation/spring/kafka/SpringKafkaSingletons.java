/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.kafka;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.spring.kafka.v2_7.SpringKafkaTelemetry;
import io.opentelemetry.javaagent.bootstrap.internal.ExperimentalConfig;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingConsumerMetrics;
import io.opentelemetry.javaagent.bootstrap.internal.InstrumentationConfig;

public final class SpringKafkaSingletons {

  private static final SpringKafkaTelemetry TELEMETRY =
      SpringKafkaTelemetry.builder(GlobalOpenTelemetry.get())
          .setCaptureExperimentalSpanAttributes(
              InstrumentationConfig.get()
                  .getBoolean("otel.instrumentation.kafka.experimental-span-attributes", false))
          .setPropagationEnabled(
              InstrumentationConfig.get()
                  .getBoolean("otel.instrumentation.kafka.client-propagation.enabled", true))
          .setMessagingReceiveInstrumentationEnabled(
              ExperimentalConfig.get().messagingReceiveInstrumentationEnabled())
          .build();

  private static final Instrumenter<ConsumerRecords<?, ?>, Void> PROCESS_INSTRUMENTER =
      buildProcessInstrumenter();

  private static Instrumenter<ConsumerRecords<?, ?>, Void> buildProcessInstrumenter() {
    KafkaBatchProcessAttributesExtractor attributesExtractor =
        new KafkaBatchProcessAttributesExtractor();
    SpanNameExtractor<ConsumerRecords<?, ?>> spanNameExtractor =
        MessagingSpanNameExtractor.create(attributesExtractor);

    return Instrumenter.<ConsumerRecords<?, ?>, Void>builder(
            GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanNameExtractor)
        .addAttributesExtractor(attributesExtractor)
        .addSpanLinksExtractor(
            new KafkaBatchProcessSpanLinksExtractor(GlobalOpenTelemetry.getPropagators()))
        .setErrorCauseExtractor(new KafkaBatchErrorCauseExtractor())
        .addRequestMetrics(MessagingConsumerMetrics.get())
        .newInstrumenter(SpanKindExtractor.alwaysConsumer());
  }

  public static Instrumenter<ConsumerRecord<?, ?>, Void> receiveInstrumenter() {
    return null;
  }

  public static Instrumenter<ConsumerRecords<?, ?>, Void> processInstrumenter() {
    return PROCESS_INSTRUMENTER;
  }

  private SpringKafkaSingletons() {}
}
