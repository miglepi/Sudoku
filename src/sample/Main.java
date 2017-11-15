package sample;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

    private TextArea[][] sudokuCells = new TextArea[9][9];

    Stage window;
    Scene mainScene, easyScene, hardScene, emptyScene;
    ISudokuGenerator sudokuGenerator = new SudokuGenerator();

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Sudoku");

        // Main scene
        Label label1 = new Label("PICK A GAME:");

        Button easyButton = new Button("Easy");
        easyButton.setOnAction(e -> {
            easyScene = createSudokuScene(true);
            int[][] generatedSudoku = sudokuGenerator.generate(SudokuType.Easy);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    TextArea tempCell = sudokuCells[i][j];
                    tempCell.setDisable(false);
                    if (generatedSudoku[i][j] != 0) {
                        tempCell.setText(String.valueOf(generatedSudoku[i][j]));
                    }
                }
            }
            window.setScene(easyScene);
        });

        Button hardButton = new Button("Hard");
        hardButton.setOnAction(e -> {
            hardScene = createSudokuScene(true);
            int[][] generatedSudoku = sudokuGenerator.generate(SudokuType.Hard);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    TextArea tempCell = sudokuCells[i][j];
                    tempCell.setDisable(false);
                    if (generatedSudoku[i][j] != 0) {
                        tempCell.setText(String.valueOf(generatedSudoku[i][j]));
                    }
                }
            }
            window.setScene(hardScene);
        });

        Button emptyButton = new Button("Empty");
        emptyButton.setOnAction(e -> {
            emptyScene = createSudokuScene(false);
            window.setScene(emptyScene);
        });

        //Layout 1 - children laid out in vertical column
        VBox layout1 = new VBox(40);
        layout1.getChildren().addAll(label1, easyButton, hardButton, emptyButton);
        mainScene = new Scene(layout1, 300, 300);
        layout1.setAlignment(Pos.TOP_CENTER);
        label1.setId("mainLabel");
        layout1.getStylesheets().add(getClass().getResource("sudoku.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private Scene createSudokuScene(boolean addCheckButton) {
        BorderPane borderPane = new BorderPane();
        HBox hbox = addHBox();
        VBox vbox = addVBox();
        borderPane.setBottom(hbox);
        borderPane.setCenter(vbox);

        Button buttonSolve = new Button("Solve!");
        hbox.getChildren().add(buttonSolve);
        buttonSolve.setOnAction(event -> {
            int sudoku[][] = new int[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    TextArea tempCell = sudokuCells[i][j];
                    tempCell.setDisable(true);
                    if (tempCell.getText().isEmpty()) {
                        sudoku[i][j] = 0;
                    } else {
                        sudoku[i][j] = Integer.valueOf(tempCell.getText());
                    }
                }
            }

            ISudokuSolver solver = new BackTrackingSudokuSolver();
            int[][] solution = solver.solve(sudoku);

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    TextArea tempCell = sudokuCells[i][j];
                    tempCell.setDisable(false);
                    tempCell.setText(String.valueOf(solution[i][j]));
                }
            }
        });

        // button back to main scene

        Button buttonBack = new Button("Back");
        hbox.getChildren().add(buttonBack);
        buttonBack.setOnAction(event -> {
            window.setScene(mainScene);
        });


        Scene scene = new Scene(borderPane, 640, 720);
        scene.getStylesheets().add(getClass().getResource("sudoku.css").toExternalForm());

        return scene;
    }

    private VBox addVBox() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(15));
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(1);

        Text title = new Text("Please input a puzzle to solve \n");
        title.setId("title");
        vbox.getChildren().add(title);

        addCells(vbox);

        return vbox;
    }

    private HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(5, 12, 15, 12));
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(10);

        return hbox;
    }

    private void addCells(VBox vbox) {
        PseudoClass right = PseudoClass.getPseudoClass("right");
        PseudoClass bottom = PseudoClass.getPseudoClass("bottom");
        for (int i = 0; i < 9; i++) {
            HBox hbox = new HBox(1);
            for (int j = 0; j < 9; j++) {
                final int MAX_CHARS = 1;
                TextArea textArea = new TextArea();
                textArea.getStyleClass().add("cell");
                textArea.pseudoClassStateChanged(right, j == 2 || j == 5);
                textArea.pseudoClassStateChanged(bottom, i == 2 || i == 5);
                textArea.setText("");
                textArea.setTextFormatter(new TextFormatter<String>(change ->
                        change.getControlNewText().length() <= MAX_CHARS ? change : null));
                textArea.setOnKeyTyped(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (isNumeric(event.getCharacter())) {
                            textArea.setText(event.getCharacter());
                        } else if (!isNumeric(textArea.getText())) {
                            textArea.setText("0");
                        }
                        System.out.println("A: " + event.getCharacter());
                    }
                });
                hbox.getChildren().add(textArea);
                sudokuCells[i][j] = textArea;
            }
            vbox.getChildren().add(hbox);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


}
