package com.example.snsmap;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class NumToResources {
	//iconナンバーを指定したら対象のiconリソースが帰ってくるクラス
	
	private static List<Integer> list = new ArrayList<Integer>(); 
	
	static{
		for(IconListData i:IconListData.values()){
			list.add(i.getId());
		}
	}
	
	public static Integer toResources(Integer num){
		return list.get(num-1);
	}

	private NumToResources(){
		
	}
}
