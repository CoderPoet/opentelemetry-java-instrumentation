package io.opentelemetry.instrumentation.api.instrumenter.operation;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public final class OperationMetricsView {

  private static final Set<AttributeKey> alwaysInclude = buildAlwaysInclude();

  private static Set<AttributeKey> buildAlwaysInclude() {
    Set<AttributeKey> view = new HashSet<>();
    view.add(OperationSemanticAttributes.DESTINATION_CANONICAL_SERVICE);
    view.add(OperationSemanticAttributes.SOURCE_CANONICAL_SERVICE);
    view.add(OperationSemanticAttributes.CANONICAL_SERVICE);
    view.add(OperationSemanticAttributes.OPERATION);
    view.add(OperationSemanticAttributes.REQUEST_PROTOCOL);
    view.add(OperationSemanticAttributes.RESPONSE_CODE);
    view.add(OperationSemanticAttributes.SPAN_KIND);
    view.add(OperationSemanticAttributes.STATUS_CODE);
    return view;
  }

  private static Set<AttributeKey> buildClientView() {
    Set<AttributeKey> view = new HashSet<>(alwaysInclude);
    return view;
  }

  public static Attributes applyClientView(Attributes startAttributes, Attributes endAttributes) {
    return applyView(alwaysInclude, startAttributes, endAttributes);
  }

  public static Attributes applyServerView(Attributes startAttributes, Attributes endAttributes) {
    return applyView(alwaysInclude, startAttributes, endAttributes);
  }

  public static Attributes applyView(
      Set<AttributeKey> view, Attributes startAttributes, Attributes endAttributes) {
    AttributesBuilder filtered = Attributes.builder();
    applyView(filtered, startAttributes, view);
    applyView(filtered, endAttributes, view);
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

  private OperationMetricsView() {}
}
