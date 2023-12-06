package com.jnu.student;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.jnu.student.data.DataBank;
import com.jnu.student.data.ShopItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShoppingListFragment.
     */
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shopping_list, container, false);


        RecyclerView mainRecyclerView = rootView.findViewById(R.id.recyclerview_main);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        shopItems = new DataBank().LoadShopItems(requireActivity());
        if(0 == shopItems.size()) {
            shopItems.add(new ShopItem("name", 1.5, R.drawable.book_no_name));
        }


        shopItemAdapter = new ShopItemAdapter(shopItems);
        mainRecyclerView.setAdapter(shopItemAdapter);

        registerForContextMenu(mainRecyclerView);

        addItemLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String name = data.getStringExtra("name"); // 获取返回的数据
                        String priceText = data.getStringExtra("price"); // 获取返回的数据

                        double price= Double.parseDouble(priceText);
                        shopItems.add(new ShopItem(name, price, R.drawable.book_1));
                        shopItemAdapter.notifyItemInserted(shopItems.size());

                        new DataBank().SaveShopItems(requireActivity(), shopItems);


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


                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // 处理取消操作
                    }
                }
        );
        return rootView;
    }
    private ArrayList<ShopItem> shopItems = new ArrayList<>();
    private ShopItemAdapter shopItemAdapter;


    ActivityResultLauncher<Intent> addItemLauncher;
    ActivityResultLauncher<Intent> updateItemLauncher;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:

                Intent intent = new Intent(requireActivity(), ShopItemDetailsActivity.class);
                addItemLauncher.launch(intent);
                break;
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Delete Data");
                builder.setMessage("Are you sure you want to delete this data?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shopItems.remove(item.getOrder());
                        shopItemAdapter.notifyItemRemoved(item.getOrder());

                        new DataBank().SaveShopItems(requireActivity(), shopItems);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();
                break;
            case 2:
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

                menu.add(0, 0, this.getAdapterPosition(), "添加任务" );
                menu.add(0, 1, this.getAdapterPosition(), "删除任务" );
                menu.add(0, 2, this.getAdapterPosition(), "修改任务" );
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

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            viewHolder.getTextViewName().setText(shopItemArrayList.get(position).getName());
            viewHolder.getTextViewPrice().setText(shopItemArrayList.get(position).getPrice() + "");
            viewHolder.getImageViewItem().setImageResource(shopItemArrayList.get(position).getImageResourceId());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return shopItemArrayList.size();
        }
    }
}