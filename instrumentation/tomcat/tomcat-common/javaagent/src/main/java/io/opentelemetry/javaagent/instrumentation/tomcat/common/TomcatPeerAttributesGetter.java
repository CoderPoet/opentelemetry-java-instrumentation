/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tomcat.common;

import io.opentelemetry.instrumentation.api.instrumenter.peer.PeerAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.peer.PeerResourceAttributes;
import org.apache.coyote.Request;

import javax.annotation.Nullable;

public class TomcatPeerAttributesGetter implements PeerAttributesGetter<Request> {
  
  @Nullable
  @Override
  public String peerServiceName(Request request) {
    return request.getHeader(PeerResourceAttributes.PEER_SERVICE);
  }
  
  @Nullable
  @Override
  public String peerServiceNamespace(Request request) {
    return request.getHeader(PeerResourceAttributes.PEER_SERVICE_NAMESPACE);
  }
  
  @Nullable
  @Override
  public String peerServiceVersion(Request request) {
    return request.getHeader(PeerResourceAttributes.PEER_SERVICE_VERSION);
  }
  
  @Nullable
  @Override
  public String peerServiceInstanceID(Request request) {
    return request.getHeader(PeerResourceAttributes.PEER_SERVICE_INSTANCE_ID);
  }
  
  @Nullable
  @Override
  public String peerDeploymentEnvironment(Request request) {
    return request.getHeader(PeerResourceAttributes.PEER_DEPLOYMENT_ENVIRONMENT);
  }
}
