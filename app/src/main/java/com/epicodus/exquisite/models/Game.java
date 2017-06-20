package com.epicodus.exquisite.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Game {
    String openingLine;
    String ownerUid;
    String ownerName;
    String collaboratorUid;
    String collaboratorName;
    List<String> ownerSentences = new ArrayList<>();
    List<String> collaboratorSentences = new ArrayList<>();

    public Game() {}

    public Game(String openingLine, String ownerUid, String ownerName) {
        this.openingLine = openingLine;
        this.ownerUid = ownerUid;
        this.ownerName = ownerName;
        this.ownerSentences.add(openingLine);
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

    public List<String> getOwnerSentences() {
        return ownerSentences;
    }

    public void setOwnerSentences(List<String> ownerSentences) {
        this.ownerSentences = ownerSentences;
    }

    public List<String> getCollaboratorSentences() {
        return collaboratorSentences;
    }

    public void setCollaboratorSentences(List<String> collaboratorSentences) {
        this.collaboratorSentences = collaboratorSentences;
    }
}
