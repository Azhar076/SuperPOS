package com.app.spos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spos.Constant;
import com.app.spos.R;
import com.app.spos.model.Customer;
import com.app.spos.model.getAllCustomersReceiveModel;
import com.app.spos.pdf_report.PDFTemplate;
import com.app.spos.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.List;

public class AllCustomerReceiveAdapter extends RecyclerView.Adapter<AllCustomerReceiveAdapter.MyViewHolder> {
     Context context;
     List<getAllCustomersReceiveModel> customerData;
    Utils utils;
    private PDFTemplate templatePDF;
    String imageUrl,shopName,shop_vat_no,shopEmail,shopContact,shopAddress;
    String longText, shortText, userName;


    public AllCustomerReceiveAdapter(Context context, List<getAllCustomersReceiveModel> customerData,String imageUrl,String shopName,String shop_vat_no,String shopEmail,String shopContact,String shopAddress) {
        this.context = context;
        this.customerData = customerData;
        this.imageUrl = imageUrl;
        this.shopName = shopName;
        this.shop_vat_no = shop_vat_no;
        this.shopEmail = shopEmail;
        this.shopContact = shopContact;
        this.shopAddress = shopAddress;
        utils=new Utils();


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_customer_receive, parent, false);
        return new MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        getAllCustomersReceiveModel model = customerData.get(position);
        holder.txtDate.setText(model.getPayDate());
        holder.txtPayment.setText(model.getAmount());
        holder.txtCustomer.setText(model.getCustomerName());

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.image_placeholder)
                .into(holder.cart_product_image);
        templatePDF = new PDFTemplate(context);
        holder.cart_product_image.setVisibility(View.GONE);

        shortText = "Customer Name: Mr/Mrs. " + holder.txtCustomer.getText().toString();
        longText = "< Have a nice day. Visit again >";

        holder.imgPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BitmapDrawable drawable = (BitmapDrawable) holder.cart_product_image.getDrawable();
                Bitmap bitmap_shop = drawable.getBitmap();

                templatePDF.openDocument(true);
                templatePDF.addMetaData(Constant.ORDER_RECEIPT, Constant.ORDER_RECEIPT, "Smart POS");
                templatePDF.addImageLogo(bitmap_shop);
                templatePDF.addTitle(shopName, shopAddress+ "\n Email: " + shopEmail + "\nContact: " + shopContact + "\nVat No:" + shop_vat_no, "Date: "+model.getPayDate()+"\nCreated By: "+model.getCreated_by());
                templatePDF.addParagraph(shortText);

                //  templatePDF.addImage(bm);

                templatePDF.addParagraph("Paid Amount:  "+model.getAmount());


                templatePDF.closeDocument();
                templatePDF.viewPDF();
            }
        });


    }

    @Override
    public int getItemCount() {
        return customerData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtPayment, txtCustomer, txtNote;
        ImageView imgPrint,cart_product_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txt_date);
            txtPayment = itemView.findViewById(R.id.txt_payment);
            txtCustomer = itemView.findViewById(R.id.txt_customer);
            txtNote = itemView.findViewById(R.id.txt_note);

            imgPrint = itemView.findViewById(R.id.img_print);
            cart_product_image = itemView.findViewById(R.id.cart_product_image);



        }
    }
}
