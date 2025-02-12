/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.playws;

import io.opentelemetry.instrumentation.api.instrumenter.http.HttpClientAttributesGetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import java.util.List;
import javax.annotation.Nullable;
import play.shaded.ahc.org.asynchttpclient.Request;
import play.shaded.ahc.org.asynchttpclient.Response;

final class PlayWsClientHttpAttributesGetter
    implements HttpClientAttributesGetter<Request, Response> {

  @Override
  public String method(Request request) {
    return request.getMethod();
  }

  @Override
  public String url(Request request) {
    return request.getUri().toUrl();
  }

  @Override
  public List<String> requestHeader(Request request, String name) {
    return request.getHeaders().getAll(name);
  }

  @Override
  public Integer statusCode(Request request, Response response, @Nullable Throwable error) {
    return response.getStatusCode();
  }

  @Override
  public String flavor(Request request, @Nullable Response response) {
    return SemanticAttributes.HttpFlavorValues.HTTP_1_1;
  }

  @Override
  public List<String> responseHeader(Request request, Response response, String name) {
    return response.getHeaders().getAll(name);
  }
}
