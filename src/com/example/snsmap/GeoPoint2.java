package com.example.snsmap;


import com.google.android.maps.GeoPoint;
/*
 * GeoPoint���m���r��boolean�ŕԂ����\�b�h�����������N���X
 * ��������
 * .toStoring().equals();�ŗǂ������񂶂�Ȃ����Ǝv���_(^o^)�^
 */

public class GeoPoint2 extends GeoPoint {

	public GeoPoint2(int latitudeE6, int longitudeE6) {
		super(latitudeE6, longitudeE6);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}
	
	public boolean equalsGP(GeoPoint gp2){
		if(gp2 == null)
			return false;
		if(this.getLatitudeE6() == gp2.getLatitudeE6() && this.getLongitudeE6() == gp2.getLongitudeE6())
			return true;
		else
			return false;
	}
	
	

}
