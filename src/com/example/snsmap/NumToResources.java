package com.example.snsmap;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class NumToResources {
	//icon�i���o�[���w�肵����Ώۂ�icon���\�[�X���A���Ă���N���X
	
	private static List<Integer> list = new ArrayList<Integer>(); 
	
	static{
		for(IconListData i:IconListData.values()){
			list.add(i.getId());
		}
	}
	
	public static Integer toResources(Integer num){
		if(num >= list.size())
			num = 0;
		return list.get(num);
	}

	private NumToResources(){
		
	}
}
