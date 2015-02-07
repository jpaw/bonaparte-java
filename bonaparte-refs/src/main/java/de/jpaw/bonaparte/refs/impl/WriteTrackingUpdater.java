package de.jpaw.bonaparte.refs.impl;

import de.jpaw.bonaparte.pojos.api.WriteTracking;
import de.jpaw.bonaparte.refs.RequestContext;
import de.jpaw.bonaparte.refs.TrackingUpdater;

public class WriteTrackingUpdater implements TrackingUpdater<WriteTracking> {

    @Override
    public void preCreate(RequestContext ctx, WriteTracking tr) {
        tr.setWhenCreated(ctx.getExecutionStart());
    }

    @Override
    public void preUpdate(RequestContext ctx, WriteTracking tr) {
        // no changes upon update
    }
}
