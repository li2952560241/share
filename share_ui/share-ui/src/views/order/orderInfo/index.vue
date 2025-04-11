<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
            v-model="queryParams.orderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-select
            v-model="queryParams.orderStatus"
            class="m-2"
            placeholder="订单状态"
            style="width: 100%"
        >
          <el-option
              v-for="item in orderStatusList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker
            v-model="dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['order:orderInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderInfoList" @selection-change="handleSelectionChange">
      <el-table-column label="订单号" prop="orderNo" width="100"/>
      <el-table-column label="用户" prop="userInfoVo.nickname" width="100"/>
      <el-table-column label="借用开始时间" prop="startTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="借用地点名称" prop="startStationName" />
      <el-table-column label="归还时间" prop="endTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="归还地点名称" prop="endStationName" />
      <el-table-column label="借用时长(分钟)" prop="duration" />
      <el-table-column label="总金额(元)" prop="totalAmount" />
      <el-table-column label="订单状态" prop="status">
        <template #default="scope">
          {{ scope.row.status == '0' ? '充电中' : scope.row.status == '1' ? '未支付' : '已完成' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleShow(scope.row.id)" v-hasPermi="['order:orderInfo:query']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改订单对话框 -->
    <el-dialog :title="title" v-model="open" width="50%" append-to-body>
      <el-form ref="orderInfoRef" :model="form"  label-width="120px">
        <el-divider />
        <span style="margin-bottom: 5px;font-weight:bold;">订单基本信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单号">
              {{ form.orderNo }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="充电宝编号">
              {{ form.powerBankNo }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="借用开始时间">
              {{ form.startTime }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="借用地点名称">
              {{ form.startStationName }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="归还时间">
              {{ form.endTime }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归还地点名称">
              {{ form.endStationName }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="借用时长">
              {{ form.duration }}分钟
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额">
              {{ form.totalAmount }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单状态">
              {{ form.status == '0' ? '充电中' : form.status == '1' ? '未支付' : '已支付' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用规则">
              {{ form.feeRule }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="创建时间">
              {{ form.createTime }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付时间">
              {{ form.payTime }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />
        <span style="margin-bottom: 10px;font-weight:bold;">账单信息</span>
        <el-table :data="form.orderBillList" border>
          <el-table-column label="费用项目" prop="billItem"/>
          <el-table-column label="金额" prop="billAmount"/>
        </el-table>

        <el-divider />
        <span style="margin-bottom: 5px;font-weight:bold;">用户信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="昵称">
              {{ form.userInfoVo.nickname }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="微信标识">
              {{ form.userInfoVo.wxOpenId }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="注册时间">
              {{ form.userInfoVo.createTime }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最近登录时间">
              {{ form.userInfoVo.lastLoginTime }}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="OrderInfo">
import { listOrderInfo, getOrderInfo } from "@/api/order/orderInfo";

const { proxy } = getCurrentInstance();

const orderInfoList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: null,
    orderNo: null,
    startTime: null,
    startStationId: null,
    startStationName: null,
    endTime: null,
    endStationId: null,
    endStationName: null,
    duration: null,
    feeRuleId: null,
    totalAmount: null,
    deductAmount: null,
    realAmount: null,
    payTime: null,
    transactionId: null,
    status: null,
  },
});

const orderStatusList = ref([
  { id: 0, name: '充电中' },
  { id: 1, name: '未支付' },
  { id: 2, name: '已完成' },
])

const { queryParams, form } = toRefs(data);
const dateRange = ref([]);

/** 查询订单列表 */
function getList() {
  loading.value = true;
  listOrderInfo(queryParams.value).then(response => {
    orderInfoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}


/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 修改按钮操作 */
function handleShow(id) {
  getOrderInfo(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改订单";
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('order/orderInfo/export', {
    ...queryParams.value
  }, `orderInfo_${new Date().getTime()}.xlsx`)
}

getList();
</script>
