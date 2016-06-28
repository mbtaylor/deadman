package uk.ac.bristol.star.deadman;

/**
 * Defines the known alert statuses for use with Alert updates.
 * Note that null is a valid status, it means nothing to worry about.
 *
 * @author   Mark Taylor
 * @since    28 Jun 2016
 */
public enum Status {

    /** Warning. */
    WARNING,

    /** Danger. */
    DANGER;
}
