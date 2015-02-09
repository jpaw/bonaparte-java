package de.jpaw.bonaparte.refsp.impl;

import de.jpaw.bonaparte.pojos.api.ReadWriteTracking;
import de.jpaw.bonaparte.refsp.RequestContext;
import de.jpaw.bonaparte.refsp.TrackingUpdater;

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
