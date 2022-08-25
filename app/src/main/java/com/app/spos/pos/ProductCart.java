package com.app.spos.pos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spos.Constant;
import com.app.spos.QRCode.DateUtil;
import com.app.spos.R;
import com.app.spos.adapter.CartAdapter;
import com.app.spos.adapter.OrderAdapter;
import com.app.spos.database.DatabaseAccess;
import com.app.spos.model.Customer;
import com.app.spos.model.OrderList;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.orders.OrderDetailsActivity;
import com.app.spos.orders.OrdersActivity;
import com.app.spos.pdf_report.BarCodeEncoder;
import com.app.spos.pdf_report.PDFTemplate;
import com.app.spos.utils.BaseActivity;
import com.app.spos.utils.Utils;
import com.bumptech.glide.Glide;
import com.example.zatca_qr_generation.ZatcaQRCodeGeneration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCart extends BaseActivity{


    CartAdapter productCartAdapter;
    ImageView imgNoProduct;
    Button btnSubmitOrder;
    TextView txtNoProduct, txtTotalPrice;
    LinearLayout linearLayout;
    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(ProductCart.this);


    List<String> customerNames,customerIDs, orderTypeNames, paymentMethodNames;
    List<Customer> customerData,customer_by_default = new ArrayList<>();
    ArrayAdapter<String> customerAdapter, orderTypeAdapter, paymentMethodAdapter;
    SharedPreferences sp;
    String servedBy,staffId,shopTax,currency,shopID,ownerId,staff_id;
    String user_type;
    DecimalFormat f;

    String taxType, discount1 ;
     double getTax;
    double netTotal = 0.0;
    double subTotal = 0.0;
    double totalCost,calculatedTotalCost_1;
    String selectedCustomerId;
    double paid = 0.0,due = 0.0;
   int invoice_id ;

   // for pdf
    String logo,shopName,shop_vat_no,shopAddress,shopEmail,shopContact,orderDate,orderTime;
    Bitmap bitmap = null;
    Bitmap bm = null;
    private PDFTemplate templatePDF;
    ImageView img_logo;
    String imageUrl ;

    String longText, shortText, userName;
    private String[] header = {"Description", "Price"};
    String currentDate,currentTime;
    String selectedItem_name;

    List<HashMap<String, String>> cartProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart);
        img_logo = findViewById(R.id.img_logo);
        templatePDF = new PDFTemplate(getApplicationContext());

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.product_cart);



        f = new DecimalFormat("#0.00");
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        servedBy = sp.getString(Constant.SP_STAFF_NAME, "");
        staffId = sp.getString(Constant.SP_STAFF_ID, "");
        shopTax= sp.getString(Constant.SP_TAX, "");
        currency= sp.getString(Constant.SP_CURRENCY_SYMBOL, "");

        shopID = sp.getString(Constant.SP_SHOP_ID, "");
        ownerId = sp.getString(Constant.SP_OWNER_ID, "");
        taxType = sp.getString(Constant.TAX_TYPE, "");
        staff_id = sp.getString(Constant.SP_USER_ID, "");
        user_type = sp.getString(Constant.USER_TYPE, "");

        shopName = sp.getString(Constant.SP_SHOP_NAME, "N/A");
        shop_vat_no = sp.getString(Constant.SP_VAT_NO, "N/A");
        logo = sp.getString(Constant.SP_LOGO, "N/A");
        shopEmail = sp.getString(Constant.SP_SHOP_EMAIL, "N/A");
        shopContact = sp.getString(Constant.SP_SHOP_CONTACT, "N/A");
        shopAddress = sp.getString(Constant.SP_SHOP_ADDRESS, "N/A");
        userName = sp.getString(Constant.SP_STAFF_NAME, "N/A");
        getInvoiceID();
        BarCodeEncoder qrCodeEncoder = new BarCodeEncoder();
        try {
            bm = qrCodeEncoder.encodeAsBitmap(""+invoice_id, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            Log.d("Data", e.toString());
        }



        RecyclerView recyclerView = findViewById(R.id.cart_recyclerview);
        imgNoProduct = findViewById(R.id.image_no_product);
        btnSubmitOrder = findViewById(R.id.btn_submit_order);
        txtNoProduct = findViewById(R.id.txt_no_product);
        linearLayout = findViewById(R.id.linear_layout);
        txtTotalPrice = findViewById(R.id.txt_total_price);

         imageUrl = "https://superpos.ishtrii.com/shops_images/"+logo;



        txtNoProduct.setVisibility(View.GONE);
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.image_placeholder)
                .into(img_logo);

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);
        getCustomerDetail_ByDefault(shopID);

        getCustomers(shopID,ownerId);

        databaseAccess.open();


        //get data from local database

        cartProductList = databaseAccess.getCartProduct();

        Log.d("CartSize", "" + cartProductList.size());

        if (cartProductList.isEmpty()) {

            imgNoProduct.setImageResource(R.drawable.empty_cart);
            imgNoProduct.setVisibility(View.VISIBLE);
            txtNoProduct.setVisibility(View.VISIBLE);
            btnSubmitOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            txtTotalPrice.setVisibility(View.GONE);
        } else {


            imgNoProduct.setVisibility(View.GONE);
            productCartAdapter = new CartAdapter(ProductCart.this, cartProductList, txtTotalPrice, btnSubmitOrder, imgNoProduct, txtNoProduct);

            recyclerView.setAdapter(productCartAdapter);


        }
        img_logo.setVisibility(View.INVISIBLE);

        btnSubmitOrder.setOnClickListener(v -> dialog());

    }
    private Bitmap getQRCode(String base64String) {
        try {
            BarcodeEncoder barcodeEncoder =  new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(base64String, BarcodeFormat.QR_CODE, 300, 300);
        } catch (Exception e) {
        }
        return null;
    }
    private void getCustomerDetail_ByDefault(String shopID) {

            ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

            Call<List<Customer>> call;


            call = apiInterface.getCustomerDefault(shopID);

            call.enqueue(new Callback<List<Customer>>() {
                @Override
                public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {


                    if (response.isSuccessful() && response.body() != null) {
                        customer_by_default = response.body();

                    }

                }

                @Override
                public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {
                   Log.d("",""+call.toString());
                    //write own action
                }
            });




        }




    public void proceedOrder(String type, String paymentMethod, String customerName, double tax, String discount, double price,String taff_id,String selectedCustomerId,double due ,double paid) {

        databaseAccess = DatabaseAccess.getInstance(ProductCart.this);
        databaseAccess.open();

        int itemCount = databaseAccess.getCartItemCount();

        databaseAccess.open();
        double orderPrice = databaseAccess.getTotalPrice();


        if (itemCount > 0) {

            databaseAccess.open();
            //get data from local database
            final List<HashMap<String, String>> lines;
            lines = databaseAccess.getCartProduct();

            if (lines.isEmpty()) {
                Toasty.error(ProductCart.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();
            } else {

                //get current timestamp
                 currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
                String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
                //H denote 24 hours and h denote 12 hour hour format
                 currentTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date()); //HH:mm:ss a

                //timestamp use for invoice id for unique
                Long tsLong = System.currentTimeMillis() / 1000;
                String timeStamp = tsLong.toString();
                Log.d("Time", timeStamp);
                //Invoice number=INV+StaffID+CurrentYear+timestamp
               // String invoiceNumber="INV"+staffId+currentYear+timeStamp;
                String invoiceNumber= String.valueOf(invoice_id);

                final JSONObject obj = new JSONObject();
                try {


                    obj.put("invoice_id", invoiceNumber);
                    obj.put("order_date", currentDate);
                    obj.put("order_time", currentTime);
                    obj.put("order_type", type);
                    obj.put("order_payment_method", paymentMethod);
                    obj.put("customer_name", customerName);
                    obj.put("customer_id", selectedCustomerId);
                    obj.put("paid_amount", paid);
                    obj.put("due_amount", due);

                    obj.put("order_price", String.valueOf(netTotal));
                    obj.put("tax", String.valueOf(getTax));
                    obj.put("discount", discount);
                    obj.put("served_by", servedBy);
                    obj.put("shop_id", shopID);
                    obj.put("owner_id", ownerId);
                    obj.put("staff_id", staff_id);

                    JSONArray array = new JSONArray();


                    for (int i = 0; i < lines.size(); i++) {

                        databaseAccess.open();
                        String invoiceId = invoiceNumber;
                        String productId = lines.get(i).get("product_id");
                        String productName = lines.get(i).get("product_name");
                        String productImage = lines.get(i).get("product_image");

                        String productWeightUnit = lines.get(i).get("product_weight_unit");

                        JSONObject objp = new JSONObject();
                        objp.put("invoice_id", invoiceId);
                        objp.put("product_id", productId);
                        objp.put("product_name", productName);
                        objp.put("product_image", productImage);
                        objp.put("product_weight", lines.get(i).get("product_weight") + " " + productWeightUnit);
                        objp.put("product_qty", lines.get(i).get("product_qty"));
                        objp.put("product_price", lines.get(i).get("product_price"));
                        objp.put("product_order_date", currentDate);
                        double tax_calculated ;
                        if(taxType.equals("exclusive")){
                           String  quantity  = lines.get(i).get("product_qty");
                           int qty_value = Integer.parseInt(quantity);

                           String  product_Price  = lines.get(i).get("product_price");
                           Double price_double = Double.valueOf(product_Price);

                            tax_calculated = qty_value*price_double*0.15;

                        }else{

                            String  quantity  = lines.get(i).get("product_qty");
                            int qty_value = Integer.parseInt(quantity);

                            String  product_Price  = lines.get(i).get("product_price");
                            Double price_double = Double.valueOf(product_Price);

                         double   total_price  = qty_value*price_double;
                            double without_tax_Rate = total_price/1.15;
                           tax_calculated = total_price-without_tax_Rate;


                        }
                        String final_tax = String.valueOf(tax_calculated);

                    if(final_tax.length() >3) {
                        objp.put("tax", final_tax.substring(0, 4));
                    }else{
                        objp.put("tax", final_tax);
                    }
                        array.put(objp);

                    }
                    obj.put("lines", array);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Utils utils=new Utils();

                if(utils.isNetworkAvailable(ProductCart.this))
                {
                   orderSubmit(obj);
                }
                else
                {
                    Toasty.error(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                }




            }

        } else {
            Toasty.error(ProductCart.this, R.string.no_product_in_cart, Toast.LENGTH_SHORT).show();
        }
    }





    private void orderSubmit(final JSONObject obj) {

        Log.d("Json",obj.toString());


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        RequestBody body2 = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), String.valueOf(obj));


        Call<String> call = apiInterface.submitOrders(body2);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful()) {

                    progressDialog.dismiss();
                    Toasty.success(ProductCart.this, R.string.order_successfully_done, Toast.LENGTH_SHORT).show();

                    databaseAccess.open();
                    databaseAccess.emptyCart();
                    dialogSuccess();

                } else {

                    Toasty.error(ProductCart.this, R.string.error, Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                    Log.d("error", response.toString());

                }


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Log.d("onFailure", t.toString());

            }
        });


    }




    public void dialogSuccess() {


        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        ImageButton dialogBtnCloseDialog = dialogView.findViewById(R.id.btn_close_dialog);
        Button dialogBtnViewAllOrders = dialogView.findViewById(R.id.btn_view_all_orders);
        Button btn_view_pdf = dialogView.findViewById(R.id.btn_view_pdf);

        AlertDialog alertDialogSuccess = dialog.create();

        dialogBtnCloseDialog.setOnClickListener(v -> {

            alertDialogSuccess.dismiss();

            Intent intent = new Intent(ProductCart.this,PosActivity.class);
            startActivity(intent);
            finish();

        });


        dialogBtnViewAllOrders.setOnClickListener(v -> {

            alertDialogSuccess.dismiss();

            Intent intent = new Intent(ProductCart.this, OrdersActivity.class);
            startActivity(intent);
            finish();

        });

        btn_view_pdf.setOnClickListener(v -> {

            alertDialogSuccess.dismiss();

            if(customer_by_default.size()>0) {
                selectedItem_name =    customer_by_default.get(0).getCustomerName();

            }
            shortText = "Customer Name: Mr/Mrs. " + selectedItem_name;
            longText = "< Have a nice day. Visit again >";

            BitmapDrawable drawable = (BitmapDrawable) img_logo.getDrawable();
            Bitmap bitmap_shop = drawable.getBitmap();

            ZatcaQRCodeGeneration.Builder builder = new ZatcaQRCodeGeneration.Builder();
            builder.sellerName(shopName)
                    .taxNumber(shop_vat_no)
                    .invoiceDate(DateUtil.getCurrentDate())
                    .totalAmount(""+netTotal)
                    .taxAmount(""+getTax);
            String base64String = builder.getBase64();
            bitmap = getQRCode(base64String);


            templatePDF.openDocument(false);
            templatePDF.addMetaData(Constant.ORDER_RECEIPT, Constant.ORDER_RECEIPT, "Smart POS");
            templatePDF.addImageLogo(bitmap_shop);
            templatePDF.addTitle(shopName, shopAddress+ "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nVat No:" + shop_vat_no+ "\nInvoice Id: " + ""+invoice_id, "Order Time:"+currentDate + " " + currentTime);
            templatePDF.addParagraph(shortText);

            templatePDF.createTable(header, getPDFReceipt());
            //  templatePDF.addImage(bm);
            templatePDF.addImage(bitmap);

            templatePDF.addRightParagraph(longText);

            templatePDF.closeDocument();
            templatePDF.viewPDF();
            this.finish();

            databaseAccess.open();
            databaseAccess.emptyCart();



        });

        alertDialogSuccess.show();


    }


    private ArrayList<String[]> getPDFReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();



        String name, price, qty, weight;
        double final_total_all_items=0;
        double costTotal;


        for (int i = 0; i <  cartProductList.size(); i++) {

            name = cartProductList.get(i).get("product_name");

            price = cartProductList.get(i).get("product_price");
            qty = cartProductList.get(i).get("product_qty");
            weight = cartProductList.get(i).get("product_weight");

            costTotal = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x" + currency + price + ")", ""+ costTotal});

//            if(tax_type.equals("inclusive")) {
//                final_total_all_items = final_total_all_items + costTotal - getTax;
//            }else{
//                final_total_all_items = final_total_all_items + costTotal;
//            }

        }
        double number1 = subTotal;
        subTotal = (int)(Math.round(number1 * 100))/100.0;
        //   rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Sub Total: ", String.valueOf(subTotal)});
        rows.add(new String[]{"Discount: ",   discount1});
        rows.add(new String[]{"Total Tax: ", String.valueOf(getTax)});

        //    rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Total Price: ", currency+String.valueOf(netTotal)});
        rows.add(new String[]{"Paid Amount: ",String.valueOf(paid)});
        rows.add(new String[]{"Due Amount: ", String.valueOf(due)});

//        you can add more row above format
        return rows;
    }



    //dialog for taking otp code
    public void dialog() {

        databaseAccess.open();
        double totalTax = databaseAccess.getTotalTax();


        String shopCurrency = currency;
       // String tax = shopTax;

         getTax = totalTax;



        AlertDialog.Builder dialog = new AlertDialog.Builder(ProductCart.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final Button dialogBtnSubmit = dialogView.findViewById(R.id.btn_submit);
        final ImageButton dialogBtnClose = dialogView.findViewById(R.id.btn_close);
        final TextView dialogOrderPaymentMethod = dialogView.findViewById(R.id.dialog_order_status);
        final TextView dialogOrderType = dialogView.findViewById(R.id.dialog_order_type);
        final TextView dialogCustomer = dialogView.findViewById(R.id.dialog_customer);
        final TextView dialogTxtsubTotal = dialogView.findViewById(R.id.dialog_txt_total);
        final TextView dialogTxtTotalTax = dialogView.findViewById(R.id.dialog_txt_total_tax);
        final TextView dialogTxtLevelTax = dialogView.findViewById(R.id.dialog_level_tax);
        final TextView dialogTxtTotalCost = dialogView.findViewById(R.id.dialog_txt_total_cost);
        final EditText dialogTxtPaid = dialogView.findViewById(R.id.etxt_dialog_paid);
        final TextView dialogTxtDue = dialogView.findViewById(R.id.dialog_txt_due);
        final TextView dialog_txt_remaining = dialogView.findViewById(R.id.dialog_txt_remaining);
        final EditText dialogEtxtDiscount = dialogView.findViewById(R.id.etxt_dialog_discount);


        final ImageButton dialogImgCustomer = dialogView.findViewById(R.id.img_select_customer);
        final ImageButton dialogImgOrderPaymentMethod = dialogView.findViewById(R.id.img_order_payment_method);
        final ImageButton dialogImgOrderType = dialogView.findViewById(R.id.img_order_type);


        totalCost = CartAdapter.totalPrice;

        double discount = 0;

        if(taxType.equals("inclusive")){
           double price_without_tax = totalCost/1.15;
            getTax = totalCost - price_without_tax;
            subTotal = price_without_tax;
        }else{
            subTotal = totalCost;
            getTax = totalCost*0.15;
        }
        netTotal = subTotal+getTax;


        dialogTxtsubTotal.setText(shopCurrency + f.format(subTotal));
        dialogTxtTotalTax.setText(shopCurrency + f.format(getTax));
        dialogTxtTotalCost.setText(shopCurrency + f.format(netTotal));
        dialogEtxtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {

                    double discount = 0;
                    String getDiscount = s.toString();
                    if (!getDiscount.isEmpty() && !getDiscount.equals(".")) {
                        discount = Double.parseDouble(getDiscount);

                          totalCost = totalCost-discount;

                        if(taxType.equals("inclusive")){
                            double price_without_tax = totalCost/1.15;
                            getTax = totalCost - price_without_tax;
                            subTotal = price_without_tax;
                        }else{
                            subTotal = totalCost;
                            getTax = totalCost*0.15;
                        }
                        netTotal = subTotal+getTax;

                        dialogTxtsubTotal.setText(shopCurrency + f.format(subTotal));
                        dialogTxtTotalTax.setText(shopCurrency + f.format(getTax));
                        dialogTxtTotalCost.setText(shopCurrency + f.format(netTotal));
                    }else{
                        totalCost = CartAdapter.totalPrice;
                        if(taxType.equals("inclusive")){
                            double price_without_tax = totalCost/1.15;
                            getTax = totalCost - price_without_tax;
                            subTotal = price_without_tax;
                        }else{
                            subTotal = totalCost;
                            getTax = totalCost*0.15;
                        }
                        netTotal = subTotal+getTax;


                        dialogTxtsubTotal.setText(shopCurrency + f.format(subTotal));
                        dialogTxtTotalTax.setText(shopCurrency + f.format(getTax));
                        dialogTxtTotalCost.setText(shopCurrency + f.format(netTotal));
                    }

                }catch(Exception e){
           Log.i("exceeption",""+e);
                }
                }


            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }
        });
        dialogTxtPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                 paid = 0;
                String getPaid = charSequence.toString();
                if (!getPaid.isEmpty() && !getPaid.equals(".")) {

                    paid = Double.parseDouble(getPaid);

                    if(paid > netTotal ){
                        double remaining = paid - netTotal;
                        dialog_txt_remaining.setText(shopCurrency + f.format(remaining));
                        dialogTxtDue.setText("0.0");
//                        dialogTxtPaid.setError(getString(R.string.paid_cant_be_greater_than_total_price));
//                        dialogEtxtDiscount.requestFocus();
//                        return;
                        paid = netTotal;
                        due = 0;
                    }else{
                        paid = Double.parseDouble(getPaid);
                        netTotal = subTotal+getTax;
                        due = netTotal - paid;

                        dialogTxtDue.setText(shopCurrency + f.format(due));
                        dialog_txt_remaining.setText("0.0");
                    }


                }else{
                    dialogTxtDue.setText("0.0");
                    dialog_txt_remaining.setText("0.0");
                }

                }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        orderTypeNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> orderType;
        orderType = databaseAccess.getOrderType();

        for (int i = 0; i < orderType.size(); i++) {

            // Get the ID of selected Country
            orderTypeNames.add(orderType.get(i).get("order_type_name"));

        }


        //payment methods
        paymentMethodNames = new ArrayList<>();
        databaseAccess.open();

        //get data from local database
        final List<HashMap<String, String>> paymentMethod;
        paymentMethod = databaseAccess.getPaymentMethod();

        for (int i = 0; i < paymentMethod.size(); i++) {

            // Get the ID of selected Country
            paymentMethodNames.add(paymentMethod.get(i).get("payment_method_name"));

        }


        dialogImgOrderPaymentMethod.setOnClickListener(v -> {

            paymentMethodAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            paymentMethodAdapter.addAll(paymentMethodNames);

            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ProductCart.this);
            View dialogView1 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog1.setView(dialogView1);
            dialog1.setCancelable(false);

            Button dialogButton = (Button) dialogView1.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView1.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView1.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView1.findViewById(R.id.dialog_list);


            dialogTitle.setText(R.string.select_payment_method);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(paymentMethodAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    paymentMethodAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog1.create();

            dialogButton.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                String selectedItem = paymentMethodAdapter.getItem(position);
                dialogOrderPaymentMethod.setText(selectedItem);


            });
        });


        dialogImgOrderType.setOnClickListener(v -> {


            orderTypeAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            orderTypeAdapter.addAll(orderTypeNames);

            AlertDialog.Builder dialog12 = new AlertDialog.Builder(ProductCart.this);
            View dialogView12 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog12.setView(dialogView12);
            dialog12.setCancelable(false);

            Button dialogButton = (Button) dialogView12.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView12.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView12.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView12.findViewById(R.id.dialog_list);


            dialogTitle.setText(R.string.select_order_type);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(orderTypeAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    orderTypeAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog12.create();

            dialogButton.setOnClickListener(v13 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                String selectedItem = orderTypeAdapter.getItem(position);


                dialogOrderType.setText(selectedItem);


            });
        });


        dialogImgCustomer.setOnClickListener(v -> {
            customerAdapter = new ArrayAdapter<>(ProductCart.this, android.R.layout.simple_list_item_1);
            customerAdapter.addAll(customerNames);

            AlertDialog.Builder dialog13 = new AlertDialog.Builder(ProductCart.this);
            View dialogView13 = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
            dialog13.setView(dialogView13);
            dialog13.setCancelable(false);

            Button dialogButton = (Button) dialogView13.findViewById(R.id.dialog_button);
            EditText dialogInput = (EditText) dialogView13.findViewById(R.id.dialog_input);
            TextView dialogTitle = (TextView) dialogView13.findViewById(R.id.dialog_title);
            ListView dialogList = (ListView) dialogView13.findViewById(R.id.dialog_list);

            dialogTitle.setText(R.string.select_customer);
            dialogList.setVerticalScrollBarEnabled(true);
            dialogList.setAdapter(customerAdapter);

            dialogInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    Log.d("data", s.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    customerAdapter.getFilter().filter(charSequence);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.d("data", s.toString());
                }
            });


            final AlertDialog alertDialog = dialog13.create();

            dialogButton.setOnClickListener(v12 -> alertDialog.dismiss());

            alertDialog.show();


            dialogList.setOnItemClickListener((parent, view, position, id) -> {

                alertDialog.dismiss();
                 selectedItem_name = customerAdapter.getItem(position);
                 selectedCustomerId = customerIDs.get(position);
                dialogCustomer.setText(selectedItem_name);


            });
        });


        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
         if(customer_by_default.size()>0) {
             dialogCustomer.setText(customer_by_default.get(0).getCustomerName());
             selectedCustomerId = customer_by_default.get(0).getCustomerId();
         }
        dialogBtnSubmit.setOnClickListener(v -> {

            String orderType1 = dialogOrderType.getText().toString().trim();
            String orderPaymentMethod = dialogOrderPaymentMethod.getText().toString().trim();

            String customerName = dialogCustomer.getText().toString().trim();
            if(customerName.equals("Choose Customer") || customerName.equals("حدد العميل")){
                Toasty.error(getApplicationContext(),getApplicationContext().getString(R.string.choose_customer),Toasty.LENGTH_SHORT).show();
                return;
            }
             discount1 = dialogEtxtDiscount.getText().toString().trim();
            if (discount1.isEmpty()) {
                discount1 = "0.00";
            }

            double number1 = getTax;
             getTax = (int)(Math.round(number1 * 100))/100.0;
          //  getTax =Double.parseDouble(new DecimalFormat("##.##").format(getTax));

            proceedOrder(orderType1, orderPaymentMethod, customerName, getTax, discount1, netTotal,staff_id,selectedCustomerId,due,paid);


            alertDialog.dismiss();
        });


        dialogBtnClose.setOnClickListener(v -> alertDialog.dismiss());


    }




    public void getCustomers(String shopId,String ownerId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Customer>> call;
        call = apiInterface.getCustomers("",shopId,ownerId);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(@NonNull Call<List<Customer>> call, @NonNull Response<List<Customer>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    customerData = response.body();

                    customerNames = new ArrayList<>();
                    customerIDs = new ArrayList<>();

                    for (int i = 0; i < customerData.size(); i++) {

                       customerNames.add(customerData.get(i).getCustomerName());
                       customerIDs.add(customerData.get(i).getCustomerId());

                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Customer>> call, @NonNull Throwable t) {

                //write own action
            }
        });




    }

    public void  getInvoiceID(){
       int id= Integer.parseInt(shopID);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

            Call<Integer> call;


            call = apiInterface.getInvoiceId(id);

            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {


                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("",""+response.body());
                          invoice_id = response.body();

                    }

                }

                @Override
                public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                    Log.d("",""+call);

                    //write own action
                }
            });




        }




    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            Intent intent = new Intent(this,PosActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this,PosActivity.class);
        startActivity(intent);

    }
}

