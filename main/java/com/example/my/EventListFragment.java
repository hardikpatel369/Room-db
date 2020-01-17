package com.example.my;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.example.my.entity.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class EventListFragment extends Fragment implements EventAdapter.Callback,StartDragListener {

    private Context context;
    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private DatabaseClass db;
    private DaoClass dao;
    private EditText etTitle,etDescription,etId;
    private TextView tvSdate,tvEdate,tvStime,tvEtime;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private List<Event> events;
    private Event event;
    private EventAdapter adapter;
    private String strDate = "";
    private String strMonth = "";
    private String strYear = "";
    private SwipeController swipeController = null;
    private ItemTouchHelper touchHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        frameLayout = view.findViewById(R.id.frameLayout);
        setupRecyclerView();

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                addEventDialog();
            }
        });

        db = Room.databaseBuilder(context, DatabaseClass.class, "myEventDatabase")
                .allowMainThreadQueries()
                .build();
        dao = db.daoClass();
        getAllData();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addEventDialog() {
        final Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.dialog_add_event);
        mDialog.setCanceledOnTouchOutside(true);
        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCancelable(true);
        mDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mDialog.getWindow().setAttributes(lp);

        etTitle = mDialog.findViewById(R.id.et_title);
        etDescription = mDialog.findViewById(R.id.et_description);
        tvSdate = mDialog.findViewById(R.id.tv_sDate);
        tvEdate = mDialog.findViewById(R.id.tv_eDate);
        tvStime = mDialog.findViewById(R.id.tv_sTime);
        tvEtime = mDialog.findViewById(R.id.tv_eTime);
        Button btnAddEvent = mDialog.findViewById(R.id.btn_add_event);

        tvSdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 calendar= Calendar.getInstance();
                 int year = calendar.get(Calendar.YEAR);
                 int month = calendar.get(Calendar.MONTH);
                 int date = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvSdate.setText("Start Date : "+dayOfMonth+"/"+(month+1)+"/"+year);

                        strYear = String.valueOf(year);
                        strMonth = String.valueOf(month);
                        strDate = String.valueOf(dayOfMonth);
                    }
                },year,month,date);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10);
                datePickerDialog.show();
            }
        });

        tvEdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar= Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tvEdate.setText("End Date : "+dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,date);
                calendar.set(Calendar.YEAR,Integer.parseInt(strYear));
                calendar.set(Calendar.MONTH,Integer.parseInt(strMonth));
                calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(strDate));
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        tvStime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar= Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvStime.setText("Start Time : "+hourOfDay+":"+minute+":00");
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });

        tvEtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar= Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvEtime.setText("End Time : "+hourOfDay+":"+minute+":00");
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
                mDialog.dismiss();
                Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(events.size()-1);
                getAllData();
            }
        });
    }

    private void addEvent() {
        event = new Event();
        event.setTitle(etTitle.getText().toString());
        event.setDescription(etDescription.getText().toString());
        event.setStartDate(tvSdate.getText().toString());
        event.setEndDate(tvEdate.getText().toString());
        event.setStartTime(tvStime.getText().toString());
        event.setEndTime(tvEtime.getText().toString());

        dao.addEvent(event);
        Toast.makeText(context, "Event Added Successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateEvent() {
        event = new Event();
        event.setId(Integer.parseInt(etId.getText().toString()));
        event.setTitle(etTitle.getText().toString());
        event.setDescription(etDescription.getText().toString());
        event.setStartDate(tvSdate.getText().toString());
        event.setEndDate(tvEdate.getText().toString());
        event.setStartTime(tvStime.getText().toString());
        event.setEndTime(tvEtime.getText().toString());

        dao.updateEvent(event);
        Toast.makeText(context, "Event Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private void getAllData() {
        events = db.daoClass().getEvents();
        if (events.size() > 0) {
            adapter = new EventAdapter(events, this);

            ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
            touchHelper  = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);

            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView(){

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(final int position) {

                event = events.get(position);
                dao.deleteEvent(event);
                adapter.removeItem(position);

                Snackbar snackbar = Snackbar.make(frameLayout, "Event was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        adapter.restoreItem(position,event);
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
                mDialog.setContentView(R.layout.dialog_update_event);
                mDialog.setCanceledOnTouchOutside(true);
                Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                mDialog.setCancelable(true);
                mDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mDialog.getWindow().setAttributes(lp);

                event = events.get(position);

                etId = mDialog.findViewById(R.id.et_id);
                etTitle = mDialog.findViewById(R.id.et_title);
                etDescription = mDialog.findViewById(R.id.et_description);
                tvSdate = mDialog.findViewById(R.id.tv_sDate);
                tvEdate = mDialog.findViewById(R.id.tv_eDate);
                tvStime = mDialog.findViewById(R.id.tv_sTime);
                tvEtime = mDialog.findViewById(R.id.tv_eTime);
                Button btnUpdateEvent = mDialog.findViewById(R.id.btn_add_event);

                tvSdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar= Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int date = calendar.get(Calendar.DAY_OF_MONTH);

                        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                tvSdate.setText("Start Date : "+dayOfMonth+"/"+(month+1)+"/"+year);

                                strYear = String.valueOf(year);
                                strMonth = String.valueOf(month);
                                strDate = String.valueOf(dayOfMonth);
                            }
                        },year,month,date);
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                        datePickerDialog.show();
                    }
                });

                tvEdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar= Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        final int date = calendar.get(Calendar.DAY_OF_MONTH);

                        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                tvEdate.setText("End Date : "+dayOfMonth+"/"+(month+1)+"/"+year);
                            }
                        },year,month,date);
                        calendar.set(Calendar.YEAR,Integer.parseInt(strYear));
                        calendar.set(Calendar.MONTH,Integer.parseInt(strMonth));
                        calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(strDate));
                        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                        datePickerDialog.show();
                    }
                });

                tvStime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar= Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tvStime.setText("Start Time : "+hourOfDay+":"+minute+":00");
                            }
                        },hour,minute,true);
                        timePickerDialog.show();
                    }
                });

                tvEtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar= Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                tvEtime.setText("End Time : "+hourOfDay+":"+minute+":00");
                            }
                        },hour,minute,true);
                        timePickerDialog.show();
                    }
                });

                etId.setText(String.valueOf(event.getId()));
                etId.setEnabled(false);
                etTitle.setText(event.getTitle());
                etDescription.setText(event.getDescription());
                tvSdate.setText(event.getStartDate());
                tvEdate.setText(event.getEndDate());
                tvStime.setText(event.getStartTime());
                tvEtime.setText(event.getEndTime());

                btnUpdateEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateEvent();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(int position) {
//        final Dialog mDialog = new Dialog(context);
//        mDialog.setContentView(R.layout.dialog_event_of_member);
//        mDialog.setCanceledOnTouchOutside(true);
//        Objects.requireNonNull(mDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        mDialog.setCancelable(true);
//        mDialog.show();
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(Objects.requireNonNull(mDialog.getWindow()).getAttributes());
//        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        mDialog.getWindow().setAttributes(lp);
//
//        ListView listView = mDialog.findViewById(R.id.listview);
//        Button btnAddEventofMember = mDialog.findViewById(R.id.btn_add_event_of_member);
//        btnAddEventofMember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("btn",true);
//                MemberListFragment btn = new MemberListFragment();
//                btn.setArguments(bundle);
//
//                Fragment fragment = new MemberListFragment();
//                FragmentManager manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.replace(R.id.frame,fragment);
//                transaction.commit();
//                mDialog.dismiss();
//            }
//        });
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }
}