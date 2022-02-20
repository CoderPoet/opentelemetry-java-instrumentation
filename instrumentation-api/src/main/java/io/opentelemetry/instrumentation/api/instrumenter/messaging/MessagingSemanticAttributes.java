package io.opentelemetry.instrumentation.api.instrumenter.messaging;

import io.opentelemetry.api.common.AttributeKey;

public final class MessagingSemanticAttributes {
  public static final AttributeKey<String> MESSAGING_CONSUMER_GROUP =
      AttributeKey.stringKey("messaging.consumer_group");
  public static final AttributeKey<Long> MESSAGING_PARTITION =
      AttributeKey.longKey("messaging.partition");
  public static final AttributeKey<Long> SOURCE_CANONICAL_SERVICE =
      AttributeKey.longKey("source_canonical_service");
}
