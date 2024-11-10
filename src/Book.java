package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class Book {

    private String title;
    private String author;
    private List<Double> ratings;
    private List<String> reviews;

    public Book(String title, String author) {
        this.title = (title.isEmpty() ? "Unknown" : title);
        this.author = (author.isEmpty() ? "Unknown" : author);
        this.ratings = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public List<Double> getRating() {
        return Collections.unmodifiableList(ratings);
    }

    public List<String> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public Optional<Double> calculateAverageRating(List<Double> ratings) {
        OptionalDouble optionalAverage = ratings.stream()
                .mapToDouble(Double::doubleValue) // Convert to primitive double stream
                .average(); // Get the average as OptionalDouble

        return optionalAverage.isPresent() ? Optional.of(optionalAverage.getAsDouble()) : Optional.empty(); // Return
                                                                                                            // Optional<Double>
    }

    public String toString() {
        return "src.Book{" +
                "author='" + author + '\'' +
                ", ratings=" + ratings +
                ", reviews=" + reviews +
                ", title='" + title + '\'' +
                '}';
    }

}
