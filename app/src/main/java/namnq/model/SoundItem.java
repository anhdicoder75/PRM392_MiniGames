package namnq.model;

public class SoundItem {
    public String name;
    public String assetPath;
    public String category;
    public boolean isSelected;

    public SoundItem(String name, String assetPath, String category) {
        this.name = name;
        this.assetPath = assetPath;
        this.category = category;
        this.isSelected = false;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getCategory() {
        return category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}