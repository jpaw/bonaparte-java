package de.jpaw.bonaparte.refsp;

import de.jpaw.bonaparte.pojos.api.TrackingBase;

/** Defines the methods to implement for update of the tracking columns. */
public interface TrackingUpdater<TRACKING extends TrackingBase> {
    /** Updates the relevant columns before a record is persisted the first time (i.e. all fields should be initialized). */
    void preCreate(RequestContext ctx, TRACKING tr);

    /** Updates columns before some record is updated. Only modification timestamps should be altered. */
    void preUpdate(RequestContext ctx, TRACKING tr);
}