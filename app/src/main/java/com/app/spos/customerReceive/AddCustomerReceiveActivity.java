package com.app.spos.customerReceive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.spos.Constant;
import com.app.spos.HomeActivity;
import com.app.spos.R;
import com.app.spos.adapter.CustomerAdapter;
import com.app.spos.customers.AddCustomersActivity;
import com.app.spos.customers.CustomersActivity;
import com.app.spos.model.AddCustomerReceive;
import com.app.spos.model.Customer;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.product.AddProductActivity;
import com.app.spos.utils.BaseActivity;
import com.app.spos.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomerReceiveActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    EditText etxt_date,etxt_amount,etxt_note;
    Spinner spinner_payment,spinner_customer;
    DatePickerDialog picker;
    Button btn_save;

    String[] payment_type = new String[2];
    List<String> customer_names;
    List<String> customer_id;
    SharedPreferences sp;
    String id_selected = "",pay_type_selected = "";
    ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_receive);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.customers_receive);


        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
         payment_type = getResources().getStringArray(R.array.system);

        initView();


    }

    private void initView() {
        etxt_date = (EditText)findViewById(R.id.etxt_date);
        spinner_payment = (Spinner)findViewById(R.id.spinner_payment);
        spinner_customer = (Spinner)findViewById(R.id.spinner_customer);
        etxt_amount = (EditText)findViewById(R.id.etxt_amount);
        etxt_note = (EditText)findViewById(R.id.etxt_note);
        btn_save = (Button)findViewById(R.id.btn_save);

        String shopID = sp.getString(Constant.SP_SHOP_ID, "");
        String ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        String staffID = sp.getString(Constant.SP_USER_ID, "");

        Utils utils=new Utils();

        if (utils.isNetworkAvailable(AddCustomerReceiveActivity.this))
        {
            getCustomerData("",shopID,ownerId);
        }
        else
        {
            Toasty.error(AddCustomerReceiveActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }


        spinner_payment.setOnItemSelectedListener(this);
        spinner_customer.setOnItemSelectedListener(this);


        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,payment_type);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_payment.setAdapter(aa);



        etxt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddCustomerReceiveActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                etxt_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

btn_save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String note = etxt_note.getText().toString();
        String amount = etxt_amount.getText().toString();
        String date = etxt_date.getText().toString();
        if(pay_type_selected.equals("Cash")){
            pay_type_selected = "cash";
        }else if(pay_type_selected.equals("Bank")){
            pay_type_selected = "bank";
        }
        if(id_selected.equals("")){
            Toasty.error(getApplicationContext(),"Customer ID is missing",Toasty.LENGTH_SHORT).show();
            return;
        }
        if(pay_type_selected.equals("")){
            Toasty.error(getApplicationContext(),"Pay Type is missing",Toasty.LENGTH_SHORT).show();
            return;
        }
        if(shopID.equals("")){
            Toasty.error(getApplicationContext(),"Shop Id  is missing",Toasty.LENGTH_SHORT).show();
            return;
        }
        if(ownerId.equals("")){
            Toasty.error(getApplicationContext(),"Owner Id  is missing",Toasty.LENGTH_SHORT).show();
            return;
        }
        if(staffID.equals("")){
            Toasty.error(getApplicationContext(),"Staff Id  is missing",Toasty.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(date)){
            etxt_date.setError("Date is missing");
            return;
        }
        if(TextUtils.isEmpty(note)){
            etxt_note.setError("Notes are missing");
            return;
        }
        if(TextUtils.isEmpty(amount)){
            etxt_amount.setError("Amount is missing");
            return;
        }
        addCustomerReceive(id_selected,date,note,amount,pay_type_selected,shopID,ownerId,staffID);


    }
});



    }



    public void getCustomerData(String searchText,String shopId,String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Customer>> call;
        call = apiInterface.getCustomers(searchText,shopId,ownerId);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<Customer> customerList;
                    customerList = response.body();
                    customer_names = new ArrayList<>();
                    customer_id = new ArrayList<>();
                    for(int i = 0;i<customerList.size();i++){
                        customer_names.add(customerList.get(i).getCustomerName());
                        customer_id.add(customerList.get(i).getCustomerId());
                    }


                    ArrayAdapter aa_customer = new ArrayAdapter(AddCustomerReceiveActivity.this,android.R.layout.simple_spinner_item,customer_names);
                    aa_customer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_customer.setAdapter(aa_customer);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {

                Toast.makeText(AddCustomerReceiveActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }

    private void addCustomerReceive(String id_selected, String date, String note, String amount, String pay_type_selected, String shopID, String ownerId, String staffID) {


        loading=new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<JSONObject> call = apiInterface.addCustomersReceive(id_selected,date,note,amount,pay_type_selected,shopID,ownerId,staffID);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(@NonNull Call<JSONObject> call, @NonNull Response<JSONObject> response) {
                if(response.isSuccessful()){
                    loading.dismiss();
                    Toasty.success(getApplicationContext(), R.string.customer_receive_successfully_added, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }



            }

            @Override
            public void onFailure(@NonNull Call<JSONObject> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(AddCustomerReceiveActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        switch(adapterView.getId()) {
            case R.id.spinner_payment:
                pay_type_selected = payment_type[i];
                break;
            case R.id.spinner_customer:
                id_selected = customer_id.get(i);
                break;

        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }




}