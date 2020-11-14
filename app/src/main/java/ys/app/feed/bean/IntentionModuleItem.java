package ys.app.feed.bean;

public class IntentionModuleItem {
    private String moduleName;
    private int moduleImage;
    private boolean isSelected;
    private String intention;

    public String getIntention() {
        return intention;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public IntentionModuleItem(String moduleName, int moduleImage, String intention, boolean isSelected){
        this.moduleName = moduleName;
        this.moduleImage = moduleImage;
        this.intention = intention;
        this.isSelected = isSelected;
    }
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getModuleImage() {
        return moduleImage;
    }

    public void setModuleImage(int moduleImage) {
        this.moduleImage = moduleImage;
    }
}
