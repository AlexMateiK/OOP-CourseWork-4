import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 *  Helper class that builds an individual statistic viewer.
 *  Construct an object of this class to build a single statistic viewer ( text + arrow buttons ),
 *  and call the refreshStat() method whenever you need to refresh the information it displays.
 *
 *  @author Alexandru Matei, K20054925
 *  @version 7/04/2021
 */

public class StatViewer {

    private int currentStatIndex;
    private Text statLabel;
    private BorderPane container;
    private StatLogicHelper statLogicHelper;

    /**
     * Searches for the nearest available statistic that is not currently displayed and comes before the current stat.
     * @param actionEvent
     */
    private void previousStat(ActionEvent actionEvent){
        int newStatIndex = statLogicHelper.getPreviousStatIndex(currentStatIndex);
        currentStatIndex = newStatIndex;

        String stat = statLogicHelper.getStatTextByIndex(newStatIndex);
        statLabel.setText(stat);
    }

    /**
     * Searches for the nearest available statistic that is not currently displayed and comes after the current stat.
     * @param actionEvent
     */
    private void nextStat(ActionEvent actionEvent){
        int newStatIndex = statLogicHelper.getNextStatIndex(currentStatIndex);
        currentStatIndex = newStatIndex;

        String stat = statLogicHelper.getStatTextByIndex(newStatIndex);
        statLabel.setText(stat);
    }

    /**
     * Refreshes the displayed information. This method must be called whenever the statistics have been updated because of a change ( in the user's price range or favorite property list )
     */
    public void refreshStat(){
        String stat = statLogicHelper.getStatTextByIndex(currentStatIndex);
        statLabel.setText(stat);
    }

    /**
     * Creates a statistic viewer.
     * @param startingIndex A number that represents which of the 8 statistics this statistic viewer should start at. The statistics are numbered 0 through 7.
     * @param statLogicHelper The StatLogicHelper type object that this class needs to source the information to be displayed.
     */
    public StatViewer(int startingIndex, StatLogicHelper statLogicHelper){
        currentStatIndex = startingIndex;
        this.statLogicHelper = statLogicHelper;

        container = new BorderPane();

        statLabel = new Text("test123");
        container.setCenter(statLabel);

        Button leftButton = new Button("<");
        leftButton.setOnAction(this::previousStat);

        Button rightButton = new Button(">");
        rightButton.setOnAction(this::nextStat);

        leftButton.getStyleClass().add("stat-button");
        rightButton.getStyleClass().add("stat-button");

        //statLabel.getStyleClass().add("stat-text");

        leftButton.prefHeightProperty().bind(container.heightProperty());
        rightButton.prefHeightProperty().bind(container.heightProperty());

        container.setLeft(leftButton);
        container.setRight(rightButton);

        refreshStat();
    }

    /**
     * Returns the javafx container of the statistic viewer.
     * @return The container of all of the statistic viewer's components.
     */
    public BorderPane getStatViewer(){
        return container;
    }
}
