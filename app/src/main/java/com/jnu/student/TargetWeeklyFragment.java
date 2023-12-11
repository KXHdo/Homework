package com.jnu.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jnu.student.data.CompletedTask;
import com.jnu.student.data.DataBank;
import com.jnu.student.data.ScoreItem;
import com.jnu.student.data.TargetWeekly;

import java.util.ArrayList;
import java.util.Date;

public class TargetWeeklyFragment extends Fragment {
    public TargetWeeklyFragment() {
        // Required empty public constructor
    }

    public static TargetWeeklyFragment newInstance() {
        TargetWeeklyFragment fragment = new TargetWeeklyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }
    private ActivityResultLauncher<Intent> addItemLauncher2;
    private ActivityResultLauncher<Intent> updateItemLauncher2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_target_weekly, container, false);

        RecyclerView mainRecyclerView = rootView.findViewById(R.id.recyclerview_target_weekly);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));



        targetWeeklies = new DataBank().LoadTargetWeeklies(requireActivity());

        if(0 == targetWeeklies.size()) {
            targetWeeklies.add(new TargetWeekly("添加任务", 0.0, R.drawable.taskphoto));
        }


        targetWeeklyAdapter = new TargetWeeklyAdapter(targetWeeklies);
        mainRecyclerView.setAdapter(targetWeeklyAdapter);
        registerForContextMenu(mainRecyclerView);

        addItemLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Log.d("Serialization", "3");
                        String name = data.getStringExtra("name"); // 获取返回的数据
                        String priceText = data.getStringExtra("price"); // 获取返回的数据

                        double price= Double.parseDouble(priceText);
                        targetWeeklies.add(new TargetWeekly(name, price, R.drawable.taskphoto));
                        targetWeeklyAdapter.notifyItemInserted(targetWeeklies.size());
                        Log.d("Serialization", "4");
                        new DataBank().SaveTargetWeeklies(requireActivity(), targetWeeklies);
                        Log.d("Serialization", "2Data loaded successfully.item count "+targetWeeklies.size());

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // 处理取消操作
                    }
                }
        );
        updateItemLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        int position = data.getIntExtra("position",0);
                        String name = data.getStringExtra("name"); // 获取返回的数据
                        String priceText = data.getStringExtra("price"); // 获取返回的数据

                        double price= Double.parseDouble(priceText);
                        TargetWeekly targetWeekly = targetWeeklies.get(position);
                        targetWeekly.setPrice(price);
                        targetWeekly.setName(name);
                        targetWeeklyAdapter.notifyItemChanged(position);
                        Log.d("Serialization", "2Data loaded successfully.item count "+targetWeeklies.size());
                        new DataBank().SaveTargetWeeklies(requireActivity(), targetWeeklies);


                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // 处理取消操作
                    }
                }
        );

        return rootView;
    }

    private ArrayList<TargetWeekly> targetWeeklies = new ArrayList<>();
    private TargetWeeklyAdapter targetWeeklyAdapter;
    private ScoreItem scoreItem;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != 1)
        {
            Log.d("Serialization", "False");
            return false;
        }
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
                builder1.setTitle("完成任务");
                builder1.setMessage("你确定完成该任务吗?");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TargetWeekly targetWeekly= targetWeeklies.get(item.getOrder());

                        int currentScore =new DataBank().loadScore(requireActivity());
                        int newScore = currentScore + (int)targetWeekly.getPrice();
                        new DataBank().saveScore(requireActivity(), newScore);
                        updateScoreInView(newScore);

                        CompletedTask completedTask = new CompletedTask(targetWeekly.getName(), targetWeekly.getPrice(), targetWeekly.getImageResourceId(), new Date());
                        new DataBank().saveCompletedTask(requireActivity(), completedTask);

                        targetWeeklies.remove(item.getOrder());
                        targetWeeklyAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveTargetWeeklies(requireActivity(), targetWeeklies);
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder1.create().show();
                break;
            case 1:
                Log.d("Serialization", "1");
                Intent intent = new Intent(requireActivity(), TargetWeeklyDetailsActivity.class);
                Log.d("Serialization", "2");
                addItemLauncher2.launch(intent);

                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("删除任务");
                builder.setMessage("你确定要删除该任务吗?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetWeeklies.remove(item.getOrder());
                        targetWeeklyAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveTargetWeeklies(requireActivity(), targetWeeklies);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                break;
            case 3:
                Intent intentUpdate = new Intent(requireActivity(), TargetWeeklyDetailsActivity.class);
                TargetWeekly targetWeekly= targetWeeklies.get(item.getOrder());
                intentUpdate.putExtra("name",targetWeekly.getName());
                intentUpdate.putExtra("price",targetWeekly.getPrice());
                intentUpdate.putExtra("position",item.getOrder());
                updateItemLauncher2.launch(intentUpdate);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public class TargetWeeklyAdapter extends RecyclerView.Adapter<TargetWeeklyAdapter.ViewHolder> {

        private ArrayList<TargetWeekly> targetWeeklyArrayList;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView textViewName;
            private final TextView textViewPrice;
            private final ImageView imageViewItem;

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("具体操作");
                menu.add(1, 0, this.getAdapterPosition(), "完成任务" );
                menu.add(1, 1, this.getAdapterPosition(), "添加任务" );
                menu.add(1, 2, this.getAdapterPosition(), "删除任务" );
                menu.add(1, 3, this.getAdapterPosition(), "修改任务" );
            }

            public ViewHolder(View targetWeeklyView) {
                super(targetWeeklyView);
                // Define click listener for the ViewHolder's View

                textViewName = targetWeeklyView.findViewById(R.id.textview_item_name);
                textViewPrice = targetWeeklyView.findViewById(R.id.textview_item_price);
                imageViewItem = targetWeeklyView.findViewById(R.id.imageview_item);
                targetWeeklyView.setOnCreateContextMenuListener(this);
            }

            public TextView getTextViewName() {
                return textViewName;
            }

            public TextView getTextViewPrice() {
                return textViewPrice;
            }

            public ImageView getImageViewItem() {
                return imageViewItem;
            }
        }

        public TargetWeeklyAdapter(ArrayList<TargetWeekly> targetWeeklies) {
            targetWeeklyArrayList = targetWeeklies;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.target_weekly_row, viewGroup, false);
            //view.setBackgroundColor(Color.rgb(204,204,204));
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewName().setText(targetWeeklyArrayList.get(position).getName());
            viewHolder.getTextViewName().setTextColor(Color.BLACK);
            viewHolder.getTextViewPrice().setText("+"+(int)targetWeeklyArrayList.get(position).getPrice());
            viewHolder.getTextViewPrice().setTextColor(Color.rgb(0,180,51));
            viewHolder.getImageViewItem().setImageResource(targetWeeklyArrayList.get(position).getImageResourceId());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return targetWeeklyArrayList.size();
        }
    }
    private void updateScoreInView(int newScore) {
        Activity activity = getActivity();
        if (activity != null) {
            TextView scoreTextView = activity.findViewById(R.id.scoreTextView);
            scoreTextView.setText("分数: " + newScore);
        }
    }

}