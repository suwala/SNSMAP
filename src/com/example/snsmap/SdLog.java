package com.example.snsmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Environment;

/*
 * コンストラクタで保存した文字列を
 * txtファイルで出力するクラス
 * 
 * String String[] ArrayList<String>に対応
 * getNameで保存したパス込みファイル名を返す
 * 
 * 
 */
public class SdLog{
	
	private ArrayList<String> arrayList= new ArrayList<String>();
	private String filePath;
	public  SdLog(String[] str){
		for(String str2:str){
			this.arrayList.add(str2);
		}
	}
	
	public SdLog(String str){
		this.arrayList.add(str);
	}
	
	public SdLog(ArrayList<String> arrayList){
		this.arrayList = arrayList;
	}

	public String getFileName(){
		return this.filePath;
	}
	
	public void outFile(){
		//SDカードのディレクトリパス
		File sdcardPath = new File(Environment.getExternalStorageDirectory().getPath()+ "/MapTest/");
		
		//ファイル名用フォーマット
		Date todya = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

		//パス区切りようセパレータ
		String fs = File.separator;

		//テキストファイル保存先のファイルパス
		String filePath = sdcardPath + fs + dateFormat.format(todya)+ ".txt";

		//フォルダがなければ作成
		if(!sdcardPath.exists()){
			sdcardPath.mkdir();
		}
						
		try{
			Integer i =1;
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
			for(String str:this.arrayList){
				pw.write(String.format("%02d_%s\n",i,str));
				pw.flush();
				i++;
				
			}
			
			
			this.filePath = filePath;
			
			pw.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	

}
