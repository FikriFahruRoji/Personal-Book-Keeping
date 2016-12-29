package id.barkost.personalbookkeeping.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.barkost.personalbookkeeping.R;
import id.barkost.personalbookkeeping.adapter.AdapterTransaction;
import id.barkost.personalbookkeeping.helper.DatabaseHelper;
import id.barkost.personalbookkeeping.listener.RecyclerTouchListener;
import id.barkost.personalbookkeeping.model.ModelTransaction;


/**
 * Created by fikri on 25/12/16.
 */

public class Dashboard extends Fragment {

    public static int total_income = 0, total_outcome = 0;

    private SimpleDateFormat format;
    Calendar myCalendar;
    java.sql.Time timeValue;

    private List<ModelTransaction> transactionsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterTransaction mAdapter;

    private DatabaseHelper myDB;

    private TextView tv_sum_income, tv_sum_outcome, tv_sum_balance;
    private EditText description_input, amount_input, date_input, time_input;
    private RadioButton rbIn, rbOut;
    private String type = "In";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_dashboard, container, false);

        myDB = new DatabaseHelper(getActivity());
        recyclerView = (RecyclerView) viewRoot.findViewById(R.id.recycler_dashboard);
//        myDB.save_table_transaction("01-12-2016 06:05:00", "Out", "Bensin", 5000);
//        myDB.save_table_transaction("01-12-2016 08:25:00", "Out", "Makan", 15000);
//        myDB.save_table_transaction("01-12-2016 06:00:00", "In", "No desc", 20000);


        tv_sum_income = (TextView) viewRoot.findViewById(R.id.tv_sum_income);
        tv_sum_outcome = (TextView) viewRoot.findViewById(R.id.tv_sum_outcome);
        tv_sum_balance = (TextView) viewRoot.findViewById(R.id.tv_sum_balance);

        final FloatingActionButton fab = (FloatingActionButton) viewRoot.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View dialoglayout1 = inflater.inflate(R.layout.layout_input, null);

                date_input = (EditText) dialoglayout1.findViewById(R.id.date_input);
                time_input = (EditText) dialoglayout1.findViewById(R.id.time_input);
                description_input = (EditText) dialoglayout1.findViewById(R.id.description_input_ET);
                amount_input = (EditText) dialoglayout1.findViewById(R.id.amount_input_ET);
                rbIn = (RadioButton) dialoglayout1.findViewById(R.id.radio_income);
                rbOut = (RadioButton) dialoglayout1.findViewById(R.id.radio_outcome);

                rbIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { if (rbIn.isChecked()) { type = "In";} }
                });
                rbOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { if (rbOut.isChecked()) { type = "Out"; } }
                });

                DateFormat timeF = new SimpleDateFormat("HH:mm");
                DateFormat dateF = new SimpleDateFormat("dd-MM-yyyy");
                String time = timeF.format(Calendar.getInstance().getTime());
                final String date = dateF.format(Calendar.getInstance().getTime());
                date_input.setText(date);
                time_input.setText(time);

                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Add transaction");
                builder.setView(dialoglayout1);
                builder.setCancelable(true);
                datePicker();
                timePicker();
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String datetime = date_input.getText() + " " + time_input.getText();
                        String desc = String.valueOf(description_input.getText());
                        int amount =Integer.parseInt(String.valueOf(amount_input.getText()));
                        boolean a = myDB.save_table_transaction(datetime, type, desc, amount);
                        if (a) {
                            getDataFromTable();
                            Toast.makeText(getActivity(), "Transaction added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });

        mAdapter = new AdapterTransaction(transactionsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplication());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
//                if (dy > 0)
//                    fab.hide();
//                else if (dy == 0)
//                    fab.show();
//            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    fab.hide();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.show();
                        }
                    }, 500);
                }
            }
        });

        getDataFromTable();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final ModelTransaction transaction = transactionsList.get(position);
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                final View dialoglayout1 = inflater.inflate(R.layout.layout_input, null);

                date_input = (EditText) dialoglayout1.findViewById(R.id.date_input);
                time_input = (EditText) dialoglayout1.findViewById(R.id.time_input);
                description_input = (EditText) dialoglayout1.findViewById(R.id.description_input_ET);
                amount_input = (EditText) dialoglayout1.findViewById(R.id.amount_input_ET);
                rbIn = (RadioButton) dialoglayout1.findViewById(R.id.radio_income);
                rbOut = (RadioButton) dialoglayout1.findViewById(R.id.radio_outcome);

                rbIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { if (rbIn.isChecked()) { type = "In";} }
                });
                rbOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { if (rbOut.isChecked()) { type = "Out"; } }
                });

                date_input.setText(transaction.getTransaction_date()
                        .substring(0, transaction.getTransaction_date().indexOf(' ')));
                time_input.setText(transaction.getTransaction_date()
                        .substring(10));;
                description_input.setText(transaction.getTransaction_detail());
                amount_input.setText(String.valueOf(transaction.getTransaction_amount()));

//                if (transaction.getTransaction_type() == "In") {
//                    rbIn.setChecked(true);
//                } else if (transaction.getTransaction_type() == "Out") {
//                    rbOut.setChecked(true);
//                }

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Edit transaction");
                builder.setView(dialoglayout1);
                builder.setCancelable(true);
                datePicker();
                timePicker();
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int trans_id = transaction.getTransaction_id();
                        String datetime = date_input.getText() + " " + time_input.getText();
                        String desc = String.valueOf(description_input.getText());
                        int amount =Integer.parseInt(String.valueOf(amount_input.getText()));

                        boolean a = myDB.update_table_transaction(trans_id, datetime, type, desc, amount);
                        if (a) {
                            getDataFromTable();
                            Toast.makeText(getActivity(), "Transaction edited", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }

            @Override
            public void onLongClick(View view, int position) {
                final ModelTransaction transaction = transactionsList.get(position);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Delete transaction");
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                myDB.delete_transaction(String.valueOf(transaction.getTransaction_id()));
                                getDataFromTable();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        }));


        return viewRoot;
    }

    public void getDataFromTable() {
        transactionsList.clear();
        Cursor transactions = myDB.list_table_transaction();
        if (transactions.getCount() == 0) {
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (transactions.moveToNext()) {
            ModelTransaction transaction = new ModelTransaction(transactions.getInt(0), transactions.getString(1), transactions.getString(2), transactions.getString(3), transactions.getInt(4));
            transactionsList.add(transaction);
        }
        mAdapter.notifyDataSetChanged();

        final DecimalFormat df = new DecimalFormat();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);

        myDB.sum_in_transaction();
        myDB.sum_out_transaction();

        tv_sum_income.setText("Rp. " + String.valueOf(df.format(total_income)));
        tv_sum_outcome.setText("Rp. " + String.valueOf(df.format(total_outcome)));
        tv_sum_balance.setText(getString(R.string.tx_balance) + " : Rp. " + String.valueOf(df.format(total_income - total_outcome)));
    }

    private void datePicker() {
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                date_input.setText(sdf.format(myCalendar.getTime()));
            }

        };
        date_input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void timePicker() {
        time_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        try {
                            String dtStart = String.valueOf(selectedHour) + ":" + String.valueOf(selectedMinute);
                            format = new SimpleDateFormat("HH:mm");
                            timeValue = new java.sql.Time(format.parse(dtStart).getTime());
                            time_input.setText(String.valueOf(timeValue));
                        } catch (Exception ex) {
                            time_input.setText(ex.getMessage().toString());
                        }
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
}