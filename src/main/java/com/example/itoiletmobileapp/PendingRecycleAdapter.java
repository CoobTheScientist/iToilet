package com.example.itoiletmobileapp;


import android.graphics.Color;
import android.util.Log;
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

public class PendingRecycleAdapter extends RecyclerView.Adapter<PendingRecycleAdapter.PendingViewHolder> implements OnBathroomClickListener {

    private List<Bathroom> list;
    private DBHandler db;

    //constructor
    public PendingRecycleAdapter(List<Bathroom> list, DBHandler db){
        this.list = list;
        this.db = db;
        //TEST++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Log.d("PendingRecycleAdapter", "Adapter List size: " + list.size());
    }
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup over, int type){
        //inflate layout for adapter holder
        View pendingView = LayoutInflater.from(over.getContext()).inflate(R.layout.pending_adapter, over, false);
        return new PendingViewHolder(pendingView, this);
    }

    //highlight bathroom in list
    @Override
    public void onBathroomClick(int position){
        Bathroom clicked = list.get(position);
        clicked.setHighlighted(!clicked.isHighlighted());
        notifyDataSetChanged();
    }
    //bind bathroom
    public void onBindViewHolder(@NonNull PendingViewHolder pending, int index){
        Bathroom bathroom = list.get(index);
        pending.bind(bathroom);

        //handle highlight layout
        if(bathroom.isHighlighted()){
            pending.itemView.setBackgroundColor(ContextCompat.getColor(pending.itemView.getContext(), R.color.primaryBlue));
        }
        else{
            pending.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    //method to get list count
    @Override
    public int getItemCount(){
        return list.size();
    }
    //method to delete highlighted bathroom
    public void deleteHighlighted(){
        Iterator<Bathroom> iterator = list.iterator();
        while(iterator.hasNext()){
            Bathroom bathroom = iterator.next();
            if(bathroom.isHighlighted()){
                db.deleteBathroom(bathroom.getId());
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    //embedded holder class
    public class PendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView addressText;
        private TextView locationText;
        private TextView genderText;
        private TextView overallText;
        private TextView cleanText;
        private TextView paperText;
        private TextView handicapText;
        private TextView dryingText;
        private TextView babyText;
        private OnBathroomClickListener clickListener;
        private Button button;



        public PendingViewHolder(@NonNull View pending, OnBathroomClickListener clickListener){
            super(pending);
            //associate all layout elements
            addressText = pending.findViewById(R.id.addressText);
            locationText = pending.findViewById(R.id.locationText);
            genderText = pending.findViewById(R.id.genderText);
            overallText = pending.findViewById(R.id.overallText);
            cleanText = pending.findViewById(R.id.cleanlinessText);
            paperText = pending.findViewById(R.id.toiletPaperText);
            handicapText = pending.findViewById(R.id.handicapText);
            dryingText = pending.findViewById(R.id.dryingText);
            babyText = pending.findViewById(R.id.babyStationText);
            this.clickListener = clickListener;
            pending.setOnClickListener(this);

        }
        @Override
        public void onClick(View view){
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION && clickListener != null){
                clickListener.onBathroomClick(position);

            }
        }

        //bind info
        public void bind(Bathroom bathroom){
            addressText.setText(bathroom.getAddress());
            locationText.setText(bathroom.getLocation());
            genderText.setText(bathroom.getGender());
            overallText.setText(String.valueOf(bathroom.getOverall()));
            cleanText.setText(String.valueOf(bathroom.getCleanliness()));
            paperText.setText(String.valueOf(bathroom.getToiletPaper()));
            handicapText.setText(bathroom.getHandicap());
            dryingText.setText(bathroom.getDrying());
            babyText.setText(bathroom.getBabyStation());
        }

    }
}
