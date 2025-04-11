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
      <el-col :span="12">
        <div ref="districtToAmountChart" style="width: 100%; height: 500px;"></div>
      </el-col>
      <el-col :span="12">
        <div ref="districtToNumChart" style="width: 100%; height: 500px;"></div>
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
  provinceCode: '110000',
  cityCode: '110100',
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
  if (regionCodeList.value.length == 2) {
    searchObj.value.provinceCode = regionCodeList.value[0];
    searchObj.value.cityCode = regionCodeList.value[1];
  }
  const { code, data, message } = await API.getRegionOrderStatisticsData(
      searchObj.value
  )

  setDistrictToAmountChartOption(data.districtToAmountData)
  setDistrictToNumChartOption(data.districtToNumData)
}

//重置
const resetData = () => {
  searchObj.value = {}
  orderDates.value = []
  fetchData()
}

const districtToAmountChart = ref()
const setDistrictToAmountChartOption = (districtToAmountData) => {
  const myChart = echarts.init(districtToAmountChart.value)
  // 指定图表的配置项和数据
  const option = {
    title: {
      text: '区域站点对应订单金额统计',
      subtext: '区域',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: '50%',
        data: districtToAmountData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option)
}

const districtToNumChart = ref()
const setDistrictToNumChartOption = (districtToNumData) => {
  const myChart = echarts.init(districtToNumChart.value)
  // 指定图表的配置项和数据
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '区域站点对应订单数量统计',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 40,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: districtToNumData
      }
    ]
  };
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option)
}

import { getTreeSelect } from "@/api/device/region";
const regionCodeList = ref(['110000', '110100'])
//三级分类
const props = {
  lazy: true,
  value: 'code',
  label: 'name',
  leaf: 'leaf',
  async lazyLoad(node, resolve) {
    const { level } = node
    console.log('level:', level)
    if (typeof node.value == 'undefined') node.value = 0
    const { code, data, message } = await getTreeSelect(
        node.value
    )
    //hasChildren判断是否有子节点
    data.forEach(function(item) {
      item.leaf = !item.hasChildren
      if(level == 1) {
        item.leaf = true
      }
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
