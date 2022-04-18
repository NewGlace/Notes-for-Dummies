package fr.newglace.notedesnazes.Styles.Notes;

public class noteArray {
    private String title, desc, password, visual, folder, colorFolder;
    private int i, position, folderPosition;
    private boolean favorite;

    public noteArray(String title, String desc, int i, boolean favorite, String password, String visual,
                     String folder, int position, int folderPosition, String colorFolder) {
        this.title = title;
        this.desc = desc;
        this.i = i;
        this.favorite = favorite;
        this.password = password;
        this.visual = visual;
        this.folder = folder;
        this.position = position;
        this.folderPosition = folderPosition;
        this.colorFolder = colorFolder;
    }

    public int getI() {
        return i;
    }
    public String getDesc() {
        return desc;
    }
    public String getTitle() {
        return title;
    }
    public String getPassword() {
        return password;
    }
    public String getVisual() {
        return visual;
    }
    public String getFolder() {
        return folder;
    }
    public boolean isFavorite() {
        return favorite;
    }
    public int getPosition() {
        return position;
    }
    public int getFolderPosition() {
        return folderPosition;
    }
    public String getColorFolder() {
        return colorFolder;
    }
}
