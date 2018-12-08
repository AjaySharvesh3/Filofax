package com.calender.filofax.filofax.CalenderMonthView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.calender.filofax.filofax.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.calender.filofax.filofax.CalenderMonthView.CalendarSnapHelper.ITEM_PER_MONTH;

class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int MONTH_PER_YEAR = 12;
    static final int TYPE_TITLE = 0;
    static final int TYPE_DAY = 1;
    private static final int DAY_PER_WEEK = 7;
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    private int[] mCalendarMatrix = new int[49];
    private int mItemWidth;
    private Context context;

    CalendarAdapter() {
        int[][] matrix = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                matrix[i][j] = i * 7 + j;
            }
        }

        // rotate -90
        int[][] rotateMatrix = new int[7][7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                rotateMatrix[i][j] = matrix[j][6 - i];
            }
        }

        // translate y + 1
        for (int i = 0; i < 7; i++) {
            System.arraycopy(rotateMatrix[i], 0, mCalendarMatrix, i * 7 + 1, 6);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % DAY_PER_WEEK == 0 ? TYPE_TITLE : TYPE_DAY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        switch (viewType) {
            case TYPE_TITLE:
                View titleView = LayoutInflater.from(context).inflate(R.layout.title_view_holder, parent, false);
                titleView.getLayoutParams().width = mItemWidth;
                return new CalendarAdapter.TitleViewHolder(titleView);
            case TYPE_DAY:
            default:
                View itemView = LayoutInflater.from(context).inflate(R.layout.day_view_holder, parent, false);
                itemView.getLayoutParams().width = mItemWidth;
                return new DayViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_TITLE:
                String title = "";
                switch (position / DAY_PER_WEEK % DAY_PER_WEEK) {
                    case 0:
                        title = "S";
                        break;
                    case 1:
                        title = "F";
                        break;
                    case 2:
                        title = "T";
                        break;
                    case 3:
                        title = "W";
                        break;
                    case 4:
                        title = "T";
                        break;
                    case 5:
                        title = "M";
                        break;
                    case 6:
                        title = "S";
                        break;
                }
                ((TitleViewHolder) holder).title.setText(title);
                break;
            case TYPE_DAY:
                Calendar calendar = getFirstDayOfMonth(position);

                int firstDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                int dayPosition = mCalendarMatrix[position % ITEM_PER_MONTH];
                if (dayPosition >= firstDayIndex && dayPosition < calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + firstDayIndex) {
                    ((DayViewHolder) holder).day.setVisibility(View.VISIBLE);
                    int date = calendar.get(Calendar.DAY_OF_MONTH) + dayPosition - firstDayIndex;
                    ((DayViewHolder) holder).day.setText(String.valueOf(date));

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

                    //Getting date from device
                    Date dateS = Calendar.getInstance().getTime();
                    String deviceDate = df.format(dateS);


                    int snapPosition = CalendarSnapHelper.getSnapPosition();
                    if (snapPosition == -1) {
                        snapPosition = CalendarSnapHelper.getSnapPosition();
                    }
                    Calendar currentMonthCalendar = getFirstDayOfMonth(snapPosition);
                    int currentMonth = currentMonthCalendar.get(Calendar.MONTH)+1;
                    int currentYear = currentMonthCalendar.get(Calendar.YEAR);
                    currentMonthCalendar.set(Calendar.DATE,date);
                    Date dateC =  currentMonthCalendar.getTime();
                    //Toast.makeText(context, "Month: " + currentMonth + " Year: " + currentYear, Toast.LENGTH_SHORT).show();
                    Log.d("rrr1",String.valueOf( date));
                    Log.d("rrr1",String.valueOf(currentMonth));
                    Log.d("rrr1",String.valueOf( currentYear) + "\n");
                    String calendarDate = df.format(dateC);

                    if (deviceDate.equals(calendarDate)){
                        ((DayViewHolder) holder).iv_dayselector.setVisibility(View.VISIBLE);
                        Toast.makeText(context, deviceDate, Toast.LENGTH_SHORT).show();
                        Log.d("rrr1","calendarDate = " +calendarDate);
                        Log.d("rrr1","deviceDate = " +deviceDate);
                    }else {
                        ((DayViewHolder) holder).iv_dayselector.setVisibility(View.INVISIBLE);
                    }
                } else {
                    ((DayViewHolder) holder).day.setVisibility(View.INVISIBLE);
                    ((DayViewHolder) holder).iv_dayselector.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mStartCalendar == null || mEndCalendar == null) return 0;
        return ((mEndCalendar.get(Calendar.YEAR) - mStartCalendar.get(Calendar.YEAR)) * MONTH_PER_YEAR + mEndCalendar.get(Calendar.MONTH) - mStartCalendar.get(Calendar.MONTH) + 1) * ITEM_PER_MONTH;
    }

    Calendar getFirstDayOfMonth(int position) {
        Calendar calendar = (Calendar) mEndCalendar.clone();
        int monthDistance = -position / ITEM_PER_MONTH;
        calendar.add(Calendar.YEAR, monthDistance / MONTH_PER_YEAR);
        calendar.add(Calendar.MONTH, monthDistance % MONTH_PER_YEAR);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

    Calendar getStartCalendar() {
        return mStartCalendar;
    }

    void setStartCalendar(Calendar startCalendar) {
        mStartCalendar = startCalendar;
    }

    Calendar getEndCalendar() {
        return mEndCalendar;
    }

    void setEndCalendar(Calendar endCalendar) {
        mEndCalendar = endCalendar;
    }

    void setDimensions(int itemWidth) {
        mItemWidth = itemWidth;
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView;
        }
    }

    private static class DayViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        ImageView iv_dayselector;

        DayViewHolder(View itemView) {
            super(itemView);
            day = (TextView) itemView.findViewById(R.id.tv_date);
            iv_dayselector =   itemView.findViewById(R.id.view_date_selector);
        }
    }
}
