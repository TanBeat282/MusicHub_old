package com.example.musichub.model.chart.chart_home;

import java.io.Serializable;

public class Data implements Serializable {
    private RTCharts RTChart;

    public RTCharts getRTChart() {
        return RTChart;
    }

    public void setRTChart(RTCharts RTChart) {
        this.RTChart = RTChart;
    }
}

