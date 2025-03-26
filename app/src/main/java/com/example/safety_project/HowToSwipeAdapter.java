package com.example.safety_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HowToSwipeAdapter extends RecyclerView.Adapter<HowToSwipeAdapter.ViewHolder> {

    private Context context;
    private int[] images = { R.drawable.s1, R.drawable.s2, R.drawable.s3 };
    private String[] titles = {
            "About Naari Rakshak",
            "How to use in trouble?",
            "What happens after 5 seconds?"
    };
    private String[] contents = {
            "To ask for help, add your family and friends' mobile numbers.",
            "Press the volume up/down button for 5 seconds.",
            "SOS will be triggered, sending a call, message, and location to the registered numbers."
    };

    public HowToSwipeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
        holder.title.setText(titles[position]);
        holder.content.setText(contents[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, content;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
        }
    }
}
