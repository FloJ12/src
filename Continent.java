/**
 * Contains all territories of
 * Created by Florian on 13.01.2016.
 */
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class Continent {
    private String c_name;
    public Map<String, Territory> territories = new HashMap<>();
    public int bonus;

    public Continent(String name, int bonus) {
        this.c_name = name;
        this.bonus = bonus;
    }

    public boolean is_Of_Player( Player player) {
        boolean result = true;
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            result = result && (entry.getValue().owned_by == player);
        }
        return result;
    }

    public void addTerritory(Territory territory) {
        territories.put(territory.name, territory);
    }

    public void paintBorders(Color color) {
        for (Map.Entry<String, Territory> entry : territories.entrySet()) {
            Territory t = entry.getValue();
            t.setBorderColor(color);
        }
    }
}
