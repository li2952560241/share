<template>
  <div class="app-container">

    <!-- 搜索表单 -->
    <el-form ref="queryRef" :inline="true" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
            v-model="queryParams.name"
            placeholder="请输入名称"
            clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 功能按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['device:cabinetType:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            @click="handleDelete"
        >删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-loading="loading" :data="cabinetTypeList" 
          @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="名称" prop="name" width="150"/>
      <el-table-column label="总插槽数量" prop="totalSlots" width="110"/>
      <el-table-column label="描述" prop="description" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope">
          {{ scope.row.status == '1' ? '正常' : '停用' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页条组件 -->
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="cabinetTypeRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="总插槽数量" prop="totalSlots">
          <el-select
              v-model="form.totalSlots"
              class="m-2"
              placeholder="请选择总插槽数量"
              style="width: 100%"
          >
            <el-option
                v-for="item in 20"
                :key="item"
                :label="item"
                :value="item"
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

  </div>
</template>
  
<script setup name="CabinetType">
  //引入api接口
 import { listCabinetType,addCabinetType,getCabinetType,updateCabinetType,delCabinetType } from "@/api/device/cabinetType";
 //引入ElMessage组件
 import {ElMessage,ElMessageBox} from "element-plus";
  //定义分页列表数据模型
  const cabinetTypeList = ref([]);
  //定义列表总记录数模型
  const total = ref(0);
  //数据加载使用图标
  const loading = ref(true);
  //弹框
  const open = ref(false)
  //弹出框页面显示名称
  const title = ref("")
  //定义分页参数和条件数据
  const data = reactive({
      //定义搜索模型
    queryParams: {
      pageNum: 1,//当前页
      pageSize: 2, //每页记录数
      name: null
    },
    //封装表单数据
    form:{}
  })
  //转换普通对象
  const { queryParams,form } = toRefs(data);
  //定义批量操作id列表模型
  const ids = ref([]);
  //定义单选控制模型
  const single = ref(true);
  //定义多选控制模型
  const multiple = ref(true);

  ///////////////////==删除==////////////////////////////////////
  function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.id);
    // single.value = selection.length != 1;
    // multiple.value = !selection.length;

  }
  function handleDelete(row) {
    const cid = row.id || ids.value
    ElMessageBox.confirm('是否确认删除柜机类型数据项？', "系统提示", {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: "warning",
    }).then(function() {
      return delCabinetType(cid);
    }).then(() => {
      getList();
      ElMessage.success("删除成功");
    });
  }

  ///////////////////==修改==////////////////////////////////////
  //根据id查询数据回显
  function handleUpdate(row) {
    reset()
    //获取修改数据id值
    const cid = row.id
    //调用方法
    getCabinetType(cid).then(response => {
      //得到接口返回数据
      form.value = response.data
      //弹框
      open.value = true
      title.value = "修改柜机类型";
    })
  }

  ///////////////////==添加==////////////////////////////////////
  function handleAdd() {
    reset();
    open.value = true //弹出框
    title.value = "添加柜机类型"
  }

  //添加方法
  function submitForm() {
    //判断添加还是修改
    if(form.value.id != null) {//修改
      updateCabinetType(form.value).then(response => {
        //提示
        ElMessage.success("修改成功")
        //关闭弹框
        open.value = false
        //刷新页面
        getList()
      })
    } else { //添加
      addCabinetType(form.value).then(response => {
        //提示
        ElMessage.success("新增成功")
        //关闭弹框
        open.value = false
        //刷新页面
        getList()
      })
    }

  }

  // 表单重置
  function reset() {
    form.value = {
      id: null,
      name: null,
      totalSlots: null,
      description: null,
      status: null,
      remark: null
    };
  }

  // 取消按钮
  function cancel() {
    open.value = false;
    reset();
  }

  ////////////////////==分页列表==///////////////////////////////////
  //分页列表调用
  function getList() {
    listCabinetType(queryParams.value).then(response => {
      cabinetTypeList.value = response.rows;
      total.value = response.total;
      loading.value = false;
    })
  }

  //搜索
  function handleQuery() {
    getList()
  }

  //重置
  function resetQuery() {
    queryParams.value.pageNum = 1
    queryParams.value.pageSize = 2
    queryParams.value.name = null
    handleQuery();
  }

  //执行查询柜机类型列表
  getList()


</script>