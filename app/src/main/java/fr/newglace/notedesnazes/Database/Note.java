package fr.newglace.notedesnazes.Database;

import java.io.Serializable;

public class Note implements Serializable {

    private String noteTitle, password, noteContent, visual, folder;
    private boolean favorite;
    private int position, folderPosition;

    public Note(String noteTitle, String noteContent, boolean favorite, String password, String visual, String folder,
                int position, int folderPosition) {
        this.noteTitle= noteTitle;
        this.noteContent= noteContent;
        this.favorite = favorite;
        this.password = password;
        this.visual = visual;
        this.folder = folder;
        this.position = position;
        this.folderPosition = folderPosition;
    }
    public String getNoteTitle() {
        return noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public String getPassword() {
        return password;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public int getFolderPosition() {
        return folderPosition;
    }

    public int getPosition() {
        return position;
    }

    public String getFolder() {
        return folder;
    }

    public String getVisual() {
        return visual;
    }

    @Override
    public String toString()  {
        return this.noteTitle;
    }
}
