package io.opentelemetry.instrumentation.api.instrumenter.peer;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import javax.annotation.Nullable;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;

public final class PeerAttributesExtractor<REQUEST, RESPONSE>
    implements AttributesExtractor<REQUEST, RESPONSE> {
    
    private final PeerAttributesGetter<REQUEST> getter;
    
    public static <REQUEST, RESPONSE> PeerAttributesExtractor<REQUEST, RESPONSE> create(
            PeerAttributesGetter<REQUEST> getter) {
        return new PeerAttributesExtractor<>(getter);
    }
    
    public PeerAttributesExtractor(PeerAttributesGetter<REQUEST> getter) {
        this.getter = getter;
    }
    
    @Override
    public void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {
        String peerServiceName = getter.peerServiceName(request);
        if (peerServiceName != null) {
            internalSet(attributes, SemanticAttributes.PEER_SERVICE, getter.peerServiceName(request));
        }
    
        String peerServiceNamespace = getter.peerServiceNamespace(request);
        if (peerServiceNamespace != null) {
            internalSet(attributes, SemanticAttributes.PEER_SERVICE_NAMESPACE, peerServiceNamespace);
        }
    
        String peerDeploymentEnvironment = getter.peerDeploymentEnvironment(request);
        if (peerDeploymentEnvironment != null) {
            internalSet(attributes, SemanticAttributes.PEER_DEPLOYMENT_ENVIRONMENT, peerDeploymentEnvironment);
        }
    
        String peerServiceVersion = getter.peerServiceVersion(request);
        if (peerServiceVersion != null) {
            internalSet(attributes, SemanticAttributes.PEER_SERVICE_VERSION, peerServiceVersion);
        }
    
        String peerServiceInstanceID = getter.peerServiceInstanceID(request);
        if (peerServiceInstanceID != null) {
            internalSet(attributes, SemanticAttributes.PEER_SERVICE_INSTANCE_ID, peerServiceInstanceID);
        }
    }
    
    @Override
    public void onEnd(AttributesBuilder attributes,
            Context context,
            REQUEST request,
            @Nullable RESPONSE response,
            @Nullable Throwable error) {}
}
