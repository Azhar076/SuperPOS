package com.app.spos.orders;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spos.Constant;
import com.app.spos.QRCode.DateUtil;
import com.app.spos.R;
import com.app.spos.adapter.OrderDetailsAdapter;
import com.app.spos.bt_device.DeviceListActivity;
import com.app.spos.database.DatabaseAccess;
import com.app.spos.model.OrderDetails;
import com.app.spos.networking.ApiClient;
import com.app.spos.networking.ApiInterface;
import com.app.spos.pdf_report.BarCodeEncoder;
import com.app.spos.pdf_report.PDFTemplate;
import com.app.spos.utils.BaseActivity;
import com.app.spos.utils.IPrintToPrinter;
import com.app.spos.utils.PrefMng;
import com.app.spos.utils.Tools;
import com.app.spos.utils.WoosimPrnMng;
import com.app.spos.utils.printerFactory;
import com.bumptech.glide.Glide;
import com.example.zatca_qr_generation.ZatcaQRCodeGeneration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends BaseActivity {


    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPriceWithTax, txtTax, txtDiscount, txtTotalCost,txt_total_paid,txt_total_due;
    String invoiceId,shopName,shop_vat_no,logo, orderDate,orderTime, orderPrice, customerName, tax, discount,shopAddress,shopEmail,shopContact,tax_type,paid_amount,due_amount,order_id;
    double  calculatedTotalPrice;

    Button btnPdfReceipt,btnThermalPrinter;
    List<OrderDetails> orderDetails;

    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"Description", "Price"};


    String longText, shortText, userName;

    private PDFTemplate templatePDF;
    SharedPreferences sp;
    String currency;
    Bitmap bm = null;
    Bitmap bitmap = null;
    ProgressDialog loading;
    RecyclerView recyclerView;
    DecimalFormat f;
    ImageView img_shop_logo;
    String taxType;

    private static final int REQUEST_CONNECT = 100;

    private WoosimPrnMng mPrnMng = null;
    double getOrderPriceWithTax;
    double getTax,getDiscount,getPaidAmount,getDueAmount;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        img_shop_logo = findViewById(R.id.img_shop_logo);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtTotalPriceWithTax = findViewById(R.id.txt_total_price_with_tax);
        txtTax = findViewById(R.id.txt_tax);
        txtDiscount = findViewById(R.id.txt_discount);
        txtTotalCost = findViewById(R.id.txt_total_cost);
        txt_total_paid = findViewById(R.id.txt_total_paid);
        txt_total_due = findViewById(R.id.txt_total_due);
        btnPdfReceipt = findViewById(R.id.btn_pdf_receipt);
        txtNoProducts = findViewById(R.id.txt_no_products);
        btnThermalPrinter = findViewById(R.id.btn_thermal_printer);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        shopName = sp.getString(Constant.SP_SHOP_NAME, "N/A");
        shop_vat_no = sp.getString(Constant.SP_VAT_NO, "N/A");
        logo = sp.getString(Constant.SP_LOGO, "N/A");
        shopEmail = sp.getString(Constant.SP_SHOP_EMAIL, "N/A");
        shopContact = sp.getString(Constant.SP_SHOP_CONTACT, "N/A");
        shopAddress = sp.getString(Constant.SP_SHOP_ADDRESS, "N/A");
        userName = sp.getString(Constant.SP_STAFF_NAME, "N/A");
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "N/A");
        tax_type = sp.getString(Constant.TAX_TYPE, "N/A");

        f = new DecimalFormat("#0.00");


        orderPrice = getIntent().getExtras().getString(Constant.ORDER_PRICE);
        tax = getIntent().getExtras().getString(Constant.TAX);
        orderDate = getIntent().getExtras().getString(Constant.ORDER_DATE);
        orderTime = getIntent().getExtras().getString(Constant.ORDER_TIME);
        discount = getIntent().getExtras().getString(Constant.DISCOUNT);
        invoiceId = getIntent().getExtras().getString(Constant.INVOICE_ID);
        customerName = getIntent().getExtras().getString(Constant.CUSTOMER_NAME);
        paid_amount = getIntent().getExtras().getString(Constant.PAID_AMOUNT);
        due_amount = getIntent().getExtras().getString(Constant.DUE_AMOUNT);
        order_id = getIntent().getExtras().getString(Constant.ORDER_ID);

        getProductsData(order_id);


        calculatedTotalPrice=Double.parseDouble(orderPrice)+Double.parseDouble(tax)-Double.parseDouble(discount);

       // double totalPrice=Double.parseDouble(orderPrice)+Double.parseDouble(tax)-Double.parseDouble(discount);
         totalPrice=Double.parseDouble(orderPrice);




        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.order_details);
        String imageUrl = "https://superpos.ishtrii.com/shops_images/"+logo;
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.image_placeholder)
                .into(img_shop_logo);

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        tax = tax.substring(0,4);
         getTax=Double.parseDouble(tax);
         getDiscount=Double.parseDouble(discount);
        getOrderPriceWithTax =Double.parseDouble(orderPrice);
        getPaidAmount =Double.parseDouble(paid_amount);
        getDueAmount =Double.parseDouble(due_amount);





        txtTax.setText(getString(R.string.total_tax) + " : " + currency + f.format(getTax));
//        txtTax.setText(getString(R.string.total_tax) + " : " + currency + f.format(getTax));
        txtDiscount.setText(getString(R.string.discount) + "  " + currency+ discount);
        txtTotalCost.setText(getString(R.string.total_price)+" "+currency+f.format(totalPrice));
        txt_total_paid.setText(getString(R.string.total_paid)+" "+currency+paid_amount);
        txt_total_due.setText(getString(R.string.due_amount)+" "+currency+f.format(getDueAmount));



        OrderDetailsAdapter.subTotalPrice=0;


        //for pdf report
        shortText = "Customer Name: Mr/Mrs. " + customerName;
        longText = "< Have a nice day. Visit again >";
        templatePDF = new PDFTemplate(getApplicationContext());


        BarCodeEncoder qrCodeEncoder = new BarCodeEncoder();
        try {
            bm = qrCodeEncoder.encodeAsBitmap(invoiceId, BarcodeFormat.CODE_128, 600, 300);
        } catch (WriterException e) {
            Log.d("Data", e.toString());
        }


        btnPdfReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                BitmapDrawable drawable = (BitmapDrawable) img_shop_logo.getDrawable();
                Bitmap bitmap_shop = drawable.getBitmap();



                ZatcaQRCodeGeneration.Builder builder = new ZatcaQRCodeGeneration.Builder();
                builder.sellerName(shopName)
                .taxNumber(shop_vat_no)
                .invoiceDate(DateUtil.getCurrentDate())
                .totalAmount(""+totalPrice)
                .taxAmount(""+getTax);
                String base64String = builder.getBase64();
                 bitmap = getQRCode(base64String);


                templatePDF.openDocument(false);
                templatePDF.addMetaData(Constant.ORDER_RECEIPT, Constant.ORDER_RECEIPT, "Smart POS");
                templatePDF.addImageLogo(bitmap_shop);
                templatePDF.addTitle(shopName, shopAddress+ "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nVat No:" + shop_vat_no, "Order Time:"+orderDate + " " + orderTime+"\nServed By: "+userName);
                templatePDF.addParagraph(shortText);

                templatePDF.createTable(header, getPDFReceipt());
              //  templatePDF.addImage(bm);
                templatePDF.addImage(bitmap);

                templatePDF.addRightParagraph(longText);

                templatePDF.closeDocument();
                templatePDF.viewPDF();


            }
        });



        btnThermalPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if the Bluetooth is available and on.
                if (!Tools.isBlueToothOn(OrderDetailsActivity.this)) return;

                PrefMng.saveActivePrinter(OrderDetailsActivity.this, PrefMng.PRN_WOOSIM_SELECTED);

                //Pick a Bluetooth device
                Intent i = new Intent(OrderDetailsActivity.this, DeviceListActivity.class);
                startActivityForResult(i, REQUEST_CONNECT);
            }
        });


    }

    private Bitmap getQRCode(String base64String) {
        try {
            BarcodeEncoder barcodeEncoder =  new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(base64String, BarcodeFormat.QR_CODE, 300, 300);
        } catch (Exception e) {
        }
        return null;
    }


    //for pdf
    private ArrayList<String[]> getPDFReceipt() {
        ArrayList<String[]> rows = new ArrayList<>();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrderDetailsActivity.this);
        databaseAccess.open();

        String name, price, qty, weight;
        double final_total_all_items=0;
        double costTotal;

        for (int i = 0; i < orderDetails.size(); i++) {

            name = orderDetails.get(i).getProductName();

            price = orderDetails.get(i).getProductPrice();
            qty = orderDetails.get(i).getProductQuantity();
            weight = orderDetails.get(i).getProductWeight();

            costTotal = Integer.parseInt(qty) * Double.parseDouble(price);

            rows.add(new String[]{name + "\n" + weight + "\n" + "(" + qty + "x" + currency + price + ")", ""+ costTotal});

            if(tax_type.equals("inclusive")) {
                final_total_all_items = final_total_all_items + costTotal - getTax;
            }else{
                final_total_all_items = final_total_all_items + costTotal;
            }

        }
     //   rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Sub Total: ", String.valueOf(final_total_all_items)});
        rows.add(new String[]{"Discount: ",   discount});
        rows.add(new String[]{"Total Tax: ", String.valueOf(getTax)});

    //    rows.add(new String[]{"..........................................", ".................................."});
        rows.add(new String[]{"Total Price: ", currency+String.valueOf(totalPrice)});
        rows.add(new String[]{"Paid Amount: ",paid_amount});
        rows.add(new String[]{"Due Amount: ", String.valueOf(getDueAmount)});

//        you can add more row above format
        return rows;
    }





    public void getProductsData(String order_id) {


        loading=new ProgressDialog(OrderDetailsActivity.this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<OrderDetails>> call;
        call = apiInterface.OrderDetailsByInvoice(order_id);

        call.enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetails>> call, @NonNull Response<List<OrderDetails>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    orderDetails = response.body();
                    loading.dismiss();


                    if (orderDetails.isEmpty()) {


                        Toasty.warning(OrderDetailsActivity.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();


                    } else {


                        



                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetails>> call, @NonNull Throwable t) {

                loading.dismiss();
                Toast.makeText(OrderDetailsActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CONNECT && resultCode == RESULT_OK) {
            try {
                //Get device address to print to.
                String blutoothAddr = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                //The interface to print text to thermal printers.
                IPrintToPrinter testPrinter = new TestPrinter(this, shopName, shopAddress, shopEmail, shopContact, invoiceId, orderDate, orderTime, shortText, longText, Double.parseDouble(orderPrice), f.format(calculatedTotalPrice), tax, discount, currency, userName,orderDetails);
                //Connect to the printer and after successful connection issue the print command.
                mPrnMng = printerFactory.createPrnMng(this, blutoothAddr, testPrinter);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (mPrnMng != null) mPrnMng.releaseAllocatoins();
        super.onDestroy();
    }







}

