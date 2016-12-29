package id.barkost.personalbookkeeping.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import id.barkost.personalbookkeeping.R;
import id.barkost.personalbookkeeping.model.ModelTransaction;


/**
 * Created by fikri on 21/12/16.
 */

public class AdapterTransaction extends RecyclerView.Adapter<AdapterTransaction.MyViewHolder> {
    private List<ModelTransaction> transactionsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, detail, amount;
        public ImageView img;

        public MyViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.tv_date);
            img = (ImageView) view.findViewById(R.id.img_type);
            detail = (TextView) view.findViewById(R.id.tv_detail);
            amount = (TextView) view.findViewById(R.id.tv_amount);
        }
    }

    public AdapterTransaction(List<ModelTransaction> transactionsList) {
        this.transactionsList = transactionsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        ModelTransaction transaction = transactionsList.get(position);
        if (transaction.getTransaction_type().equals("Out")){
            holder.img.setImageResource(R.drawable.ic_outcome);
            holder.amount.setTextColor(Color.rgb(213,00,00)); //parseColor(String.valueOf(R.color.outcome))
        } else {
            holder.img.setImageResource(R.drawable.ic_income);
            holder.amount.setTextColor(Color.rgb(00,200,53)); //String.valueOf(R.color.income)
        }
        holder.date.setText(transaction.getTransaction_date());
        holder.detail.setText(transaction.getTransaction_detail());
        holder.amount.setText("Rp. " + String.valueOf(df.format(transaction.getTransaction_amount())));
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }
}