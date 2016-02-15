/**
 * An object is a "Game".
 * It consist of its territories, its continents, its human players and its kiPlayers.
 * When a new object is created, it reads all necessary information from the file
 * of the command line parameter.
 * There is also the listener for mouseclicks on territories, so the user can control his moves.
 * Created by nam on 08.01.16.
 * 
 * 
 * kommentare zu klassen
 * reduce complexity (players, ...)
 * uml
 * reset move
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AllThoseTerritories {

    private Map<String, Territory> territories;
    private Map<String, Continent> continents;
    private Player humanPlayer;
    private Player kiPlayer;
    private boolean phaseOccupy;
    private boolean phaseConqer;
    private boolean stepReinforcements = true;
    private boolean stepAttackAndMove = false;
    private Label status;
    private Button btn;
    private Territory own;
    private Territory sourceOfMovedTroups;
    private Territory destOfMovedTroups;
    private Territory enemy;
    private Territory newlyObtainedLand;
    private Territory sourceOfSuccessfulAttack;

    public AllThoseTerritories(Player humanPlayer, Player kiPlayer, String pathToMap) {
        this.territories = readTerritories(pathToMap);
        this.continents = readContinents(pathToMap);
        this.humanPlayer = humanPlayer;
        this.kiPlayer = kiPlayer;
    }

    // Returns a Map containing all Territories. By doing this,
    // it instantiates all Territory objects, adds it to the map,
    // and then calls addNeighbors.
    private Map<String, Territory> readTerritories(String pathToMap) {
        try {
            // Two buffered reader exceptions like the examples in the lecture
            BufferedReader in = null;
            try {
                Map<String, Territory> territoryMap = new HashMap<>();

                // Fill array with Territories
                in = new BufferedReader(new FileReader(pathToMap));
                String line;
                for(int i = 0; (line = in.readLine()) != null; i++){
                    String[] parts = line.split(" ");
                    String territoryName = getName(parts);

                    if(parts[0].equals("patch-of")) {
                        territoryMap.put(territoryName, new Territory(territoryName));
                    }
                }
                addNeighbors(pathToMap, territoryMap);
                return territoryMap;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        catch(IOException ex) {
            System.err.println("I/O Error: " + ex.getMessage());
        }
        return null;
    }

    // Adds all neighbors of territories. Territories and all neighbors of these territories
    // specified in "neighbor-of" lines must exist in territoryMap
    private void addNeighbors(String pathToMap, Map<String, Territory> territoryMap) {
        try {
            // Two buffered reader exceptions like the examples in the lecture
            BufferedReader in = null;
            try {
                // Read file line by line, process only "neighbors-of" lines
                in = new BufferedReader(new FileReader(pathToMap));
                String line;
                for(int i = 0; (line = in.readLine()) != null; i++){
                    String[] parts = line.split(" ");
                    String territoryName = getName(parts);
                    if(parts[0].equals("neighbors-of")) {
                        // Get place of first neighbor in line
                        int firstNeighborIndex = getFirstTerritoryIndex(parts);

                        // Build a String containing only neighbors seperated by -
                        // i.e. Alaska-Great Britain-North Western Territory
                        StringBuilder builder = new StringBuilder();
                        for (int j = firstNeighborIndex; j < parts.length; j++) {
                            // Add space only for Territories, not before and after separator - and :
                            if(!parts[j-1].equals("-") && !parts[j-1].equals(":") && !parts[j].equals("-")) {
                                builder.append(" " + parts[j]);
                            } else {
                                builder.append(parts[j]);
                            }
                        }
                        String neighborsString = builder.toString();

                        // Split String to get Array containing only neighbors as strings
                        String[] neighborsArray = neighborsString.split("-");

                        // Add every corresponding neighbor Territory object to the current territory
                        // and if current territory is not already a neighbor of neighbor Territory, add it there as well
                        for (String neighborString : neighborsArray) {
                            Territory neighbor = territoryMap.get(neighborString);
                            territoryMap.get(territoryName).addNeighbor(neighbor);
                            territoryMap.get(neighbor.name).addNeighbor(territoryMap.get(territoryName));
                        }
                    }
                }

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        catch(IOException ex) {
            System.err.println("I/O Error: " + ex.getMessage());
        }
    }

    // Returns a map containing all continents. By doing this, it instantiates and creates all
    // necessary Continent objects. Members of continents must
    // exist in the territoryMap of "this" object.
    private Map<String, Continent> readContinents(String pathToMap) {
        try {
            // Two buffered reader exceptions like the examples in the lecture
            BufferedReader in = null;
            try {
                Map<String, Continent> continentMap = new HashMap<>();

                // Read file line after line, process only continents
                in = new BufferedReader(new FileReader(pathToMap));
                String line;
                for(int i = 0; (line = in.readLine()) != null; i++){
                    String[] parts = line.split(" ");
                    String continentName = getName(parts);
                    if(parts[0].equals("continent")) {
                        // Get place of first member in line
                        int firstTerritoryIndex = getFirstTerritoryIndex(parts);
                        // Instantiate continent object and add it to the map
                        int bonus = Integer.parseInt(parts[firstTerritoryIndex - 2]);
                        continentMap.put(continentName, new Continent(continentName, bonus));

                        // Build a String containing only members seperated by -
                        // i.e. Alaska-Great Britain-North Western Territory
                        StringBuilder builder = new StringBuilder();
                        for (int j = firstTerritoryIndex; j < parts.length; j++) {
                            // Add space only for Territories, not before and after separator - and :
                            if(!parts[j-1].equals("-") && !parts[j-1].equals(":") && !parts[j].equals("-")) {
                                builder.append(" " + parts[j]);
                            } else {
                                builder.append(parts[j]);
                            }
                        }
                        String territoriesString = builder.toString();

                        // Split String to get Array containing only members as strings
                        String[] territoriesArray = territoriesString.split("-");

                        // Add every corresponding member Territory object to the current continent
                        for (String territoryString : territoriesArray) {
                            Territory territory = territories.get(territoryString);
                            continentMap.get(continentName).addTerritory(territory);
                        }
                    }
                }
                return continentMap;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        catch(IOException ex) {
            System.err.println("I/O Error: " + ex.getMessage());
        }
        return null;
    }

    // Returns first index containing a Territory for a "neighbor-of" line
    // or a "continent" line in an array resulting from a split line (separator " ")
    private int getFirstTerritoryIndex(String[] parts) {
        int firstTerritoryIndex = 2;
        for(; !parts[firstTerritoryIndex].equals(":");
            firstTerritoryIndex++) {}
        return firstTerritoryIndex + 1;
    }

    // Returns territory name from an array resulting
    // from a split line (separator " ")
    private String getName(String[] parts) {
        String territoryName = parts[1];

        // find Territory name (can be 1, 2 or 3 Words)
        int firstCoordinateIndex = 2;
        for(; !parts[firstCoordinateIndex].matches("[0-9]+") && !parts[firstCoordinateIndex].equals(":");
            firstCoordinateIndex++) {

            // For every item that is not a coordinate concatenate the items
            // to get territoryName
            territoryName = territoryName + " " + parts[firstCoordinateIndex];

        }
        return territoryName;
    }

    // Checks if a game is won by a player and returns the player
    public Player isWon() {
        boolean result = true;
        // Get any TerritoryEntry of the map
        Map.Entry<String, Territory> anyTerritoryEntry = territories.entrySet().iterator().next();
        // Get its owner
        Player player = anyTerritoryEntry.getValue().owned_by;
        // Check if he/she/it owns every territory
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            result = result && entry.getValue().owned_by == player;
        }
        if(result == true) {
            return player;
        }
        else {
            return null;
        }
    }

    // Returns true iff user owns all territories
    private boolean isWon(Player user) {
        // Iterate over whole Map
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            Territory territory = entry.getValue();
            if (!(territory.owned_by == user)) {
                return false;
            }
        }
        return true;
    }

    // Returns true iff user owns any territory
    private boolean ownsATerritory(Player user) {
        // Iterate over whole Map
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            Territory territory = entry.getValue();
            if (territory.owned_by == user) {
                return true;
            }
        }
        return false;
    }

    public void endTurn() {
        // computer attack & reinforcements and new turn
        stepAttackAndMove = false;
        int attacks = (int) (Math.random() * 100);

        Territory the_own = kiPlayer.getRandomOwndTerritory();
        Territory the_enemy;
        while (attacks > 0) {
            // search for attack opportunities
            do {
                the_own = getRndEnemyAdjaceTerri(the_own) != null
                        && the_own.armyStrength > 1 ? the_own: kiPlayer.getRandomOwndTerritory();
                attacks--;

            } while (getRndEnemyAdjaceTerri(the_own) == null && attacks > 0);

            // attack if able
            if (getRndEnemyAdjaceTerri(the_own) != null && the_own.armyStrength > 1) {
                the_enemy = getRndEnemyAdjaceTerri(the_own);
                boolean attackSuccessful = attack(the_own, the_enemy);

                if (attackSuccessful) {
                    if (isWon(the_own.owned_by)) {
                        endGame(the_own.owned_by);
                    }
                }
            }
            attacks--;
        }

        this.humanPlayer.availableReinforcements = calc_reinforce(humanPlayer);
        this.humanPlayer.updateLabel();
        this.kiPlayer.availableReinforcements = calc_reinforce(kiPlayer);
        stepReinforcements = true;
        sourceOfMovedTroups = destOfMovedTroups = null;
    }

    private void endGame(Player winner) {
        System.out.println("Game over");
        System.out.println(winner + " won the Game.");
    }

    //Is called when a territory is clicked.
    public void territoryClicked(Territory territory) {
        if (phaseOccupy) {
            // Only call code if clicked territory is not owned by someone else
            if (territory.owned_by == null) {
                // Human selection
                territory.setOwner(humanPlayer);
                territory.changeArmyStrength(1);
                status.setText(territory + " is now owned by " + territory.owned_by);

                // KI selection
                Territory randomTerritory =  getRandomUnoccupiedTerritory();
                randomTerritory.setOwner(kiPlayer);
                randomTerritory.changeArmyStrength(1);
                status.setText(randomTerritory.name + " is now owned by " + randomTerritory.owned_by);

                // if no territories are available any more, start conquer phase
                if (allOccupied()) {
                    phaseOccupy = false;
                    phaseConqer = true;
                    // Erste Verstärkungen ermitteln
                    this.humanPlayer.availableReinforcements = calc_reinforce(humanPlayer);
                    this.humanPlayer.updateLabel();
                    status.setText("Reinforcements: Deploy your available reinforcements in your territories by left-clicking.");
                    btn.setVisible(true);

                    this.kiPlayer.availableReinforcements = calc_reinforce(kiPlayer);
                }
            }
        } else if (phaseConqer) {
            if(stepReinforcements) {
                this.humanPlayer.deployReinforcement(territory);
                if(this.humanPlayer.availableReinforcements == 0) {
                    while (this.kiPlayer.availableReinforcements > 0) {
                        Territory rndTerri = kiPlayer.getRandomOwndTerritory();

                        if (getRndEnemyAdjaceTerri(rndTerri) != null) {
                            rndTerri.changeArmyStrength(1);
                            kiPlayer.availableReinforcements--;
                        }

                    }
                    stepReinforcements = false;
                    stepAttackAndMove = true;
                    status.setText("Attack or move: Select one of your territories by left-clicking.");
                }
            }
            else if(stepAttackAndMove) {
                // when own territory is clicked
                if (territory.owned_by == this.humanPlayer) {
                    //if no territory is selected yet
                    if (own == null) {
                        own = territory;
                        own.setSelected(true);
                        status.setText(own + " selected. Attack by left-clicking on enemy territory. Move army by right-clicking on own territory.");
                    }
                    // switch selected territory
                    else if (own != territory) {
                        own.setSelected(false); // is old selected territory, deselect
                        own = territory; // set selected territory
                        own.setSelected(true);
                        status.setText(own + " selected. Attack by left-clicking on enemy territory. Move army by right-clicking on own territory.");
                    }
                    // deselect
                    else if (own == territory) {
                        own.setSelected(false);
                        status.setText(own + " deselected. Select another own territory by left-clicking.");
                        own = null;
                    }
                }
                // when enemy territory is clicked
                // attack if: 1) base selected, 2) enemy is neighbour of base, 3) base armyStrength > 1
                else if (own != null && own.isNeighbor(territory) && own.armyStrength > 1 && territory.owned_by != own.owned_by) {
                    enemy = territory;
                    enemy.setSelected(true);
                    status.setText("Attack " + enemy + "!");
                    boolean successfulAttack = attack(own, enemy);
                    if (successfulAttack) {
                        // variables for follow-up move
                        if (isWon(own.owned_by)) {
                            endGame(own.owned_by);
                        }
                        newlyObtainedLand = enemy;
                        sourceOfSuccessfulAttack = own;
                    }
                    //deselect after attack
                    own.setSelected(false);
                    own = null;
                    enemy.setSelected(false);
                    enemy = null;

                }
            }
        }
    }

    public void move(Territory source, Territory dest) {
        if (source.armyStrength > 1) {
            source.changeArmyStrength(-1);
            dest.changeArmyStrength(1);
        }
    }

    public void move(int countArmies, Territory source, Territory dest) {
        for (int i = 0; i < countArmies; i++) {
            move(source, dest);
        }
    }

    public boolean attack(Territory own, Territory enemy) {
        int attackers = Math.min(3, own.armyStrength - 1);
        int defenders = Math.min(2, enemy.armyStrength);
        int[] atk_dice = new int[attackers];
        int[] def_dice = new int[defenders];

        for (int i = 0; i < atk_dice.length; i++) {
            atk_dice[i] = (int) (Math.random() * 6) + 1;
            System.out.println("Würfel ATK: " + atk_dice[i]);
        }

        for (int i = 0; i < def_dice.length; i++) {
            def_dice[i] = (int) (Math.random() * 6) + 1;
            System.out.println("Würfel DEF: " + def_dice[i]);
        }

        Arrays.sort(atk_dice);
        Arrays.sort(def_dice);


        if (atk_dice[attackers-1] > def_dice[defenders-1]) {
            enemy.changeArmyStrength(-1);
        } else {
            own.changeArmyStrength(-1);
        }
        if (defenders == 2 && attackers >= 2) {
            if (atk_dice[attackers - 2] > def_dice[defenders - 2]) {
                enemy.changeArmyStrength(-1);
            } else {
                own.changeArmyStrength(-1);
            }
        }
        // attack successful
        if (enemy.armyStrength == 0) {
            move(attackers, own, enemy);
            enemy.setOwner(own.owned_by);
            return true;
        }
        // attack not successful
        return false;
    }

    private int calc_reinforce(Player player) {
        int result = 0;
        for (Map.Entry<String, Continent> entry : continents.entrySet()) {

            if (entry.getValue().is_Of_Player(player)) {
                result += entry.getValue().bonus * 3;
            }

            for (Map.Entry<String, Territory> territory : entry.getValue().territories.entrySet()) {
                if (territory.getValue().owned_by == player) {
                    result++;
                }
            }
        }
        return result / 3;
    }

    public void start() {
        this.phaseOccupy = true;
        status.setText("Occupy Phase: Select your desired territories by left-clicking.");
    }

    //Returns a random unoccupied territory
    private Territory getRandomUnoccupiedTerritory() {
        Random random = new Random();
        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, Territory> entry : this.getTerritoriesMap().entrySet()) {
            String key = entry.getKey();
            if (entry.getValue().owned_by == null) {
                keys.add(key);
            }
        }
        String randomKey = keys.get(random.nextInt(keys.size()));
        return this.getTerritoriesMap().get(randomKey);
    }

    private Territory getRndEnemyAdjaceTerri( Territory center) {
        Random random = new Random();
        List<Territory> keys = new ArrayList<Territory>();
        for (Map.Entry<String, Territory> entry : this.getTerritoriesMap().entrySet()) {

            if (entry.getValue().owned_by == humanPlayer && entry.getValue().isNeighbor(center)) {
                keys.add(entry.getValue());
            }
        }
        if (keys.isEmpty()) {
            return null;
        }
        else {
            return keys.get(random.nextInt(keys.size()));
        }

    }

    public Map<String, Territory> getTerritoriesMap() {
        return territories;
    }

    public Map<String, Continent> getContinentsMap() {
        return continents;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

     public Player getKiPlayer() {return kiPlayer;}

    // Returns an array containing human players first and then KI players
    /* public Player[] getPlayers() {
        int aLen = getHumanPlayer().length;
        int bLen = getKiPlayer().length;
        Player[] c = new Player[aLen+bLen];
        System.arraycopy(getHumanPlayers(), 0, c, 0, aLen);
        System.arraycopy(getKiPlayers(), 0, c, aLen, bLen);
        return c;
    } */

    // Returns true iff all Territories of the game are occupied by a user
    private boolean allOccupied() {
        return !ownsATerritory(null);
    }

    public void addAllToGUI(Group g) {
        // First the connecting lines between neighbors, so that those of land-neighbors are later hidden
        // by the land-polygons
        for(Map.Entry<String, Territory> t_entry : getTerritoriesMap().entrySet()) {
            Territory t = t_entry.getValue();
            t.addLinesToGUI(g);
        }
        // Then the polygons
        for(Map.Entry<String, Territory> t_entry : getTerritoriesMap().entrySet()) {
            Territory t = t_entry.getValue();
            t.addPolygonsToGUI(g);
        }
    }

    // Other colors for borders of territories of other continents
    public void paintContinentBorders(Color[] colors) {
        int colorIndex = 0;
        // For every continent paint borders
        for (Map.Entry<String, Continent> c_entry : continents.entrySet()) {
            Continent continent = c_entry.getValue();
            continent.paintBorders(colors[colorIndex]);
            colorIndex++;
        }
    }

    public void territoryRightClicked(Territory territory) {
        // normal move, if 1) selected base, 2) selected own second territory 3) base armyStrength > 1, 4) no move happened before
        if (own != null && territory.owned_by == humanPlayer && own.armyStrength > 1 && own.isNeighbor(territory) && sourceOfMovedTroups == null && destOfMovedTroups == null && territory != newlyObtainedLand) {
            sourceOfMovedTroups = own;
            destOfMovedTroups = territory;
            move(sourceOfMovedTroups, destOfMovedTroups);
        }
        // move happened, undo move
        else if (own == destOfMovedTroups && territory == sourceOfMovedTroups) {
            move(destOfMovedTroups, sourceOfMovedTroups);
            sourceOfMovedTroups = null;
            destOfMovedTroups = null;
        }
        // follow-up move after attack
        else if (territory == newlyObtainedLand && sourceOfSuccessfulAttack.armyStrength > 1) {
            // fixing bug: moving army in territory which made a successful attack => this army could be follow-upped (wrong)
            if(destOfMovedTroups == sourceOfSuccessfulAttack && sourceOfSuccessfulAttack.armyStrength == 2) {}
            else {
                move(sourceOfSuccessfulAttack, newlyObtainedLand);
            }
        }
    }

    public void addLabel(Label status) {
        this.status = status;
        this.status.relocate(480, 620);
    }

    public void addGameElements(Button btn, Label reinforce_status) {
        this.btn = btn;
        humanPlayer.addLabel(reinforce_status);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                endTurn();
            }
        });
        btn.relocate(355, 610);
        btn.setVisible(true);
    }
}


