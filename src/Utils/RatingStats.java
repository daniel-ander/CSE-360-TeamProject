package Utils;

import java.math.BigDecimal;

public class RatingStats {
    private final BigDecimal averageRating;
    private final int upvoteCount;

    public RatingStats(BigDecimal averageRating, int upvoteCount) {
        this.averageRating = averageRating;
        this.upvoteCount = upvoteCount;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }
}