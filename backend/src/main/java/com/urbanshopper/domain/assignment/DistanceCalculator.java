package com.urbanshopper.domain.assignment;

import org.springframework.stereotype.Component;

/**
 * C-002: Distance calculation using Haversine formula.
 *
 * Step 1: Haversine (straight-line) to quickly filter nearby shoppers.
 * Step 2 (future): Road distance via Maps API for final ranking.
 *
 * Haversine is accurate to ~0.5% over hundreds of km — sufficient for zone-level filtering.
 */
@Component
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate great-circle distance between two points using Haversine formula.
     *
     * @return distance in kilometres
     */
    public double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        var dLat = Math.toRadians(lat2 - lat1);
        var dLon = Math.toRadians(lon2 - lon1);
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
              + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
              * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate distance score for the Assignment Score (C-011).
     * Returns a score from 0-100 where closer = higher score.
     * Score = max(0, 100 - (distance / maxRadius * 100))
     */
    public double distanceScore(double distanceKm, double maxRadiusKm) {
        if (maxRadiusKm <= 0) return 50; // default midpoint
        return Math.max(0, 100 - (distanceKm / maxRadiusKm) * 100);
    }

    /**
     * C-005: Check if a shopper is within the zone's maximum assignment radius.
     */
    public boolean isWithinRadius(double distanceKm, double maxRadiusKm) {
        return distanceKm <= maxRadiusKm;
    }
}
