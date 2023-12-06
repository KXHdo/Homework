package com.jnu.student.data;

import java.io.Serializable;

public class ScoreItem implements Serializable {
    private int score;

    public ScoreItem() {
        this.score = 0;
    }

    public ScoreItem(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    // 清空分数
    public void resetScore() {
        this.score = 0;
    }
}
