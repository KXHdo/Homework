package com.jnu.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jnu.student.data.DataBank;

public class UserProfileActivity extends AppCompatActivity {
    private DataBank dataBank = new DataBank();
    private int score;

    private Button resetScoreButton;
    private TextView userScoreView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        userScoreView = findViewById(R.id.userScore);
        TextView userNameView = findViewById(R.id.userName);
        resetScoreButton = findViewById(R.id.resetScoreButton);

        String userName = "美味的GGbond";
        score = dataBank.loadScore(this);
        userScoreView.setText("分数: " + score);
        userNameView.setText(userName);

        resetScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重置分数逻辑
                score = 0;
                dataBank.saveScore(UserProfileActivity.this, score);
                userScoreView.setText("分数: " + score);
                Toast.makeText(UserProfileActivity.this, "分数已重置", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
