package Interface;

import javafx.scene.control.CheckBoxTreeItem;

public class CheckItems {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CheckBoxTreeItem getItem() {
        return item;
    }

    public void setItem(CheckBoxTreeItem item) {
        this.item = item;
    }

    private CheckBoxTreeItem item;

    public CheckItems(int id, CheckBoxTreeItem item) {
        this.id = id;
        this.item = item;
    }
}
