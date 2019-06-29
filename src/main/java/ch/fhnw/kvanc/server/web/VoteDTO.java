package ch.fhnw.kvanc.server.web;

import javax.validation.constraints.NotNull;

/**
 * VoteDTO
 */
public class VoteDTO {
    @NotNull
    private Boolean vote;

    /**
     * @param vote the vote to set
     */
    public void setVote(Boolean vote) {
        this.vote = vote;
    };

    public Boolean getVote() {
        return vote;
    }

}