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
				<el-col :span="4">
					 <el-upload
					 
                          class="avatar-uploader"
                          action="http://localhost:8000/file/uploadDocument"
                          accept="xls"
                          :on-success="handleAvatarSuccess"
                          :before-upload="beforeAvatarUpload"
                          
                         >
						  <el-button size="small" type="primary">点击上传</el-button>
                           <div class="el-upload__tip" slot="tip">只能上传xls文件</div>
                        </el-upload>
				</el-col>    
			</el-row>

			<!-- 添加文档 -->
			<el-dialog title="添加文档" :visible.sync="addFormVisible">
				<el-form :model="add_form" ref="add_form">
					<el-form-item label="文档id" :label-width="formLabelWidth">
						<el-input v-model="add_form.id" autocomplete="off"></el-input>
					</el-form-item>
					<el-form-item label="key" :label-width="formLabelWidth">
						<el-input v-model="add_form.key" autocomplete="off" placeholder="多个key用空格分隔"></el-input>
					</el-form-item>
					<el-form-item label="value" :label-width="formLabelWidth">
						<el-input v-model="add_form.value" autocomplete="off"  placeholder="规则同key"></el-input>
					</el-form-item>
				</el-form>
				<div slot="footer" class="dialog-footer">
					<el-button @click="addFormVisible = false">取 消</el-button>
					<el-button type="primary" @click="addDocument('add_form')">确 定</el-button>
				</div>
			</el-dialog>

			<!-- 更新文档 -->
			<el-dialog title="更新文档" :visible.sync="editFormVisible">
				<el-form :model="edit_form" ref="edit_form">
					<el-form-item label="key" :label-width="formLabelWidth">
						<el-input v-model="edit_form.key" autocomplete="off" placeholder="多个key用空格分隔"></el-input>
					</el-form-item>
					<el-form-item label="value" :label-width="formLabelWidth">
						<el-input v-model="edit_form.value" autocomplete="off" placeholder="规则同key"></el-input>
					</el-form-item>
				</el-form>
				<div slot="footer" class="dialog-footer">
					<el-button @click="editFormVisible = false">取 消</el-button>
					<el-button type="primary" @click="editDocument">确 定</el-button>
				</div>
			</el-dialog>
					
				<!-- 列表 -->
				<el-table
					stripe
					:data="DocumentList"
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
const url = "http://localhost:8081/";

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
		editFormVisible: false,
        edit_form: {
			key:'',
			value:''
        },
		index:'',
		formLabelWidth: '120px',

        search: ' ',
		editId:'',		//标记编辑Id

		dNum:0,
		fields:'age',		//文档key
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
			this.limit = val;
			this.getList();
		},
		// 当当前页改变
		handleCurrentChange(val) {
			console.log(`当前页: ${val}`);
			this.page = val;
			this.getList();
		},
		// 处理数据
		getList() {
	
			this.showDocument(this.index,this.search,this.fields,(this.page-1)*this.limit, this.limit);
			// let tableData = this.DocumentList.filter((item, index) =>
			// 	item.data.includes(this.search)
			// );
			// this.DocumentList = tableData;
		
			this.$forceUpdate(); // vm.$forceUpdate()
			// this.total = this.dNum - tableData.length;
		},
		inputChange(){
			this.getList();
		},

		 //上传文件
		handleAvatarSuccess(res, file,fileList) {
			console.log(111111);
		
			console.log(file);
			console.log(fileList);
			this.$message({
				message: '批量导入成功',
				type: 'success'
			});

			// this.dialogImageUrl = res;
			// this.imageUrl = res;
		},
		beforeAvatarUpload(file) {
			// const isJPG = file.type === 'image/jpeg';
			const isLt2M = file.size / 1024 / 1024 < 2;

			if (!isLt2M) {
				this.$message.error('上传文件大小不能超过 2MB!');
			}
			return isLt2M;
		},

		//获取索引名称
		showIndices(){
			
			axios.get(url + 'showAllIndices').then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.editableTabsValue = res.data.detail[0];
					this.index = this.editableTabsValue;
					// this.showDocumentNum(this.editableTabsValue);
					console.log(this.limit);
					this.showDocument(this.editableTabsValue,this.search,this.fields,0,this.limit);
				
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
			this.total = 0;
			console.log(c.name);
			this.showDocument(c.name,this.search,this.fields,0,this.limit);
			this.index = c.name;

		},

		//获取文档数量指定page大小
		showDocumentNum(){
			axios.get(url + 'getDocumentNum').then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.dNum = res.data.detail;
					this.total = res.data.detail;
					console.log(this.page);
					
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
			})

		},

		//获取文档
		showDocument(index,search,field,pageNo,pageSize){
			this.DocumentList = [];
			axios.get(url + 'getDocument/' + index + '/' + search + '/' + field + '/' + pageNo + '/' + pageSize).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					console.log(res.data.detail.length);
					for (let i = 0; i < res.data.detail.length; i++){
						this.$set(this.DocumentList,i,{"id":res.data.detail[i].id,"data":JSON.stringify(res.data.detail[i].data)});
					}
					console.log(this.DocumentList);
					this.showDocumentNum();
					// this.data = this.DocumentList;
					// this.getList();
					
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
			axios.get(url + 'addDocument/' + this.index +'/' + this.add_form.id + '/' + this.add_form.key.trim().split(/\s+/) + '/' + this.add_form.value.trim().split(/\s+/)).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					console.log(this.$refs[formName]);
					this.add_form.id = '';
					this.add_form.key = '';
					this.add_form.value = '';
					// this.reload();
					// this.getList();
					this.showDocument(this.index,this.search,this.fields,0, this.limit);
					location.reload();
							
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
				
			})
			this.addFormVisible = false;

		},

		//更新文档
		updateDocument(index,id){
			console.log(this.edit_form.key.trim().split(/\s+/));
			axios.get(url + 'updateDocument/' + index +'/' + id + '/' + this.edit_form.key.trim().split(/\s+/) + '/' + this.edit_form.value.trim().split(/\s+/)).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					this.$message({
						message: res.data.detail,
						type: 'success'
					});
					// this.reload();	
					// location.reload();
					// this.getList();
					this.showDocument(this.index,this.search,this.fields,(this.page-1)*this.limit, this.limit);
					// this.showDocument(this.index);				
				}else{
					this.$message({
						message: res.data.detail,
						type: 'error'
					});
				}
				
			})
			this.editFormVisible = false;

		},



		//删除文档
		deleteDocument(index,id){
			axios.get(url + 'deleteDocument/' + index + '/' + id).then(res=>{
				console.log(res.data.status);
				if(res.data.status == 'success'){
					
					this.$message({
						message: res.data.detail,
						type: 'success'
					});
					// this.getList();
					this.showDocument(this.index,this.search,this.fields,0, this.limit);
					location.reload();
					
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
		handleEdit(index, row){
			this.editFormVisible = true;
			this.editId = row.id;
			let data = JSON.parse(row.data);
			this.edit_form.key = '';
			this.edit_form.value = '';

			console.log(data);
			console.log(this.edit_form);
			let j = 0;
			for (var key in data) {
				this.edit_form.key =  this.edit_form.key + ' ' + key;
				this.edit_form.value =  this.edit_form.value + ' ' + data[key];
				console.log(key);     //获取key值
				console.log(data[key]); //获取对应的value值
			}

		},
		
		//确认编辑
		editDocument(){
			this.updateDocument(this.index,this.editId);

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
