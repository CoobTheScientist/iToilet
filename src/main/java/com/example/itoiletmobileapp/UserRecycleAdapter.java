package com.example.itoiletmobileapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.List;


public class UserRecycleAdapter extends RecyclerView.Adapter<UserRecycleAdapter.UserViewHolder> implements OnUserClickListener {
    private List<User> list;
    private DBHandler db;

    //constructor
    public UserRecycleAdapter(List<User> list, DBHandler db) {
        this.list = list;
        this.db = db;
    }

    @Override
    public UserRecycleAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup over, int type){
        //inflate layout view for holder
        View userView = LayoutInflater.from(over.getContext()).inflate(R.layout.user_adapter, over, false);
        return new UserRecycleAdapter.UserViewHolder(userView, this);
    }

    //handle click
    public void onUserClick(int position){
        User clicked = list.get(position);
        //highlight if clicked
        clicked.setHighlighted(!clicked.isHighlighted());
        notifyDataSetChanged();
    }

    public void onBindViewHolder(@NonNull UserRecycleAdapter.UserViewHolder userHolder, int index){
        User user = list.get(index);
        userHolder.bind(user);

        //set layout for highlight
        if(user.isHighlighted()){
            userHolder.itemView.setBackgroundColor(ContextCompat.getColor(userHolder.itemView.getContext(), R.color.primaryBlue));
        }
        else{
            userHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    //get size of the list
    @Override
    public int getItemCount(){
        return list.size();
    }

    //method to delete user if highlighted
    public void deleteHighlighted(){
        Iterator<User> iterator = list.iterator();
        while (iterator.hasNext()){
            User user = iterator.next();
            if(user.isHighlighted()) {
                db.deleteUser(user.getId());
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    //embedded holder class
    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView idText;
        private TextView emailText;
        private OnUserClickListener clickListener;
        private Button button;



        public UserViewHolder(@NonNull View user, OnUserClickListener clickListener){
            super(user);
            idText = user.findViewById(R.id.userIdText);
            emailText = user.findViewById(R.id.userEmailText);
            this.clickListener = clickListener;
            user.setOnClickListener(this);

        }
        //handle click to get position for highlight
        @Override
        public void onClick(View view){
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION && clickListener != null){
                clickListener.onUserClick(position);

            }
        }

        //bind info to layout elements
        public void bind(User user){
            idText.setText(String.valueOf(user.getId()));
            emailText.setText(user.getEmail());

        }

    }

}
