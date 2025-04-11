<template>
  <div ref="chart" style="width: 600px; height: 400px;"></div>
</template>
 
<script>
import * as echarts from 'echarts';
import { getUserCount } from "@/api/sta/sta";

export default {
    data() {
        return {
            searchObj: {
                selectYear: ''
            },
            btnDisabled: false,
            chart: null,
            title: '',
            xData: [], // x轴数据
            yData: [] // y轴数据
        }
    },
    created() {
      this.showChart()
    },
    methods: {
        // 初始化图表数据
        showChart() {
          getUserCount().then(response => {
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
                    text: this.title + '用户注册统计'
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