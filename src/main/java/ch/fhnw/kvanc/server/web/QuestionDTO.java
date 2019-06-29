package ch.fhnw.kvanc.server.web;

/**
 * QuestionDTO
 */
public class QuestionDTO {
    private String questionText;

    public QuestionDTO(String text) {
        this.questionText = text;
    }

    /**
     * @return the questionText
     */
    public String getQuestionText() {
        return questionText;
    }
}