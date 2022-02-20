package io.opentelemetry.instrumentation.api.instrumenter.messaging;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

@SuppressWarnings("rawtypes")
public class MetricsView {
  private static final Set<AttributeKey> durationAlwaysInclude = buildDurationAlwaysInclude();

  private static final Set<AttributeKey> durationProducerView = buildDurationProducerView();

  private static final Set<AttributeKey> durationConsumerView = buildDurationConsumerView();

  private static final Set<AttributeKey> messagesProducerView = buildProducerMessagesView();

  private static final Set<AttributeKey> messagesConsumerView = buildConsumerMessagesView();

  private static Set<AttributeKey> buildDurationAlwaysInclude() {
    Set<AttributeKey> view = new HashSet<>();
    view.add(SemanticAttributes.MESSAGING_DESTINATION);
    view.add(SemanticAttributes.MESSAGING_DESTINATION_KIND);
    view.add(SemanticAttributes.MESSAGING_SYSTEM);
    view.add(SemanticAttributes.MESSAGING_URL);
    view.add(SemanticAttributes.MESSAGING_OPERATION);
    view.add(SemanticAttributes.MESSAGING_TEMP_DESTINATION);
    view.add(SemanticAttributes.MESSAGING_PROTOCOL);
    view.add(SemanticAttributes.MESSAGING_PROTOCOL_VERSION);

    view.add(SemanticAttributes.MESSAGING_KAFKA_CLIENT_ID);
    view.add(SemanticAttributes.MESSAGING_KAFKA_CONSUMER_GROUP);
    view.add(SemanticAttributes.MESSAGING_KAFKA_PARTITION);

    view.add(SemanticAttributes.MESSAGING_ROCKETMQ_NAMESPACE);
    view.add(SemanticAttributes.MESSAGING_ROCKETMQ_CLIENT_GROUP);
    view.add(SemanticAttributes.MESSAGING_ROCKETMQ_MESSAGE_TYPE);
    view.add(SemanticAttributes.MESSAGING_ROCKETMQ_CLIENT_ID);

    view.add(SemanticAttributes.MESSAGE_TYPE);

    view.add(MessagingSemanticAttributes.MESSAGING_CONSUMER_GROUP);
    view.add(MessagingSemanticAttributes.MESSAGING_PARTITION);

    return view;
  }

  private static Set<AttributeKey> buildDurationProducerView() {
    return new HashSet<>(durationAlwaysInclude);
  }

  private static Set<AttributeKey> buildDurationConsumerView() {
    Set<AttributeKey> view = new HashSet<>(durationAlwaysInclude);
    view.add(MessagingSemanticAttributes.SOURCE_CANONICAL_SERVICE);
    return view;
  }

  private static Set<AttributeKey> buildProducerMessagesView() {
    return new HashSet<>(durationAlwaysInclude);
  }

  private static Set<AttributeKey> buildConsumerMessagesView() {
    return new HashSet<>(durationAlwaysInclude);
  }

  static Attributes applyProducerDurationView(
      Attributes startAttributes, Attributes endAttributes) {
    AttributesBuilder filtered = Attributes.builder();
    applyView(filtered, startAttributes, durationProducerView);
    applyView(filtered, endAttributes, durationProducerView);
    return filtered.build();
  }

  static Attributes applyProducerMessagesView(
      Attributes startAttributes, Attributes endAttributes) {
    AttributesBuilder filtered = Attributes.builder();
    applyView(filtered, startAttributes, messagesProducerView);
    applyView(filtered, endAttributes, messagesProducerView);
    return filtered.build();
  }

  static Attributes applyConsumerDurationView(
      Attributes startAttributes, Attributes endAttributes) {
    AttributesBuilder filtered = Attributes.builder();
    applyView(filtered, startAttributes, durationConsumerView);
    applyView(filtered, endAttributes, durationConsumerView);
    return filtered.build();
  }

  static Attributes applyConsumerMessagesView(
      Attributes startAttributes, Attributes endAttributes) {
    AttributesBuilder filtered = Attributes.builder();
    applyView(filtered, startAttributes, messagesConsumerView);
    applyView(filtered, endAttributes, messagesConsumerView);
    return filtered.build();
  }

  @SuppressWarnings("unchecked")
  private static void applyView(
      AttributesBuilder filtered, Attributes attributes, Set<AttributeKey> view) {
    attributes.forEach(
        (BiConsumer<AttributeKey, Object>)
            (key, value) -> {
              if (view.contains(key)) {
                filtered.put(key, value);
              }
            });
  }
}
