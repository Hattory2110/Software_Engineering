package boardgame;

import boardgame.UI.BoardGameApplication;
import boardgame.UI.TableViewApplication;
import boardgame.result.GameResult;
import boardgame.result.GameResultManager;
import boardgame.result.JsonGameResultManager;
import boardgame.model.BoardGameModel;
import com.github.javafaker.Faker;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    private static final List<String> PLAYER_NAMES = new ArrayList<>();

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    static {
        for (var i = 0; i < 10; i++) {
            PLAYER_NAMES.add(FAKER.name().firstName());
        }
    }

    private static GameResult createGameResult() {
        String player1Name = FAKER.options().nextElement(PLAYER_NAMES);
        String player2Name = FAKER.options().nextElement(PLAYER_NAMES);
        return GameResult.builder()
                .player1Name(player1Name)
                .player2Name(player2Name)
                .winnerName(FAKER.options().option(player1Name, player2Name))
                .moves(FAKER.number().numberBetween(10, 50))
                .startDateTime(ZonedDateTime.now().minusMinutes(FAKER.number().numberBetween(0, 60)))
                .build();
    }



    public static void main(String[] args) throws IOException {
        GameResultManager manager = new JsonGameResultManager(Path.of("results.json"));
        for (var i = 0; i < 100; i++) {
            manager.add(createGameResult());
        }
        manager.getBestPlayers(10).forEach(System.out::println);

        Application.launch(BoardGameApplication.class, args);
    }
}
