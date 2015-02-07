package de.jpaw.bonaparte.refs.impl;

import de.jpaw.bonaparte.pojos.api.ReadWriteTracking;
import de.jpaw.bonaparte.refs.RequestContext;
import de.jpaw.bonaparte.refs.TrackingUpdater;

public class ReadWriteTrackingUpdater implements TrackingUpdater<ReadWriteTracking> {

    @Override
    public void preCreate(RequestContext ctx, ReadWriteTracking tr) {
        tr.setWhenCreated(ctx.getExecutionStart());
    }

    @Override
    public void preUpdate(RequestContext ctx, ReadWriteTracking tr) {
        tr.setWhenUpdated(ctx.getExecutionStart());
    }
}
