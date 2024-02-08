package boardgame;

import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.tinylog.Logger;

/**
 * Handles the selection of the squares in the application.
 */
public class BoardGameMoveSelector {
    /**
     * Phases of the move process.
     */
    public enum Phase {
        SELECT_FROM,
        ERROR_AT_FROM,
        SELECT_TO,
        ERROR_AT_TO,
        READY_TO_MOVE

    }

    private BoardGameModel model;
    private ReadOnlyObjectWrapper<Phase> phase = new ReadOnlyObjectWrapper<>(Phase.SELECT_FROM);
    private boolean invalidSelection = false;
    private Position from;
    private Position to;

    /**
     * Selects the game model, for the moving.
     * @param model selected game model.
     */
    public BoardGameMoveSelector(BoardGameModel model) {
        Logger.info("Game model selected.");
        this.model = model;
    }

    /**
     * @return the current Phase.
     */
    public Phase getPhase() {
        return phase.get();
    }

    /**
     * Read only wrapper for Phase enum.
     * @return a Phase property.
     */
    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return phase.getReadOnlyProperty();
    }

    /**
     * Checks if the current Phase is {@code READY_TO_MOVE}.
     * @return true if current Phase is {@code READY_TO_MOVE}
     */
    public boolean isReadyToMove() {
        return phase.get() == Phase.READY_TO_MOVE;
    }

    /**
     * Handles the selection process at the Phases.
     * @param position of the selected square.
     */
    public void select(Position position) {
        switch (phase.get()) {
            case SELECT_FROM -> selectFrom(position);
            case SELECT_TO -> selectTo(position);
            case READY_TO_MOVE -> throw new IllegalStateException();
            default -> reset();
        }
    }

    private void selectFrom(Position position) {
        Logger.info("Selecting From position.");
        if (!model.isEmpty(position) && model.isCorrectPlayer(position, model.getPlayer())) {
            from = position;
            phase.set(Phase.SELECT_TO);
            invalidSelection = false;
            Logger.info("Valid position selected.");
        } else {
            Logger.warn("Invalid Selection.");
            invalidSelection = true;
            phase.set(Phase.ERROR_AT_FROM);
        }
    }

    private void selectTo(Position position) {
        Logger.info("Selecting To position");
        if (model.canMove(from, position)) {
            to = position;
            phase.set(Phase.READY_TO_MOVE);
            invalidSelection = false;
            Logger.info("Valid position selected.");
        } else {
            Logger.warn("Invalid Selection.");
            invalidSelection = true;
            phase.set(Phase.ERROR_AT_TO);
        }
    }

    /**
     *
     * @return the from position if it already passes the SELECT_FROM Phase.
     */
    public Position getFrom() {
        if (phase.get() == Phase.SELECT_FROM) {
            Logger.warn("Illegal state, from hasn't been selected.");
            throw new IllegalStateException();
        }
        return from;
    }

    /**
     * @return the to position if it already passes the READY_TO_MOVE Phase.
     */
    public Position getTo() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            Logger.warn("Illegal state, to hasn't been selected.");
            throw new IllegalStateException();
        }
        return to;
    }

    /**
     * @return the value of invalidSelection.
     */
    public boolean isInvalidSelection() {
        return invalidSelection;
    }


    /**
     * Handles the movement form one position to another.
     */
    public void makeMove() {
        Logger.info("Started to move");
        if (phase.get() != Phase.READY_TO_MOVE) {
            Logger.warn("Illegal state, program isn't capable of moving yet.");
            throw new IllegalStateException();
        }
        Logger.info("Succesful movement.");
        model.move(from, to);
        reset();
    }

    /**
     * Resets the selection process.
     */
    public void reset() {
        from = null;
        to = null;
        phase.set(Phase.SELECT_FROM);
        invalidSelection = false;
        Logger.info("Reseted the selection process.");
    }
}
