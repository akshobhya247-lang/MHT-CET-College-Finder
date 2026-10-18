
package com.cet.finder.service;

public class EligibilityService {

    public EligibilityService() {
        // Initializes the eligibility service
    }

    /**
     * Checks whether a candidate meets the supplied minimum percentile.
     *
     * @param candidatePercentile candidate's percentile
     * @param minimumPercentile required minimum percentile
     * @return true if the candidate meets the requirement
     */
    public boolean isEligible(
            double candidatePercentile,
            double minimumPercentile) {

        if (Double.isNaN(candidatePercentile)
                || Double.isInfinite(candidatePercentile)
                || Double.isNaN(minimumPercentile)
                || Double.isInfinite(minimumPercentile)) {
            throw new IllegalArgumentException(
                    "Percentile must be a valid number.");
        }

        if (candidatePercentile < 0 || candidatePercentile > 100
                || minimumPercentile < 0 || minimumPercentile > 100) {
            throw new IllegalArgumentException(
                    "Percentile must be between 0 and 100.");
        }

        return candidatePercentile >= minimumPercentile;
    }
}
