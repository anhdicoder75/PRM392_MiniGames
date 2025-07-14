package com.example.prm392_minigames.models;

public class MemoryCard {
    public int id;
    public int iconRes;
    public boolean isFlipped = false;
    public boolean isMatched = false;
    public MemoryCard(int id, int iconRes) {
        this.id = id;
        this.iconRes = iconRes;
    }
}
