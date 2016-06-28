package uk.ac.bristol.star.deadman;

/**
 * Defines the interface for responding to a given alert status.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public interface Alert {

    /**
     * Sets the current alert status.
     * Expected to be invoked from the Event Dispatch Thread,
     * so should not take time.
     * In general, multiple consecutive calls with the same value
     * should have no visible effect; only changes to status
     * should trigger an action.
     *
     * @param  status  new status
     */
    void setStatus( Status status );
}
