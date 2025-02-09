
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PathFinder extends Application {
    private ListGraph<City> lp = new ListGraph<City>();

    private ArrayList<Button> buttons = new ArrayList<>();
    private String fileName = "europa.graph";
    
    private String imageUrl = "file:europa.gif";
    private boolean showsImage = false; 
    private Boolean isSaved = true; 
    private float heightWithoutMap = 200f; 
    private float width = 615f; 
    private Boolean placingNewCity = false; 


    
    private Pane pane = new Pane();
    private BorderPane root = new BorderPane();
    private MenuBar menuBar = new MenuBar();

    private HBox horizonalBox = new HBox(10);
    private VBox verticalBox = new VBox(menuBar);
    private City firstCity = null;
    private City secondCity = null; 
    
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        pane.setId("outputArea");
        
        Alert unsavedChanges = new Alert(AlertType.CONFIRMATION);
        unsavedChanges.setTitle("Warning!");
        unsavedChanges.setHeaderText(null);
        unsavedChanges.setContentText("Unsaved changes, continue anyway?");

        Alert noSavedMap = new Alert(AlertType.WARNING);
        noSavedMap.setTitle("Warning!");
        noSavedMap.setContentText("No saved maps found");

        Button findPath = new Button();
        buttons.add(findPath);
        findPath.setText("Find Path");
        findPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkIfBothSpotsAreFilled()){
                    List<Edge<City>> edges = lp.getPath(firstCity, secondCity);
                    if(edges != null){
                        Alert showPathAlert = new Alert(AlertType.INFORMATION);
                    
                        showPathAlert.setTitle("Message");
                        showPathAlert.setHeaderText("The path from " + firstCity.getName() + " to " + secondCity.getName());
                        showPathAlert.setContentText("");
                        String fullSentence = "";
                        int total = 0; 
                        int totalFromNormal = 0; 
                        int totalFromReversed = 0; 
                        // Det kan vara vilken väg som helst, den kortaste vägen eller den snabbaste vägen. Jag tolkar detta som att man inte får ta den långsammaste vägen. 
                        // detta säkrar att den snabbaste vägen väljs ifall det finns ställen där det är lika. 
                        for(Edge<City> edge : edges){
                            totalFromNormal += edge.getWeight();
                        }
                        List<Edge<City>> reversedEdges = lp.getPath(secondCity, firstCity);
                        for(Edge<City> edge : reversedEdges){
                            totalFromReversed += edge.getWeight();
                        }
                        boolean reverseWorked = true; 
                        try {
                            Collections.reverse(reversedEdges);
                        } catch (UnsupportedOperationException e) {
                            reverseWorked = false; 
                        }
                        if(totalFromReversed > totalFromNormal && reverseWorked){
                            for(Edge<City> edge : reversedEdges){
                                fullSentence += "to " + edge.getDestination().getName() + " by " + edge.getName() + " takes "  + edge.getWeight() + "\n";
                                total += edge.getWeight();
                            }
                        }
                        else{
                            for(Edge<City> edge : edges){
                                fullSentence += "to " + edge.getDestination().getName() + " by " + edge.getName() + " takes "  + edge.getWeight() + "\n";
                                total += edge.getWeight();
                            }
                        }
                        
                        fullSentence += "Total " + total; 
                        
                        showPathAlert.setContentText(fullSentence);
                        showPathAlert.showAndWait();
                    }
                    else{
                        pathDoesntExist();
                    }
                    
                }
                else{
                    showFigur9();
                }
                
            }
        });

        Button showConnection = new Button();
        buttons.add(showConnection);
        showConnection.setText("Show Connection");
        showConnection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkIfBothSpotsAreFilled()){
                    if(lp.getEdgeBetween(secondCity, firstCity) != null){
                        TextInputDialog showConnectionAlert = new TextInputDialog("");
                        String tempNumber = "" + lp.getEdgeBetween(secondCity, firstCity).getWeight();
                        showConnectionAlert(showConnectionAlert, lp.getEdgeBetween(secondCity, firstCity).getName(), tempNumber, false, false);
                        showConnectionAlert.showAndWait();
                    }
                    else{
                        pathDoesntExist();
                    }
                }
                else{
                    showFigur9();
                }
                
            }
        });

        Button newPlace = new Button();
        buttons.add(newPlace);
        newPlace.setText("New Place");

        Button changeConnection = new Button();
        buttons.add(changeConnection);
        changeConnection.setText("Change Connection");
        changeConnection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkIfBothSpotsAreFilled()){
                    if(lp.getEdgeBetween(secondCity, firstCity) != null){
                        Edge<City> edge = lp.getEdgeBetween(secondCity, firstCity);
                        int number = 0; 
                        edge.setWeight(number);
                        TextInputDialog changeConnectionAlert = new TextInputDialog("");
                        TextField tf = showConnectionAlert(changeConnectionAlert, lp.getEdgeBetween(secondCity, firstCity).getName(), "", true, true);
                        

                        
                        changeConnectionAlert.setOnCloseRequest(eventTwo -> {
                            if(!tf.getText().isEmpty()){
                                try{
                                    if(Integer.parseInt(tf.getText()) >=0) {
                                        lp.setConnectionWeight(firstCity, secondCity, Integer.parseInt(tf.getText()));
                                        isSaved = false; 
                                    }
                                }
                                catch (NumberFormatException e){
                                    onlyIntegersAreAllowedAlert();
                                    tf.clear();
                                    eventTwo.consume();
                                }
                            }
                        });
                        changeConnectionAlert.showAndWait();
                    }
                    else{
                        pathDoesntExist();
                    }
                }
                else{
                    showFigur9();
                }
                
            }
        });

        // Meny

        Menu menu = new Menu("File");

        
        menuBar.getMenus().addAll(menu);


        MenuItem newMap = new MenuItem("New Map");
        newMap.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearScreen();
                showImage(primaryStage, "file:europa.gif");
            
                toggleButtons(false);
                isSaved = false; 
            }
        });

        MenuItem open = new MenuItem("Open");
        open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = new File(fileName);
                if(!file.exists()){
                    noSavedMap.showAndWait();
                }
                else{
                    if (!isSaved) {
                        Optional<ButtonType> result = unsavedChanges.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            clearScreen();
                            //showImage(primaryStage, imageUrl);
                            textReader(fileName, primaryStage);
                            
                            toggleButtons(false);
                        }
                    } 
                    else{
                        clearScreen();
                        //showImage(primaryStage, imageUrl);
                        textReader(fileName, primaryStage);
                        
                        toggleButtons(false);
                    }
                }
            }
        });

        MenuItem save = new MenuItem("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FileWriter myWriter = new FileWriter(fileName);
                    StringBuilder sb = new StringBuilder();
                    sb.append(imageUrl);
                    sb.append(System.lineSeparator());

                    Set<City> temp = lp.getNodes();

                    //Detta printar rad 2
                    Boolean first = true;
                    for(City city : temp){
                        if(!first){
                            sb.append(";");
                        }
                        first = false; 
                        sb.append(city.getName());
                        sb.append(";" + city.getX());
                        sb.append(";" + city.getY());
                    }
                    sb.append(System.lineSeparator());

                    for(City city : temp){
                        Set<Edge<City>> listOfEdges = lp.getEdgesFrom(city);
                        for(Edge<City> edge : listOfEdges){
                            sb.append(city.getName() + ";");
                            sb.append(edge.getDestination().getName() + ";");
                            sb.append(edge.getName() + ";");
                            sb.append(edge.getWeight());
                            sb.append(System.lineSeparator());
                        }
                    }
                    myWriter.write(sb.toString());
                    myWriter.close();
                } catch (IOException e) {
                }
            }
        });

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isSaved) {
                    Optional<ButtonType> result = unsavedChanges.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        ((Stage) (((MenuItem) event.getSource()).getParentPopup().getOwnerWindow())).close();
                    }
                } 
                else {
                    ((Stage) (((MenuItem) event.getSource()).getParentPopup().getOwnerWindow())).close();
                }
            }
        });


        horizonalBox.setPrefWidth(width);
        horizonalBox.setAlignment(Pos.CENTER);
        horizonalBox.setPadding(new Insets(5));
        horizonalBox.getChildren().addAll(findPath, showConnection, newPlace);

        verticalBox.getChildren().addAll(horizonalBox);
        root.setTop(verticalBox);
        root.setCenter(pane);
        Scene scene = new Scene(root, width, heightWithoutMap);
        toggleButtons(true);

        MenuItem saveImage = new MenuItem("Save Image");
        saveImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveScreenshot(primaryStage, scene);
            }
        });


        menu.getItems().addAll(newMap, open, save, saveImage, exit);

        Button newConnection = new Button();
        buttons.add(newConnection);
        newConnection.setText("New Connection");
        newConnection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(checkIfBothSpotsAreFilled()){
                    if(lp.getEdgeBetween(firstCity, secondCity) != null){
                        Alert connectionAlreadyExists = new Alert(AlertType.ERROR);
                        connectionAlreadyExists.setTitle("ERROR!");
                        connectionAlreadyExists.setHeaderText(null);
                        connectionAlreadyExists.setContentText("Connection already exists!");
                        connectionAlreadyExists.showAndWait();
                    }
                    else{
                        isSaved = false; 
                        TextInputDialog nameOfNewPlace = new TextInputDialog("");
                        
                        nameOfNewPlace.setTitle("Name");
                        nameOfNewPlace.setHeaderText("Connection from " + firstCity.getName() + " to " + secondCity.getName());
                        nameOfNewPlace.setContentText("Name of place:");

                         // Create two text fields
                        TextField textField1 = new TextField();
                        TextField textField2 = new TextField();

                        // Create a GridPane and add the text fields
                        GridPane gridPane = new GridPane();
                        gridPane.setHgap(10);
                        gridPane.setVgap(10);
                        gridPane.addRow(0, new Label("Name: "), textField1);
                        gridPane.addRow(1, new Label("Time: "), textField2);
                        nameOfNewPlace.getDialogPane().setContent(gridPane);
                        String name = "";
                        int time = 0; 
                        nameOfNewPlace.showAndWait();
                        
                        if(!textField1.getText().isEmpty() || !textField2.getText().isEmpty()){ 
                            // "Namnfältet får inte vara tomt och tidfältet måste bestå av siffror."
                            // Jag anser att ett tomt fält inte består av siffror, därför kollar jag om textField2 är tomt här. 
                            try{
                                name = textField1.getText();
                                time = Integer.parseInt(textField2.getText());
                                if(time >=0) {
                                    isSaved = false;
                                    createConnections(secondCity, firstCity, name, time);
                                }
                                else{
                                    negativeIntDetected();
                                }
                                
                            }
                            catch (NumberFormatException e){

                                onlyIntegersAreAllowedAlert();
                            }
                        }
                        else{
                            Alert atleastOneFieldIsEmptyAlert = new Alert(AlertType.ERROR);
                                atleastOneFieldIsEmptyAlert.setTitle("ERROR!");
                                atleastOneFieldIsEmptyAlert.setHeaderText(null);
                                atleastOneFieldIsEmptyAlert.setContentText("Atleast one field is empty!");
                                atleastOneFieldIsEmptyAlert.showAndWait();
                        }
                                    
                    }
                }
                else{
                    showFigur9();
                }

            }
        });

        newPlace.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pane.setCursor(Cursor.CROSSHAIR); //Change cursor to crosshair
                newConnection.setDisable(true);
                placingNewCity = true; 
            }
        });



        
        
        toggleButtons(true);
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(placingNewCity){
                    TextInputDialog createNameAlert = new TextInputDialog("");
                    createNameAlert.setTitle("Name");
                    createNameAlert.setHeaderText(null);
                    createNameAlert.setContentText("Name of place:");
                    double x = event.getX();
                    double y = event.getY();
                    String enteredText = "";
                    Optional<String> result = createNameAlert.showAndWait();
                    if (result.isPresent()) {
                        enteredText = result.get();
                        addCityToScene(enteredText, x, y);
                        
                    }    
                    else{
                        placingNewCity = false; 
                    }         
                    pane.setCursor(Cursor.DEFAULT); //Change cursor to crosshair    
                    newConnection.setDisable(false);
                }
            }
        });

        menuBar.setId("menu");
        menu.setId("menuFile");
        newMap.setId("menuNewMap");
        open.setId("menuOpenFile");
        save.setId("menuSaveFile");
        saveImage.setId("menuSaveImage");
        exit.setId("menuExit");
        findPath.setId("btnFindPath");
        showConnection.setId("btnShowConnection");
        newPlace.setId("btnNewPlace");
        changeConnection.setId("btnChangeConnection");
        newConnection.setId("btnNewConnection");


        horizonalBox.getChildren().addAll(newConnection, changeConnection);
        primaryStage.setTitle("PathFinder");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST,
                new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        if (!isSaved) {
                            Optional<ButtonType> result = unsavedChanges.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                //System.out.println("pressed OK");
                            } 
                            else 
                            {
                                event.consume();
                            }
                        }
                    }
                });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void toggleButtons(Boolean toggle){
        for(Button button : buttons){
            button.setDisable(toggle);
        }
    }

    private void textReader(String filePath, Stage pstage) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line; 
            int rowCounter = 1; 
            Map<String, City> cityByName = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                if(rowCounter == 2){
                    String[] dataArray = line.split(";");
                    // Process the array elements
                    int counter = 0; 
                    String name = "";
                    double x = 0f; 
                    double y = 0f; 
                    for (String element : dataArray) {
                        if(counter == 0){
                            name = element;
                            counter++;
                        }
                        else if(counter == 1){
                            x = Double.parseDouble(element);
                            counter++;
                        }
                        else{
                            y = Double.parseDouble(element);
                            counter = 0; 
                            City city = addCityToScene(name, x, y);
                            cityByName.put(name, city);
                        }
                    }
                    
                }
                else if(rowCounter == 1){
                    imageUrl = line;
                    showImage(pstage, imageUrl);
                }
                else if(rowCounter > 2){
                    String[] dataArray = line.split(";");
                    // Process the array elements
                    int counter = 0; 
                    String name = "";
                    String nameFrom = "";
                    String nameTo = "";
                    int time = 0;                     
                    for (String element : dataArray) {
                        if(counter == 0){
                            //spara som firstCity
                            nameFrom = element;

                            counter++; 
                        }
                        // spara som secondCity
                        else if(counter == 1){
                            nameTo = element;
                            counter++;
                        }
                        else if(counter == 2){
                            //spara som namn
                            name = element;
                            counter++; 
                        }
                        else{
                            try{
                                time = Integer.parseInt(element);
                            
                                firstCity = cityByName.get(nameFrom);
                                secondCity = cityByName.get(nameTo);
                                if(lp.getEdgeBetween(secondCity, firstCity) == null && time >= 0){
                                    createConnections(secondCity, firstCity, name, time);
                                }
                            }
                            catch(NumberFormatException e){
                            }
                            counter = 0; 
                            firstCity = null; 
                            secondCity = null; 
                        }
                    }
                }
                rowCounter++;
            }
        } catch (IOException e) {
        }
    }



    private City addCityToScene(String enteredText, double x, double y) {
        City city = new City(enteredText, x, y);
        lp.add(city);
        placingNewCity = false;
        pane.getChildren().add(city);
        city.setOnMouseClicked(event -> {
            City clickedCity = (City) event.getSource();
            selectingCityForConnection(clickedCity);
        });
        
        return city; 
    }

    private void showImage(Stage primaryStage, String url){
        if(!showsImage){
            Image image = new Image(url);
            ImageView imageView = new ImageView(image);
            imageView.setDisable(true);
            primaryStage.setHeight(heightWithoutMap + image.getHeight() - 75f);
            pane.getChildren().add(imageView);
            
        }
    }

    private void selectingCityForConnection(City city){

        if(firstCity == city){
            firstCity = null; 
            city.setFill(Color.BLUE);
        }
        else if(secondCity == city){
            secondCity = null; 
            city.setFill(Color.BLUE);
        }
        else if(firstCity == null){
            firstCity = city; 
            city.setFill(Color.RED);
        }
        else if(secondCity == null){
            secondCity = city; 
            city.setFill(Color.RED);
        }
    }

    private boolean checkIfBothSpotsAreFilled(){
        if(firstCity != null && secondCity != null){
            return true;
        }
        return false; 
    }

    private void createConnections(City from, City to, String name, int time){
        lp.connect(from, to, name, time);
        Line line = new Line(firstCity.getX(), firstCity.getY(), secondCity.getX(), secondCity.getY());
        line.setStroke(Color.BLACK);
        line.setDisable(true);
        pane.getChildren().add(line); 
    }



    private TextField showConnectionAlert(TextInputDialog showConnectionAlert, String name, String time, boolean nameEdible, boolean timeedible){        
        showConnectionAlert.setTitle("Name");
        showConnectionAlert.setHeaderText("Connection from " + firstCity.getName() + " to " + secondCity.getName());
        showConnectionAlert.setContentText("Name of place:");

        // Create two text fields
        TextField textFieldName = new TextField(name);
        TextField textFieldTime = new TextField(time);
        textFieldName.setEditable(nameEdible);
        textFieldTime.setEditable(timeedible);
        // Create a GridPane and add the text fields
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, new Label("Name: "), textFieldName);
        gridPane.addRow(1, new Label("Time: "), textFieldTime);
        showConnectionAlert.getDialogPane().setContent(gridPane);
        return textFieldTime;
    }

    private void saveScreenshot(Stage primaryStage, Scene scene) {
        try {
            // Capture the scene as a WritableImage
            WritableImage writableImage = new WritableImage((int) primaryStage.getWidth(), (int) primaryStage.getHeight());
            scene.snapshot(writableImage);
            // Convert to BufferedImage
            java.awt.image.BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            // Save the image to a file
            File screenshotFile = new File("capture.png");
            ImageIO.write(bufferedImage, "png", screenshotFile);
        } catch (IOException e) {
            
        }
    }

    private void showFigur9(){
        Alert notEnoughSelected = new Alert(AlertType.ERROR);
        notEnoughSelected.setTitle("ERROR!");
        notEnoughSelected.setHeaderText(null);
        notEnoughSelected.setContentText("Two places must be selected!");
        notEnoughSelected.showAndWait();
    }

    private void clearScreen(){
        pane.getChildren().clear();
        lp = new ListGraph<City>();
        firstCity = null; 
        secondCity = null; 
    }

    private void pathDoesntExist(){
        Alert pathDoesNotExistAlert = new Alert(AlertType.ERROR);
        pathDoesNotExistAlert.setTitle("ERROR!");
        pathDoesNotExistAlert.setHeaderText(null);
        pathDoesNotExistAlert.setContentText("Path does not exist!");
        pathDoesNotExistAlert.showAndWait();
    }

    private void onlyIntegersAreAllowedAlert(){
        Alert integerAlert = new Alert(AlertType.ERROR);
        integerAlert.setTitle("ERROR!");
        integerAlert.setHeaderText(null);
        integerAlert.setContentText("Only integers are allowed!");
        integerAlert.showAndWait();
    }

    private void negativeIntDetected(){
        Alert negativeIntDetectedAlert = new Alert(AlertType.ERROR);
        negativeIntDetectedAlert.setTitle("ERROR!");
        negativeIntDetectedAlert.setHeaderText(null);
        negativeIntDetectedAlert.setContentText("Negative times are not allowed!");
        negativeIntDetectedAlert.showAndWait();
    }
}