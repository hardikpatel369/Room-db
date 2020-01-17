package com.example.my;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.my.dao.DaoClass;
import com.example.my.database.DatabaseClass;
import com.example.my.entity.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class MemberListFragment extends Fragment implements MemberAdapter.Callback{

    private DaoClass dao;
    private DatabaseClass db;
    private EditText etName, etEmail ,etUname,etUemail,etUid;
    private RecyclerView recyclerView;
    private Context context;
    private List<Member> members;
    private FrameLayout frameLayout;
    private MemberAdapter adapter;
    private Member member;
    private SwipeController swipeController = null;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public MemberListFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnAddMemberInEvent = view.findViewById(R.id.btn_add_member_in_event);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        frameLayout = view.findViewById(R.id.frameLayout);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);

        setupRecyclerView();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                addMemberDialog();
            }
        });

        btnAddMemberInEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMemberList();
            }
        });

        db = Room.databaseBuilder(context, DatabaseClass.class, "myDatabase")
                .allowMainThreadQueries()
                .build();
        dao = db.daoClass();
        getAllData();
    }

    private void AddMemberList() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addMemberDialog() {
        final Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.dialog_add);
        mDialog.setCanceledOnTouchOutside(true);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setAttributes(lp);

        etName = mDialog.findViewById(R.id.et_name);
        etEmail = mDialog.findViewById(R.id.et_email);
        Button btnAdd = mDialog.findViewById(R.id.btn_add);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
                mDialog.dismiss();
                Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(members.size()-1);
                getAllData();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(final int position) {


    }

    private void addMember() {

        dao.addMember(new Member(etName.getText().toString(), etEmail.getText().toString()));
        Toast.makeText(context, "Member Added Successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateMember(){
        dao.updateMember(new Member(Integer.parseInt(etUid.getText().toString()),etUname.getText().toString(),
                etUemail.getText().toString()));
        Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
    }

    private void getAllData() {
         members = db.daoClass().getMembers();
        if (members.size() > 0) {
            adapter = new MemberAdapter(members,this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(final int position) {
                member = members.get(position);
                dao.deleteMember(member);
                adapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(frameLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapter.restoreItem(position,member);
                        recyclerView.scrollToPosition(position);
                    }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onLeftClicked(final int position) {
                super.onLeftClicked(position);
                final Dialog mDialog = new Dialog(context);
                mDialog.setContentView(R.layout.dialog_update);
                mDialog.setCanceledOnTouchOutside(true);
                Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDialog.setCancelable(true);
                mDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mDialog.getWindow().setAttributes(lp);

                Member member = members.get(position);

                etUid = mDialog.findViewById(R.id.et_uid);
                etUname = mDialog.findViewById(R.id.et_uname);
                etUemail = mDialog.findViewById(R.id.et_uemail);
                Button btnUpdate = mDialog.findViewById(R.id.btn_update);

                etUid.setText(String.valueOf(member.getId()));
                etUid.setEnabled(false);
                etUname.setText(member.getName());
                etUemail.setText(member.getEmail());

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateMember();
                        mDialog.dismiss();
                        recyclerView.smoothScrollToPosition(position);
                        getAllData();
                    }
                });
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }
}
