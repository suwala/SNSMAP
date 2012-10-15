package com.example.snsmap;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TimeList extends ListActivity{

	/*
	 * ListActivity���p��
	 * Activity���p�������ꍇ�ƋL�q���ς��
	 * ListView list ���g�킸��
	 * ListActivity�𒼐ڃI�[�o�[���C�h����
	 *
	 *
	 */
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onListItemClick(l, v, position, id);
						
		Long[] time = LocationMinute.toTime();
		
		Intent i = new Intent();
		
		//���l��Ԃ��@�Ԃ����̓R���f�ǂ��̂��ȁ[
		i.putExtra("time", time[position]);
		this.setResult(RESULT_OK, i);
		//finish()�ŃA�N�e�B�r�e�B���I��������
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		super.onCreate(savedInstanceState);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		/* adapter.add �A�C�e����ǉ����܂�
		 * 
		 * LocationTime�̔z��͍���Ă邽��
		 * LocationTime[] time�̒��g��ǋL����Ώ���Ƀ��X�g�𑝂₵�Ă����A���΂�I
		 */
		Long[] time = LocationMinute.toTime();
		for(Long i:time){
			adapter.add(String.valueOf(i/60/1000)+"��");
		}
		
		// �A�_�v�^�[��ݒ肵�܂�
		this.setListAdapter(adapter);

		
		
	}
	

}
