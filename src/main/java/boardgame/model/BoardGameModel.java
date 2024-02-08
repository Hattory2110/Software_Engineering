package boardgame.model;

import com.github.javafaker.Faker;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;
import boardgame.result.GameResult;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The implemented model of the Dao game.
 */
public class BoardGameModel {

    /**
     * Represents the two players.
     */
    public enum Player {
        PLAYER_1,
        PLAYER_2
    }

    /**
     * {@code BOARD_SIZE} constant variable.
     */
    public static final int BOARD_SIZE=4;

    private ReadOnlyObjectWrapper<Square>[][] board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE];

    private ReadOnlyObjectWrapper<Player> player = new ReadOnlyObjectWrapper<Player>(Player.PLAYER_1);
    private ArrayList<ArrayList<Integer>> sub_board = new ArrayList<>();

    private void BoardInitializer() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (var j = 0; j < BOARD_SIZE; j++) {
                if (i==j) {
                    row.add(1);
                } else if (i+j==BOARD_SIZE-1) {
                    row.add(2);
                } else row.add(0);
            }
            sub_board.add(row);
            Logger.info("Sub_gameboard succesfully created.");
        }
    }

    /**
     * Creates the gameboard for the game.
     */
    public BoardGameModel() {
        BoardInitializer();
        var index_i=0;
        for (var row:sub_board) {
            var index_j=0;
            for (var cell:row) {
                board[index_i][index_j] = new ReadOnlyObjectWrapper<Square>(
                        switch (cell) {
                            case 1 -> Square.RED;
                            case 2 -> Square.BLUE;
                            default -> Square.NONE;
                        }
                );
                index_j++;
            }
            index_i++;
        }
        Logger.info("Gameboard succesfully constructed.");
    }

    /**
     *
     * @param i the first index of the board to locate the square.
     * @param j the secound index of the board to locate the square.
     * @return the square object's property as a readonly format.
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int i, int j) {
        return board[i][j].getReadOnlyProperty();
    }

    /**
     * @param p a Position type variable to locate the square
     * @return a square enum object
     */
    public Square getSquare(Position p) {
        return board[p.row()][p.col()].get();
    }

    private void setSquare(Position p, Square square) {
        board[p.row()][p.col()].set(square);
    }

    /**
     * Handles the moving of the pieces, and prepares the next player's turn.
     * @param from which position the piece moved.
     * @param to which position the piece will move.
     */
    public void move(Position from, Position to) {
        setSquare(to, getSquare(from));
        Logger.info("First step of the move succesful.");
        setSquare(from, Square.NONE);
        Logger.info("Second step of the move succesful.");
        gameEnd();
        changePlayer();
    }

    /**
     * Evaluates the winning ang losing states of the game.
     * @return true if the winer has been decided.
     */
    public boolean gameEnd() {
        boolean end = false;
        if (winner()) {
            Logger.info(String.format("%s, has won!", getPlayer()));
            end=true;
        } else if (loseConditon()) {
            Logger.info(String.format("%s, has lost!", getPlayer()));
            end=true;
        }
        return end;
    }

    /**
     * Evaluates if the selected move is legal or not.
     * @param from which position it wants to move.
     * @param to which position it wants to move.
     * @return true if the move is possible.
     */
    public boolean canMove(Position from, Position to) {
        return isOnBoard(from) && isOnBoard(to) && !isEmpty(from) && isEmpty(to) && isLegalMove(from, to)
                && isCorrectPlayer(from, getPlayer());
    }

    private boolean isObsacle(Position from, Direction direction, int length) {
        boolean foundObsacle = false;
        for (var i=1; i<=length;i++) {
            var x = from.row() + direction.getRowChange()*i;
            var y = from.col() + direction.getColChange()*i;
            if (!isEmpty(new Position(x,y))){
                foundObsacle=true;
                Logger.info("Obstacle found in the path.");
                break;
            }

        }

        return foundObsacle;
    }

    /**
     * Evaluates a position on the board.
     * @param p Position of the evaluation
     * @return true if the board is empty at p Position
     */
    public boolean isEmpty(Position p) {
        return getSquare(p) == Square.NONE;
    }

    /**
     * Checks if the given position is in the boundaries of the board.
     * @param p Position of the square
     * @return true if it is on the board
     */
    public static boolean isOnBoard(Position p) {
        return 0 <= p.row() && p.row() < BOARD_SIZE && 0 <= p.col() && p.col() < BOARD_SIZE;
    }


    /**
     * Checks if the move is possible.
     * @param from which position it wants to move.
     * @param to which position it wants to move.
     * @return true if the move is legal according the game rules
     */
    public boolean isLegalMove(Position from, Position to) {
        var dx = Math.abs(to.row() - from.row());
        var dy = Math.abs(to.col() - from.col());
        Direction direction = Direction.ZERO;
        int length=0;
        if (dx!=0) {
            if (dy!=0) {
                direction = Direction.of((to.row() - from.row())/dx,(to.col() - from.col())/dy);
                length=dx;
            } else {
                direction = Direction.of((to.row() - from.row())/dx,0);
                length=dx;
            }
        } else if (dy!=0) {
            direction = Direction.of(0,(to.col() - from.col())/dy);
            length=dy;
        }

        Logger.info(direction);
        return !direction.equals(Direction.ZERO) && !isObsacle(from, direction, length);
    }

    /**
     * @return the current player.
     */
    public Player getPlayer() {
        return player.get();
    }

    private void changePlayer() {
        if (getPlayer().equals(Player.PLAYER_1)) {
            player.set(Player.PLAYER_2);
        } else {
            player.set(Player.PLAYER_1);
        }
        Logger.info("Players Changed");
    }

    /**
     * Checks if the current player owns the choosen piece.
     * @param piece the square that was chosen.
     * @param player the current player
     * @return true if the player owns that piece.
     */
    public boolean isCorrectPlayer(Position piece, Player player) {
        var pieceColor = squareProperty(piece.row(), piece.col()).get();
        if (player.equals(Player.PLAYER_1) && pieceColor.equals(Square.RED)) {
            return true;
        } else if (player.equals(Player.PLAYER_2) && pieceColor.equals(Square.BLUE)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean winner() {
        return winCondition_1(getPlayer()) ||
                winCondition_2(getPlayer()) ||
                winCondition_3(getPlayer()) ||
                winCondition_4(getPlayer());
    }

    private boolean winCondition_1(Player player) {
        boolean acceptedRow = false;
        for (int i=0; i<BOARD_SIZE; i++) {
            for (int j=0; j<BOARD_SIZE; j++){
                var position = new Position(i,j);
                if (!isCorrectPlayer(position, player)) {
                    acceptedRow = false;
                    break;
                } else {
                    acceptedRow = true;
                }
            }
            if (acceptedRow) {
                break;
            }
        }
        Logger.info("Evaluating first win condition.");
        return acceptedRow;
    }
    private boolean winCondition_2(Player player) {
        boolean acceptedCol = false;
        for (int i=0; i<BOARD_SIZE; i++) {
            for (int j=0; j<BOARD_SIZE; j++){
                var position = new Position(j,i);
                if (!isCorrectPlayer(position, player)) {
                    acceptedCol = false;
                    break;
                } else {
                    acceptedCol = true;
                }
            }
            if (acceptedCol) {
                break;
            }
        }
        Logger.info("Evaluating second win condition.");
        return acceptedCol;
    }
    private boolean winCondition_3(Player player) {
        Logger.info("Evaluating third win condition.");
        return isCorrectPlayer(new Position(0,0), player) && isCorrectPlayer(new Position(BOARD_SIZE-1,0), player) &&
                isCorrectPlayer(new Position(0,BOARD_SIZE-1), player) && isCorrectPlayer(new Position(BOARD_SIZE-1,BOARD_SIZE-1), player);
    }
    private boolean check2_by_2(Position position, Player player) {
        var row = position.row();
        var col = position.col();
        Logger.info("Evaluating 2by2 grid.");
        return isCorrectPlayer(position, player) && isCorrectPlayer(new Position(row,col+1), player) &&
                isCorrectPlayer(new Position(row+1, col), player) && isCorrectPlayer(new Position(row+1,col+1), player);
    }
    private boolean winCondition_4(Player player) {
        boolean acceptedPlacement = false;
        for (int i=0; i<BOARD_SIZE-1; i++) {
            for (int j = 0; j < BOARD_SIZE - 1; j++) {
                if (check2_by_2(new Position(i,j),player)) {
                    acceptedPlacement=true;
                    break;
                }
            }
            if (acceptedPlacement) {
                break;
            }
        }
        Logger.info("Evaluating forth win condition.");
        return acceptedPlacement;
    }

    private boolean loseConditon() {
        return loseCase_1() || loseCase_2() || loseCase_3() || loseCase_4();
    }

    private boolean loseCase_1() {
        Position position = new Position(0,0);
        Player player = getPlayer();
        var row = position.row();
        var col = position.col();
        Logger.info("Evaluating first lose case.");
        return !isCorrectPlayer(position, player) && isCorrectPlayer(new Position(row,col+1), player) &&
                isCorrectPlayer(new Position(row+1, col), player) && isCorrectPlayer(new Position(row+1,col+1), player);
    }

    private boolean loseCase_2() {
        Position position = new Position(BOARD_SIZE-1,0);
        Player player = getPlayer();
        var row = position.row();
        var col = position.col();
        Logger.info("Evaluating second lose case.");
        return !isCorrectPlayer(position, player) && isCorrectPlayer(new Position(row,col+1), player) &&
                isCorrectPlayer(new Position(row-1, col), player) && isCorrectPlayer(new Position(row-1,col+1), player);
    }

    private boolean loseCase_3() {
        Position position = new Position(0,BOARD_SIZE-1);
        Player player = getPlayer();
        var row = position.row();
        var col = position.col();
        Logger.info("Evaluating third lose case.");
        return !isCorrectPlayer(position, player) && isCorrectPlayer(new Position(row,col-1), player) &&
                isCorrectPlayer(new Position(row+1, col), player) && isCorrectPlayer(new Position(row+1,col-1), player);
    }

    private boolean loseCase_4() {
        Position position = new Position(BOARD_SIZE-1,BOARD_SIZE-1);
        Player player = getPlayer();
        var row = position.row();
        var col = position.col();
        Logger.info("Evaluating forth lose case.");
        return !isCorrectPlayer(position, player) && isCorrectPlayer(new Position(row,col-1), player) &&
                isCorrectPlayer(new Position(row-1, col), player) && isCorrectPlayer(new Position(row-1,col-1), player);
    }

    /**
     * toString format for the board.
     * @return the board in string format
     */
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < BOARD_SIZE; j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var model = new BoardGameModel();
        System.out.println(model);
        var poz =new Position(0,0);
        var poz1 =new Position(0,1);
        System.out.println(model.isCorrectPlayer(poz,Player.PLAYER_1));
        model.move(poz, poz1);
        System.out.println(model.squareProperty(0,0));
    }
}
