package Model;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.Map;

/** Interface for Reports screen.
 * Defines how implementing classes handle applicable ComboBoxes, column mappings, populates tables for report types
 */
public interface Report {
    // set up combobox if applicable, set up column mappings, populate table if no selector combo box
    public void initialize(Label reportLabel, ComboBox reportComboBox, TableView<Map> reportTableView);
    // whether or not the combobox should be displayed for report
    public boolean hasComboBox();
    // get list of objects required for tableview display,
    public ObservableList<Map> getItems();
}
