<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="108px">
      <el-form-item label="平台SkuId" prop="skuId">
        <el-input
          v-model="queryParams.skuId"
          placeholder="请输入平台SkuId"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商家sku编码" prop="outerId">
        <el-input
          v-model="queryParams.outerId"
          placeholder="请输入商家sku编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ERP skuId" prop="erpSkuId">
        <el-input
          v-model="queryParams.erpSkuId"
          placeholder="请输入ERP skuId"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
            <el-form-item label="店铺" prop="shopId">
              <el-select v-model="queryParams.shopId" placeholder="请选择店铺" clearable @change="handleQuery">
               <el-option
                  v-for="item in shopList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          :loading="pullLoading"
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handlePull"
        >API拉取商品数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :disabled="multiple"
          icon="el-icon-refresh"
          size="mini"
          @click="handlePushOms"
        >推送到商品库</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goodsList" @selection-change="handleSelectionChange">
       <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column label="ID" align="center" prop="id" />-->
      <el-table-column label="平台商品ID" align="center" prop="productId" />
      <el-table-column label="图片" align="center" prop="logo" width="100">
        <template slot-scope="scope">
          <image-preview :src="scope.row.img" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" align="center" prop="name" />
       <el-table-column label="商家编码" align="center" prop="outerProductId" />
      <el-table-column label="价格" align="center" prop="formattedPrice" >
      </el-table-column>
      <el-table-column label="SKU" align="center" >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-info"
            @click="handleViewSkuList(scope.row)"
          >{{scope.row.skuList.length +' 个SKU'}}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="ERP商家ID" align="center" prop="erpGoodsId" />
<!--      <el-table-column label="状态" align="center" prop="skuStatus" >-->
<!--        <template slot-scope="scope">-->
<!--          <el-tag size="small" v-if="scope.row.skuStatus === false">已下架</el-tag>-->
<!--          <el-tag size="small" v-if="scope.row.skuStatus === true">销售中</el-tag>-->
<!--        </template>-->
<!--      </el-table-column>-->
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleLink(scope.row)"
          >关联ERP</el-button>

        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="Sku List" :visible.sync="skuOpen" width="1200px" append-to-body>
      <el-table v-loading="loading" :data="skuList">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="序号" align="center" prop="index" width="50"/>
        <el-table-column label="SKU编码" align="left" prop="code" />
        <el-table-column label="规格Id" align="center" prop="specId" />
        <el-table-column label="商品名称" align="center" prop="name" />
        <el-table-column label="图片" align="center" prop="logo" width="100">
          <template slot-scope="scope">
            <image-preview :src="scope.row.img" :width="50" :height="50"/>
          </template>
        </el-table-column>

        <el-table-column label="规格" align="center" prop="specDetailName1" >
          <template slot-scope="scope">
            {{scope.row.specDetailName1}}&nbsp;
            {{scope.row.specDetailName2}}&nbsp;
            {{scope.row.specDetailName3}}
          </template>
        </el-table-column>

        <el-table-column label="价格" align="center" prop="price" >
          <template slot-scope="scope">
            {{amountFormatter(null,null,scope.row.price/100,0)}}
          </template>
        </el-table-column>

        <el-table-column label="库存" align="center" prop="stockNum" />
        <el-table-column label="ERP SKU ID" align="center" prop="erpGoodsSkuId" />
<!--        <el-table-column label="状态" align="center" prop="status" >-->
<!--          <template slot-scope="scope">-->
<!--            <el-tag size="small" v-if="scope.row.status === 1">销售中</el-tag>-->
<!--            <el-tag size="small" v-if="scope.row.status === 2">已下架</el-tag>-->
<!--          </template>-->
<!--        </el-table-column>-->
<!--        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button-->
<!--              size="mini"-->
<!--              type="text"-->
<!--              icon="el-icon-share"-->
<!--              @click="handleLink(scope.row)"-->
<!--            >关联ERP</el-button>-->
<!--          </template>-->
<!--        </el-table-column>-->
      </el-table>
    </el-dialog>

    <!-- 添加或修改商品管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="ERP商品SkuId" prop="erpGoodsSkuId">
          <el-input v-model.number="form.erpGoodsSkuId" placeholder="请输入ERP商品SkuId" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

import {listShop} from "@/api/shop/shop";
import {pullGoodsList, listGoods, getGoodsSku, linkErpGoodsSkuId,pushToOms} from "@/api/dou/goods";
import {MessageBox} from "element-ui";
import {isRelogin} from "@/utils/request";

export default {
  name: "GoodsListDou",
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
      pullLoading: false,
      skuList:[],
      skuOpen:false,
      // 总条数
      total: 0,
      // 商品管理表格数据
      goodsList: [],
      shopList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null
      },
      // 表单参数
      form: {},
      supplierList: [],
      categoryList: [],
      categoryTree: [],
      // 表单校验
      rules: {
        id: [
          { required: true, message: "不能为空", trigger: "change" }
        ],
        erpSkuId: [
          { required: true, message: "不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    listShop({type:400}).then(response => {
      this.shopList = response.rows;
      if (this.shopList && this.shopList.length > 0) {
        this.queryParams.shopId = this.shopList[0].id
      }
      this.getList();
    });
    // this.getList();
    this.loading = false;
  },
  methods: {
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    amountFormatter(row, column, cellValue, index) {
      if(!cellValue){
        return "";
      }
      return '￥' + cellValue.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    /** 查询商品管理列表 */
    getList() {
      this.loading = true;
      listGoods(this.queryParams).then(response => {
        this.goodsList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        erpSkuId: null
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
    handleLink(row) {
      this.reset();
      const id = row.id || this.ids
      getGoodsSku(id).then(response => {
        console.log('=====00000000============',response)
        this.form = response.data;
        this.open = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          linkErpGoodsSkuId(this.form).then(response => {
            this.$modal.msgSuccess("关联成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handlePull() {
      if(this.queryParams.shopId){
        this.pullLoading = true
        pullGoodsList({shopId:this.queryParams.shopId}).then(response => {
          console.log('拉取PDD商品接口返回=====',response)
          if(response.code === 1401) {
            MessageBox.confirm('Token已过期，需要重新授权！请前往店铺列表重新获取授权！', '系统提示', { confirmButtonText: '前往授权', cancelButtonText: '取消', type: 'warning' }).then(() => {
              // isRelogin.show = false;
              this.$router.push({path:"/shop/shop_list",query:{platform:6}})
              // store.dispatch('LogOut').then(() => {
              // location.href = response.data.tokenRequestUrl+'?shopId='+this.queryParams.shopId
              // })
            }).catch(() => {
              isRelogin.show = false;
            });

            // return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
          }else if(response.code==200){
            this.pullLoading = false
            this.getList()
            this.$modal.msgSuccess(JSON.stringify(response));
          }else{
            this.pullLoading = false
            this.$modal.msgError(response.msg);
          }
          this.pullLoading = false

        })
      }else{
        this.$modal.msgSuccess("请先选择店铺");
      }

      // this.$modal.msgSuccess("请先配置API");
    },
    /** 查看SKU List*/
    handleViewSkuList(row){
      this.skuList = row.skuList
      this.skuOpen = true;

    },
    handlePushOms(){
      this.$confirm('确认同步所有商品到商品库吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        pushToOms( this.ids ).then(response => {
          this.$message.success('商品同步成功')
          this.getList()
        }).finally(() => {
          this.loading = false
        })
      })
    },
  }
};
</script>
