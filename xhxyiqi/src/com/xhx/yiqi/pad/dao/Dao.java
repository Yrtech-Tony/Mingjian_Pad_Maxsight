package com.xhx.yiqi.pad.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.xhx.yiqi.pad.utility.SqliteHelper;
import com.xhx.yiqi.pad.utility.WebServiceUtil;

import android.database.Cursor;

public class Dao {

	public Cursor SearchAllProjects()
	{
		String sql = "SELECT ProjectCode, ProjectName FROM Projects ORDER By OrderNO DESC";
		return SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, null);
	}
	
	public Cursor SearchSubjectStart(String projectCode, String shopCode, String subjectTypeCode)
	{
		//获取之前打过分的题目的OrderNO
		String sql = "SELECT b.OrderNO " +
					 "FROM Answer a " +
					 "JOIN Subjects b on a.ProjectCode = b.ProjectCode AND a.SubjectCode = b.SubjectCode " +
					 "WHERE a.ProjectCode = ? " +
					 "AND a.ShopCode = ? " +
					 "AND (b.SubjectTypeCodeExam = ? OR b.SubjectTypeCodeExam ='C') " + 
					 "AND (b.SubjectDelChk=0 OR b.SubjectDelChk IS NULL)";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, shopCode, subjectTypeCode});
		int orderNO = 0;
		if(cursor.getCount()>0)
		{
			cursor.moveToLast();
			orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO")); 
		}
		
		//获取之前打过分的题目的下一道题的OrderNO
		sql="SELECT MIN(OrderNO) as OrderNO " +
			"FROM Subjects " +
			"WHERE ProjectCode = ? " +
			"AND (SubjectTypeCodeExam = ? OR SubjectTypeCodeExam ='C') " +
			"AND OrderNO > ? " + 
			"AND (SubjectDelChk=0 OR SubjectDelChk IS NULL)";
		cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectTypeCode, ""+orderNO});
		if(cursor.getCount()>0)
		{
			cursor.moveToLast();
			orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO")); 
		}
		
		//获取题目
		sql="SELECT a.SubjectCode, a.OrderNO, a.Implementation " +
			"FROM Subjects AS a " +
			"WHERE a.ProjectCode = ? " +
			"AND a.OrderNO = ?";
		cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, ""+orderNO});
		
		return cursor;
	}

	public Cursor SearchScoreAndRemark(String projectCode, String shopCode, String subjectCode){
		String sql = "SELECT Score, Remark " +
					 "FROM Answer " +
					 "WHERE ProjectCode=? " +
					 "AND SubjectCode=? " +
					 "AND ShopCode=?";
		return SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode });
	}
	
	public Cursor SearchLastScore(String projectCode, String shopCode, String subjectCode){
		String sql = "SELECT Score FROM Answer WHERE ProjectCode=( " +
					 "    SELECT ProjectCode FROM Projects WHERE OrderNO = ( " +
					 "        SELECT min(OrderNO) FROM Projects WHERE OrderNO < ( " +
					 "            SELECT OrderNO FROM Projects WHERE ProjectCode=?))) " +
					 "AND SubjectCode=? " +
					 "AND ShopCode=?";
		return SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode });
	}
	
	public Cursor SearchSubjectCurrent(String projectCode, String subjectCode)
	{
		String sql="SELECT a.SubjectCode, a.CheckPoint " +
				"FROM Subjects AS a " +
				"WHERE a.ProjectCode = ? " +
				"AND a.SubjectCode = ?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectCode});
			
		return cursor;
	}
	
	public Cursor SearchSubjectNext(String projectCode, int orderNO, String subjectTypeCode)
	{
		//获取下道题的OrderNO
		String sql = "SELECT MIN(OrderNO) AS OrderNO " +
					 "FROM Subjects " +
					 "WHERE ProjectCode=? " +
					 "AND OrderNO>? " +
					 "AND (SubjectTypeCodeExam=? OR SubjectTypeCodeExam='C') " + 
					 "AND (SubjectDelChk=0 OR SubjectDelChk IS NULL)";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,""+orderNO,subjectTypeCode });
		if(cursor.getCount()>0)
		{
			cursor.moveToLast();
			orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO")); 
		}
		
		//获取题目
		sql="SELECT a.SubjectCode, a.OrderNO, a.CheckPoint, a.Implementation " +
			"FROM Subjects AS a " +
			"WHERE a.ProjectCode = ? " +
			"AND a.OrderNO = ?";
		cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, ""+orderNO});
		
		return cursor;
	}
	
	public Cursor SearchSubjectPrevious(String projectCode, int orderNO, String subjectTypeCode)
	{
		//获取下道题的OrderNO
		String sql = "SELECT MAX(OrderNO) AS OrderNO " +
					 "FROM Subjects " +
					 "WHERE ProjectCode=? " +
					 "AND OrderNO<? " +
					 "AND (SubjectTypeCodeExam=? OR SubjectTypeCodeExam='C') " + 
					 "AND (SubjectDelChk=0 OR SubjectDelChk IS NULL)";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,""+orderNO,subjectTypeCode });
		if(cursor.getCount()>0)
		{
			cursor.moveToLast();
			orderNO = cursor.getInt(cursor.getColumnIndex("OrderNO")); 
		}
		
		//获取题目
		sql="SELECT a.SubjectCode, a.OrderNO, a.CheckPoint, a.Implementation " +
			"FROM Subjects AS a " +
			"WHERE a.ProjectCode = ? " +
			"AND a.OrderNO = ?";
		cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, ""+orderNO});
		
		return cursor;
	}
	
	public Cursor SearchInspectionStandardList(String projectCode, String subjectCode)
	{
		String sql = "select InspectionStandardName,SeqNO AS InspectionStandardSeqNO from inspectionstandard " +
					 "where projectcode=? " +
					 "and subjectcode=? " +
					 "order by seqno";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectCode});
	
		return cursor;
	}
	
	public Cursor SearchInspectionStandardCheckOptionValues(String projectCode, String shopCode, String subjectCode){
		String sql = "select SeqNO,CheckOptionCode " +
					 "from AnswerDtl " +
					 "where projectcode=? " +
					 "and shopcode=? " +
					 "and subjectcode=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, shopCode, subjectCode});

		return cursor;
	}
	
	public Cursor SearchInspectionImgList(String projectCode, String subjectCode)
	{
		String sql = "select FileName,SeqNO " +
					 "from FileAndPicture " +
					 "where projectcode=? " +
					 "and subjectcode=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectCode});
	
		return cursor;
	}
	
	public Cursor SerachInspectionImgCheckOptionValues(String projectCode, String shopCode, String subjectCode){
		String sql = "SELECT SeqNO,CheckOptionCode " +
					 "FROM AnswerDtl2 " +
					 "WHERE ProjectCode=? " +
					 "AND ShopCode=? " +
					 "AND SubjectCode=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, shopCode, subjectCode});
	
		return cursor;
	}
	
	public Cursor SearchShop(String shopCode, String shopName)
	{
		Cursor cursor = null;
		String sql = "SELECT ShopCode, ShopName " +
				 	 "FROM Shop ";
		if(shopName == null || shopName.trim().equals(""))
		{
			sql += "WHERE UseChk=1 AND ShopCode LIKE ?";
			cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{"%"+shopCode+"%"});
		}
		else
		{
			sql += "WHERE UseChk=1 AND ShopName LIKE ?";
			cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{"%"+shopName+"%"});
		}
		
		return cursor;
	}

	public Cursor SearchSubjectTypeCode(String projectCode, String shopCode){
		String sql = "SELECT SubjectTypeCodeExam FROM ShopProjects "+
					 "WHERE ProjectCode=? "+
					 "AND ShopCode=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode, shopCode});
		return cursor;
	}
	
	public Cursor SearchScoreSet(String projectCode, String subjectCode){
		String sql = "SELECT Score FROM ScoreSet "+
				 	 "WHERE ProjectCode=? " +
				 	 "AND SubjectCode=? "+
				 	 "ORDER BY SeqNO DESC";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode, subjectCode});
		return cursor;
	}
	
	public Cursor SearchUserInfo(String currentUserID){
		String sql = "SELECT UserID, PSW " +
					 "FROM UserInfo "+
				 	 "WHERE UserID=? ";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { currentUserID});
		return cursor;
	}
	
	public void SaveSubject(String projectCode, String shopCode, String subjectCode, String score, String remark)
	{
		Cursor cursor = null;
		
		String sql = "select ProjectCode, SubjectCode, ShopCode " +
			 	 	 "FROM Answer " +
			 	 	 "where ProjectCode = ? " +
			 	 	 "AND SubjectCode = ? " +
			 	 	 "AND ShopCode = ? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode});
		if(cursor.moveToNext()){
			sql = "UPDATE Answer SET " +
				  "Score=?," +
				  "Remark=?," +
				  "ImageName=?," +
				  "AssessmentDate=datetime()," +
				  "Flag=0 " +
				  "WHERE ProjectCode=? " +
				  "AND SubjectCode=? " +
				  "AND ShopCode=? ";
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{score,remark,"ImageName",projectCode,subjectCode,shopCode});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{score,remark,"ImageName",projectCode,subjectCode,shopCode});
		}
		else {
			sql = "INSERT INTO Answer(ProjectCode,SubjectCode,ShopCode,Score,Remark,InUserID,InDateTime,ImageName,AssessmentDate,Flag) " +
					  "VALUES(?,?,?,?,?,?,datetime(),?,datetime(),0)";
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,score,remark,SqliteHelper.getCurrentUserID(),"ImageName"});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,score,remark,SqliteHelper.getCurrentUserID(),"ImageName"});
		}
	}
	
	public void SaveAnswerLog(String projectCode, String shopCode, String subjectCode, String score, String remark){
Cursor cursor = null;
		
		String sql = "select ProjectCode, SubjectCode, ShopCode " +
			 	 	 "FROM AnswerLog " +
			 	 	 "where ProjectCode=? " +
			 	 	 "AND SubjectCode=? " +
			 	 	 "AND ShopCode=? " +
			 	 	 "AND StatusCode='00'";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode});
		if(cursor.getCount()>0){
			sql = "UPDATE AnswerLog SET " +
				  "Score=?," +
				  "[Desc]=?," +
				  "ModiUserID=?," +
				  "ModiDateTime=datetime(), " +
				  "Flag=0 " +
				  "WHERE ProjectCode=? " +
				  "AND SubjectCode=? " +
				  "AND ShopCode=? " +
				  "AND StatusCode='00'";
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{score,remark,SqliteHelper.getCurrentUserID(),projectCode,subjectCode,shopCode});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{score,remark,SqliteHelper.getCurrentUserID(),projectCode,subjectCode,shopCode});
		}
		else {
			sql = "INSERT INTO AnswerLog(ProjectCode,SubjectCode,ShopCode,StatusCode,Score,[Desc],InUserID,InDateTime,Flag,ModiUserID,ModiDateTime) " +
					  "VALUES(?,?,?,?,?,?,?,datetime(),0,?,datetime())";
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,"00",score,remark,SqliteHelper.getCurrentUserID(),SqliteHelper.getCurrentUserID()});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,"00",score,remark,SqliteHelper.getCurrentUserID(),SqliteHelper.getCurrentUserID()});
		}
	}
	
	public void SaveAnswerDtl(String projectCode, String shopCode, String subjectCode, Map<String, String> checkOptionValues)
	{
		Cursor cursor = null;
		Set<String> keySet =checkOptionValues.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()){
			String seqNO = (String)it.next();
			String sql = "SELECT ProjectCode " +
						 "FROM AnswerDtl " +
						 "WHERE ProjectCode=? " +
						 "AND SubjectCode=? " +
						 "AND ShopCode=? " +
						 "AND SeqNO=?";
			cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO});
			if(cursor.getCount()>0){
				sql = "UPDATE AnswerDtl SET " +
						  "CheckOptionCode=?," +
						  "Flag=0 " +
					  "WHERE ProjectCode=? " +
					  "AND SubjectCode=? " +
					  "AND ShopCode=? " +
					  "AND SeqNO=?";
				SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{checkOptionValues.get(seqNO),projectCode,subjectCode,shopCode,seqNO});
				SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{checkOptionValues.get(seqNO),projectCode,subjectCode,shopCode,seqNO});
			}
			else{
				sql = "INSERT INTO AnswerDtl(ProjectCode,SubjectCode,ShopCode,SeqNO,InUserID,InDateTime,CheckOptionCode,Flag) " +
						  "VALUES(?,?,?,?,?,datetime(),?,0)";
				SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,SqliteHelper.getCurrentUserID(),checkOptionValues.get(seqNO)});
				SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,SqliteHelper.getCurrentUserID(),checkOptionValues.get(seqNO)});
			}
		}
	}
	
	public void SaveInspectionStandardImg(String projectCode, String shopCode, String subjectCode,String seqNO, String imgName){
		seqNO = "1";
		Cursor cursor = null;
		String sql = "SELECT PicName AS PicName " +
					 "FROM AnswerDtl3 " +
					 "WHERE ProjectCode=? " +
					 "AND SubjectCode=? " +
					 "AND ShopCode=? " +
					 "AND SeqNO=?";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO});
		if(cursor.getCount()>0){
			cursor.moveToFirst();
			String picName = cursor.getString(cursor.getColumnIndex("PicName"));
			sql = "UPDATE AnswerDtl3 " +
				  "SET PicName=?, " +
				  "Flag=0 " +
				  "WHERE ProjectCode=? " +
				  "AND SubjectCode=? " +
				  "AND ShopCode=? " +
				  "AND SeqNO=?";
			if(null == picName) picName = imgName;
			else picName = picName+";"+imgName;
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{picName,projectCode,subjectCode,shopCode,seqNO});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{picName,projectCode,subjectCode,shopCode,seqNO});
		}
		else{
			sql = "INSERT INTO AnswerDtl3(ProjectCode,SubjectCode,ShopCode,SeqNO,LossDesc,PicName,Flag) " +
				  "VALUES(?,?,?,?,'',?,0)";
			SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,imgName});
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,imgName});
		}
	}
	
	public void SaveAnswerDtl2(String projectCode, String shopCode, String subjectCode, Map<String, String> checkOptionValues)
	{
		Cursor cursor = null;
		Set<String> keySet =checkOptionValues.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()){
			String seqNO = (String)it.next();
			String sql = "SELECT ProjectCode " +
						 "FROM AnswerDtl2 " +
						 "WHERE ProjectCode=? " +
						 "AND SubjectCode=? " +
						 "AND ShopCode=? " +
						 "AND SeqNO=?";
			cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO});
			if(cursor.getCount()>0){
				sql = "UPDATE AnswerDtl2 SET " +
						  "CheckOptionCode=?," +
						  "ImageContent=?," +
						  "Flag=0," +
						  "FileExtend=? " +
					  "WHERE ProjectCode=? " +
					  "AND SubjectCode=? " +
					  "AND ShopCode=? " +
					  "AND SeqNO=?";
				SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{checkOptionValues.get(seqNO),"","",projectCode,subjectCode,shopCode,seqNO});
				SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{checkOptionValues.get(seqNO),"","",projectCode,subjectCode,shopCode,seqNO});
			}
			else{
				sql = "INSERT INTO AnswerDtl2(ProjectCode,SubjectCode,ShopCode,SeqNO,InUserID,InDateTime,CheckOptionCode,ImageContent,Flag,FileExtend) " +
						  "VALUES(?,?,?,?,?,datetime(),?,?,0,?)";
				SqliteHelper.execSQL_OnWriteableDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,SqliteHelper.getCurrentUserID(),checkOptionValues.get(seqNO),"",""});
				SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,SqliteHelper.getCurrentUserID(),checkOptionValues.get(seqNO),"",""});
			}
		}
	}
	
	public Cursor SearchScoreList(String projectCode, String shopCode)
	{
		//String sql = "SELECT a.SubjectCode as SubjectCode,a.OrderNO as OrderNO,a.CheckPoint as CheckPoint,b.Score as Score FROM Subjects AS a  JOIN Answer AS b ON a.ProjectCode =b.ProjectCode and a.subjectCode = b.subjectCode WHERE a.ProjectCode=? AND b.ShopCode=? and (a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL) union all select a.subjectcode,a.OrderNO,a.CheckPoint,null from Subjects a where not exists ( select 1 from Answer b  where a.ProjectCode = b.ProjectCode and a.subjectCode = b.subjectCode  AND b.ShopCode=? )   and a.ProjectCode=? and (a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL) ORDER BY a.orderNO";
		
		String sql = "SELECT a.SubjectCode as SubjectCode,"+
				"a.OrderNO as OrderNO," +
				"a.[CheckPoint] as [CheckPoint]," +
				"b.Score as Score," +
				"CASE WHEN Exists(select 1 from answerdtl3 where projectcode = b.projectCode " +
				"and shopcode = b.shopCode " +
				"and subjectCode = b.SubjectCode " +
				"AND picName IS NOT NULL " +
				"AND PicName<>'' )" +
				"THEN 'Y' " +
				"ELSE 'N' " +
				"END AS PicChk " +
				"FROM Subjects " +
				"AS a  JOIN Answer " +
				"AS b ON a.ProjectCode =b.ProjectCode and a.subjectCode = b.subjectCode " +
				"WHERE a.ProjectCode=? AND b.ShopCode=? and " +
				"(a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL) " +
				"union all select a.subjectcode,a.OrderNO, " +
				"a.[CheckPoint],null,'N' AS PicChk from Subjects a where not exists  " +
				"( select 1 from Answer b  where a.ProjectCode = b.ProjectCode and a.subjectCode = b.subjectCode  " +
				"AND b.ShopCode=? )   and a.ProjectCode=? and (a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL)  " +
				"ORDER BY a.orderNO";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode,
				shopCode, shopCode, projectCode });
		return cursor;
	}
	
	public Cursor SearchDetail(String projectCode, String subjectCode) {
		String sql = "SELECT Desc,InspectionDesc,Implementation FROM Subjects WHERE ProjectCode =? AND SubjectCode =?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode,
				subjectCode });
		return cursor;
	}
	
	public Cursor SearchImageList(String projectCode, String subjectCode) {
		String sql = "SELECT FileName FROM FileAndPicture WHERE ProjectCode =? AND SubjectCode =?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode,
				subjectCode });
		return cursor;
	}
	
	public Cursor SearchScoreListSortByCol(String projectCode, String shopCode,String col,String type)
	{
		if(col == null || col == ""){
			col = "a.SubjectCode";
		}
		if(type == null || type == ""){
			type = "DESC";
		}
		String sql = "SELECT a.SubjectCode as SubjectCode,a.OrderNO as OrderNO,a.CheckPoint as CheckPoint,b.Score as Score FROM Subjects AS a  JOIN Answer AS b ON a.ProjectCode =b.ProjectCode and a.subjectCode = b.subjectCode WHERE a.ProjectCode=? AND b.ShopCode=? and (a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL) union all select a.subjectcode,a.OrderNO,a.CheckPoint,null from Subjects a where not exists ( select 1 from Answer b  where a.ProjectCode = b.ProjectCode and a.subjectCode = b.subjectCode  AND b.ShopCode=? )   and a.ProjectCode=?  and (a.SubjectDelChk = 0 OR a.SubjectDelChk IS NULL)  ORDER BY " + col + " " + type;
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode,
				shopCode, shopCode, projectCode });
		return cursor;
	}
	
	public Cursor SearchInspectionStandard(String projectCode, String subjectCode, String seqNo){
		String sql = "SELECT InspectionStandardName " +
					 "FROM InspectionStandard " +
					 "WHERE projectcode=? " +
					 "AND subjectcode=? " +
					 "AND seqno=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectCode, seqNo});
	
		return cursor;
	}
	public Cursor SearchInspectionStandardImageList(String projectCode, String shopCode, String subjectCode,String seqNO)
	{
		seqNO = "1";
		String sql = "SELECT PicName " +
					 "FROM AnswerDtl3 " +
					 "WHERE ProjectCode=? " +
					 "AND SubjectCode=? " +
					 "AND ShopCode=? " +
					 "AND SeqNO=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO});
	
		return cursor;

	}
	public Cursor SearchInspectionImg(String projectCode, String subjectCode, String seqNO)
	{
		String sql = "select FileName " +
					 "from FileAndPicture " +
					 "where projectcode=? " +
					 "and subjectcode=? " +
					 "and seqno=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode, subjectCode, seqNO});
	
		return cursor;
	}

	//同步数据：Writeable.db -> ReadOnly.db
	public void SyncData(){
		//Sync Answer
		{
			Cursor cursor = null;
			String sql = "DELETE FROM Answer";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{});
			sql = "SELECT * FROM Answer ";
			cursor= SqliteHelper.execSQL_OnWriteableDB_SELECT(sql, new String[]{});
			SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
			try{
				while (cursor.moveToNext()) {
					String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode"));
					String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
					String shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
					String score = cursor.getString(cursor.getColumnIndex("Score"));
					String inUserID = cursor.getString(cursor.getColumnIndex("InUserID"));
					String inDateTime = cursor.getString(cursor.getColumnIndex("InDateTime"));
					String remark = cursor.getString(cursor.getColumnIndex("Remark"));
					String assessmentDate = cursor.getString(cursor.getColumnIndex("AssessmentDate"));
					int flag = cursor.getInt(cursor.getColumnIndex("Flag"));
					SyncAnswer(projectCode, shopCode, subjectCode, score, remark, assessmentDate, flag, inUserID, inDateTime);
				}
				SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
			}finally{
				SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
			}
		}
		
		//Sync AnswerLog
		{
			Cursor cursor = null;
			String sql = "DELETE FROM AnswerLog";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{});
			sql = "SELECT * FROM AnswerLog ";
			cursor= SqliteHelper.execSQL_OnWriteableDB_SELECT(sql, new String[]{});
			SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
			try{
				while (cursor.moveToNext()) {
					String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode"));
					String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
					String shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
					String statusCode = cursor.getString(cursor.getColumnIndex("StatusCode"));
					String score = cursor.getString(cursor.getColumnIndex("Score"));
					String remark = cursor.getString(cursor.getColumnIndex("Desc"));
					String inUserID = cursor.getString(cursor.getColumnIndex("InUserID"));
					String inDateTime = cursor.getString(cursor.getColumnIndex("InDateTime"));
					int flag = cursor.getInt(cursor.getColumnIndex("Flag"));
					String modiUserID = cursor.getString(cursor.getColumnIndex("ModiUserID"));
					String modiDateTime = cursor.getString(cursor.getColumnIndex("ModiDateTime"));
					SyncAnswerLog(projectCode, shopCode, subjectCode, statusCode, score, remark, inUserID, inDateTime, flag, modiUserID, modiDateTime);
				}
				SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
			}finally{
				SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
			}
		}
		
		//Sync AnswerDtl
		{
			Cursor cursor = null;
			String sql = "DELETE FROM AnswerDtl";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{});
			sql = "SELECT * FROM AnswerDtl ";
			cursor= SqliteHelper.execSQL_OnWriteableDB_SELECT(sql, new String[]{});
			SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
			try{
				while (cursor.moveToNext()) {
					String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode"));
					String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
					String shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
					String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
					String inUserID = cursor.getString(cursor.getColumnIndex("InUserID"));
					String inDateTime = cursor.getString(cursor.getColumnIndex("InDateTime"));
					String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
					String picNameList = cursor.getString(cursor.getColumnIndex("PicNameList"));
					int flag = cursor.getInt(cursor.getColumnIndex("Flag"));
					SyncAnswerDtl(projectCode, shopCode, subjectCode, seqNO, inUserID, inDateTime, checkOptionCode, picNameList, flag);
				}
				SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
			}finally{
				SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
			}
		}
		
		//Sync AnswerDtl2
		{
			Cursor cursor = null;
			String sql = "DELETE FROM AnswerDtl2";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{});
			sql = "SELECT * FROM AnswerDtl2 ";
			cursor= SqliteHelper.execSQL_OnWriteableDB_SELECT(sql, new String[]{});
			SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
			try{
				while (cursor.moveToNext()) {
					String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode"));
					String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
					String shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
					String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
					String inUserID = cursor.getString(cursor.getColumnIndex("InUserID"));
					String inDateTime = cursor.getString(cursor.getColumnIndex("InDateTime"));
					String checkOptionCode = cursor.getString(cursor.getColumnIndex("CheckOptionCode"));
					String fileExtend = cursor.getString(cursor.getColumnIndex("FileExtend"));
					int flag = cursor.getInt(cursor.getColumnIndex("Flag"));
					SyncAnswerDtl2(projectCode, shopCode, subjectCode, seqNO, inUserID, inDateTime, checkOptionCode, fileExtend, flag);
				}
				SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
			}finally{
				SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
			}
		}
		
		//Sync AnswerDtl3
		//下面这段，加到SyncData()里
		//Sync AnswerDtl3
				{
					Cursor cursor = null;
					String sql = "DELETE FROM AnswerDtl3";
					SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{});
					sql = "SELECT * FROM AnswerDtl3 ";
					cursor= SqliteHelper.execSQL_OnWriteableDB_SELECT(sql, new String[]{});
					SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
					try{
						while (cursor.moveToNext()) {
							String projectCode = cursor.getString(cursor.getColumnIndex("ProjectCode"));
							String subjectCode = cursor.getString(cursor.getColumnIndex("SubjectCode"));
							String shopCode = cursor.getString(cursor.getColumnIndex("ShopCode"));
							String seqNO = cursor.getString(cursor.getColumnIndex("SeqNO"));
							String lossDesc = cursor.getString(cursor.getColumnIndex("LossDesc"));
							String picName = cursor.getString(cursor.getColumnIndex("PicName"));
							int flag = cursor.getInt(cursor.getColumnIndex("Flag"));
							SyncAnswerDtl3(projectCode, shopCode, subjectCode, seqNO, lossDesc, picName, flag);
						}
						SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
					}finally{
						SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
					}
				}
	}
	public void SyncAnswer(String projectCode, String shopCode, String subjectCode, String score, String remark, String assessmentDate, int flag, String inUserID, String inDateTime)
	{
		String sql = "INSERT INTO Answer(ProjectCode,SubjectCode,ShopCode,Score,InUserID,InDateTime,Remark,ImageName,AssessmentDate,Flag) " +
				  "VALUES(?,?,?,?,?,?,?,?,?,"+flag+")";
		SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
				projectCode,subjectCode,shopCode,score,inUserID,inDateTime,remark,"",assessmentDate});
	}
	public void SyncAnswerLog(String projectCode, String shopCode, String subjectCode, String statusCode, String score, String remark, String inUserID, String inDateTime, int flag, String modiUserID, String modiDateTime){
		String sql = "INSERT INTO AnswerLog(ProjectCode,SubjectCode,ShopCode,StatusCode,Score,[Desc],InUserID,InDateTime,Flag,ModiUserID,ModiDateTime) " +
				  "VALUES(?,?,?,?,?,?,?,?,"+flag+",?,?)";
		SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
				projectCode,subjectCode,shopCode,statusCode,score,remark,inUserID,inDateTime,modiUserID,modiDateTime});
	}
	public void SyncAnswerDtl(String projectCode, String shopCode, String subjectCode, String seqNO, String inUserID, String inDateTime, String checkOptionCode, String picNameList, int flag)
	{
		String sql = "INSERT INTO AnswerDtl(ProjectCode,SubjectCode,ShopCode,SeqNO,InUserID,InDateTime,CheckOptionCode,PicNameList,Flag) " +
				  "VALUES(?,?,?,?,?,?,?,?,"+flag+")";
		SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,inUserID,inDateTime,checkOptionCode,picNameList});
	}
	public void SyncAnswerDtl2(String projectCode, String shopCode, String subjectCode, String seqNO, String inUserID, String inDateTime, String checkOptionCode, String fileExtend, int flag)
	{
		String sql = "INSERT INTO AnswerDtl2(ProjectCode,SubjectCode,ShopCode,SeqNO,InUserID,InDateTime,CheckOptionCode,Flag,FileExtend) " +
				  "VALUES(?,?,?,?,?,?,?,"+flag+",?)";
		SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,inUserID,inDateTime,checkOptionCode,fileExtend});
	}

	//从WebService获取更新数据
	public void SyncDataForWebService(){
		Dao _dao = new Dao();
		//SaveProjects
		ArrayList<HashMap<String, String>> projectArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllProjectsForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> project : projectArrayList) {
				_dao.SaveProjects(project);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveChapters
		ArrayList<HashMap<String, String>> chapterArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllChaptersForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> chapter : chapterArrayList) {
				_dao.SaveChapters(chapter);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveCharterLink
		ArrayList<HashMap<String, String>> charterLinkArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllCharterLinkForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> charterLink : charterLinkArrayList) {
				_dao.SaveCharterLink(charterLink);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveSubjects
		ArrayList<HashMap<String, String>> subjectArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllSubjectsForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> subject : subjectArrayList) {
				_dao.SaveSubjects(subject);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		_dao = new Dao();
		//SaveLossResult
		ArrayList<HashMap<String, String>> lossResultArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllLossResultForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> lossResult : lossResultArrayList) {
				_dao.SaveLossResult(lossResult);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveFileAndPicture
		ArrayList<HashMap<String, String>> fileAndPictureArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllFileAndPictureForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> fileAndPicture : fileAndPictureArrayList) {
				_dao.SaveFileAndPicture(fileAndPicture);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveScoreSet
		ArrayList<HashMap<String, String>> scoreSetArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllScoreSetForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> scoreSet : scoreSetArrayList) {
				_dao.SaveScoreSet(scoreSet);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveShopProjects
		ArrayList<HashMap<String, String>> shopProjectsArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllShopProjectsForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> shopProjects : shopProjectsArrayList) {
				_dao.SaveShopProjects(shopProjects);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveUserInfo
		ArrayList<HashMap<String, String>> userInfoArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllUserInfoForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> userInfo : userInfoArrayList) {
				_dao.SaveUserInfo(userInfo);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveSpecialCase
		ArrayList<HashMap<String, String>> specialCaseArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllSpecialCaseForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> specialCase : specialCaseArrayList) {
				_dao.SaveSpecialCase(specialCase);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveArea
		ArrayList<HashMap<String, String>> areaArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllAreaForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> area : areaArrayList) {
				_dao.SaveArea(area);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveAreaShop
		ArrayList<HashMap<String, String>> areaShopArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllAreaShopForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> areaShop : areaShopArrayList) {
				_dao.SaveAreaShop(areaShop);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveShop
		ArrayList<HashMap<String, String>> shopArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllShopForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> shop : shopArrayList) {
				_dao.SaveShop(shop);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
		
		//SaveInspectionStandard
		ArrayList<HashMap<String, String>> inspectionStandardArrayList = WebServiceUtil.CallWebServiceMethod("SearchAllInspectionStandardForPad", null);
		SqliteHelper.beginTransaction_OnReadOnlyDB();//开始事务
		try{
			for (HashMap<String, String> inspectionStandard : inspectionStandardArrayList) {
				_dao.SaveInspectionStandard(inspectionStandard);
			}
			SqliteHelper.setTransactionSuccessful_OnReadOnlyDB();//设置成功完成事务
		}finally{
			SqliteHelper.endTransaction_OnReadOnlyDB(); //处理完成
		}
	}
	public void SaveProjects(HashMap<String, String> project)
	{
		Cursor cursor = null;
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM Projects " +
			 	 	 "where ProjectCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				project.get("ProjectCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Projects SET " +
				  "ProjectName=?," +
				  "InUserID=?," +
				  "InDateTime=?," +
				  "Year=?," +
				  "Quarter=?," +
				  "OrderNO=? " +
				  "WHERE ProjectCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					project.get("ProjectName"),
					project.get("InUserID"),
					project.get("InDateTime"),
					project.get("Year"),
					project.get("Quarter"),
					project.get("OrderNO"),
					project.get("ProjectCode")});
		}
		else {
			sql = "INSERT INTO Projects(ProjectCode," +
									   "ProjectName," +
									   "InUserID," +
									   "InDateTime," +
									   "Year," +
									   "Quarter," +
									   "OrderNO) " +
					  "VALUES(?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					project.get("ProjectCode"),
					project.get("ProjectName"),
					project.get("InUserID"),
					project.get("InDateTime"),
					project.get("Year"),
					project.get("Quarter"),
					project.get("OrderNO")});
		}
		cursor.close();
	}
	public void SaveChapters(HashMap<String, String> chapter)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM Chapters " +
			 	 	 "where ProjectCode=? AND CharterCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				chapter.get("ProjectCode"),chapter.get("CharterCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Chapters SET " +
				  "CharterName=?," +
				  "CharterContent=?," +
				  "OrderNo=?," +
				  "InUserID=?," +
				  "InDateTime=?," +
				  "Weight=? " +
				  "WHERE ProjectCode=? AND CharterCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					chapter.get("CharterName"),
					chapter.get("CharterContent"),
					chapter.get("OrderNo"),
					chapter.get("InUserID"),
					chapter.get("InDateTime"),
					chapter.get("Weight"),
					chapter.get("ProjectCode"),
					chapter.get("CharterCode"),});
		}
		else {
			sql = "INSERT INTO Chapters(ProjectCode," +
									   "CharterCode," +
									   "CharterName," +
									   "CharterContent," +
									   "OrderNo," +
									   "InUserID," +
									   "InDateTime," +
									   "Weight) " +
					  "VALUES(?,?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					chapter.get("ProjectCode"),
					chapter.get("CharterCode"),
					chapter.get("CharterName"),
					chapter.get("CharterContent"),
					chapter.get("OrderNo"),
					chapter.get("InUserID"),
					chapter.get("InDateTime"),
					chapter.get("Weight")});
		}
		cursor.close();
	}
	public void SaveCharterLink(HashMap<String, String> charterLink)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM Charter_Link " +
			 	 	 "where ProjectCode=? AND CharterCode=? AND LinkCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				charterLink.get("ProjectCode"),
				charterLink.get("CharterCode"),
				charterLink.get("LinkCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Charter_Link SET " +
				  "LinkName=?," +
				  "LinkContent=?," +
				  "InUserID=?," +
				  "InDateTime=?," +
				  "OrderNO=? "+
				  "WHERE ProjectCode=? AND CharterCode=? AND LinkCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					charterLink.get("LinkName"),
					charterLink.get("LinkContent"),
					charterLink.get("InUserID"),
					charterLink.get("InDateTime"),
					charterLink.get("OrderNO"),
					charterLink.get("ProjectCode"),
					charterLink.get("CharterCode"),
					charterLink.get("LinkCode")});
		}
		else {
			sql = "INSERT INTO Charter_Link(ProjectCode," +
									   "CharterCode," +
									   "LinkCode," +
									   "LinkName," +
									   "LinkContent," +
									   "InUserID," +
									   "InDateTime," +
									   "OrderNO) " +
					  "VALUES(?,?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					charterLink.get("ProjectCode"),
					charterLink.get("CharterCode"),
					charterLink.get("LinkCode"),
					charterLink.get("LinkName"),
					charterLink.get("LinkContent"),
					charterLink.get("InUserID"),
					charterLink.get("InDateTime"),
					charterLink.get("OrderNO")});
		}
		cursor.close();
	}
	
	public void SaveSubjects(HashMap<String, String> subject)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM Subjects " +
			 	 	 "where ProjectCode=? AND SubjectCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				subject.get("ProjectCode"),
				subject.get("SubjectCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Subjects SET " +
				  "FullScore=?," +
				  "Implementation=?," +
				  "CheckPoint=?," +
				  "Desc=?," +
				  "AdditionalDesc=?," +
				  "InspectionDesc=?," +
				  "InspectionNeedFile=?," +
				  "Remark=?," +
				  "LinkCode=?," +
				  "OrderNO=?," +
				  "FCheck=?," +
				  "InUserID=?," +
				  "InDateTime=?," +
				  "ScoreCheck=?," +
				  "SubjectTypeCode=?," +
				  "SubjectTypeCodeExam=?," +
				  "SubjectDelChk=?," +
				  "AddErrorChk=?," +
				  "ScoreInspection=? "+
				  "WHERE ProjectCode=? AND SubjectCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					subject.get("FullScore"),
					subject.get("Implementation"),
					subject.get("CheckPoint"),
					subject.get("Desc"),
					subject.get("AdditionalDesc"),
					subject.get("InspectionDesc"),
					subject.get("InspectionNeedFile"),
					subject.get("Remark"),
					subject.get("LinkCode"),
					subject.get("OrderNO"),
					subject.get("FCheck")==null?null:subject.get("FCheck").equals("true")?"1":"0",
					subject.get("InUserID"),
					subject.get("InDateTime"),
					subject.get("ScoreCheck")==null?null:subject.get("ScoreCheck").equals("true")?"1":"0",
					subject.get("SubjectTypeCode"),
					subject.get("SubjectTypeCodeExam"),
					subject.get("SubjectDelChk")==null?null:subject.get("SubjectDelChk").equals("true")?"1":"0",
					subject.get("AddErrorChk")==null?null:subject.get("AddErrorChk").equals("true")?"1":"0",
					subject.get("ProjectCode"),
					subject.get("SubjectCode"),
					subject.get("ScoreInspection")});
		}
		else {
			sql = "INSERT INTO Subjects(ProjectCode," +
									   "SubjectCode," +
									   "FullScore," +
									   "Implementation," +
									   "CheckPoint," +
									   "Desc," +
									   "AdditionalDesc," +
									   "InspectionDesc," +
									   "InspectionNeedFile," +
									   "Remark," +
									   "LinkCode," +
									   "OrderNO," +
									   "FCheck," +
									   "InUserID," +
									   "InDateTime," +
									   "ScoreCheck," +
									   "SubjectTypeCode," +
									   "SubjectTypeCodeExam," +
									   "SubjectDelChk," +
									   "AddErrorChk," +
									   "ScoreInspection=? ) " +
					  "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					subject.get("ProjectCode"),
					subject.get("SubjectCode"),
					subject.get("FullScore"),
					subject.get("Implementation"),
					subject.get("CheckPoint"),
					subject.get("Desc"),
					subject.get("AdditionalDesc"),
					subject.get("InspectionDesc"),
					subject.get("InspectionNeedFile"),
					subject.get("Remark"),
					subject.get("LinkCode"),
					subject.get("OrderNO"),
					subject.get("FCheck")==null?null:subject.get("FCheck").equals("true")?"1":"0",
					subject.get("InUserID"),
					subject.get("InDateTime"),
					subject.get("ScoreCheck")==null?null:subject.get("ScoreCheck").equals("true")?"1":"0",
					subject.get("SubjectTypeCode"),
					subject.get("SubjectTypeCodeExam"),
					subject.get("SubjectDelChk")==null?null:subject.get("SubjectDelChk").equals("true")?"1":"0",
					subject.get("AddErrorChk")==null?null:subject.get("AddErrorChk").equals("true")?"1":"0",
					subject.get("ScoreInspection")});
		}
		cursor.close();
	}

	
	public void SaveInspectionStandard(HashMap<String, String> inspectionStandard)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM InspectionStandard " +
			 	 	 "where ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				inspectionStandard.get("ProjectCode"),
				inspectionStandard.get("SubjectCode"),
				inspectionStandard.get("SeqNO")});
		if(cursor.moveToNext()){
			sql = "UPDATE InspectionStandard SET " +
				  "InspectionStandardName=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					inspectionStandard.get("InspectionStandardName"),
					inspectionStandard.get("InUserID"),
					inspectionStandard.get("InDateTime"),
					inspectionStandard.get("ProjectCode"),
					inspectionStandard.get("SubjectCode"),
					inspectionStandard.get("SeqNO")});
		}
		else {
			sql = "INSERT INTO InspectionStandard(ProjectCode," +
									   			 "SubjectCode," +
												 "SeqNO," +
												 "InspectionStandardName," +
												 "InUserID," +
												 "InDateTime) " +
					  "VALUES(?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					inspectionStandard.get("ProjectCode"),
					inspectionStandard.get("SubjectCode"),
					inspectionStandard.get("SeqNO"),
					inspectionStandard.get("InspectionStandardName"),
					inspectionStandard.get("InUserID"),
					inspectionStandard.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveLossResult(HashMap<String, String> lossResult)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM LossResult " +
			 	 	 "where ProjectCode=? AND SubjectCode=? AND LossCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				lossResult.get("ProjectCode"),
				lossResult.get("SubjectCode"),
				lossResult.get("LossCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE LossResult SET " +
				  "LossName=?," +
				  "LossType=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE ProjectCode=? AND SubjectCode=? AND LossCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					lossResult.get("LossName"),
					lossResult.get("LossType"),
					lossResult.get("InUserID"),
					lossResult.get("InDateTime"),
					lossResult.get("ProjectCode"),
					lossResult.get("SubjectCode"),
					lossResult.get("LossCode")});
		}
		else {
			sql = "INSERT INTO LossResult(ProjectCode," +
									   	 "SubjectCode," +
										 "LossCode," +
										 "LossName," +
										 "LossType," +
										 "InUserID," +
										 "InDateTime) " +
					  "VALUES(?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					lossResult.get("ProjectCode"),
					lossResult.get("SubjectCode"),
					lossResult.get("LossCode"),
					lossResult.get("LossName"),
					lossResult.get("LossType"),
					lossResult.get("InUserID"),
					lossResult.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveFileAndPicture(HashMap<String, String> fileAndPicture)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM FileAndPicture " +
			 	 	 "where ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				fileAndPicture.get("ProjectCode"),
				fileAndPicture.get("SubjectCode"),
				fileAndPicture.get("SeqNO")});
		if(cursor.moveToNext()){
			sql = "UPDATE FileAndPicture SET " +
				  "FileName=?," +
				  "FileType=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					fileAndPicture.get("FileName"),
					fileAndPicture.get("FileType"),
					fileAndPicture.get("InUserID"),
					fileAndPicture.get("InDateTime"),
					fileAndPicture.get("ProjectCode"),
					fileAndPicture.get("SubjectCode"),
					fileAndPicture.get("SeqNO")});
		}
		else {
			sql = "INSERT INTO FileAndPicture(" +
				  "ProjectCode," +
			   	  "SubjectCode," +
			   	  "SeqNO," +
			   	  "FileName," +
			   	  "FileType," +
			   	  "InUserID," +
			      "InDateTime" +
			   	  ") " +
				  "VALUES(?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					fileAndPicture.get("ProjectCode"),
					fileAndPicture.get("SubjectCode"),
					fileAndPicture.get("SeqNO"),
					fileAndPicture.get("FileName"),
					fileAndPicture.get("FileType"),
					fileAndPicture.get("InUserID"),
					fileAndPicture.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveScoreSet(HashMap<String, String> scoreSet)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM ScoreSet " +
			 	 	 "where ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				scoreSet.get("ProjectCode"),
				scoreSet.get("SubjectCode"),
				scoreSet.get("SeqNO")});
		if(cursor.moveToNext()){
			sql = "UPDATE ScoreSet SET " +
				  "Score=?," +
				  "NotInvolved=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE ProjectCode=? AND SubjectCode=? AND SeqNO=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					scoreSet.get("Score"),
					scoreSet.get("NotInvolved")==null?null:scoreSet.get("NotInvolved").equals("true")?"1":"0",
					scoreSet.get("InUserID"),
					scoreSet.get("InDateTime"),
					scoreSet.get("ProjectCode"),
					scoreSet.get("SubjectCode"),
					scoreSet.get("SeqNO")});
		}
		else {
			sql = "INSERT INTO ScoreSet(" +
				  "ProjectCode," +
			   	  "SubjectCode," +
			   	  "SeqNO," +
			   	  "Score," +
			   	  "NotInvolved," +
			   	  "InUserID," +
			      "InDateTime" +
			   	  ") " +
				  "VALUES(?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					scoreSet.get("ProjectCode"),
					scoreSet.get("SubjectCode"),
					scoreSet.get("SeqNO"),
					scoreSet.get("Score"),
					scoreSet.get("NotInvolved")==null?null:scoreSet.get("NotInvolved").equals("true")?"1":"0",
					scoreSet.get("InUserID"),
					scoreSet.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveShopProjects(HashMap<String, String> shopProjects)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ProjectCode " +
			 	 	 "FROM ShopProjects " +
			 	 	 "where ProjectCode=? AND ShopCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				shopProjects.get("ProjectCode"),
				shopProjects.get("ShopCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE ShopProjects SET " +
				  "SubjectTypeCodeExam=?," +
				  "CheckUserId=?," +
				  "CheckDate=? " +
				  "WHERE ProjectCode=? AND ShopCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					shopProjects.get("SubjectTypeCodeExam"),
					shopProjects.get("CheckUserId"),
					shopProjects.get("CheckDate"),
					shopProjects.get("ProjectCode"),
					shopProjects.get("ShopCode")});
		}
		else {
			sql = "INSERT INTO ShopProjects(" +
				  "ProjectCode," +
			   	  "ShopCode," +
			   	  "SubjectTypeCodeExam," +
			   	  "CheckUserId," +
			      "CheckDate" +
			   	  ") " +
				  "VALUES(?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					shopProjects.get("ProjectCode"),
					shopProjects.get("ShopCode"),
					shopProjects.get("SubjectTypeCodeExam"),
					shopProjects.get("CheckUserId"),
					shopProjects.get("CheckDate")});
		}
		cursor.close();
	}
	public void SaveUserInfo(HashMap<String, String> userInfo)
	{
		Cursor cursor = null;
		
		String sql = "SELECT UserID " +
			 	 	 "FROM UserInfo " +
			 	 	 "where UserID=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				userInfo.get("UserID")});
		if(cursor.moveToNext()){
			sql = "UPDATE UserInfo SET " +
				  "PSW=?," +
				  "RoleType=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE UserID=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					userInfo.get("PSW"),
					userInfo.get("RoleType"),
					userInfo.get("InUserID"),
					userInfo.get("InDateTime"),
					userInfo.get("UserID")});
		}
		else {
			sql = "INSERT INTO UserInfo(" +
				  "UserID," +
			   	  "PSW," +
			   	  "RoleType," +
			   	  "InUserID," +
			      "InDateTime" +
			   	  ") " +
				  "VALUES(?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					userInfo.get("UserID"),
					userInfo.get("PSW"),
					userInfo.get("RoleType"),
					userInfo.get("InUserID"),
					userInfo.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveSpecialCase(HashMap<String, String> specialCase)
	{
		Cursor cursor = null;
		
		String sql = "SELECT SpecialCaseCode " +
			 	 	 "FROM SpecialCase " +
			 	 	 "where SpecialCaseCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				specialCase.get("SpecialCaseCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE SpecialCase SET " +
				  "ProjectCode=?," +
				  "ShopCode=?," +
				  "SubjectCode=?," +
				  "Title=?," +
				  "ApplyDesc=?," +
				  "FinalAdvice=?," +
				  "FinalStatus=?," +
				  "ImageName=?," +
				  "ApplyId=?," +
				  "ApplyDate=?," +
				  "ConfirmId=?," +
				  "ConfirmDate=?," +
				  "IndateTime=?," +
				  "InUserID=?," +
				  "NeedVICoConfirmChk=?," +
				  "VICoAdvice=? " +
				  "WHERE SpecialCaseCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					specialCase.get("ProjectCode"),
					specialCase.get("ShopCode"),
					specialCase.get("SubjectCode"),
					specialCase.get("Title"),
					specialCase.get("ApplyDesc"),
					specialCase.get("FinalAdvice"),
					specialCase.get("FinalStatus"),
					specialCase.get("ImageName"),
					specialCase.get("ApplyId"),
					specialCase.get("ApplyDate"),
					specialCase.get("ConfirmId"),
					specialCase.get("ConfirmDate"),
					specialCase.get("IndateTime"),
					specialCase.get("InUserID"),
					specialCase.get("NeedVICoConfirmChk")==null?null:specialCase.get("NeedVICoConfirmChk").equals("true")?"1":"0",
					specialCase.get("VICoAdvice"),
					specialCase.get("SpecialCaseCode")});
		}
		else {
			sql = "INSERT INTO SpecialCase(SpecialCaseCode," +
										  "ProjectCode," +
										  "ShopCode," +
										  "SubjectCode," +
										  "Title," +
										  "ApplyDesc," +
										  "FinalAdvice," +
										  "FinalStatus," +
										  "ImageName," +
										  "ApplyId," +
										  "ApplyDate," +
										  "ConfirmId," +
										  "ConfirmDate," +
										  "IndateTime," +
										  "InUserID," +
										  "NeedVICoConfirmChk," +
										  "VICoAdvice) " +
					  "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					specialCase.get("SpecialCaseCode"),
					specialCase.get("ProjectCode"),
					specialCase.get("ShopCode"),
					specialCase.get("SubjectCode"),
					specialCase.get("Title"),
					specialCase.get("ApplyDesc"),
					specialCase.get("FinalAdvice"),
					specialCase.get("FinalStatus"),
					specialCase.get("ImageName"),
					specialCase.get("ApplyId"),
					specialCase.get("ApplyDate"),
					specialCase.get("ConfirmId"),
					specialCase.get("ConfirmDate"),
					specialCase.get("IndateTime"),
					specialCase.get("InUserID"),
					specialCase.get("NeedVICoConfirmChk")==null?null:specialCase.get("NeedVICoConfirmChk").equals("true")?"1":"0",
					specialCase.get("VICoAdvice")});
		}
		cursor.close();
	}
	public void SaveArea(HashMap<String, String> area)
	{
		Cursor cursor = null;
		
		String sql = "SELECT AreaCode " +
			 	 	 "FROM Area " +
			 	 	 "where AreaCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				area.get("AreaCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Area SET " +
				  "AreaName=?," +
				  "UpperAreaCode=?," +
				  "AreaTypeCode=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE AreaCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					area.get("AreaName"),
					area.get("UpperAreaCode"),
					area.get("AreaTypeCode"),
					area.get("InUserID"),
					area.get("InDateTime"),
					area.get("AreaCode")});
		}
		else {
			sql = "INSERT INTO Area(" +
				  "AreaCode," +
			   	  "AreaName," +
			   	  "UpperAreaCode," +
			   	  "AreaTypeCode," +
			   	  "InUserID," +
			      "InDateTime" +
			   	  ") " +
				  "VALUES(?,?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					area.get("AreaCode"),
					area.get("AreaName"),
					area.get("UpperAreaCode"),
					area.get("AreaTypeCode"),
					area.get("InUserID"),
					area.get("InDateTime")});
		}
		cursor.close();
	}
	public void SaveAreaShop(HashMap<String, String> areaShop)
	{
		Cursor cursor = null;
		
		String sql = "SELECT AreaCode " +
			 	 	 "FROM AreaShop " +
			 	 	 "where AreaCode=? AND ShopCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				areaShop.get("AreaCode"),
				areaShop.get("ShopCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE AreaShop SET " +
				  "Remark=? " +
				  "WHERE AreaCode=? AND ShopCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					areaShop.get("Remark"),
					areaShop.get("AreaCode"),
					areaShop.get("ShopCode")});
		}
		else {
			sql = "INSERT INTO AreaShop(" +
				  "AreaCode," +
			   	  "ShopCode," +
			   	  "Remark" +
			   	  ") " +
				  "VALUES(?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					areaShop.get("AreaCode"),
					areaShop.get("ShopCode"),
					areaShop.get("Remark")});
		}
		cursor.close();
	}
	public void SaveShop(HashMap<String, String> shop)
	{
		Cursor cursor = null;
		
		String sql = "SELECT ShopCode " +
			 	 	 "FROM Shop " +
			 	 	 "where ShopCode=? ";
		cursor= SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{
				shop.get("ShopCode")});
		if(cursor.moveToNext()){
			sql = "UPDATE Shop SET " +
				  "ShopName=?," +
				  "UseChk=?," +
				  "InUserID=?," +
				  "InDateTime=? " +
				  "WHERE ShopCode=? ";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					shop.get("ShopName"),
					shop.get("UseChk")==null?null:shop.get("UseChk").equals("true")?"1":"0",
					shop.get("InUserID"),
					shop.get("InDateTime"),
					shop.get("ShopCode")});
		}
		else {
			sql = "INSERT INTO Shop(" +
				  "ShopCode," +
			   	  "ShopName," +
			   	  "UseChk," +
			   	  "InUserID," +
			      "InDateTime" +
			   	  ") " +
				  "VALUES(?,?,?,?,?)";
			SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{
					shop.get("ShopCode"),
					shop.get("ShopName"),
					shop.get("UseChk")==null?null:shop.get("UseChk").equals("true")?"1":"0",
					shop.get("InUserID"),
					shop.get("InDateTime")
					});
		}
		cursor.close();
	}
	
	public Cursor SearchFullScoreANDScoreInspection(String projectCode, String subjectCode){
		String sql = "select FullScore,ScoreInspection from Subjects WHERE ProjectCode = ? AND SubjectCode = ?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[] { projectCode, subjectCode});
		return cursor;
	}
	
	//下面这段，是属于//同步数据：Writeable.db -> ReadOnly.db
	public void SyncAnswerDtl3(String projectCode, String shopCode, String subjectCode, String seqNO, String lossDesc, String picName, int flag)
	{
		String sql = "INSERT INTO AnswerDtl3(ProjectCode,SubjectCode,ShopCode,SeqNO,LossDesc,PicName,Flag) " +
				  "VALUES(?,?,?,?,?,?,"+flag+")";
		SqliteHelper.execSQL_OnReadOnlyDB_NOTSELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO,lossDesc,picName});
	}
	
	public Cursor SearchInspectionStandardImageList_NEw(String projectCode, String shopCode, String subjectCode,String seqNO)
	{
		seqNO = "1";
		String sql = "SELECT PicName " +
					 "FROM AnswerDtl3 " +
					 "WHERE ProjectCode=? " +
					 "AND SubjectCode=? " +
					 "AND ShopCode=? " +
					 "AND SeqNO=?";
		Cursor cursor = SqliteHelper.execSQL_OnReadOnlyDB_SELECT(sql, new String[]{projectCode,subjectCode,shopCode,seqNO});
	
		return cursor;
	}

}
