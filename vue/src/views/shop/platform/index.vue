<template>
  <div class="app-container">


    <el-row :gutter="10" class="mb8">

      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="shopList" >
<!--      <el-table-column type="selection" width="55" align="center" />-->
      <el-table-column label="ID" align="center" prop="id" width="100"/>
      <el-table-column label="平台" align="left" prop="name" />
<!--       <el-table-column label="AppKey" align="left" prop="appKey" />-->
<!--       <el-table-column label="AppSecret" align="left" prop="appSecret" />-->
<!--       <el-table-column label="回调URL" align="left" prop="redirectUri" />-->
      <el-table-column label="接口请求URL" align="left" prop="serverUrl" />
      <el-table-column label="是否开启" align="center" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
<!--      <el-table-column label="描述" align="center" prop="remark" />-->
<!--      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
<!--        <template slot-scope="scope">-->
<!--          <el-button-->
<!--            v-if="scope.row.id !== 500 && scope.row.id !== 999"-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit"-->
<!--            @click="handleUpdate(scope.row)"-->
<!--            v-hasPermi="['shop:shop:edit']"-->
<!--          >设置参数</el-button>-->

<!--&lt;!&ndash;          <el-button&ndash;&gt;-->
<!--&lt;!&ndash;            size="mini"&ndash;&gt;-->
<!--&lt;!&ndash;            type="text"&ndash;&gt;-->
<!--&lt;!&ndash;            icon="el-icon-location"&ndash;&gt;-->
<!--&lt;!&ndash;            @click="handleUpdate(scope.row)"&ndash;&gt;-->
<!--&lt;!&ndash;            v-hasPermi="['shop:shop:edit']"&ndash;&gt;-->
<!--&lt;!&ndash;          >省市区地址库</el-button>&ndash;&gt;-->
<!--        </template>-->
<!--      </el-table-column>-->
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改店铺对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="平台名" prop="name">
          <el-input v-model="form.name" disabled placeholder="请输入平台名" />
        </el-form-item>
        <el-form-item label="编码" prop="code">
          <el-input v-model="form.code" disabled placeholder="请输入平台编码" />
        </el-form-item>
        <el-form-item label="AppKey" prop="appKey">
          <el-input v-model="form.appKey" placeholder="请输入AppKey" />
        </el-form-item>
        <el-form-item label="AppSecret" prop="appSecret">
          <el-input v-model="form.appSecret" placeholder="请输入AppSecret" />
        </el-form-item>
        <el-form-item label="回调URL" prop="redirectUri">
          <el-input v-model="form.redirectUri" placeholder="请输入回调URL" />
        </el-form-item>
        <el-form-item label="接口请求URL" prop="serverUrl">
          <el-input v-model="form.serverUrl" disabled placeholder="请输入接口请求URL" />
        </el-form-item>

<!--        <el-form-item label="描述" prop="remark">-->
<!--          <el-input type="textarea" v-model="form.remark" placeholder="请输入描述" />-->
<!--        </el-form-item>-->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- API参数设置对话框 -->
    <el-dialog :title="title" :visible.sync="apiOpen" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="appkey" prop="appkey">
          <el-input v-model="form.appkey" placeholder="请输入appkey" />
        </el-form-item>
        <el-form-item label="appSercet" prop="appSercet">
          <el-input v-model="form.appSercet" placeholder="请输入appSercet" />
        </el-form-item>
        <el-form-item label="API请求URL" prop="apiRequestUrl">
          <el-input v-model="form.apiRequestUrl" placeholder="请输入API请求URL" />
        </el-form-item>
        <el-form-item label="卖家UserId" prop="sellerUserId">
          <el-input v-model="form.sellerUserId" placeholder="请输入sellerUserId" />
        </el-form-item>
<!--        <el-form-item label="描述" prop="remark">-->
<!--          <el-input type="textarea" v-model="form.remark" placeholder="请输入描述" />-->
<!--        </el-form-item>-->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listPlatform, updatePlatform, getPlatform, changePlatformStatus} from "@/api/shop/shop";
import {changeRoleStatus} from "@/api/system/role";

export default {
  name: "Shop",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 店铺表格数据
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      apiOpen: false,
      // 查询参数
      queryParams: {},
      // 表单参数
      form: {
        type:null
      },
      // 表单校验
      rules: {
        name: [{ required: true, message: "不能为空", trigger: "blur" }],
        code: [{ required: true, message: "不能为空", trigger: "blur" }],
        appKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        appSecret: [{ required: true, message: "不能为空", trigger: "blur" }],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询店铺列表 */
    getList() {
      this.loading = true;
      listPlatform().then(response => {
        this.shopList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.apiOpen = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        nickName: null,
        ename: null,
        company: null,
        type: null,
        url: null,
        orderNum: null,
        isDelete: null,
        isShow: null,
        modifyOn: null,
        remark: null,
        sellerUserId: null,
        sellerUserIdStr: null,
        sessionKey: null,
        appkey: null,
        appSercet: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 角色状态修改
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"平台吗？').then(function() {
        return changePlatformStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getPlatform(id).then(response => {
        this.form = response.data;
        this.$nextTick(()=>{
          this.form.type = response.data.type+'';
        })

        this.open = true;
        this.title = "修改";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePlatform(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.apiOpen = false
              this.getList();
            });
          }
        }
      });
    }
  }
};
</script>
