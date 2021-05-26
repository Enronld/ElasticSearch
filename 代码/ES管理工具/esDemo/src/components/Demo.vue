<template>
	<div style="height:720px;">
	 <el-tabs type="card" tab-position="top" editable @edit="handleTabsEdit" @tab-click="clickTab" v-model="editableTabsValue">
		<el-tab-pane v-for="(item, index) in IndexList" :key="index" :label="item.title" :name="item.name" >
			<el-row>
				<el-col :span="8">
					<el-input v-model="search" placeholder="请输入" style="margin:10px;"  @input="inputChange" />   
				</el-col>
				<el-col :span="2">
					<el-button type="primary" icon="el-icon-plus" circle @click="addFormVisible = true"></el-button>  
				</el-col>     
			</el-row>

			<!-- 添加文档 -->
			<el-dialog title="添加文档" :visible.sync="addFormVisible">
				<el-form :model="add_form" ref="add_form">
					<el-form-item label="文档id" :label-width="formLabelWidth">
						<el-input v-model="add_form.id" autocomplete="off"></el-input>
					</el-form-item>
					<el-form-item label="key" :label-width="formLabelWidth">
						<el-input v-model="add_form.key" autocomplete="off"></el-input>
					</el-form-item>
					<el-form-item label="value" :label-width="formLabelWidth">
						<el-input v-model="add_form.value" autocomplete="off"></el-input>
					</el-form-item>
				</el-form>
				<div slot="footer" class="dialog-footer">
					<el-button @click="addFormVisible = false">取 消</el-button>
					<el-button type="primary" @click="addDocument('add_form')">确 定</el-button>
				</div>
			</el-dialog>
					
				<!-- 列表 -->
				<el-table
					stripe
					:data="DocumentList.filter(data => !search || data.nickname.toLowerCase().includes(search.toLowerCase()))"
					>
					<el-table-column
					prop="id"
					label="id"
					>
					</el-table-column>
					<el-table-column
					prop="data"
					label="数据"
					
					>
					</el-table-column>
				
					<el-table-column
					prop="option"
					label="操作">
						 <template slot-scope="scope">
							<el-button
							size="mini"
							@click="handleEdit(scope.$index, scope.row)">编辑</el-button>
							<el-button
							size="mini"
							type="danger"
							@click="handleDelete(scope.$index, scope.row)">删除</el-button>
						</template>

					</el-table-column>
				</el-table>
				<!-- 分页 -->
				<div align="center" style="padding-top: 10px;">
				<el-pagination			
					@size-change="handleSizeChange" @current-change="handleCurrentChange"
					:current-page.sync="page" :page-sizes="[1, 2,5, 10]" :page-size="limit"
					layout="total, sizes, prev, pager, next, jumper" :total="total"
					v-if="pageshow" style="margin-top: 10px;"
				> 
				</el-pagination>
				</div>
		
		</el-tab-pane>
		
	</el-tabs>
   
	</div>
    
</template>

<script>
import axios from 'axios';
const url = "http://localhost:8080/";

export default {
    inject:['reload'],
    data() {
      return {

		page:1, //初始页
		limit: 5,
		total: null,
		pageshow: true,
		// pagesize:10,    //    每页的数据

		IndexList:[],
		editableTabsValue: '',
        DocumentList: [
		],
		data:[],
		addFormVisible: false,
        add_form: {
			id:'',
			key:'',
			value:''
        },
		index:'',
		formLabelWidth: '120px',

        search: '',
			
	
		};
    },
	created(){
		this.showIndices();
		// console.log(reload);
		// this.reload();
	},
	methods:{
		 // 当每页数量改变
		handleSizeChange(val) {
			console.log(`每页 ${val} 条`);
			this.limit = val
			this.getList()
		},
		// 当当前页改变
		handleCurrentChange(val) {
			console.log(`当前页: ${val}`);
			this.page = val
			this.getList()
		},
		// 处理数据
		getList() {
			// es6过滤得到满足搜索条件的展示数据list
			let tableData = this.data.filter((item, index) =>
				item.nickname.includes(this.search)
			);
			console.log(tableData);
			this.LandlordList = tableData.filter((item, index) =>
				index < this.page * this.limit && index >= this.limit * (this.page - 1)
			)
			this.$forceUpdate() // vm.$forceUpdate()
			this.total = tableData.length
		},
		inputChange(){
			this.getList();

		},

		//获取索引名称
		showIndices(){
			axios.get(url + 'showAllIndices').then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.editableTabsValue = res.data.detail[0];
					this.index = this.editableTabsValue;
					this.showDocument(this.editableTabsValue);
					for (let i = 0; i < res.data.detail.length; i++){
						console.log(res.data.detail[i]);
						this.$set(this.IndexList,i,{"title":res.data.detail[i],"name":res.data.detail[i]});
						
					}
					console.log(this.IndexList);
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})

		},

		//添加索引
		addIndex(index){
			axios.get(url + 'createIndex/' + index).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.$message({
						type: 'success',
						message: '添加成功!'
					});
					this.$set(this.IndexList,this.IndexList.length,{"title":index,"name":index});
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})

		},
		//删除索引
		deleteIndex(index){
			axios.get(url + 'deleteIndex/' + index).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.$message({
						type: 'success',
						message: '删除成功!'
					});
					 for (let i = 0; i < this.IndexList.length; i++){
						if (this.IndexList[i].name == index){
							this.IndexList.splice(i,1);
							break;
						}
					}
					if (this.editableTabsValue == index){
						this.editableTabsValue = '';
					}
					
					
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})

		},

		//点击tab
		clickTab(c){
			console.log(c.name);
			this.showDocument(c.name);
			this.index = c.name;

		},

		//获取文档
		showDocument(index){
			this.DocumentList = [];
			axios.get(url + 'getDocument/' + index).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					for (let i = 0; i < res.data.detail.length; i++){
						this.$set(this.DocumentList,i,{"id":res.data.detail[i].id,"data":JSON.stringify(res.data.detail[i].data)});
					}
					console.log(this.DocumentList);
					
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})

		},

		//添加文档
		addDocument(formName){
			axios.get(url + 'addDocument/' + this.index +'/' + this.add_form.id + '/' + this.add_form.key.split(',') + '/' + this.add_form.value.split(',')).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					console.log(this.$refs[formName]);
					this.add_form.id = '';
					this.add_form.key = '';
					this.add_form.value = '';
					this.reload();
							
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
				
			})
			this.addFormVisible = false;

		},

		//删除文档
		deleteDocument(index,id){
			axios.get(url + 'deleteDocument/' + index + '/' + id).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					for (let i = 0; i < this.DocumentList.length; i++){
						if (id == this.DocumentList[i].id){
							this.DocumentList.splice(i,1);
							break;
						}
					}
					this.$message({
						message: res.data.detail,
						type: 'success'
					});
					
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})
		},
		handleDelete(index, row){
			console.log(row);
			this.$confirm('确定删除文档?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				this.deleteDocument(this.index,row.id);
	
			}).catch(() => {
				this.$message({
					type: 'info',
					message: '已取消删除'
				});          
			});

			

		},


		handleTabsEdit(targetName, action) {
        if (action === 'add') {
			this.$prompt('请输入索引名字', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
			}).then(({ value }) => {
				this.addIndex(value);

			}).catch(() => {
				this.$message({
					type: 'info',
					message: '取消输入'
				});       
			});
      
        }
        if (action === 'remove') {
			this.$confirm('确定删除索引?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				this.deleteIndex(targetName);
	
			}).catch(() => {
				this.$message({
					type: 'info',
					message: '已取消删除'
				});          
			});

        }
      }
		
	}
}


</script>

<style>
	.toolbar{
		height: 60px;
		background-color: #eff1f1;
		margin: 10px 0 10px 0;
	}
	.btnM{
		margin-left: 10px;
		margin-top: 10px;
	}
</style>
