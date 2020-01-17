package com.example.my;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my.databinding.RawDataEventBinding;
import com.example.my.entity.Event;

import java.util.Collections;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private List<Event> events;

    interface Callback{
        void onClick(int position);
    }

    private Callback callback;

    EventAdapter(List<Event> events, Callback callback) {
        this.events = events;
        this.callback = callback;
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RawDataEventBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.raw_data_event,parent,false);

        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.EventViewHolder holder, final int position) {
        Event event = events.get(position);
        holder.binding.setEvent(event);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    void removeItem(int position){
        events.remove(position);
        notifyItemRemoved(position);
    }

    void restoreItem(int position, Event event){
        events.add(position, event);
        notifyItemInserted(position);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        RawDataEventBinding binding;
        EventViewHolder(@NonNull RawDataEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(events, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(events, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(EventViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.GRAY);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRowClear(EventViewHolder myViewHolder) {
        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
    }
}
