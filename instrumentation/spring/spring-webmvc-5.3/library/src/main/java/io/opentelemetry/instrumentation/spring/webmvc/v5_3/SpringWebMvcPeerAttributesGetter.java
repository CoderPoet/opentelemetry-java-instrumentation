package io.opentelemetry.instrumentation.spring.webmvc.v5_3;

import io.opentelemetry.instrumentation.api.instrumenter.peer.PeerAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.peer.PeerResourceAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Nullable;


public class SpringWebMvcPeerAttributesGetter
    implements PeerAttributesGetter<HttpServletRequest> {
    
    @Nullable
    @Override
    public String peerServiceName(HttpServletRequest request) {
        return request.getHeader(PeerResourceAttributes.PEER_SERVICE);
    }
    
    @Nullable
    @Override
    public String peerServiceNamespace(HttpServletRequest request) {
        return request.getHeader(PeerResourceAttributes.PEER_SERVICE_NAMESPACE);
    }
    
    @Nullable
    @Override
    public String peerServiceVersion(HttpServletRequest request) {
        return request.getHeader(PeerResourceAttributes.PEER_SERVICE_VERSION);
    }
    
    @Nullable
    @Override
    public String peerServiceInstanceID(HttpServletRequest request) {
        return request.getHeader(PeerResourceAttributes.PEER_SERVICE_INSTANCE_ID);
    }
    
    @Nullable
    @Override
    public String peerDeploymentEnvironment(HttpServletRequest request) {
        return request.getHeader(PeerResourceAttributes.PEER_DEPLOYMENT_ENVIRONMENT);
    }
}
