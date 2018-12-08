package com.calender.filofax.filofax;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter;
import com.calender.filofax.filofax.CalenderMonthView.CalendarSnapHelper;
import com.calender.filofax.filofax.Fragments.ExpenditureBottomSheetFragment;
import com.calender.filofax.filofax.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.MONTH_PER_YEAR;
import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.TYPE_DAY;
import static com.calender.filofax.filofax.CalenderMonthView.CalendarAdapter.TYPE_TITLE;

public class CalenderActivity extends AppCompatActivity implements View.OnClickListener {

    private CalendarSnapHelper mSnapHelper;
    private static int month;
    private static int year;
    private ExpenditureBottomSheetFragment expenditureBottomSheetFragment;

    @BindView(R.id.month)
    TextView monthView;
    @BindView(R.id.next)
    View nextView;
    @BindView(R.id.prev)
    View prevView;
    @BindView(R.id.days)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setUI(this);
    }
    void setUI(final Context context) {

        findViewById(R.id.expenditures_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expenditureBottomSheetFragment = new ExpenditureBottomSheetFragment();
                expenditureBottomSheetFragment.setExpenditureBottomSheetFragment(expenditureBottomSheetFragment);
                expenditureBottomSheetFragment.show(getSupportFragmentManager(), expenditureBottomSheetFragment.getTag());
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
                    Calendar currentMonthCalendar = adapter.getFirstDayOfMonth(snapPosition);
                    int currentMonth = currentMonthCalendar.get(Calendar.MONTH);
                    int currentYear = currentMonthCalendar.get(Calendar.YEAR);
                    Calendar endCalendar = adapter.getEndCalendar();
                    Calendar startCalendar = adapter.getStartCalendar();
                    nextView.setVisibility(currentMonth == endCalendar.get(Calendar.MONTH) && currentYear == endCalendar.get(Calendar.YEAR) ? View.GONE : View.VISIBLE);
                    prevView.setVisibility(currentMonth == startCalendar.get(Calendar.MONTH) && currentYear == startCalendar.get(Calendar.YEAR) ? View.GONE : View.VISIBLE);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_monthlyView) {

            return true;
        }else if (id == R.id.action_weeklyView) {

            return true;
        }else if (id == R.id.action_signOut) {
            FirebaseAuth.getInstance().signOut();
            finish();
            overridePendingTransition(R.anim.slide_from_left , R.anim.slide_to_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
