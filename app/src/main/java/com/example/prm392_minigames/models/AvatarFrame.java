package com.example.prm392_minigames.models;

public class AvatarFrame {
    public int id; // 0: default
    public int iconRes; // icon của frame, dùng drawable
    public String name;
    public int cost; // Số điểm để mở khóa

    public AvatarFrame(int id, int iconRes, String name, int cost) {
        this.id = id;
        this.iconRes = iconRes;
        this.name = name;
        this.cost = cost;
    }
}
