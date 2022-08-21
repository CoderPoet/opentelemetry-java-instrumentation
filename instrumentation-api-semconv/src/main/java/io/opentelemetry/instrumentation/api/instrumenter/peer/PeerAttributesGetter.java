package io.opentelemetry.instrumentation.api.instrumenter.peer;

import javax.annotation.Nullable;

public interface PeerAttributesGetter<REQUEST> {
    @Nullable
    String peerServiceName(REQUEST request);
    
    @Nullable
    String peerServiceNamespace(REQUEST request);
    
    @Nullable
    String peerServiceVersion(REQUEST request);
    
    @Nullable
    String peerServiceInstanceID(REQUEST request);
    
    @Nullable
    String peerDeploymentEnvironment(REQUEST request);
}
