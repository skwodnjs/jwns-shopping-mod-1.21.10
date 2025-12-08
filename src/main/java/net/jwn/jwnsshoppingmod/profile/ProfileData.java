package net.jwn.jwnsshoppingmod.profile;

public class ProfileData {
    private String name;
    private int level;
    private String alias;
    private int coins;
    private int time;
    private Boolean isMinute;
    private String comment;

    public ProfileData(String name, int level, String alias, int coins, int time, Boolean isMinute, String comment) {
        this.name = name;
        this.level = level;
        this.alias = alias;
        this.coins = coins;
        this.time = time;
        this.isMinute = isMinute;
        this.comment = comment;
    }

    // --- name ---
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // --- level ---
    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    // --- alias ---
    public String getAlias() {
        return this.alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    // --- coins ---
    public int getCoins() {
        return this.coins;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }

    // --- time ---
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    // --- isMinute ---
    public Boolean getIsMinute() {
        return this.isMinute;
    }
    public void setIsMinute(Boolean isMinute) {
        this.isMinute = isMinute;
    }

    // --- comment ---
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}

