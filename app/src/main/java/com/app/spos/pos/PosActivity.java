package com.app.spos.pos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spos.Constant;
import com.app.spos.R;
import com.app.spos.adapter.PosProductAdapter;
import com.app.spos.adapter.ProductCategoryAdapter;
import com.app.spos.database.DatabaseAccess;
import com.app.spos.model.Category;
import com.app.spos.model.Product;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosActivity extends BaseActivity implements CartCount{


    private RecyclerView recyclerView, categoryRecyclerView;
    PosProductAdapter productAdapter;
    TextView txtNoProducts,txtReset;
    ProductCategoryAdapter categoryAdapter;

    ImageView imgNoProduct,imgScanner;
    public static EditText etxtSearch;

    private ShimmerFrameLayout mShimmerViewContainer;
    private  static Menu menu;


     DatabaseAccess databaseAccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);





        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_product);

        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner=findViewById(R.id.img_scanner);
        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        txtReset=findViewById(R.id.txt_reset);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");



        imgScanner.setOnClickListener(v -> {
            Intent intent=new Intent(PosActivity.this,ScannerActivity.class);
            startActivity(intent);
        });

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getProductCategory(shopID,ownerId);

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        //Load data from server
        getProductsData("",shopID,ownerId);

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductsData("",shopID,ownerId);
            }
        });


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(PosActivity.this,LinearLayoutManager.HORIZONTAL,false);
        categoryRecyclerView.setLayoutManager(linerLayoutManager); // set LayoutManager to RecyclerView
        categoryRecyclerView.setHasFixedSize(true);
        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data",s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 1) {

                    //search data from server
                    getProductsData(s.toString(),shopID,ownerId);
                } else {
                    getProductsData("",shopID,ownerId);
                }



            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data",s.toString());
            }


        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        this.menu = menu;
        showAddedItemCount();
        return true;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



            case R.id.menu_cart_button:

                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
                return true;


            case android.R.id.home:

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void getProductCategory(String shopId,String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory(shopId,ownerId);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {


                if (response.isSuccessful() && response.body() != null) {



                    List<Category> productCategory;
                    productCategory = response.body();

                    if (productCategory.isEmpty())
                    {
                        Toasty.info(PosActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                        imgNoProduct.setImageResource(R.drawable.no_data);


                    }

                    else {

                        categoryAdapter = new ProductCategoryAdapter(PosActivity.this,productCategory, recyclerView,imgNoProduct,txtNoProducts,mShimmerViewContainer);

                        categoryRecyclerView.setAdapter(categoryAdapter);

                    }



                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }




    public void getProductsData(String searchText,String shopId,String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Product>> call;
        call = apiInterface.getProducts(searchText,shopId,ownerId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productsList;
                    productsList = response.body();


                    if (productsList.isEmpty()) {

                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);
                        imgNoProduct.setImageResource(R.drawable.not_found);
                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);


                    } else {


                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        productAdapter = new PosProductAdapter(getApplicationContext(), productsList,PosActivity.this);

                        recyclerView.setAdapter(productAdapter);

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {

                Toast.makeText(PosActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }




    @Override
    public void count() {
        showAddedItemCount();


    }

    private void showAddedItemCount() {
        databaseAccess = DatabaseAccess.getInstance(PosActivity.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();
        View view;
        MenuItem menuItem = menu.findItem(R.id.menu_cart_button);
        MenuItemCompat.setActionView(menuItem, R.layout.custom_menu_layout);
        view = MenuItemCompat.getActionView(menuItem);
        TextView textView = (TextView) view.findViewById(R.id.tv_count);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewEdit);


        textView.setText(""+itemCount);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
            }
        });
    }


}
