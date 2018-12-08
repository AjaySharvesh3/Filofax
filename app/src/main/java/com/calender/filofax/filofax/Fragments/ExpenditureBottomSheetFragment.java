package com.calender.filofax.filofax.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.calender.filofax.filofax.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExpenditureBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener  {

    private Context context;
    private Calendar calendar;
    private int date , month , year  ;
    private String srtDate;
    
    ExpenditureBottomSheetFragment expenditureBottomSheetFragment;
    private DatePickerDialog datePickerDialog;


    public ExpenditureBottomSheetFragment() {
    }

    @BindView(R.id.img_btn_close)
    ImageButton img_btn_close;
    @BindView(R.id.btn_select_date)
    Button btn_select_date;
    @BindView(R.id.switch_monthy_report)
    Switch switch_monthy_report;
    @BindView(R.id.tv_date)
    TextView tv_date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_expenditure,container,false);;
        ButterKnife.bind(this,view);

        context = view.getContext();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        date = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, date);

        datePickerDialog = new DatePickerDialog(
                context, this, 1900, 0, 1);

        img_btn_close.setOnClickListener(this);
        btn_select_date.setOnClickListener(this);
        switch_monthy_report.setOnCheckedChangeListener(this);

        return view;
    }
/*
    @OnClick(R.id.img_btn_close)
    public void onClickCloseAbout(View view){

    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_btn_close:
                if(expenditureBottomSheetFragment != null) {
                    expenditureBottomSheetFragment.dismiss();
                }
                break;
            case R.id.btn_select_date:
                datePickerDialog.updateDate(year,month,date);
                datePickerDialog.show();
                break;
            case R.id.switch_monthy_report:

                break;
        }
    }

    public void setExpenditureBottomSheetFragment(ExpenditureBottomSheetFragment expenditureBottomSheetFragment) {
        this.expenditureBottomSheetFragment = expenditureBottomSheetFragment;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    private void showDate(int year, int month , int date) {

        srtDate = new StringBuilder().append(date).append("/").append(month)
                .append("/").append(year).toString();
        tv_date.setText(srtDate);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int arg1, int arg2, int arg3) {
        showDate(arg1, arg2+1, arg3);
    }
}
