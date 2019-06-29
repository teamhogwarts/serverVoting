package ch.fhnw.kvanc.server.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.fhnw.kvanc.server.domain.Vote;

/**
 * VoteRepository
 */
@Component
public class VoteRepository {
    private Logger logger = LoggerFactory.getLogger(VoteRepository.class);

    private Map<String, Vote> votes = new HashMap<String, Vote>();

    public Vote createVote(String email) {
        for (Vote vote : votes.values()) {
            if (vote.getEmail().equals(email)) {
                return null;
            }
        }
        Vote vote = new Vote(email);
        votes.put(vote.getId(), vote);
        return vote;
    }

    public Vote findVote(String id) {
        return votes.get(id);
    }

    public void updateVote(Vote vote) {
        if (vote.getId() != null) {
            // just overwrites existing vote
            votes.put(vote.getId(), vote);
        }
    }

    public void reset() {
        votes.clear();
        logger.debug("Reset was successful");
    }

    public void reOpenAll() {
        for (Vote vote : votes.values()) {
            vote.setClosed(false);
        }
    }

    public List<Vote> findAll() {
        List<Vote> result = new ArrayList<Vote>();
        Collection<Vote> values = votes.values();
        // return only those entries from users which have already voted
        for (Vote vote : values) {
            if (vote.isClosed()) {
                result.add(vote);
            }
        }
        return result;
    }
}