import javafx.scene.control.Label;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian on 13.01.2016.
 */
public class Player {
    int availableReinforcements = 0;
    String name;
    boolean isHuman;
    private Label reinforce_status;
    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }

    public void deployReinforcement(Territory t) {
        if(availableReinforcements > 0) {
            availableReinforcements--;
            t.changeArmyStrength(1);
            updateLabel();
        } else {
            System.out.println("You don't have enough reinforcements available.");
        }
    }

    public void updateLabel() {
        reinforce_status.setText("Available reinforcements: " + availableReinforcements);
    }

    public void addLabel(Label label) {
        this.reinforce_status = label;
        updateLabel();
        label.relocate(800, 600);
    }

    public String toString() {
        return name;
    }

    public void occupy() {
        // TODO: acquire an uncontested territory
    }

    public void attackAndMove() {
        boolean turnCompleted = false;
        boolean gameWon   = false;

        while (!turnCompleted && !gameWon) {
            // TODO: attacking and moving of armies
            gameWon = checkIfGameWon();
        }
    }

    public void deployReinforcements() {
        availableReinforcements = checkCountOfReinforcements();
        if (availableReinforcements > 0) {
            // TODO: deploy reinforcements
        }
    }

    private int checkCountOfReinforcements() {
        // TODO: count territories owned by this player and calculate resulting available reinforcements
        return 0;
    }

    private boolean checkIfGameWon() {
        // TODO: check if all territories are owned by this player
        return false;
    }

}
