package src;

public interface CRUD<T> {
    void add(T book);
    void update(int rowIndex, T updatedBook);
    void delete(int rowIndex);
    void removeReview(int index, String review);
    
}
