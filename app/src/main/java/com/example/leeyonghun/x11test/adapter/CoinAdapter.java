package com.example.leeyonghun.x11test.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeyonghun.x11test.MainActivity;
import com.example.leeyonghun.x11test.R;

import com.example.leeyonghun.x11test.SendActivity;
import com.example.leeyonghun.x11test.data.Coin;
import com.example.leeyonghun.x11test.data.CoinType;

import org.bitcoinj.params.MainNetParams;

import java.util.ArrayList;
import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {
    private List<Coin> mData;
    private Context mContext;
    private ItemClickListener mClickListener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public  class CoinViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView coinName , balanace, address;
        ImageView coinImage;
        Button btn_refresh  , btn_send , btn_copy;

        CoinViewHolder(View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            balanace = itemView.findViewById(R.id.balance);
            coinImage = itemView.findViewById(R.id.coinImage);
            address = itemView.findViewById(R.id.address);

            btn_refresh = itemView.findViewById(R.id.refresh);
            btn_send = itemView.findViewById(R.id.send);
            btn_copy = itemView.findViewById(R.id.copy);

            btn_refresh.setOnClickListener(this);
            btn_send.setOnClickListener(this);
            btn_copy.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        public void copyToClipBoard(String text)
        {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", text);
            clipboard.setPrimaryClip(clip);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            Toast.makeText(mContext , "onClick",Toast.LENGTH_SHORT).show();
            switch (view.getId())
            {
                case R.id.copy:
                    copyToClipBoard(address.getText().toString());
                    break;
                case R.id.send:
                    Intent intent = new Intent(mContext, SendActivity.class);
                    intent.putExtra("coinName", coinName.getText().toString());
                    intent.putExtra("address", address.getText().toString());
                    mContext.startActivity(intent);
                    break;
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CoinAdapter(List<Coin> data) {
        this.mData = data;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CoinAdapter.CoinViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        mContext = parent.getContext();
        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_list_row, parent, false);
        return new CoinAdapter.CoinViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CoinAdapter.CoinViewHolder holder, int position) {
        Coin coin = mData.get(position);
        holder.coinName.setText(coin.getCoinName());
        holder.balanace.setText(coin.getBalance());
        holder.address.setText(coin.getAddress());
        holder.coinImage.setImageDrawable(getImage(coin.getCoinName()));
    }

    private Drawable getImage(String coin)
    {
        switch (CoinType.valueOf(coin))
        {
            case BITCOIN:
                return mContext.getDrawable(R.drawable.btc_logo);
            case BITCOINCASH:
                return mContext.getDrawable(R.drawable.bch_logo);
            case QTUM:
                return mContext.getDrawable(R.drawable.qtum_logo);
            case LITECOIN:
                return mContext.getDrawable(R.drawable.ltc_logo);
            case DASH:
                return mContext.getDrawable(R.drawable.dash_logo);
            default:
                return mContext.getDrawable(R.drawable.qtum_logo);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}