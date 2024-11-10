package src;

public class RatingData {
    private String averageRating;
    private String numberOfRatings;

    public RatingData(Double averageRating, int numberOfRatings) {
        this.averageRating = String.valueOf(averageRating);
        this.numberOfRatings = String.valueOf(numberOfRatings);
    }

    public double getAverageRating() {
        return Double.parseDouble(averageRating);
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = String.valueOf(averageRating);
    }

    public int getNumberOfRatings() {
        return Integer.parseInt(numberOfRatings);
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = String.valueOf(numberOfRatings);
    }

    @Override
    public String toString() {
        return "Average Rating: " + getAverageRating() + " / Number of Ratings: " + getNumberOfRatings();
    }
}
