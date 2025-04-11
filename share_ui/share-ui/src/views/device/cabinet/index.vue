<template>
    <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="机柜编号" prop="cabinetNo">
          <el-input
            v-model="queryParams.cabinetNo"
            placeholder="请输入机柜编号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="分类" prop="cabinetTypeId">
          <el-select
              v-model="queryParams.cabinetTypeId"
              class="m-2"
              placeholder="选择分类"
              size="small"
              style="width: 100%"
          >
            <el-option
                v-for="item in cabinetTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
  
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
  
      <el-table v-loading="loading" :data="cabinetList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="机柜编号" prop="cabinetNo" width="120"/>
        <el-table-column label="名称" prop="name" width="120"/>
        <el-table-column label="分类" prop="cabinetTypeName" width="120"/>
        <el-table-column label="总插槽数" prop="totalSlots" width="100"/>
        <el-table-column label="空闲插槽数" prop="freeSlots" width="100"/>
        <el-table-column label="可用数量" prop="availableNum" width="100"/>
        <el-table-column label="描述" prop="description" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            {{ scope.row.status == '0' ? '未投放' : scope.row.status == '1' ? '使用中' : '故障' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
          <template #default="scope">
            <el-button link type="primary" icon="Info" @click="handleInfo(scope.row.id)">详情</el-button>
            <el-button link type="primary" v-if="scope.row.status == '0'" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button link type="primary" v-if="scope.row.status == '0'" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
  
      <!-- 添加或修改充电宝柜机对话框 -->
      <el-dialog :title="title" v-model="open" width="600px" append-to-body>
        <el-form ref="cabinetRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入名称" />
          </el-form-item>
          <el-form-item label="编号" prop="cabinetNo">
            <el-input v-model="form.cabinetNo" placeholder="请输入柜机编号" />
          </el-form-item>
          <el-form-item label="分类" prop="cabinetTypeId">
            <el-select
                v-model="form.cabinetTypeId"
                class="m-2"
                placeholder="分类"
            >
              <el-option
                  v-for="item in cabinetTypeList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
      <!-- 添加或修改充电宝柜机对话框 -->
      <el-dialog title="柜机详细信息" v-model="openInfo" width="50%" append-to-body>
        <el-descriptions title="柜机信息">
          <el-descriptions-item label="机柜编号">{{ cabinet.cabinetNo }}</el-descriptions-item>
          <el-descriptions-item label="机柜名称">{{ cabinet.name }}</el-descriptions-item>
          <el-descriptions-item label="总插槽数量">{{ cabinet.totalSlots }}</el-descriptions-item>
          <el-descriptions-item label="空闲插槽量">{{ cabinet.freeSlots }}</el-descriptions-item>
          <el-descriptions-item label="已使用插槽">{{ cabinet.usedSlots }}</el-descriptions-item>
          <el-descriptions-item label="可用数量">{{ cabinet.availableNum }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ cabinet.description }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ cabinet.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-descriptions title="柜机插槽信息"></el-descriptions>
        <el-table :data="cabinetSlotList" style="width: 100%;">
          <el-table-column label="插槽编号" prop="slotNo" width="120"/>
          <el-table-column label="使用状态" prop="status" width="100">
            <template #default="scope">
              {{ scope.row.status == '1' ? '占用' : '空闲' }}
            </template>
          </el-table-column>
          <el-table-column label="充电宝标号" prop="powerBank.powerBankNo" width="130"/>
          <el-table-column label="充电宝电量(%)" prop="powerBank.electricity" width="130"/>
          <el-table-column label="创建时间" align="center" prop="createTime">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
  
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancelInfo">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup name="Cabinet">
  import { listCabinet, getCabinet, delCabinet, addCabinet, updateCabinet, getAllInfo } from "@/api/device/cabinet";
  import { getCabinetTypeList } from "@/api/device/cabinetType";
  
  const { proxy } = getCurrentInstance();
  
  const cabinetList = ref([]);
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
      cabinetNo: null,
      name: null,
      cabinetTypeId: null,
      totalSlots: null,
      availableSlots: null,
      description: null,
      locationId: null,
      status: null,
    },
    rules: {
      cabinetTypeId: [
        { required: true, message: "分类必须选择", trigger: "blur" }
      ],
      name: [
        { required: true, message: "名称不能为空", trigger: "blur" }
      ],
      cabinetNo: [
        { required: true, message: "柜机编号不能为空", trigger: "blur" }
      ],
    }
  });
  
  const { queryParams, form, rules } = toRefs(data);
  
  /** 查询充电宝柜机列表 */
  function getList() {
    loading.value = true;
    listCabinet(queryParams.value).then(response => {
      cabinetList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    });
  }
  
  //分类
  const cabinetTypeList = ref([])
  function getCabinetTypeAllList() {
    getCabinetTypeList().then(response => {
      cabinetTypeList.value = response.data
    })
  }
  getCabinetTypeAllList();
  
  // 取消按钮
  function cancel() {
    open.value = false;
    reset();
  }
  
  // 表单重置
  function reset() {
    form.value = {
      id: null,
      cabinetNo: null,
      name: null,
      cabinetTypeId: null,
      totalSlots: null,
      availableSlots: null,
      description: null,
      locationId: null,
      status: null,
      delFlag: null,
      createBy: null,
      createTime: null,
      updateBy: null,
      updateTime: null,
      remark: null
    };
    proxy.resetForm("cabinetRef");
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
  
  // 多选框选中数据
  function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id);
    single.value = selection.length != 1;
    multiple.value = !selection.length;
  }
  
  /** 新增按钮操作 */
  function handleAdd() {
    reset();
    open.value = true;
    title.value = "添加充电宝柜机";
  }
  
  /** 修改按钮操作 */
  function handleUpdate(row) {
    reset();
    const _id = row.id || ids.value
    getCabinet(_id).then(response => {
      form.value = response.data;
      open.value = true;
      title.value = "修改充电宝柜机";
    });
  }
  
  /** 提交按钮 */
  function submitForm() {
    proxy.$refs["cabinetRef"].validate(valid => {
      if (valid) {
        if (form.value.id != null) {
          updateCabinet(form.value).then(response => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addCabinet(form.value).then(response => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }
  
  const cabinet = ref({});
  const cabinetSlotList = ref([]);
  const openInfo = ref(false);
  function handleInfo(id) {
    getAllInfo(id).then(response => {
      cabinet.value = response.data.cabinet;
      cabinetSlotList.value = response.data.cabinetSlotList;
      openInfo.value = true;
    });
  }
  
  function cancelInfo() {
    openInfo.value = false;
  }
  
  /** 删除按钮操作 */
  function handleDelete(row) {
    const _ids = row.id || ids.value;
    proxy.$modal.confirm('是否确认删除充电宝柜机编号为"' + _ids + '"的数据项？').then(function() {
      return delCabinet(_ids);
    }).then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    }).catch(() => {});
  }
  
  getList();
  </script>