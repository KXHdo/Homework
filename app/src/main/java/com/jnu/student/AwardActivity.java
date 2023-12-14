package com.jnu.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

import com.jnu.student.data.CompletedTask;
import com.jnu.student.data.DataBank;
import com.jnu.student.data.ScoreItem;
import com.jnu.student.data.AwardItem; // Updated import
import com.jnu.student.data.ShopItem;

import java.util.ArrayList;
import java.util.Date;

public class AwardActivity extends AppCompatActivity { // Changed to Activity

    private ActivityResultLauncher<Intent> addAwardLauncher;
    private ActivityResultLauncher<Intent> updateAwardLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_award); // Set the layout for this activity

        RecyclerView mainRecyclerView = findViewById(R.id.recyclerview_main); // Use the correct RecyclerView id

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        DataBank dataBank = new DataBank();
        int score = dataBank.loadScore(this);
        scoreTextView.setText("分数: " + score);
        scoreTextView.setTextColor(Color.BLACK);
        scoreTextView.setTextSize(20);

        registerForContextMenu(mainRecyclerView);
        Log.d("Serialization", "registerShopping");

        awardItems = new DataBank().LoadAwardItem(this); // Load AwardItems instead of ShopItems
        if (awardItems.size() == 0) {
            awardItems.add(new AwardItem("添加奖励", 0.0, R.drawable.taskphoto)); // Add a default AwardItem
        }

        awardItemAdapter = new AwardItemAdapter(awardItems);
        mainRecyclerView.setAdapter(awardItemAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addAwardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("name");
                        String priceText = data.getStringExtra("price");

                        double price = Double.parseDouble(priceText);
                        awardItems.add(new AwardItem(name, price, R.drawable.taskphoto));
                        awardItemAdapter.notifyItemInserted(awardItems.size());

                        new DataBank().SaveAwardItems(this, awardItems);
                        Log.d("Serialization", "AwardActivity add");

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // Handle cancel operation
                    }
                }
        );

        updateAwardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        int position = data.getIntExtra("position", 0);
                        String name = data.getStringExtra("name");
                        String priceText = data.getStringExtra("price");

                        double price = Double.parseDouble(priceText);
                        AwardItem awardItem = awardItems.get(position);
                        awardItem.setPrice(price);
                        awardItem.setName(name);
                        awardItemAdapter.notifyItemChanged(position);

                        new DataBank().SaveAwardItems(this, awardItems);
                        Log.d("Serialization", "AwardActivity update");

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // Handle cancel operation
                    }
                }
        );
    }

    private ArrayList<AwardItem> awardItems = new ArrayList<>(); // Use AwardItem instead of ShopItem
    private AwardItemAdapter awardItemAdapter; // Use AwardItemAdapter instead of ShopItemAdapter
    private ScoreItem scoreItem;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != 3)
        {
            return false;
        }
        Log.d("Serialization", "onContextItemSelected in " + this.getClass().getSimpleName());
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("完成奖励");
                builder1.setMessage("你确定完成该奖励吗?");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AwardItem awardItem= awardItems.get(item.getOrder());

                        int currentScore =new DataBank().loadScore(AwardActivity.this);
                        int newScore = currentScore - (int)awardItem.getPrice();
                        new DataBank().saveScore(AwardActivity.this, newScore);
                        updateScoreInView(newScore);

                        awardItems.remove(item.getOrder());
                        awardItemAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveAwardItems(AwardActivity.this, awardItems);
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

                Intent intent = new Intent(this, AwardDetailsActivity.class);
                addAwardLauncher.launch(intent);
                break;

            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("删除奖励");
                builder.setMessage("你确定要删除该奖励吗?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        awardItems.remove(item.getOrder());
                        awardItemAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveAwardItems(AwardActivity.this, awardItems);
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
                Intent intentUpdate = new Intent(this, ShopItemDetailsActivity.class);
                AwardItem awardItem= awardItems.get(item.getOrder());
                intentUpdate.putExtra("name",awardItem.getName());
                intentUpdate.putExtra("price",awardItem.getPrice());
                intentUpdate.putExtra("position",item.getOrder());
                updateAwardLauncher.launch(intentUpdate);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public class AwardItemAdapter extends RecyclerView.Adapter<AwardItemAdapter.ViewHolder> {
        private ArrayList<AwardItem> awardItemArrayList;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView textViewName;
            private final TextView textViewPrice;
            private final ImageView imageViewItem;

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("具体操作");
                menu.add(3, 0, this.getAdapterPosition(), "完成奖励" );
                menu.add(3, 1, this.getAdapterPosition(), "添加奖励" );
                menu.add(3, 2, this.getAdapterPosition(), "删除奖励" );
                menu.add(3, 3, this.getAdapterPosition(), "修改奖励" );
            }

            public ViewHolder(View awardItemView) {
                super(awardItemView);
                // Define click listener for the ViewHolder's View

                textViewName = awardItemView.findViewById(R.id.textview_item_name);
                textViewPrice = awardItemView.findViewById(R.id.textview_item_price);
                imageViewItem = awardItemView.findViewById(R.id.imageview_item);
                awardItemView.setOnCreateContextMenuListener(this);
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
        public AwardItemAdapter(ArrayList<AwardItem> awardItems) {
            awardItemArrayList = awardItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AwardActivity.AwardItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.shop_item_row, viewGroup, false);
            //view.setBackgroundColor(Color.rgb(204,204,204));
            return new  AwardActivity.AwardItemAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AwardActivity.AwardItemAdapter.ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewName().setText(awardItemArrayList.get(position).getName());
            viewHolder.getTextViewName().setTextColor(Color.BLACK);
            viewHolder.getTextViewPrice().setText("- "+(int)awardItemArrayList.get(position).getPrice());
            viewHolder.getTextViewPrice().setTextColor(Color.rgb(180,0,51));
            viewHolder.getImageViewItem().setImageResource(awardItemArrayList.get(position).getImageResourceId());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return awardItemArrayList.size();
        }
    }

    private void updateScoreInView(int newScore) {
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setText("分数: " + newScore);

    }
}
