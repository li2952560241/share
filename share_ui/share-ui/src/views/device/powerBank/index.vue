<template>
    <div class="app-container">
  
      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="编号" prop="powerBankNo">
          <el-input
              v-model="queryParams.powerBankNo"
              placeholder="请输入充电宝编号"
              clearable
              @keyup.enter="handleQuery"
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
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
  
      <!-- 数据展示表格 -->
      <el-table v-loading="loading" :data="powerBankList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" prop="powerBankNo" width="120"/>
        <el-table-column label="电量" prop="electricity" width="100">
          <template #default="scope">
            {{ scope.row.electricity }}%
          </template>
        </el-table-column>
        <el-table-column label="描述" prop="description" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            {{ scope.row.status == '0' ? '未投放' : scope.row.status == '1' ? '可用' : scope.row.status == '2' ? '已租用' : scope.row.status == '3' ? '充电中' : "故障" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
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

      <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="powerBankRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="编号" prop="powerBankNo">
          <el-input v-model="form.powerBankNo" placeholder="请输入充电宝编号" />
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
  
<script setup name="PowerBank">
    import {listPowerBank,addPowerBank,getPowerBank,updatePowerBank,delPowerBank} from "@/api/device/powerBank";

    const { proxy } = getCurrentInstance();

    //定义分页列表数据模型
    const powerBankList = ref([]);
    //定义列表总记录数模型
    const total = ref(0);
    const data = reactive({
        //定义搜索模型
        queryParams: {
            pageNum: 1,
            pageSize: 10,
            powerBankNo: null
        },
        form: {}
    });
    const { queryParams,form } = toRefs(data);

    //新增与修改弹出层标题模型
    const title = ref("");
    //新增与修改弹出层控制模型
    const open = ref(false);
    //定义批量操作id列表模型
    const ids = ref([]);
    //定义单选控制模型
    const single = ref(true);
    //定义多选控制模型
    const multiple = ref(true);
    // 多选框选中数据
    function handleSelectionChange(selection) {
        ids.value = selection.map(item => item.id);
    }

    // 删除按钮操作
    function handleDelete(row) {
        const _ids = row.id || ids.value;
        proxy.$modal.confirm('是否确认删除充电宝编号为"' + _ids + '"的数据项？').then(function() {
            return delPowerBank(_ids);
        }).then(() => {
            getList();
            proxy.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    }

    //===============添加===========================
    // 表单重置
    function reset() {
        form.value = {
            id: null,
            name: null
        };
        proxy.resetForm("productUnitRef");
    }
    //弹框
    function handleAdd() {
        open.value = true
        reset()
    }

    //确定方法
    function submitForm() {
        if(form.value.id != null) {
            //修改
            updatePowerBank(form.value).then(response => {
                proxy.$modal.msgSuccess("修改成功");
                open.value = false;
                getList();
            })

        } else { //添加
            addPowerBank(form.value).then(response => {
                //提示
                //关闭弹框
                //刷新页面
                proxy.$modal.msgSuccess("新增成功");
                open.value = false;
                getList();
            })
        }
    }
    //===============修改===============================
    function handleUpdate(row) {
        const pid = row.id
        getPowerBank(pid).then(response => {
            form.value = response.data;
            open.value = true;
            title.value = "修改商品单位";
        })
    }
    //===============分页列表===========================
    function getList() {
        listPowerBank(queryParams.value).then(response => {
            powerBankList.value = response.rows;
            total.value = response.total;
        })
    }

    /** 搜索按钮操作 */
    function handleQuery() {
        getList();
    }

    /** 重置按钮操作 */
    function resetQuery() {
        proxy.resetForm("queryRef");
        handleQuery();
    }

    //调用分页列表方法
    getList()

</script>