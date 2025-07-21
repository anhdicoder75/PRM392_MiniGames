package com.example.prm392_minigames.models;

public class AdminGame {
    public int id;        // auto increment or position in list
    public int iconRes;   // drawable icon resource id
    public String title;
    public String desc;

    public AdminGame(int id, int iconRes, String title, String desc) {
        this.id = id;
        this.iconRes = iconRes;
        this.title = title;
        this.desc = desc;
    }
}
