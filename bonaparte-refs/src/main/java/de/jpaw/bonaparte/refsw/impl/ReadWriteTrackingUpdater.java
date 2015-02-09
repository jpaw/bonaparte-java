package de.jpaw.bonaparte.refsw.impl;

import de.jpaw.bonaparte.pojos.api.ReadWriteTracking;
import de.jpaw.bonaparte.refsw.RequestContext;
import de.jpaw.bonaparte.refsw.TrackingUpdater;

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
