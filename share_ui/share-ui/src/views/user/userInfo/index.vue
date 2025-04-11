<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
          v-hasPermi="['user:userInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userInfoList" @selection-change="handleSelectionChange">
      <el-table-column label="微信openId" prop="wxOpenId" />
      <el-table-column label="会员昵称" prop="nickname" width="120"/>
      <el-table-column prop="lastLoginTime" label="最后一次登录时间" width="180"/>
      <el-table-column prop="status" label="状态" #default="scope" width="100">
        {{ scope.row.status == 1 ? '正常' : '停用' }}
      </el-table-column>
      <el-table-column prop="depositStatus" label="认证状态" #default="scope" width="100">
        {{ scope.row.depositStatus == 0 ? '未认证' : scope.row.depositStatus == 1 ? '免押金' : '已交押金' }}
      </el-table-column>
      <el-table-column label="注册时间" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template #default="scope">
          <el-button link type="primary" @click="handleShow(scope.row.id)" v-hasPermi="['user:userInfo:query']">详情</el-button>
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

    <!-- 添加或修改用户对话框 -->
    <el-dialog :title="title" v-model="open" width="70%" append-to-body>
      <el-form ref="userInfoRef" :model="form" label-width="140px">
        <el-divider />
        <span style="margin-bottom: 5px;">基本信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="头像">
              <img :src="form.avatarUrl" width="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称">
              {{ form.nickname }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="微信标识">
              {{ form.wxOpenId }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              {{ form.sex == 1 ? '女' : '男' }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最后一次登录ip">
              {{ form.lastLoginIp }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最后一次登录时间">
              {{ form.lastLoginTime }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">
              {{ form.status == 1 ? '正常' : '停用' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              {{ form.createTime }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="认证状态">
              {{ form.depositStatus == 0 ? '未认证' : form.depositStatus == 1 ? '免押金' : '已交押金' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">

          </el-col>
        </el-row>

        <el-divider />
        <span style="margin-bottom: 5px;">订单信息</span>
        <el-table :data="orderInfoList">
          <el-table-column label="订单号" prop="orderNo" width="90"/>
          <el-table-column label="借用开始时间" prop="startTime" width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.startTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="借用地点名称" prop="startStationName" />
          <el-table-column label="归还时间" prop="endTime" width="160">
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
        </el-table>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="UserInfo">
import { listUserInfo, getUserInfo } from "@/api/user/userInfo";
import { listUserOrderInfo } from "@/api/order/orderInfo";

const { proxy } = getCurrentInstance();

const userInfoList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    wxOpenId: null,
    nickname: null,
    gender: null,
    avatarUrl: null,
    phone: null,
    status: null,
  },
  rules: {
    wxOpenId: [
      { required: true, message: "微信openId不能为空", trigger: "blur" }
    ],
    gender: [
      { required: true, message: "性别不能为空", trigger: "blur" }
    ],
    createTime: [
      { required: true, message: "创建时间不能为空", trigger: "blur" }
    ],
    updateTime: [
      { required: true, message: "更新时间不能为空", trigger: "blur" }
    ],
    delFlag: [
      { required: true, message: "删除标志不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);
const dateRange = ref([]);

/** 查询用户列表 */
function getList() {
  loading.value = true;
  listUserInfo(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    userInfoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
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
  getUserInfo(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "详情";

    getUserOrderList(form.value.id);
  });
}

const orderInfoList = ref([]);
function getUserOrderList(id) {
  listUserOrderInfo(id).then(response => {
    orderInfoList.value = response.data;
  });
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('user/userInfo/export', {
    ...queryParams.value
  }, `userInfo_${new Date().getTime()}.xlsx`)
}

getList();
</script>
