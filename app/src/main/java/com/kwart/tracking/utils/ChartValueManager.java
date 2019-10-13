package com.kwart.tracking.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.kwart.tracking.R;
import com.kwart.tracking.utils.workout.WorkoutRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChartValueManager {
    private Context context;
    private ArrayList<Entry> values;
    private LineChart lineChart;


    public ChartValueManager(Context context, LineChart lineChart) {
        this.values = new ArrayList<>();
        this.context = context;
        this.lineChart = lineChart;
    }

    public void initChart(float maxValue, float minValue, List<String> xDescriptionList, int backgroundColor, int axisTextColor){
        lineChart.setBackgroundColor(backgroundColor);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(axisTextColor);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xDescriptionList));


        YAxis yAxis = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);
        yAxis.enableGridDashedLine(0f, 0f, 0f);
        yAxis.setAxisMaximum(maxValue);
        yAxis.setAxisMinimum(minValue);
        yAxis.setTextColor(axisTextColor);
        lineChart.animateX(2000);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    public void setValues(List<Float> valuesList, int lineColor) {
        int count = 0;
        for(float val:valuesList){
            values.add(new Entry(count, val, context.getResources().getDrawable(R.drawable.dot)));
            count++;
        }

        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, " ");

            set1.setDrawIcons(false);

            // draw dashed line
            set1.enableDashedLine(0f, 0f, 0f);

            // black lines and points
            set1.setColor(lineColor);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(2f);
            set1.setDrawCircles(false);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            // text size of values
            set1.setValueTextSize(0f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            lineChart.setData(data);
            lineChart.invalidate();
        }

    }


    private class XAxisFormatter extends IndexAxisValueFormatter {
        private List<String> strings;

        XAxisFormatter(List<String> set){
            this.strings = set;

        }



    }
}
