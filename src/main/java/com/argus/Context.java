package com.argus;

import java.time.ZoneId;

/**
 * Provide additional context for queries, e.g. the user's local time zone.
 */
public class Context {

    private ZoneId zoneId = null;
    private String clientIp = null;

    public Context(final String timeZone) {
        this(timeZone, null);
    }
    
    public Context(final String timeZone,
                   final String clientIp) {
        if (timeZone != null) {
            try {
                this.zoneId = ZoneId.of(timeZone);
                this.clientIp = clientIp;
            }
            catch (Exception exception) {
                // If we don't have the time zone we don't need to fail.
                // @todo Log the error.
            }
        }
    }

    /** 
     * Return the zone ID of the user's local time zone or null if we
     * do not have it.
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Return the IP address of the client.
     */
    public String getClientIp() {
        return clientIp;
    }
}
