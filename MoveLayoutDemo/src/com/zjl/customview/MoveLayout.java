package com.zjl.customview;

import java.util.LinkedList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
/**
 * @author ZhengJingle
 */

/*
 * MoveLayout���ƶ����֣�ʵ�ֿؼ�������һ�������ƶ�view����Ӧ��������ƶ�views
 */
public class MoveLayout extends FrameLayout{
	private View activeView;//�����ƶ�view
	private boolean isStraight=true;//�����ƶ�view�Ƿ���ֱ��
	private double distance=0;//����view�ƶ��ľ���
	private double distanceX,distanceY;////����view�ƶ���x,y���ϵľ���
	private boolean useMoveArea;//����viewʹ���ƶ�����
	private int minX,maxX,minY,maxY;//����view�ƶ�����
	private int fromX,fromY;//����view��ʼ����
	private int toX,toY;//����viewĿ������
	private double k=0,b=0;//����viewֱ�ߵ�k��b
	private double kd=0,bd=0;//��ֱ������viewֱ�ߵ�k��b
	final int X=1;//X���ƶ�
	final int Y=2;//Y���ƶ�
	final int XY=3;//XY���ƶ�
	private int mode=XY;
	private double movingPrecent=0;//�ƶ��ٷֱ�
	
	private LinkedList<PassiveMoveItem> pmiList=new LinkedList<PassiveMoveItem>();//�����ƶ�views
	
	public MoveLayout(Context context) {
		super(context);
		// TODO �Զ����ɵĹ��캯�����
	}

	public MoveLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO �Զ����ɵĹ��캯�����
	}

	public MoveLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO �Զ����ɵĹ��캯�����
	}
	/**
	 * ��������view���ƶ����򣬱�����
	 * @param useMoveArea trueʹ�ã�false��ʹ�ã�����Ĳ����������� 
	 * @param minX x���ϵ���Сֵ
	 * @param maxX x���ϵ����ֵ
	 * @param minY y���ϵ���Сֵ
	 * @param maxY y���ϵ����ֵ
	 */
	public void setActiveMoveArea(boolean useMoveArea,int minX,int maxX,int minY,int maxY){
		this.useMoveArea=useMoveArea;
		this.minX=minX;
		this.minY=minY;
		this.maxX=maxX;
		this.maxY=maxY;
	}
	
	/**
	 * �ƶ����ٷֱȶ�Ӧ��λ�á��������view����ֱ�ߣ����Զ���Ϊ��ֱ���ϵ�λ�á�
	 * @param precent 0.0����0%��1.0����100%����������
	 */
	public void moveToPrecent(double precent){
		this.movingPrecent=precent;
		runActiveMoveItem();
		runPassiveMoveItem();
		
		if(myOnMovingListener!=null){
			myOnMovingListener.onMoving(movingPrecent);
		}
	}
	
	/**
	 * ���������ƶ�view
	 * @param view �����ƶ�view
	 * @param toX Ŀ��x����
	 * @param toY Ŀ��y����
	 * @param isStraight true��ֱ�ߣ�false����ֱ�ߣ�������movingPrecentʱ���������ƶ�view�ƶ�ʱ���Ͻǵ�����ͶӰ��ֱ���ϵ��������
	 */
	public void setActiveMoveItem(View view,int toX,int toY,boolean isStraight){
		view.setOnTouchListener(new MoveItemTouchListener(view,toX,toY));
		this.activeView=view;
		this.isStraight=isStraight;
	}
	
	/**
	 * ���ñ����ƶ�view
	 * @param view �����ƶ�view
	 * @param toX Ŀ��x����
	 * @param toY Ŀ��y����
	 */
	public void setPassiveMoveItem(View view,int toX,int toY){
		PassiveMoveItem pmi=new PassiveMoveItem(view,toX,toY);
		pmiList.add(pmi);
	}
	
	private void passiveMoveItemInit(){
		for(PassiveMoveItem pmi:pmiList){
			
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pmi.view.getLayoutParams();
			layoutParams.gravity=-1;//�������gravity����
			layoutParams.leftMargin=pmi.view.getLeft();
			layoutParams.topMargin=pmi.view.getTop();
			pmi.view.setLayoutParams(layoutParams);
			
			//��ʼ��
			pmi.fromX=pmi.view.getLeft();
			pmi.fromY=pmi.view.getTop();
			
			pmi.distanceX=Math.abs(pmi.toX-pmi.fromX);
			pmi.distanceY=Math.abs(pmi.toY-pmi.fromY);
			
		}
	}
	
	private void runPassiveMoveItem(){
		if(Double.isInfinite(movingPrecent) || Double.isNaN(movingPrecent))return;
		
		for(PassiveMoveItem pmi:pmiList){
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) pmi.view.getLayoutParams();
			
			if(pmi.fromX>pmi.toX){
				layoutParams.leftMargin=(int) (pmi.fromX-pmi.distanceX*movingPrecent);
			}else{
				layoutParams.leftMargin=(int) (pmi.fromX+pmi.distanceX*movingPrecent);
			}
			
			if(pmi.fromY>pmi.toY){
				layoutParams.topMargin=(int) (pmi.fromY-pmi.distanceY*movingPrecent);
			}else{
				layoutParams.topMargin=(int) (pmi.fromY+pmi.distanceY*movingPrecent);
			}
			pmi.view.setLayoutParams(layoutParams);
			
			pmi.view.invalidate();
		}
	}
	
	private void runActiveMoveItem(){
		if(activeView==null)return;
		
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) activeView.getLayoutParams();
		
		if(fromX>toX){
			layoutParams.leftMargin=(int) (fromX-distanceX*movingPrecent);
		}else{
			layoutParams.leftMargin=(int) (fromX+distanceX*movingPrecent);
		}
		
		if(fromY>toY){
			layoutParams.topMargin=(int) (fromY-distanceY*movingPrecent);
		}else{
			layoutParams.topMargin=(int) (fromY+distanceY*movingPrecent);
		}
		activeView.setLayoutParams(layoutParams);
		
		activeView.invalidate();
	}
	
	private class PassiveMoveItem{
		public View view;
		public int fromX,fromY;
		public int toX,toY;
		public int distanceX,distanceY;
		
		public PassiveMoveItem(View view,int toX,int toY){
			this.view=view;
			this.toX=toX;
			this.toY=toY;
		}
		
	}
	
	OnMovingListener myOnMovingListener;//�ƶ�������
	/**
	 * �����ƶ������ӿ�
	 * @param myOnMovingListener �ƶ�������
	 */
	public void setOnMovingListener(OnMovingListener myOnMovingListener){
		this.myOnMovingListener=myOnMovingListener;
	}
	public interface OnMovingListener{
		/**
		 * �ƶ���
		 * @param movingPrecent �ƶ��ٷֱ�
		 */
		public void onMoving(double movingPrecent);
	}
	
	class MoveItemTouchListener implements OnTouchListener{
		View view;
		
		public MoveItemTouchListener(final View view,int toX,int toY){
			this.view=view;
			MoveLayout.this.toX=toX;
			MoveLayout.this.toY=toY;
			
			ViewTreeObserver vto = view.getViewTreeObserver();
			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
				boolean hasMeasured=false;
				public boolean onPreDraw() {
					if (hasMeasured == false) {
						FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
						layoutParams.leftMargin=view.getLeft();
						layoutParams.topMargin=view.getTop();
						layoutParams.gravity=-1;//�������gravity����
						view.setLayoutParams(layoutParams);
						
						activeMoveItemInit();
						passiveMoveItemInit();
						
						hasMeasured=true;
					}
					return true;
				}
			});
		}
		
		private void activeMoveItemInit(){
			fromX=view.getLeft();
			fromY=view.getTop();
			
			distance=Math.sqrt(Math.pow(toX-fromX,2)+Math.pow(toY-fromY,2));
			distanceX=Math.abs(toX-fromX);
			distanceY=Math.abs(toY-fromY);
			
			if(fromX==toX){
				mode=Y;
				
			}else if(fromY==toY){
				mode=X;
				
			}else{
				mode=XY;
				
				k=(toY-fromY)/(double)(toX-fromX);
				b=fromY-k*fromX;
				
				kd=0-1/k;
			}
		}
		
		private int _xDelta;  
	    private int _yDelta;  
	    
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			// TODO �Զ����ɵķ������
			final int rawX = (int) event.getRawX();
			final int rawY = (int) event.getRawY();
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				LayoutParams lParams = (LayoutParams) view.getLayoutParams();
				_xDelta = rawX - lParams.leftMargin;
				_yDelta = rawY - lParams.topMargin;
				break;
			case MotionEvent.ACTION_MOVE:
				
				//ģʽ
				if(mode==X){
					int movingDistance=Math.abs(view.getLeft()-fromX);
					if(movingDistance==0){
						movingPrecent=0;
					}else{
						movingPrecent=movingDistance/distance;
						
						if(view.getLeft()<fromX){//����
							movingPrecent=0-movingPrecent;
						}
						
						if(toX<fromX){//����
							movingPrecent=0-movingPrecent;
						}
					}
				}else if(mode==Y){
					int movingDistance=Math.abs(view.getTop()-fromY);
					if(movingDistance==0){
						movingPrecent=0;
					}else{
						movingPrecent=movingDistance/distance;
						
						if(view.getTop()<fromY){//����
							movingPrecent=0-movingPrecent;
						}
						
						if(toY<fromY){//����
							movingPrecent=0-movingPrecent;
						}
					}
				}else{//XY
					bd=view.getTop()-kd*view.getLeft();
					
					float[][] matrix = { { (float) -k, 1, (float) b }, { (float) -kd, 1, (float) bd } };
					Matrix.simple(2, matrix);
					float[] xy = Matrix.getResult(2, matrix);//ͶӰ��ֱ���ϵĵ�
					
					double movingDistance=Math.sqrt(Math.pow(xy[0]-fromX,2)+Math.pow(xy[1]-fromY,2));//�ƶ�����

					movingPrecent=movingDistance/distance;
					if(xy[0]<fromX || xy[1]<fromY){//����
						movingPrecent=0-movingPrecent;
					}
					
					if(toX<fromX && toY<fromY){//����
						movingPrecent=0-movingPrecent;
					}
				}
				
				
				int left=0;
				int top=0;
				
				if(isStraight){//��ֱ��
					
					if(mode==X){
						left = rawX - _xDelta;
						top = fromY;
					}else if(mode==Y){
						left = fromX;
						top = rawY - _yDelta;
					}else{
						int x=(int) Math.abs(event.getRawX()-rawX);
						int y=(int) Math.abs(event.getRawY()-rawY);
						if(x>y){
							left = rawX - _xDelta;
							top = (int) (k*left+b);
						}else{
							top = rawY - _yDelta;
							left = (int) ((top-b)/k);
						}						
					}
					
				}else{
					left=rawX - _xDelta;
					top=rawY - _yDelta;
				}
				
				if(useMoveArea){//�ƶ�����
					if(left<minX)left=minX;
					if(left>maxX)left=maxX;
					if(top<minY)top=minY;
					if(top>maxY)top=maxY;
					
					if(isStraight && mode==XY){
						int x=(int) Math.abs(event.getRawX()-rawX);
						int y=(int) Math.abs(event.getRawY()-rawY);
						if(x>y){
							left = rawX - _xDelta;
							if(left<minX)left=minX;
							if(left>maxX)left=maxX;
							
							top = (int) (k*left+b);
						}else{
							top = rawY - _yDelta;
							if(top<minY)top=minY;
							if(top>maxY)top=maxY;
							
							left = (int) ((top-b)/k);
						}
					}
					
				}
				
				//����view�ƶ�
				FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) activeView.getLayoutParams();
				layoutParams.leftMargin = left;
		    	layoutParams.topMargin = top;
		    	
		    	//����view�ƶ�
				runPassiveMoveItem();
				
				//�ƶ�����
				if(myOnMovingListener!=null){
					myOnMovingListener.onMoving(movingPrecent);
				}
				
				view.invalidate();
				
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_SCROLL:
				break;
			}

			return true;
		}
	}
	
	/**
	 * ��ȡ�ƶ��ٷֱ�
	 * @return 0.0����0%��1.0����100%����������
	 */
	public double getMovingPrecent(){
		return movingPrecent;
	}
	
	/**
	 * �ⷽ����
	 */
	static class Matrix {

		public static void simple(int n, float[][] matrix) {
			for (int k = 0; k < n; k++) {
				if (matrix[k][k] == 0) {
					changeRow(n, k, matrix);
				}

				for (int i = 0; i < n; i++) {
					// ��¼�Խ���Ԫ�أ���Ϊ����
					float temp = matrix[i][k];
					for (int j = 0; j < n + 1; j++) {
						// i<kʱ,i���Ѿ��������
						if (i < k)
							break;
						if (temp == 0)
							continue;
						if (temp != 1) {
							matrix[i][j] /= temp;
						}

						if (i > k)
							matrix[i][j] -= matrix[k][j];
					}
				}
			}

		}

		public static float[] getResult(int n, float[][] matrix) {
			float[] result = new float[n];
			for (int i = n - 1; i >= 0; i--) {
				float temp = matrix[i][n];
				for (int j = n - 1; j >= 0; j--) {
					if (i < j && matrix[i][j] != 0) {
						temp = temp - result[j] * matrix[i][j];
					}
				}
				temp /= matrix[i][i];
				result[i] = temp;

			}

			for (int k = 0; k < result.length; k++) {
				System.out.println("X" + (k + 1) + " = " + result[k]);
			}

			return result;
		}

		// �Խ�����Ԫ��Ϊ��ʱ������н���
		public static void changeRow(int n, int k, float[][] matrix) {
			float[] temp = new float[n + 1];
			// if()
			for (int i = k; i < n; i++) {
				// �ѵ����һ��,���ܼ�������
				if (i + 1 == n && matrix[k][k] == 0) {
					System.out.println("�޽���в�Ψһ�⣡");
					return;
				}

				for (int j = 0; j < n + 1; j++) {
					temp[j] = matrix[k][j];
					matrix[k][j] = matrix[i + 1][j];
					matrix[i + 1][j] = temp[j];
				}
				if (matrix[k][k] != 0)
					return;

			}
		}
	}
}
