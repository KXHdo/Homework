package com.jnu.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button option1Button, option2Button, option3Button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);
        option3Button = findViewById(R.id.option3Button);

        option1Button.setOnClickListener(this);
        option2Button.setOnClickListener(this);
        option3Button.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.option1Button) {
            Toast.makeText(this, "选项1被点击", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, TargetActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.option2Button) {
            Toast.makeText(this, "选项2被点击", Toast.LENGTH_SHORT).show();

        } else if (viewId == R.id.option3Button) {
            Toast.makeText(this, "选项3被点击", Toast.LENGTH_SHORT).show();

        }
    }

}