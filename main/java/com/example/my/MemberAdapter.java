package com.example.my;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.my.databinding.RawDataMemberBinding;
import com.example.my.entity.Member;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Member> data;

    interface Callback{
         void onClick(int position);
    }
    private Callback callback;

    MemberAdapter(List<Member> data,Callback callback) {
        this.data = data;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MemberAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        RawDataMemberBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.raw_data_member, parent, false);
        return new MemberViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MemberAdapter.MemberViewHolder holder, final int position) {
        final Member member = data.get(position);
        holder.binding.setMember(member);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    void removeItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    void restoreItem(int position, Member member){
        data.add(position, member);
        notifyItemInserted(position);
    }

     static class MemberViewHolder extends RecyclerView.ViewHolder {
        RawDataMemberBinding binding;

        MemberViewHolder(@NonNull RawDataMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
