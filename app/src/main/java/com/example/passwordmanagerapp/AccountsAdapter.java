package com.example.passwordmanagerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;


public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder>
        implements Filterable {
    private Context context;

    private List<Account> accountList;
    private List<Account> accountListFiltered;
    private ContactsAdapterListener listener;
    private String letter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView accountName,accountPassowrd;
        public ImageView imageLetter;

        public MyViewHolder(View view) {
            super(view);
            accountName = view.findViewById(R.id.name);
            accountPassowrd = view.findViewById(R.id.password);
            imageLetter = view.findViewById(R.id.image_letter);

            //change password symbol dot to asterisk
            accountPassowrd.setTransformationMethod(new AsteriskPasswordTransformationMethod());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(accountListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public AccountsAdapter(Context context, List<Account> accountList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.accountList = accountList;
        this.accountListFiltered = accountList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Account account = accountListFiltered.get(position);
        holder.accountName.setText(account.getName());
        holder.accountPassowrd.setText(account.getPassword());
        // holder.thumbnail.setText(account.getImage());
        letter= String.valueOf(account.getName().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL;
        // Create a new TextDrawable for our image's background
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(letter, account.getImageColor());
        holder.imageLetter.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return accountListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    accountListFiltered = accountList;
                } else {
                    List<Account> filteredList = new ArrayList<>();
                    for (Account row : accountList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    accountListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = accountListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                accountListFiltered = (ArrayList<Account>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Account contact);
    }
}

