package com.epicodus.exquisite.models;

import org.parceler.Parcel;

@Parcel
public class Game {
    private String openingLine;
    private String ownerUid;
    private String ownerName;
    private String collaboratorUid;
    private String collaboratorName;

    public Game() {}

    public Game(String openingLine, String ownerUid, String ownerName) {
        this.openingLine = openingLine;
        this.ownerUid = ownerUid;
        this.ownerName = ownerName;
    }

    public String getOpeningLine() {
        return openingLine;
    }

    public void setOpeningLine(String openingLine) {
        this.openingLine = openingLine;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCollaboratorUid() {
        return collaboratorUid;
    }

    public void setCollaboratorUid(String collaboratorUid) {
        this.collaboratorUid = collaboratorUid;
    }

    public String getCollaboratorName() {
        return collaboratorName;
    }

    public void setCollaboratorName(String collaboratorName) {
        this.collaboratorName = collaboratorName;
    }
}
