<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="100px">
      <el-form-item label="查询" prop="orderNo">
        <el-input
            v-model="searchObj.select"
            placeholder="请输入订单报表需求"
            clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="showChart">查询</el-button>
      </el-form-item>
    </el-form>
    <div ref="chart" style="width: 600px; height: 400px;"></div>
  </div>
</template>
 
<script>
import * as echarts from 'echarts';
import { getOrderCount } from "@/api/sta/sta";

export default {
    data() {
        return {
            searchObj: {
                select: ''
            },
            btnDisabled: false,
            chart: null,
            title: '',
            xData: [], // x轴数据
            yData: [] // y轴数据
        }
    },
    methods: {
        // 初始化图表数据
        showChart() {
          getOrderCount(this.searchObj.select).then(response => {
                this.yData = response.data.countList
                this.xData = response.data.dateList
                this.setChartData()
            })
        },
        
        setChartData() {
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(this.$refs.chart)
            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: this.title + '订单统计'
                },
                tooltip: {},
                legend: {
                    data: [this.title]
                },
                xAxis: {
                    data: this.xData
                },
                yAxis: {
                    minInterval: 1
                },
                series: [{
                    name: this.title,
                    type: 'bar',
                    data: this.yData
                }]
            }
            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option)
        },
    }
}
</script>