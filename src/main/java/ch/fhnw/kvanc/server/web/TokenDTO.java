package ch.fhnw.kvanc.server.web;

/**
 * TokenDTO
 */
public class TokenDTO {
    private String token;
    private String email;

    public TokenDTO() {
    }
    
    public TokenDTO(String token, String email) {
        this.token = token;
        this.email = email;
    }
    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}