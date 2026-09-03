package qa.dmitriy.model;

public record PostResponse(
        int userId,
        int id,
        String title,
        String body
) {
}