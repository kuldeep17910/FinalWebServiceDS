package com.suriyal.webservicevollery2insertds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolser> {
    private List<Product> list;

    public ProductAdapter(List<Product> list) {
        this.list = list;
    }

    private Context context;
    @NonNull
    @Override
    public MyViewHolser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       context= parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolser holder, int position) {

        Product product=list.get(position);
        holder.tname.setText(product.getName());
        //Glide picaso
        Glide.with(context).load("http://192.168.225.97:9090/MyServerTom/productimage/"+product.getImage()).into(holder.timage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolser extends RecyclerView.ViewHolder {
        private TextView tname;
        private ImageView timage;

        public MyViewHolser(@NonNull View itemView) {
            super(itemView);
            timage = itemView.findViewById(R.id.myimage);
            tname = itemView.findViewById(R.id.myname);

        }
    }

}
