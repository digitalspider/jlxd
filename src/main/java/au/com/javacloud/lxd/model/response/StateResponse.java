package au.com.javacloud.lxd.model.response;

import au.com.javacloud.lxd.model.State;

/**
 * Created by david.vittor on 12/07/16.
 */
public class StateResponse extends AbstractResponse {
    private State metadata;

    @Override
    public State getMetadata() {
        return metadata;
    }

    public void setMetadata(State metadata) {
        this.metadata = metadata;
    }
}
