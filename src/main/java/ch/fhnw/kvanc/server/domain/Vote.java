package ch.fhnw.kvanc.server.domain;

import java.util.UUID;

/**
 * Vote
 */
public class Vote {
    private String id;
    private String email;
    private boolean isTrue;
    private boolean isClosed;

    public Vote(String email) {
        this.email = email;
        this.id = UUID.randomUUID().toString();
        this.isClosed = false;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    /**
     * @return the isTrue
     */
    public boolean isTrue() {
        return isTrue;
    }

    /**
     * @param isTrue the isTrue to set
     */
    public void setTrue(boolean isTrue) {
        this.isTrue = isTrue;
        this.isClosed = true;
    }

    /**
     * @return the isClosed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * @param isClosed the isClosed to set
     */
    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }
}