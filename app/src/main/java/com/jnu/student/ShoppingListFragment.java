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
import com.jnu.student.data.ShopItem;

import java.util.ArrayList;
import java.util.Date;


public class ShoppingListFragment extends Fragment {

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
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
    private ActivityResultLauncher<Intent> addItemLauncher;
    private ActivityResultLauncher<Intent> updateItemLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        RecyclerView mainRecyclerView = rootView.findViewById(R.id.recyclerview_main);


        registerForContextMenu(mainRecyclerView);
        Log.d("Serialization", "registerShopping");
        shopItems = new DataBank().LoadShopItems(requireActivity());
        if(0 == shopItems.size()) {
            shopItems.add(new ShopItem("添加任务", 0.0, R.drawable.taskphoto));
        }

        shopItemAdapter = new ShopItemAdapter(shopItems);
        mainRecyclerView.setAdapter(shopItemAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("name"); // 获取返回的数据
                        String priceText = data.getStringExtra("price"); // 获取返回的数据

                        double price= Double.parseDouble(priceText);
                        shopItems.add(new ShopItem(name, price, R.drawable.taskphoto));
                        shopItemAdapter.notifyItemInserted(shopItems.size());


                        new DataBank().SaveShopItems(requireActivity(), shopItems);
                        Log.d("Serialization", "ShoppinglistFragment add");

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // 处理取消操作
                    }
                }
        );
        updateItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        int position = data.getIntExtra("position",0);
                        String name = data.getStringExtra("name"); // 获取返回的数据
                        String priceText = data.getStringExtra("price"); // 获取返回的数据

                        double price= Double.parseDouble(priceText);
                        ShopItem shopItem = shopItems.get(position);
                        shopItem.setPrice(price);
                        shopItem.setName(name);
                        shopItemAdapter.notifyItemChanged(position);

                        new DataBank().SaveShopItems(requireActivity(), shopItems);
                        Log.d("Serialization", "ShoppinglistFragment update");

                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // 处理取消操作
                    }
                }
        );
        return rootView;
    }

    private ArrayList<ShopItem> shopItems = new ArrayList<>();
    private ShopItemAdapter shopItemAdapter;
    private ScoreItem scoreItem;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != 0)
        {
            Log.d("Serialization", "shoppingFalse");
            return false;
        }
        Log.d("Serialization", "onContextItemSelected in " + this.getClass().getSimpleName());
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(requireActivity());
                builder1.setTitle("完成任务");
                builder1.setMessage("你确定完成该任务吗?");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShopItem shopItem= shopItems.get(item.getOrder());

                        int currentScore =new DataBank().loadScore(requireActivity());
                        int newScore = currentScore + (int)shopItem.getPrice();
                        new DataBank().saveScore(requireActivity(), newScore);
                        updateScoreInView(newScore);

                        CompletedTask completedTask = new CompletedTask(shopItem.getName(), shopItem.getPrice(), shopItem.getImageResourceId(), new Date());
                        new DataBank().saveCompletedTask(requireActivity(), completedTask);

                        shopItems.remove(item.getOrder());
                        shopItemAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveShopItems(requireActivity(), shopItems);
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

                Intent intent = new Intent(requireActivity(), ShopItemDetailsActivity.class);
                addItemLauncher.launch(intent);
                break;

            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("删除任务");
                builder.setMessage("你确定要删除该任务吗?");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopItems.remove(item.getOrder());
                        shopItemAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveShopItems(requireActivity(), shopItems);
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
                Intent intentUpdate = new Intent(requireActivity(), ShopItemDetailsActivity.class);
                ShopItem shopItem= shopItems.get(item.getOrder());
                intentUpdate.putExtra("name",shopItem.getName());
                intentUpdate.putExtra("price",shopItem.getPrice());
                intentUpdate.putExtra("position",item.getOrder());
                updateItemLauncher.launch(intentUpdate);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ViewHolder> {

        private ArrayList<ShopItem> shopItemArrayList;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final TextView textViewName;
            private final TextView textViewPrice;
            private final ImageView imageViewItem;

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("具体操作");
                menu.add(0, 0, this.getAdapterPosition(), "完成任务" );
                menu.add(0, 1, this.getAdapterPosition(), "添加任务" );
                menu.add(0, 2, this.getAdapterPosition(), "删除任务" );
                menu.add(0, 3, this.getAdapterPosition(), "修改任务" );
            }

            public ViewHolder(View shopItemView) {
                super(shopItemView);
                // Define click listener for the ViewHolder's View

                textViewName = shopItemView.findViewById(R.id.textview_item_name);
                textViewPrice = shopItemView.findViewById(R.id.textview_item_price);
                imageViewItem = shopItemView.findViewById(R.id.imageview_item);
                shopItemView.setOnCreateContextMenuListener(this);
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

        public ShopItemAdapter(ArrayList<ShopItem> shopItems) {
            shopItemArrayList = shopItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.shop_item_row, viewGroup, false);
            //view.setBackgroundColor(Color.rgb(204,204,204));
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewName().setText(shopItemArrayList.get(position).getName());
            viewHolder.getTextViewName().setTextColor(Color.BLACK);
            viewHolder.getTextViewPrice().setText("+"+(int)shopItemArrayList.get(position).getPrice());
            viewHolder.getTextViewPrice().setTextColor(Color.rgb(0,180,51));
            viewHolder.getImageViewItem().setImageResource(shopItemArrayList.get(position).getImageResourceId());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return shopItemArrayList.size();
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