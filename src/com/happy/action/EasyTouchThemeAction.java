package com.happy.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.google.gson.Gson;
import com.happy.model.EasyTouchTheme;
import com.happy.service.EasyTouchThemeService;
import com.happy.util.DateUtil;
import com.happy.util.IDGenerate;
import com.opensymphony.xwork2.ActionSupport;

public class EasyTouchThemeAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EasyTouchThemeService service;

	public EasyTouchThemeService getService() {
		return service;
	}

	@Resource(name = "easyTouchThemeService")
	public void setService(EasyTouchThemeService service) {
		this.service = service;
	}

	private Logger logger = Logger.getLogger(EasyTouchThemeAction.class
			.getName());
	private File skinfile;
	private String skinfileFileName;
	private String skinfileContentType;

	private File file;

	/**
	 * 添加启动页
	 */
	public void add() {
		HttpServletRequest request = ServletActionContext.getRequest();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", false);

		Gson gson = new Gson();

		if (file == null || skinfile == null) {
			printResponse(gson.toJson(map));
		}

		try {

			InputStream sis = new FileInputStream(skinfile);
			ByteArrayOutputStream sbaos = new ByteArrayOutputStream();
			int bs = 0;
			while ((bs = sis.read()) != -1) {
				sbaos.write(bs);
			}

			InputStream is = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b = 0;
			while ((b = is.read()) != -1) {
				baos.write(b);
			}

			String themeName = request.getParameter("themeName");

			EasyTouchTheme easyTouchTheme = new EasyTouchTheme();
			easyTouchTheme.setSid(IDGenerate.getId("hp"));
			easyTouchTheme.setThemeName(themeName);
			String createTime = DateUtil.dateToString(new Date());
			easyTouchTheme.setCreateTime(createTime);
			easyTouchTheme.setUpdateTime(createTime);
			easyTouchTheme.setSize(skinfile.length());
			easyTouchTheme.setSizeStr(getFileSize(skinfile.length()));
			easyTouchTheme.setType(getFileExtension(skinfileFileName));

			easyTouchTheme.setData(sbaos.toByteArray());
			easyTouchTheme.setPreviewImage(baos.toByteArray());

			Object result = service.add(easyTouchTheme);

			printResponse(result);

		} catch (Exception e) {
			printResponse(gson.toJson(map));
			logger.error(e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 获取启动页列表
	 */
	public void list() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String page = request.getParameter("page");// 当前第几页
		String rows = request.getParameter("rows");// 每页显示的条数
		// 当前页,page由分页工具负责传过来
		int intPage = Integer.parseInt((page == null || page == "0") ? "1"
				: page);
		// 每页显示条数
		int number = Integer.parseInt((rows == null || rows == "0") ? "10"
				: rows);
		// 每页的开始记录 第一页为1 第二页为number +1
		int offset = (intPage - 1) * number;

		String sort = request.getParameter("sort");// 'itemid';
		String order = request.getParameter("order");// 'asc';

		Object result = service.getEasyTouchThemePage(offset, number, sort, order);
		printResponse(result);
	}

	/**
	 * 删除数据
	 */
	public void delete() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String sid = request.getParameter("sid");
		Object result = service.delete(sid);
		printResponse(result);
	}

	/**
	 * 编辑数据
	 */
	public void edit() {
		HttpServletRequest request = ServletActionContext.getRequest();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("result", false);
		Gson gson = new Gson();

		String sid = request.getParameter("sid");
		EasyTouchTheme easyTouchTheme = service.getEasyTouchThemeDataByID(sid);
		String themeName = request.getParameter("themeName");
		easyTouchTheme.setThemeName(themeName);
		String updateTime = DateUtil.dateToString(new Date());
		easyTouchTheme.setUpdateTime(updateTime);

		byte[] images = null;

		byte[] data = null;

		if (skinfile != null) {
			try {
				InputStream is = new FileInputStream(skinfile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b = 0;
				while ((b = is.read()) != -1) {
					baos.write(b);
				}
				data = baos.toByteArray();
			} catch (Exception e) {
				logger.error(e.toString());
				e.printStackTrace();
			}
		}
		if (skinfile != null && data == null) {
			printResponse(gson.toJson(map));
		} else if (data != null) {
			easyTouchTheme.setSize(skinfile.length());
			easyTouchTheme.setSizeStr(getFileSize(skinfile.length()));
			easyTouchTheme.setType(getFileExtension(skinfileFileName));
			easyTouchTheme.setData(data);
		}

		if (file != null) {
			try {
				InputStream is = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b = 0;
				while ((b = is.read()) != -1) {
					baos.write(b);
				}
				images = baos.toByteArray();
			} catch (Exception e) {
				logger.error(e.toString());
				e.printStackTrace();
			}
		}
		if (file != null && images == null) {
			printResponse(gson.toJson(map));
		} else if (images != null) {
			easyTouchTheme.setPreviewImage(images);
		}

		Object result = service.edit(easyTouchTheme);
		printResponse(result);

	}

	/**
	 * 通过id来获取详情
	 */
	public void getEasyTouchThemeByID() {
		HttpServletRequest request = ServletActionContext.getRequest();
		String sid = request.getParameter("sid");
		Object result = service.getEasyTouchThemeByID(sid);
		printResponse(result);
	}

	/**
	 * 计算文件的大小，返回相关的m字符串
	 * 
	 * @param fileS
	 * @return
	 */
	private String getFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取文件后缀名
	 * 
	 * @param file
	 * @return
	 */
	private String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	/**
	 * 返回结果给服务器
	 * 
	 * @param result
	 */
	public void printResponse(Object result) {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=utf-8");

		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(result.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getSkinfile() {
		return skinfile;
	}

	public void setSkinfile(File skinfile) {
		this.skinfile = skinfile;
	}

	public String getSkinfileFileName() {
		return skinfileFileName;
	}

	public void setSkinfileFileName(String skinfileFileName) {
		this.skinfileFileName = skinfileFileName;
	}

	public String getSkinfileContentType() {
		return skinfileContentType;
	}

	public void setSkinfileContentType(String skinfileContentType) {
		this.skinfileContentType = skinfileContentType;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
