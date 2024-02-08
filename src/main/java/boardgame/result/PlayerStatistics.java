package boardgame.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * Format for showing results on the leaderboard.
 */
@Data
@AllArgsConstructor
public class PlayerStatistics {

    @NonNull private String winnerName;
    private long numberOfWins;

}