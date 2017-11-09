package gui.main;

import comp.GameEntity;
import comp.Player;
import comp.Research;
import comp.Universe;
import gui.DataHolder;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
public class Main extends Application implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox root;

    @FXML
    private Tab overViewTab;

    @FXML
    private TreeView<GameEntity> uniPlayerTree;

    @FXML
    private Text universeName;

    @FXML
    private Text playerName;

    @FXML
    private Text darkMatterValue;

    @FXML
    private Text pointsValue;

    @FXML
    private Text highscoreValue;


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("OgameBot");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DataHolder.getInstance().addListener((observable, oldValue, newValue) -> loadPlayer(newValue));
        loadContent();
        setPaneListener(root);
        readyTree();
    }

    private void loadPlayer(Player newValue) {
        if (newValue != null) {
            playerName.textProperty().bind(newValue.nameProperty());
            universeName.textProperty().bind(newValue.getUniverse().nameProperty());

            pointsValue.textProperty().bind(newValue.pointsProperty().asString());
            highscoreValue.textProperty().bind(newValue.highscoreProperty().asString());
            darkMatterValue.textProperty().bind(newValue.darkMatterProperty().asString());
        } else {
            playerName.setText("N/A");
            universeName.setText("N/A");

            darkMatterValue.setText("0");
            pointsValue.setText("0");
            highscoreValue.setText("0");
        }
    }

    private void readyTree() {
        GameEntity entity = new GameEntity() {
            @Override
            public String getText() {
                return "ROOT";
            }

            @Override
            public List<GameEntity> getChildren() {
                return getEntities();
            }
        };

        TreeItem<GameEntity> root = new TreeItem<>(entity);
        resolveTree(entity, root);
        uniPlayerTree.setRoot(root);

        readyTreeSelection();

        uniPlayerTree.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(GameEntity item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getText());
                }
            }
        });
    }

    private void readyTreeSelection() {
        uniPlayerTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isLeaf() && newValue.getValue() instanceof Player) {
                DataHolder.getInstance().setCurrentPlayer((Player) newValue.getValue());
            }
        });

        uniPlayerTree.getSelectionModel().selectFirst();
        TreeItem<GameEntity> selectedItem = uniPlayerTree.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            ObservableList<TreeItem<GameEntity>> children = selectedItem.getChildren();

            if (!children.isEmpty()) {
                uniPlayerTree.getSelectionModel().select(children.get(0));
            }
        }
    }

    private List<GameEntity> getEntities() {
        Universe value = new Universe(0, 0, 0, 0, false, false, 0, "orion");
        Player w = new Player("w", 0, 0, 0, new Research(), value);
        Player f = new Player("f", 0, 0, 0, new Research(), value);
        Player a = new Player("a", 0, 0, 0, new Research(), value);
        Player s = new Player("s", 0, 0, 0, new Research(), value);
        Player d = new Player("d", 0, 0, 0, new Research(), value);
        Player player1 = new Player("!", 0, 0, 0, new Research(), value);
        value.addPlayer(w);
        value.addPlayer(a);
        value.addPlayer(s);
        value.addPlayer(d);
        value.addPlayer(f);
        value.addPlayer(player1);

        List<GameEntity> entities = new ArrayList<>();
        entities.add(value);
        return entities;
    }

    private void resolveTree(GameEntity entity, TreeItem<GameEntity> root) {
        entity.getChildren().forEach(gameEntity -> {
            TreeItem<GameEntity> item = new TreeItem<>(gameEntity);
            root.getChildren().add(item);
            resolveTree(gameEntity, item);
        });
    }

    private void setPaneListener(Parent root) {
        for (Node node : root.getChildrenUnmodifiable()) {
            node.setOnMouseClicked(event -> node.requestFocus());

            if (node instanceof Parent) {
                setPaneListener((Parent) node);
            }
        }
    }

    private void loadContent() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/overview.fxml"));
        try {
            BorderPane root = loader.load();
            overViewTab.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
