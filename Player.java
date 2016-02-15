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
    private List<Territory> owned_territories = new LinkedList<>();
    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
    }
    public Territory getRandomOwndTerritory() {
        return owned_territories.get((int)(owned_territories.size() * Math.random()));
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
        reinforce_status.setVisible(true);
        reinforce_status.setText("Available reinforcements: " + availableReinforcements);
    }

    public void addLabel(Label label) {
        this.reinforce_status = label;
        reinforce_status.setVisible(false);
        label.relocate(480, 595);
    }

    public String toString() {
        return name;
    }

    public void addTerritory(Territory territory) {
        this.owned_territories.add(territory);
    }

    public void dropTerritoryOwnership(Territory territory) {
        this.owned_territories.remove(territory);
    }
}
