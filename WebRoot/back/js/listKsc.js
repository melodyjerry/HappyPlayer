$(function() {
	$('#win').window('close');
	$('#tt')
			.datagrid(
					{

						url : 'ksc_list.action', // 服务器地址,返回json格式数据
						nowrap : true,
						autoRowHeight : false,
						striped : true,
						collapsible : true,
						pagination : true, // 分页控件
						rownumbers : true, // 行号
						sortName : 'createTime',
						sortOrder : 'desc',
						columns : [ [
								{
									field : 'kid',
									title : '编号',
									width : 120,
									editor : 'numberbox'
								},
								{
									field : 'songName',
									title : '歌曲名称',
									width : 120,
									editor : 'text'
								},
								{
									field : 'artist',
									title : '歌手名称',
									width : 200,
									editor : 'text'
								},
								{
									field : 'sizeStr',
									title : '文件大小',
									width : 80,
									editor : 'text'
								},
								{
									field : 'createTime',
									title : '添加时间',
									width : 140,
									editor : 'datebox',
									sortable : true
								},
								{
									field : 'updateTime',
									title : '更新时间',
									width : 140,
									editor : 'datebox',
									sortable : true
								},
								{
									field : 'action',
									title : '操作',
									width : 120,
									align : 'center',
									formatter : function(value, row, index) {
										var e = '<a href="javascript:void(0);" onclick="editrow(\''
												+ row.kid + '\')">编辑</a> ';
										var d = '<a href="javascript:void(0);" onclick="deleterow(\''
												+ row.kid
												+ '\',\''
												+ (index + 1) + '\')">删除</a>';
										return e + d;

									}
								} ] ],
					// toolbar : [ {
					// text : '增加',
					// iconCls : 'icon-add',
					// handler : addrow
					// }, {
					// text : '保存',
					// iconCls : 'icon-save',
					// handler : saveall
					// }, {
					// text : '取消',
					// iconCls : 'icon-cancel',
					// handler : cancelall
					// } ],
					// onBeforeEdit : function(index, row) {
					// row.editing = true;
					// $('#tt').datagrid('refreshRow', index);
					// editcount++;
					// },
					// onAfterEdit : function(index, row) {
					// row.editing = false;
					// $('#tt').datagrid('refreshRow', index);
					// editcount--;
					// },
					// onCancelEdit : function(index, row) {
					// row.editing = false;
					// $('#tt').datagrid('refreshRow', index);
					// editcount--;
					// }
					});
	var p = $('#tt').datagrid('getPager');
	$(p).pagination({
		pageSize : 10,// 每页显示的记录条数，默认为10
		pageList : [ 5, 10, 15, 20 ],// 每页显示几条记录
		beforePageText : '第',// 页数文本框前显示的汉字
		afterPageText : '页    共 {pages} 页',
		displayMsg : '当前显示 {from} - {to} 条记录    共 {total} 条记录',
		onBeforeRefresh : function() {
			$(this).pagination('loading');// 正在加载数据中...
			$(this).pagination('loaded'); // 数据加载完毕
		}
	});
});
function editrow(aid) {

	$('#messageForm')[0].reset();
	loadAppDetail(aid);
	$('#win').window('open');
}

function deleterow(kid, index) {

	$.messager.confirm("确认", '是否删除序号为' + index + '的数据', function(r) {
		if (r) {
			$.ajax({
				url : 'ksc_delete',
				data : 'kid=' + kid,
				error : function() {
					$.messager.alert('提示', "删除数据异常!", 'info');
				},
				success : function(data) {
					if (data === 'undefined' || data == null) {
						$.messager.alert('提示', "删除数据失败!", 'info');
					} else {
						if (data.result == true) {
							$.messager.alert('提示', "删除成功!", 'info', function() {
								$('#tt').datagrid('reload');
							});
						} else {
							$.messager.alert('提示', "删除失败!", 'info');
						}
					}
				}
			});
		}
	});
}

function save() {
	if (check()) {
		var options = {
			success : showResponse,
			error : showerror,
			url : 'ksc_edit.action',
			dataType : 'json'
		};
		$('#messageForm').ajaxForm(options).submit(function() {
		});
		$('#messageForm').submit();// 传统form提交
	}
}

function showResponse(data, statusText) {
	if (data.result == true) {
		$.messager.alert('提示', "编辑成功!", 'info', function() {
			$('#win').window('close');
			setTimeout($('#tt').datagrid('reload'), 500);
		});

	} else {
		$.messager.alert('提示', "编辑失败!", 'info');
	}
}
function showerror(data) {
	console.info(data);
	console.info(data.message);
	$.messager.alert('提示', "编辑异常!", 'info');
}

function check() {

	if ($("#artist").val() == "") {
		$.messager.alert('提示', "歌手名称不能为空!", 'info');
		return false;
	}

	if ($("#songName").val() == "") {
		$.messager.alert('提示', "歌曲歌名不能为空!", 'info');
		return false;
	}

	return true;
}
/**
 * 加载详情
 * 
 * @param sid
 */
function loadAppDetail(kid) {
	$.ajax({
		url : 'ksc_getKscInfoByID',
		data : 'kid=' + kid,
		error : function() {
			$.messager.alert('提示', "获取数据异常!", 'info');
		},
		success : function(data) {
			if (data === 'undefined' || data == null) {
				$.messager.alert('提示', "获取数据失败!", 'info');
			} else {
				$('#kid').val(data.kid);
				$('#artist').val(data.artist);
				$('#songName').val(data.songName);
			}
		}
	});
}
function cancel() {
	$('#win').window('close');
}
