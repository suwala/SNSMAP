package com.example.snsmap;

public class LocationMinute{
	/* 
	 * long�̒l��15�E30�E45�E60���������Ȃ��N���X
	 * �R���X�g���N�^��private�Ȃ��߃C���X�^���X���ł��Ȃ�?�͂�
	 *  
	 */
	
	
	
	/*�Q�ƕ��@�@LocationTime.MINUTE15.getTime();�őΉ����������A���Ă���
	 * LocationTime.toTime();�Ŕz�񂪋A���Ă���
	 */
	
	
	public static final Long MINUTE3 = (long)3*60*1000;
	public static final Long MINUTE5= (long)5*60*1000;
	public static final Long MINUTE15 = (long)15*60*1000;
	public static final Long MINUTE30 = (long)30*60*1000;
		
	//�z��ň����Ƃ��Ɏg��
	private static final Long[] MINUTE = {MINUTE3,MINUTE5,MINUTE15,MINUTE30};
	
	private long l;
	
	//private�@�R���X�g���N�^
	private LocationMinute(){
	}
	
	
	//�z��擾�p LocationMinute[] lt = LocationMinute.toTime();�Ŕz����Z�b�g����
	public static Long[] toTime(){
		return MINUTE;
		
	}
	
	//�~���b�𕪂ɒ����ĕԂ����\�b�h
	public long getMinute(){
		return this.l/60/1000;
	}
	

	//debug�p�@0��Ԃ�
	public static long debug(){
		return 0;
	}

}
