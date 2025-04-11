<template>
  <div class="search-div">
    <el-form label-width="90px" size="small">
      <el-row>
        <el-col :span="10">
          <el-form-item label="区域">
            <el-cascader
                :props="regionProps"
                v-model="regionCodeList"
                style="width: 100%;"
            />
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="订单日期">
            <el-date-picker
                v-model="orderDates"
                type="daterange"
                range-separator="To"
                start-placeholder="开始日期"
                end-placeholder="截止日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </el-col>
        <el-col :span="1"></el-col>
        <el-col :span="3">
          <el-button type="primary" size="small" @click="fetchData()">
            搜索
          </el-button>
          <el-button size="small" @click="resetData">重置</el-button>
        </el-col>
      </el-row>
    </el-form>
  </div>
  <div style="padding: 10px; background-color: #ffffff;">
    <el-row>
      <el-col :span="24">
        <div ref="orderDateToAmountChart" style="width: 100%; height: 500px;"></div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="24">
        <div ref="orderDateToNumChart" style="width: 100%; height: 500px;"></div>
      </el-col>
    </el-row>
  </div>

</template>
<script setup>
import * as echarts from 'echarts'
import {onMounted, ref} from 'vue'
import * as API from '@/api/order/orderInfo'


const searchObj = ref({
  orderDateBegin: '',
  orderDateEnd: '',
  provinceCode: '',
  cityCode: '',
  districtCode: '',
})
const orderDates = ref([])

onMounted(async () => {
  fetchData()
})

const fetchData = async () => {
  if (orderDates.value.length == 2) {
    searchObj.value.orderDateBegin = orderDates.value[0]
    searchObj.value.orderDateEnd = orderDates.value[1]
  }
  if (regionCodeList.value.length == 3) {
    searchObj.value.provinceCode = regionCodeList.value[0];
    searchObj.value.cityCode = regionCodeList.value[1];
    searchObj.value.districtCode = regionCodeList.value[2];
  }
  const { code, data, message } = await API.getOrderStatisticsData(
      searchObj.value
  )

  setOrderDateToAmountChartOption(data.orderDateToAmountData.orderDateList, data.orderDateToAmountData.orderDateAmountList)
  setOrderDateToNumChartOption(data.orderDateToNumData.orderDateList, data.orderDateToNumData.orderDateNumList)
}

//重置
const resetData = () => {
  searchObj.value = {}
  orderDates.value = []
  fetchData()
}

const orderDateToAmountChart = ref()
const setOrderDateToAmountChartOption = (orderDateList, orderDateAmountList) => {
  const myChart = echarts.init(orderDateToAmountChart.value)
  // 指定图表的配置项和数据
  const option = {
    title: {
      text: '订单金额统计',
    },
    tooltip: {},
    legend: {
      data: ['订单总金额（元）'],
    },
    xAxis: {
      data: orderDateList,
    },
    yAxis: {},
    series: [
      {
        name: '订单总金额（元）',
        type: 'bar',
        data: orderDateAmountList,
      },
    ],
  }
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option)
}

const orderDateToNumChart = ref()
const setOrderDateToNumChartOption = (orderDateList, orderDateNumList) => {
  const myChart = echarts.init(orderDateToNumChart.value)
  // 指定图表的配置项和数据
  const option = {
    title: {
      text: '订单数量统计',
    },
    legend: {
      data: ['订单数量（笔）'],
    },
    xAxis: {
      type: 'category',
      data: orderDateList
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: orderDateNumList,
        type: 'line'
      }
    ]
  };
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option)
}

import { getTreeSelect } from "@/api/device/region";
const regionCodeList = ref([])
//三级分类
const props = {
  lazy: true,
  value: 'code',
  label: 'name',
  leaf: 'leaf',
  async lazyLoad(node, resolve) {
    const { level } = node
    if (typeof node.value == 'undefined') node.value = 0
    const { code, data, message } = await getTreeSelect(
        node.value
    )
    //hasChildren判断是否有子节点
    data.forEach(function(item) {
      item.leaf = !item.hasChildren
    })
    resolve(data)
  },
}
const regionProps = ref(props)
</script>
<style scoped>
.search-div {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  background-color: #fff;
}
</style>
