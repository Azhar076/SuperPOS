package com.app.spos.customers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.spos.Constant;
import com.app.spos.R;
import com.app.spos.model.Customer;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomersActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etxtCustomerName, etxtAddress, etxtCustomerCell, etxtCustomerEmail,etxt_customer_vatNo;
    TextView txtAddCustomer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_customer);


        etxtCustomerName = findViewById(R.id.etxt_customer_name);
        etxtCustomerCell = findViewById(R.id.etxt_customer_cell);
        etxtCustomerEmail = findViewById(R.id.etxt_email);
        etxtAddress = findViewById(R.id.etxt_address);
        etxt_customer_vatNo = findViewById(R.id.etxt_customer_vatNo);

        txtAddCustomer = findViewById(R.id.txt_add_customer);

        SharedPreferences sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");



        txtAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customerName = etxtCustomerName.getText().toString().trim();
                String customerCell = etxtCustomerCell.getText().toString().trim();
                String customerEmail = etxtCustomerEmail.getText().toString().trim();
                String customerAddress = etxtAddress.getText().toString().trim();
                String customerVatNo = etxt_customer_vatNo.getText().toString().trim();


                if (customerName.isEmpty()) {
                    etxtCustomerName.setError(getString(R.string.enter_customer_name));
                    etxtCustomerName.requestFocus();
                } else if (customerCell.isEmpty()) {
                    etxtCustomerCell.setError(getString(R.string.enter_customer_cell));
                    etxtCustomerCell.requestFocus();
                } else if (customerEmail.isEmpty() || !customerEmail.contains("@") || !customerEmail.contains(".")) {
                    etxtCustomerEmail.setError(getString(R.string.enter_valid_email));
                    etxtCustomerEmail.requestFocus();
                } else if (customerAddress.isEmpty()) {
                    etxtAddress.setError(getString(R.string.enter_customer_address));
                    etxtAddress.requestFocus();
                }else if(customerVatNo.isEmpty()){
                    etxt_customer_vatNo.setError(getString(R.string.customer_vatNo));
                    etxt_customer_vatNo.requestFocus();

                } else {

                     addCustomer(customerName, customerCell, customerEmail, customerAddress,customerVatNo,shopID,ownerId);


                }


            }
        });

    }





    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void addCustomer(String name,String cell,String email, String address,String customerVatNo,String shopId,String ownerId) {


        loading=new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
       ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Customer> call = apiInterface.addCustomers(name,cell,email,customerVatNo,address,shopId,ownerId);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {


                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {

                        loading.dismiss();

                        Toasty.success(AddCustomersActivity.this, R.string.customer_successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCustomersActivity.this, CustomersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                   else if (value.equals(Constant.KEY_FAILURE)) {

                        loading.dismiss();

                        Toasty.error(AddCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        finish();

                    }

                    else {
                        loading.dismiss();
                        Toasty.error(AddCustomersActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    loading.dismiss();
                    Log.d("Error","Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(AddCustomersActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
