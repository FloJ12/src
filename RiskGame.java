/**
 * Creates the GUI and starts the game
 * Created by Florian on 13.01.2016.
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RiskGame extends Application {

    private static final String errmsg = "Usage: java RiskGame <Path to .map file>";
    private Label label;
    public String pathToMap;

    @Override public void init() {
        label = new Label("Risk Game");
        // Get command line parameters
        pathToMap = getParameters().getUnnamed().get(0);
    }

    @Override public void start(Stage stage) throws Exception {
        // Start new game
        Player user = new Player("User", true);
        Player ki = new Player("KI", false);
        AllThoseTerritories game = new AllThoseTerritories(new Player[] {user}, new Player[] {ki}, pathToMap);

        Group root = new Group();
        Scene scene = new Scene(root, 1250, 650);
        stage.setTitle("Risk game");
        stage.setScene(scene);

        Group g = new Group();
        addGUIElementsToGame(game);
        // Paint everything in the right order
        game.addAllToGUI(g);
        Color[] colors = {Color.VIOLET, Color.GREEN, Color.ORANGE, Color.BLACK, Color.YELLOW, Color.BROWN};
        game.paintContinentBorders(colors);

        Button btn = new Button("Zug beenden");
        Label reinforce_status = new Label("Available reinforcements: " + game.getHumanPlayers()[0].availableReinforcements);
        game.getHumanPlayers()[0].addLabel(reinforce_status);
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                game.endTurn();
            }
        });
        btn.relocate(600, 550);
        g.getChildren().addAll(btn, reinforce_status);

        scene.setRoot(g);
        stage.show();
        game.start();
    }

    private void addGUIElementsToGame(AllThoseTerritories game) {
        try {
            // Two buffered reader exceptions like the examples in the lecture
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(pathToMap));
                String line;
                Map<Polygon, Territory> territoryOfPolygon = new HashMap<>();

                // Create a mapping for each Territory to its polygon and paint it
                for(int i = 0; (line = in.readLine()) != null; i++){
                    String[] parts = line.split(" ");
                    String name = getName(parts);
                    int firstCoordinateIndex = getFirstCoordinateIndex(parts);
                    Territory territory = game.getTerritoriesMap().get(name);

                    if(parts[0].equals("patch-of")) {
                        // add polygon and polyline to territory
                        Polygon polygon = createPolygon(parts, firstCoordinateIndex);
                        Polyline polyline = createPolyline(parts, firstCoordinateIndex);

                        // Create an event handler for the polygon
                        polygon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                game.territoryClicked(territoryOfPolygon.get(polygon));
                            }
                        });

                        // add the polygon to the Territorymap (to find territory of a given polygon)
                        territoryOfPolygon.put(polygon, territory);

                        // Add the resulting Polygon and Polylines to the Territory
                        territory.addPolygon(polygon);
                        territory.addPolyline(polyline);
                    }
                    else if(parts[0].equals("capital-of")) {
                        // Add capital to territory
                        double xcoordinate = Double.parseDouble(parts[firstCoordinateIndex]);
                        double ycoordinate = Double.parseDouble(parts[firstCoordinateIndex+1]);
                        territory.addCapital(new Point2D(xcoordinate, ycoordinate));
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

    private Polygon createPolygon(String[] parts, int firstCoordinateIndex) {
        Polygon polygon = new Polygon();

        // add every point to the polygon
        for(int j = firstCoordinateIndex; j < parts.length; j++) {
            double coordinate =  Double.parseDouble(parts[j]);
            polygon.getPoints().addAll(coordinate);
        }

        // Set a few more parameters
        polygon.setFill(Color.LIGHTGRAY);

        return polygon;
    }

    private Polyline createPolyline(String[] parts, int firstCoordinateIndex) {
        Polyline polyline = new Polyline();

        // add every point to the polygon
        for(int j = firstCoordinateIndex; j < parts.length; j++) {
            double coordinate =  Double.parseDouble(parts[j]);
            polyline.getPoints().addAll(coordinate);
        }

        // Set more parameters
        polyline.setStrokeWidth(2.5);

        return polyline;
    }

    @Override public void stop() {}

    // Finds first index of an array which is a coordinate
    //Array is constructed from a line of a file formatted like world.map (split by " ")
    private static int getFirstCoordinateIndex(String[] parts) {
        String territoryName = parts[1];

        // find Territory name (can be 1, 2 or 3 Words)
        int firstCoordinateIndex = 2;
        for(; firstCoordinateIndex < parts.length && !parts[firstCoordinateIndex].matches("[0-9]+");
            firstCoordinateIndex++) {}
        return firstCoordinateIndex;
    }

    //Finds the name of a territory/continent out of an array.
    //Array is constructed from a line of a file formatted like world.map (split by " ")
    private static String getName(String[] parts) {
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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(errmsg);
            return;
        }
        // launch() = first run init() then start(), if program closed run stop()
        launch(args);
    }
}
