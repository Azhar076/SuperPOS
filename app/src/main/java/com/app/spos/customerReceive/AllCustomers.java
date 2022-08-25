package com.app.spos.customerReceive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.spos.Constant;
import com.app.spos.HomeActivity;
import com.app.spos.R;
import com.app.spos.adapter.AllCustomerReceiveAdapter;
import com.app.spos.adapter.CustomerAdapter;
import com.app.spos.customers.AddCustomersActivity;
import com.app.spos.customers.CustomersActivity;
import com.app.spos.model.Customer;
import com.app.spos.model.getAllCustomersReceiveModel;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.utils.BaseActivity;
import com.app.spos.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCustomers extends BaseActivity {
    SharedPreferences sp;
    ProgressDialog loading;
    private RecyclerView recyclerView;
    ImageView imgNoProduct;
    String logo,shopName,shop_vat_no,shopEmail,shopContact,shopAddress;
    FloatingActionButton fabAdd;
    private ShimmerFrameLayout mShimmerViewContainer;
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_customers);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.customers_receive);


        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mSwipeRefreshLayout =findViewById(R.id.swipeToRefresh);
        //set color of swipe refresh
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
         imgNoProduct = findViewById(R.id.image_no_product);
        recyclerView = findViewById(R.id.recycler_view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        fabAdd = findViewById(R.id.fab_add);


        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        String staffID = sp.getString(Constant.SP_USER_ID, "");
        logo = sp.getString(Constant.SP_LOGO, "N/A");


        shopName = sp.getString(Constant.SP_SHOP_NAME, "N/A");
        shop_vat_no = sp.getString(Constant.SP_VAT_NO, "N/A");
        logo = sp.getString(Constant.SP_LOGO, "N/A");
        shopEmail = sp.getString(Constant.SP_SHOP_EMAIL, "N/A");
        shopContact = sp.getString(Constant.SP_SHOP_CONTACT, "N/A");
        shopAddress = sp.getString(Constant.SP_SHOP_ADDRESS, "N/A");
        Utils utils=new Utils();


        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            if (utils.isNetworkAvailable(AllCustomers.this))
            {
                getAllCustomers(shopID,staffID);
            }
            else
            {
                recyclerView.setVisibility(View.GONE);
                imgNoProduct.setVisibility(View.VISIBLE);
                imgNoProduct.setImageResource(R.drawable.not_found);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                //Stopping Shimmer Effects
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }


            //after shuffle id done then swife refresh is off
            mSwipeRefreshLayout.setRefreshing(false);
        });
        if (utils.isNetworkAvailable(AllCustomers.this))
        {
            getAllCustomers(shopID,staffID);
        }
        else
        {
            Toasty.error(AllCustomers.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllCustomers.this, AddCustomerReceiveActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getAllCustomers(String shopID,String staffID) {
        loading=new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<getAllCustomersReceiveModel>> call = apiInterface.getAllCustomers(shopID,staffID);
        call.enqueue(new Callback<List<getAllCustomersReceiveModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<getAllCustomersReceiveModel>> call, @NonNull Response<List<getAllCustomersReceiveModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loading.dismiss();
                    List<getAllCustomersReceiveModel> customerList;
                    customerList = response.body();
                    if (customerList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                    } else {
                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        String imageUrl = "https://superpos.ishtrii.com/shops_images/"+logo;
                        AllCustomerReceiveAdapter customerAdapter = new AllCustomerReceiveAdapter(AllCustomers.this, customerList,imageUrl,shopName,shop_vat_no,shopEmail,shopContact,shopAddress);
                        recyclerView.setAdapter(customerAdapter);

                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<List<getAllCustomersReceiveModel>> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(AllCustomers.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}