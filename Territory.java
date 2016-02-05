import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Florian on 13.01.2016.
 */
public class Territory {
    public String name;
    public Player owned_by;
    private int armyStrength;
    private Map<String, Territory> neighbors = new HashMap();
    private List<Polygon> polygons = new ArrayList<>();
    private List<Polyline> polylines = new ArrayList<>();
    private Point2D capital;
    private Label armyStrengthDisplay;
    private boolean isSelected = false;

    public Territory(String name) {
        this.name = name;
        this.owned_by = null;
        this.armyStrength = 0;
    }

    public void addLabel(Label label) {
        armyStrengthDisplay = label;
        armyStrengthDisplay.relocate(capital.getX(), capital.getY());
    }

    public void changeArmyStrength(int change) {    // increaseArmyS --> changeArmy...
        this.armyStrength += change;
        armyStrengthDisplay.setText(Integer.toString(armyStrength));
    }

    public void addNeighbor(Territory neighbor) {
        neighbors.put(neighbor.name, neighbor);
    }

    public void addCapital(Point2D capital) {this.capital = capital;}

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public void addPolyline(Polyline polyline) {
        polylines.add(polyline);
    }

    public void setOwner(Player new_owner) {
        if (new_owner.name.equals("User")) {
            for(Polygon p : polygons) {
                p.setFill(Color.DODGERBLUE);
            }
        } else if(new_owner.name.equals("KI")) {
            for(Polygon p : polygons) {
                p.setFill(Color.INDIANRED);
            }
        }
        owned_by = new_owner;
    }

    public void setSelected(boolean selected) {
        if(selected) {
                this.isSelected = true;
                if (polygons.get(0).getFill() == Color.DODGERBLUE) {
                    for (Polygon p : polygons) {
                        p.setFill(Color.AQUA);
                    }
                } else if (polygons.get(0).getFill() == Color.INDIANRED) {
                    for (Polygon p : polygons) {
                        p.setFill(Color.BROWN);
                    }
                }


        } else {
            this.isSelected = false;
            if (polygons.get(0).getFill() == Color.AQUA) {
                for (Polygon p : polygons) {
                    p.setFill(Color.DODGERBLUE);
                }
            } else if (polygons.get(0).getFill() == Color.BROWN) {
                for (Polygon p : polygons) {
                    p.setFill(Color.INDIANRED);
                }
            }
        }
    }

    public void toggleSelected() {
        if (this.isSelected) {
            setSelected(false);
        } else {
            setSelected(true);
        }
    }

    public void setBorderColor(Color borderColor) {
        for(Polyline p : polylines) {
            p.setStroke(borderColor);
        }
    }

    public void addLinesToGUI(Group g) {
    for (Map.Entry<String, Territory> n_entry : neighbors.entrySet()) {
            Territory n = n_entry.getValue();
            // Alaska and Kamtschatka connecting line should come from behind
            if(name.equals("Alaska") && n.name.equals("Kamchatka")) {
                Polyline connection1 = new Polyline(capital.getX(), capital.getY(), 0, capital.getY());
                Polyline connection2 = new Polyline(1250, n.capital.getY(), n.capital.getX(), n.capital.getY());
                connection1.setStroke(Color.LIGHTGRAY);
                connection2.setStroke(Color.LIGHTGRAY);
                g.getChildren().addAll(connection1, connection2);
            } else {
                Polyline connection = new Polyline(capital.getX(), capital.getY(), n.capital.getX(), n.capital.getY());
                connection.setStroke(Color.LIGHTGRAY);
                g.getChildren().add(connection);
            }
        }
    }

    public void addPolygonsToGUI(Group g) {
        // Add all polygons and polylines to the GUI
        for(Polygon p : polygons) {
            g.getChildren().add(p);
        }
        for(Polyline p : polylines) {
            g.getChildren().add(p);
        }
        // Add armystrength of territory
        Label lbl = new Label("0");
        addLabel(lbl);
        g.getChildren().add(lbl);
    }
}
