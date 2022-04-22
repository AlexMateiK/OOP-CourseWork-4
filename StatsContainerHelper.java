import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Helper class used to build a statistics viewer and to update the statistics when needed.
 *
 * @author Alexandru Matei, K20054925
 * @version 7/04/2021
 */

public class StatsContainerHelper {

    private StatViewer statViewer1;
    private StatViewer statViewer2;
    private StatViewer statViewer3;
    private StatViewer statViewer4;
    private StatLogicHelper statLogicHelper;

    /**
     * Builds the statistics viewer and returns its container.
     * @param filteredListings
     * @return The pane containing the four stats.
     */
    public Pane getStatsGrid(ArrayList<AirbnbListing> filteredListings){
        GridPane statsContainer = new GridPane();

        statsContainer.getStylesheets().add("stats.css");
        statsContainer.getStyleClass().add("stats-container");

        /*
        Until I connect this to the rest..
        * */

        statLogicHelper = new StatLogicHelper(filteredListings);
        // to get passed to all the stat viewers in order to connect to the logic part I guess...

        statViewer1 = new StatViewer(0,statLogicHelper);
        statViewer2 = new StatViewer(1,statLogicHelper);
        statViewer3 = new StatViewer(2,statLogicHelper);
        statViewer4 = new StatViewer(3,statLogicHelper);

        Pane statViewerPane1 = statViewer1.getStatViewer();
        Pane statViewerPane2 = statViewer2.getStatViewer();
        Pane statViewerPane3 = statViewer3.getStatViewer();
        Pane statViewerPane4 = statViewer4.getStatViewer();

        statViewerPane1.getStyleClass().add("stat-viewer");
        statViewerPane2.getStyleClass().add("stat-viewer");
        statViewerPane3.getStyleClass().add("stat-viewer");
        statViewerPane4.getStyleClass().add("stat-viewer");

        statsContainer.setConstraints(statViewerPane1,0,0);
        statsContainer.setConstraints(statViewerPane2,1,0);
        statsContainer.setConstraints(statViewerPane3,0,1);
        statsContainer.setConstraints(statViewerPane4,1,1);

        statsContainer.getChildren().addAll(statViewerPane1,statViewerPane2,statViewerPane3,statViewerPane4);

        return statsContainer;
    }

    /**
     * Computes all stats again and refreshes the individual statistic viewer's displayed information.
     */
    public void updateAllStats(ArrayList<AirbnbListing> list){
        statLogicHelper.updateStats(list);
        statViewer1.refreshStat();
        statViewer2.refreshStat();
        statViewer3.refreshStat();
        statViewer4.refreshStat();
    }

    /**
     * Computes only the stats related to the user's favorited properties again and refreshes the individual statistic viewer's displayed information.
     */
    public void updateOnlyFavStats(){
        statLogicHelper.updateOnlyFavoriteStats();
        statViewer1.refreshStat();
        statViewer2.refreshStat();
        statViewer3.refreshStat();
        statViewer4.refreshStat();
    }
}
