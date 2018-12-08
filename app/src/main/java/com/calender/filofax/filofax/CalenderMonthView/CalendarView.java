package com.calender.filofax.filofax.CalenderMonthView;

import android.content.Context;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.calender.filofax.filofax.Fragments.ExpenditureBottomSheetFragment;
import com.calender.filofax.filofax.R;

import java.util.Calendar;

import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.MONTH_PER_YEAR;
import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.TYPE_DAY;
import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.TYPE_TITLE;


public class CalendarView extends ConstraintLayout implements View.OnClickListener {

    private CalendarSnapHelper mSnapHelper;
    private static int month;
    private static int year;
    private static int mSnapPosition = -1;
    private ExpenditureBottomSheetFragment expenditureBottomSheetFragment;
    private FragmentManager fragmentManager;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        android.support.v4.app.FragmentActivity fragmentActivity = (android.support.v4.app.FragmentActivity) getContext();
        fragmentManager = fragmentActivity.getSupportFragmentManager();
        init(context);
    }

    void init(final Context context) {
        final View root = inflate(context, R.layout.calendar_view, this);

        final TextView monthView = root.findViewById(R.id.month);
        final View nextView = root.findViewById(R.id.next);
        final View prevView = root.findViewById(R.id.prev);
        final RecyclerView recyclerView = root.findViewById(R.id.days);
        root.findViewById(R.id.expenditures_fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                expenditureBottomSheetFragment = new ExpenditureBottomSheetFragment();
                expenditureBottomSheetFragment.setExpenditureBottomSheetFragment(expenditureBottomSheetFragment);
                expenditureBottomSheetFragment.show(fragmentManager, expenditureBottomSheetFragment.getTag());
            }
        });
        final CalendarAdapter adapter = new CalendarAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 13, LinearLayoutManager.HORIZONTAL, true);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case TYPE_TITLE:
                        return 1;
                    case TYPE_DAY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);

        mSnapHelper = new CalendarSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adapter.setDimensions(recyclerView.getMeasuredWidth() / 7);
                Calendar endCalendar = Calendar.getInstance();
                Calendar startCalendar = Calendar.getInstance();
                Calendar currentCalendar = Calendar.getInstance();
                startCalendar.set(1900, 0, 1);
                endCalendar.set(2100, 0, 1);
                adapter.setStartCalendar(startCalendar);
                adapter.setEndCalendar(endCalendar);
                recyclerView.setAdapter(adapter);

                mSnapHelper.gotoMonth((endCalendar.get(Calendar.YEAR) - currentCalendar.get(Calendar.YEAR)) * MONTH_PER_YEAR + endCalendar.get(Calendar.MONTH) - currentCalendar.get(Calendar.MONTH));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int nSnapPosition = -1;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int snapPosition = CalendarSnapHelper.getSnapPosition();
                if (snapPosition != nSnapPosition) {
                    nSnapPosition = snapPosition;
                    mSnapPosition = snapPosition;
                    Calendar currentMonthCalendar = adapter.getFirstDayOfMonth(snapPosition);
                    int currentMonth = currentMonthCalendar.get(Calendar.MONTH);
                    int currentYear = currentMonthCalendar.get(Calendar.YEAR);
                    Calendar endCalendar = adapter.getEndCalendar();
                    Calendar startCalendar = adapter.getStartCalendar();
                    nextView.setVisibility(currentMonth == endCalendar.get(Calendar.MONTH) && currentYear == endCalendar.get(Calendar.YEAR) ? GONE : VISIBLE);
                    prevView.setVisibility(currentMonth == startCalendar.get(Calendar.MONTH) && currentYear == startCalendar.get(Calendar.YEAR) ? GONE : VISIBLE);

                    monthView.setText(String.format("%tB %s", currentMonthCalendar, currentYear != Calendar.getInstance().get(Calendar.YEAR) ? currentYear : currentYear).trim());
                    month = currentMonth + 1;
                    year = currentYear;
                    Log.d("rrr0",String.valueOf(month));
                    Log.d("rrr0",String.valueOf(year));
                    Log.d("rrr0",String.valueOf(snapPosition));
                }
            }
        });

        nextView.setOnClickListener(this);
        prevView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                mSnapHelper.next();
                break;
            case R.id.prev:
                mSnapHelper.prev();
                break;
        }
    }

    public static int getSnapPosition() {
        return mSnapPosition;
    }

    public static int getMonth() {
        return month;
    }


}
