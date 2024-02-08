package boardgame.result;


import lombok.NonNull;
import org.tinylog.Logger;
import util.JacksonHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the GameResultManager interface for Json format.
 */
public class JsonGameResultManager implements GameResultManager {

    private Path filePath;

    public JsonGameResultManager(@NonNull Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<GameResult> add(@NonNull GameResult result) throws IOException {
        var results = getAll();
        results.add(result);
        try (var out = Files.newOutputStream(filePath)) {
            Logger.info("Writing game results into file.");
            JacksonHelper.writeList(out, results);
        }
        return results;
    }

    public List<GameResult> getAll() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<GameResult>();
        }
        try (var in = Files.newInputStream(filePath)) {
            Logger.info("Reading game results from file.");
            return JacksonHelper.readList(in, GameResult.class);
        }
    }

}

