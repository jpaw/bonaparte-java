package de.jpaw.bonaparte.refsw.impl;

import de.jpaw.bonaparte.pojos.api.WriteTracking;
import de.jpaw.bonaparte.refsw.RequestContext;
import de.jpaw.bonaparte.refsw.TrackingUpdater;

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
