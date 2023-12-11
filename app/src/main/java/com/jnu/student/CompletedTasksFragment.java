package com.jnu.student;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jnu.student.data.CompletedTask;
import com.jnu.student.data.DataBank;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CompletedTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private CompletedTasksAdapter adapter;

    private ArrayList<CompletedTask> completedTasks;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCompletedTasks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCompletedTasks();
            }
        });

        DataBank dataBank = new DataBank();
        completedTasks = dataBank.loadCompletedTasks(getContext());

        adapter = new CompletedTasksAdapter(completedTasks);
        recyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // 当 Fragment 重新可见时加载已完成的任务数据
        loadCompletedTasks();
    }

    private void loadCompletedTasks() {
        // 加载已完成的任务
        DataBank dataBank = new DataBank();
        ArrayList<CompletedTask> loadedTasks = dataBank.loadCompletedTasks(getContext());

        // 更新适配器数据并通知数据变化
        completedTasks.clear();
        completedTasks.addAll(loadedTasks);
        adapter.notifyDataSetChanged();
    }

    private static class CompletedTasksAdapter extends RecyclerView.Adapter<CompletedTasksAdapter.ViewHolder> {

        private final ArrayList<CompletedTask> completedTasks;

        CompletedTasksAdapter(ArrayList<CompletedTask> completedTasks) {
            this.completedTasks = completedTasks;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_task, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CompletedTask task = completedTasks.get(position);
            holder.taskNameTextView.setText(task.getName());
            holder.taskNameTextView.setTextColor(Color.BLACK);
            holder.taskScoreTextView.setText("+"+(int)task.getPrice());
            holder.taskScoreTextView.setTextColor(Color.rgb(0,180,51));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(task.getCompletionDate());
            holder.completionDateTextView.setText(formattedDate);
            // 设置图像
            holder.taskImageView.setImageResource(R.drawable.taskphoto); // 根据任务设置相应的图像
        }

        @Override
        public int getItemCount() {
            return completedTasks.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView taskNameTextView;
            final TextView taskScoreTextView;
            final TextView completionDateTextView;
            final ImageView taskImageView;

            ViewHolder(View view) {
                super(view);
                taskNameTextView = view.findViewById(R.id.textview_task_name);
                taskScoreTextView = view.findViewById(R.id.textview_task_score);
                completionDateTextView = view.findViewById(R.id.textview_completion_date);
                taskImageView = view.findViewById(R.id.imageview_completed_task);
            }
        }
    }
    private Button clearButton;


    private void clearCompletedTasks() {
        // 清空完成任务的数据
        DataBank dataBank = new DataBank();
        dataBank.clearCompletedTasks(getContext()); // 清空数据

        // 清空适配器中的数据并刷新
        completedTasks.clear();
        adapter.notifyDataSetChanged();
    }
}
