<template>
    <div class="app-container">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="站点名称" prop="name">
          <el-input
              v-model="queryParams.name"
              placeholder="请输入站点名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
              v-model="queryParams.address"
              placeholder="请输入地址"
              clearable
              @keyup.enter="handleQuery"
          />
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
  
      <el-table v-loading="loading" :data="stationList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="imageUrl" label="站点图片" #default="scope" width="80">
          <img :src="scope.row.imageUrl" width="60" />
        </el-table-column>
        <el-table-column label="站点名称" prop="name" width="130"/>
        <el-table-column label="营业时间" prop="businessHours" width="130"/>
        <el-table-column label="详细地址" prop="fullAddress" />
        <el-table-column label="负责人名称" prop="headName" width="90"/>
        <el-table-column label="负责人电话" prop="headPhone" width="130"/>
        <el-table-column label="关联柜机" prop="cabinetNo" />
        <el-table-column label="费用规则" prop="feeRuleName" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            {{ scope.row.status == '1' ? '正常' : '停用' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="230">
          <template #default="scope">
            <el-button link type="primary" @click="handleCabinetShow(scope.row)" >详情</el-button>
            <el-button link type="primary" @click="handleUpdate(scope.row)" >修改</el-button>
            <el-button link type="primary" @click="handleDelete(scope.row)" >删除</el-button>
            <el-button link type="primary" @click="handSet(scope.row)" >设置柜机</el-button>
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
  
      <!-- 添加或修改站点对话框 -->
      <el-dialog :title="title" v-model="open" width="50%" append-to-body>
        <el-form ref="stationRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="站点名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入站点名称" />
          </el-form-item>
          <el-form-item label="站点图片" prop="logo">
            <el-upload
                class="avatar-uploader"
                :action="imgUpload.url"
                :headers="imgUpload.headers"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
            >
              <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="营业时间" prop="businessHours">
            <el-input v-model="form.businessHours" placeholder="请输入营业时间" />
          </el-form-item>
          <el-form-item label="省市区" prop="categoryIdList">
            <el-cascader
                :props="regionProps"
                v-model="form.regionCodeList"
                style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label="详细地址" prop="address">
            <el-input v-model="form.address" placeholder="请输入详细地址"/>
          </el-form-item>
          <el-form-item label="设置经纬度">
            <el-input v-model="latLngString" disabled placeholder="选择经纬度" style="width: 70%"/>
            <el-button type="primary" icon="Search" @click="handShowMap">选择经纬度</el-button>
          </el-form-item>
          <el-form-item label="站点负责人名称" prop="headName">
            <el-input v-model="form.headName" placeholder="请输入站点负责人名称" />
          </el-form-item>
          <el-form-item label="站点负责人电话" prop="headPhone">
            <el-input v-model="form.headPhone" placeholder="请输入站点负责人电话" />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
      <el-dialog title="设置经纬度" v-model="openMap" width="50%" append-to-body>
        <div id="container"></div>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitMap">确 定</el-button>
            <el-button @click="openMap = false">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
      <el-dialog title="设置柜机数据" v-model="openSet" width="40%" append-to-body>
        <el-form ref="stationRef" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="柜机" prop="cabinetId">
            <el-autocomplete
                v-model="cabinetValue"
                :fetch-suggestions="querySearch"
                :trigger-on-focus="false"
                clearable
                class="inline-input w-50"
                style="width: 100%;"
                placeholder="请输入柜机编号"
                @select="handleSelect"
            />
          </el-form-item>
          <el-form-item label="费用规则" prop="feeRuleId">
            <el-select
                v-model="form.feeRuleId"
                class="m-2"
                placeholder="请选择费用规则"
                style="width: 100%;"
            >
              <el-option
                  v-for="item in feeRuleList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitSet">确 定</el-button>
            <el-button @click="openSet = false">取 消</el-button>
          </div>
        </template>
      </el-dialog>
  
      <el-dialog title="柜机详细信息" v-model="openCabinet" width="70%" append-to-body>
        <el-descriptions title="站点信息">
          <el-descriptions-item label="站点图片"><img :src="form.imageUrl" width="60" /></el-descriptions-item>
          <el-descriptions-item label="站点名称">{{ form.name }}</el-descriptions-item>
          <el-descriptions-item label="营业时间">{{ form.businessHours }}</el-descriptions-item>
          <el-descriptions-item label="详细地址">{{ form.fullAddress }}</el-descriptions-item>
          <el-descriptions-item label="站点负责人">{{ form.headName }}</el-descriptions-item>
          <el-descriptions-item label="负责人电话">{{ form.headPhone }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ form.createTime }}</el-descriptions-item>
        </el-descriptions>
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
            <el-button @click="cancelCabinet">取 消</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup name="Station">
  import { listStation, getStation, delStation, addStation, updateStation, setData } from "@/api/device/station";
  import { searchNoUseList } from "@/api/device/cabinet";
  import { getTreeSelect } from "@/api/device/region";
  import { getALLFeeRuleList } from "@/api/rule/feeRule";
  import { getToken } from "@/utils/auth.js";
  const { proxy } = getCurrentInstance();
  
  const stationList = ref([]);
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
      name: null,
      imageUrl: null,
      businessHours: null,
      provinceCode: null,
      cityCode: null,
      districtCode: null,
      longitude: null,
      latitude: null,
      address: null,
      headName: null,
      headPhone: null,
      cabinetId: null,
      feeRuleId: null,
      status: null,
    },
    rules: {
      name: [
        { required: true, message: "站点名称不能为空", trigger: "blur" }
      ],
      longitude: [
        { required: true, message: "经度不能为空", trigger: "blur" }
      ],
      latitude: [
        { required: true, message: "纬度不能为空", trigger: "blur" }
      ],
      createTime: [
        { required: true, message: "创建时间不能为空", trigger: "blur" }
      ],
      cabinetId: [
        { required: true, message: "柜机编号必须输入", trigger: "blur" }
      ],
      feeRuleId: [
        { required: true, message: "费用规则必须选择", trigger: "blur" }
      ],
    },
    imgUpload: {
      // 设置上传的请求头部
      headers: {
        Authorization: "Bearer " + getToken()
      },
      // 图片上传的方法地址:
      url: import.meta.env.VITE_APP_BASE_API + "/file/upload"
    },
  });
  
  const { queryParams, form, rules, imgUpload } = toRefs(data);
  
  /** 查询站点列表 */
  function getList() {
    loading.value = true;
    listStation(queryParams.value).then(response => {
      stationList.value = response.rows;
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
  function reset() {
    form.value = {
      id: null,
      name: null,
      imageUrl: null,
      businessHours: null,
      provinceCode: null,
      cityCode: null,
      districtCode: null,
      longitude: null,
      latitude: null,
      address: null,
      headName: null,
      headPhone: null,
      cabinetId: null,
      feeRuleId: null,
      status: null,
      regionCodeList: []
    };
    proxy.resetForm("stationRef");
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
    title.value = "添加站点";
  }
  
  /** 修改按钮操作 */
  function handleUpdate(row) {
    reset();
    const _id = row.id || ids.value
    getStation(_id).then(response => {
      form.value = response.data;
      form.value.regionCodeList = [form.value.provinceCode, form.value.cityCode, form.value.districtCode];
      open.value = true;
      title.value = "修改站点";
  
      latLngString.value = form.value.latitude + "," + form.value.longitude;
    });
  }
  
  /** 提交按钮 */
  function submitForm() {
    proxy.$refs["stationRef"].validate(valid => {
      if (valid) {
        form.value.provinceCode = form.value.regionCodeList[0];
        form.value.cityCode = form.value.regionCodeList[1];
        form.value.districtCode = form.value.regionCodeList[2];
  
        if (form.value.id != null) {
          updateStation(form.value).then(response => {
            proxy.$modal.msgSuccess("修改成功");
            open.value = false;
            getList();
          });
        } else {
          addStation(form.value).then(response => {
            proxy.$modal.msgSuccess("新增成功");
            open.value = false;
            getList();
          });
        }
      }
    });
  }
  
  /** 删除按钮操作 */
  function handleDelete(row) {
    const _ids = row.id || ids.value;
    proxy.$modal.confirm('是否确认删除站点编号为"' + _ids + '"的数据项？').then(function() {
      return delStation(_ids);
    }).then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    }).catch(() => {});
  }
  
  /** 导出按钮操作 */
  function handleExport() {
    proxy.download('device/station/export', {
      ...queryParams.value
    }, `station_${new Date().getTime()}.xlsx`)
  }
  
  getList();
  
  //上传
  function handleAvatarSuccess (response, uploadFile) {
    console.log(response)
    console.log(uploadFile)
    form.value.logo = response.data.url
  }
  
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
  
  
  const openMap = ref(false);
  const isFirst = ref(true);
  const latLng = ref({});
  const latLngString = ref();
  watch(openMap, (newVal) => {
    if (newVal && isFirst.value) {
      nextTick(initMap); // 确保DOM更新完成后初始化地图
    }
  });
  function handShowMap() {
    openMap.value = true
  }
  function initMap() {
    isFirst.value = false;
    var center = new TMap.LatLng(39.984104, 116.307503);
    //初始化地图
    var map = new TMap.Map(document.getElementById('container'), {
      rotation: 20,//设置地图旋转角度
      pitch:30, //设置俯仰角度（0~45）
      zoom:12,//设置地图缩放级别
      center: center//设置地图中心点坐标
    });
  
    //初始化marker图层
    var markerLayer = new TMap.MultiMarker({
      id: 'marker-layer',
      map: map
    });
  
    //监听点击事件添加marker
    map.on("click", (evt) => {
      markerLayer.setGeometries([])
      markerLayer.add({
        position: evt.latLng
      });
      latLng.value = evt.latLng;
      latLngString.value = latLng.value.lat + "," + latLng.value.lng;
      // calculateAddress(evt.latLng.lat, evt.latLng.lng).then(response => {
      //   mapAddress.value = response.data;
      // });
      console.log("您点击的的坐标是："+ evt.latLng);
    });
  }
  
  function submitMap() {
    openMap.value = false
    form.value.latitude = latLng.value.lat;
    form.value.longitude = latLng.value.lng;
  }
  
  const openSet = ref(false);
  const cabinetValue = ref('');
  function handSet(row) {
    openSet.value = true
    form.value.id = row.id
    form.value.cabinetId = row.cabinetId
    form.value.feeRuleId = row.feeRuleId
    cabinetValue.value = row.cabinetNo
  }
  function querySearch(keyword, cb) {
    searchNoUseList(keyword).then(response => {
      const list = response.data
      const array = list.map(item=>{
        return {
          value: item.cabinetNo,
          id: item.id
        }
      })
      debugger
      cb(array)
    });
  }
  function handleSelect(item) {
    console.log(item)
    form.value.cabinetId = item.id
  }
  
  const feeRuleList = ref([]);
  function getFeeRuleList() {
    getALLFeeRuleList().then(response => {
      feeRuleList.value = response.data
    })
  }
  getFeeRuleList()
  
  function submitSet() {
    setData(form.value).then(response => {
      proxy.$modal.msgSuccess("关联设置成功");
      openSet.value = false;
      getList();
    })
  }
  
  import { getAllInfo } from "@/api/device/cabinet";
  const openCabinet = ref(false);
  const cabinet = ref({});
  const cabinetSlotList = ref([]);
  function handleCabinetShow(row) {
    form.value = row;
    getAllInfo(row.id).then(response => {
      cabinet.value = response.data.cabinet;
      cabinetSlotList.value = response.data.cabinetSlotList;
      openCabinet.value = true;
    });
  }
  
  function cancelCabinet() {
    openCabinet.value = false;
  }
  </script>
  <style scoped>
  .avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
  </style>
  
  <style>
  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }
  
  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }
  
  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
  }
  </style>