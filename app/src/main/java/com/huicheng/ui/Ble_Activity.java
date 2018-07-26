package com.huicheng.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huicheng.collector.ActivityCollector;
import com.huicheng.service.*;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.huicheng.R;
import com.huicheng.service.BluetoothLeService;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;


public class Ble_Activity extends Activity implements OnClickListener {

	private final static String TAG = Ble_Activity.class.getSimpleName();
	//����4.0��UUID,����0000ffe1-0000-1000-8000-00805f9b34fb�ǹ��ݻ����Ϣ�Ƽ����޹�˾08����ģ���UUID
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";;
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";
	//���յ��ź�ǿ��ָʾ�����߷��Ͳ�Ŀ�ѡ���֣������ж���������  ͨ�����յ����ź�ǿ���ⶨ�źŵ�����յ�ľ��룬����������Ӧ���ݽ��ж�λ�����һ�ֶ�λ����
	//��������״̬
	private boolean mConnected = false;
	private String status = "δ����";
	//��������
	private String mDeviceName;
	//������ַ
	private String mDeviceAddress;
	//�����ź�ֵ
	private String mRssi;
	private Bundle b;
	private String rev_str = "";
	//����service,�����̨����������
	private static BluetoothLeService mBluetoothLeService;
	
	PopupMenu popupMenu;
	Menu men;
	private Button mButton;
    private Button sButton;
    private Switch mSwitch;
	private  int ke_fu=0;
	private  int biaozhi=0;
	ImageButton imagebutton1;
	ImageButton imagebutton2;
	ImageButton imagebutton3;
	private static int flag[]={0,0,0};
	String send_str=null;
	//�ı�����ʾ���ܵ�����
	private TextView rev_tv, connect_state;
	//���Ͱ�ť
	//private Button send_btn;
	//�ı��༭��
	//private EditText send_et;
	private ScrollView rev_sv;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	//��������ֵ
	private static BluetoothGattCharacteristic target_chara = null;
    public  OkPostUtils okinstance;
    private List<Book> newsList;
	private Handler mhandler = new Handler();
	private Handler myHandler = new Handler()
	{
		// 2.��д��Ϣ������
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			// �жϷ��͵���Ϣ
			case 1:
			{
				// ����View
				String state = msg.getData().getString("connect_state");
				connect_state.setText(state);

				break;
			}

			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.ble_activity);
		b = getIntent().getExtras();
		//����ͼ��ȡ��ʾ��������Ϣ
		mDeviceName = b.getString(EXTRAS_DEVICE_NAME);
		mDeviceAddress = b.getString(EXTRAS_DEVICE_ADDRESS);
		mRssi = b.getString(EXTRAS_DEVICE_RSSI);
		
		/* ��������service */
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		
		init();
		kehu_init();
		mbutton_init();
		//Toast.makeText(Ble_Activity.this,"0", Toast.LENGTH_SHORT).show(); 
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
        //����㲥������
		unregisterReceiver(mGattUpdateReceiver);
		mBluetoothLeService = null;
		ActivityCollector.removeActivity(this);
	}

	// Activity����ʱ�򣬰󶨹㲥�������������������ӷ��񴫹������¼�
	@Override
	protected void onResume()
	{
		super.onResume();
		//�󶨹㲥������
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{    
			//����������ַ����������
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
			//Toast.makeText(Ble_Activity.this,"Resume", Toast.LENGTH_SHORT).show(); 
			Log.d(TAG, "Connect request result=" + result);
			
		}
		//Toast.makeText(Ble_Activity.this,"1", Toast.LENGTH_SHORT).show(); 
	}

	/** 
	* @Title: init 
	* @Description: TODO(��ʼ��UI�ؼ�) 
	* @param  ��
	* @return void    
	* @throws 
	*/ 
	private void init()
	{
		rev_sv = (ScrollView) this.findViewById(R.id.rev_sv);
		rev_tv = (TextView) this.findViewById(R.id.rev_tv);
		connect_state = (TextView) this.findViewById(R.id.connect_state);
		connect_state.setText(status);
		//send_btn = (Button) this.findViewById(R.id.send_btn);
		//send_et = (EditText) this.findViewById(R.id.send_et);
		//send_btn.setOnClickListener(this);
        sButton= (Button) findViewById(R.id.moshi);
        sButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                handleMoShi();
            }
        });
        mSwitch=(Switch)this.findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    LitePal.getDatabase();
                    Book book = new Book(1,"jason","11");
                    book.save();
                    Book book1 = new Book(2,"jerry","22");
                    book1.save();
                    newsList = DataSupport.findAll(Book.class, 1,2);
                    Book book2=newsList.get(0);
                    String str=book2.getName();
                    StringBuilder strr=new StringBuilder();
                    strr.append(str).append("-");
                    book2=newsList.get(1);
                    str=book2.getName();
                    strr.append(str);
                    Toast.makeText(getApplicationContext(), strr.toString(), Toast.LENGTH_SHORT).show();
                    okinstance.performPostgson(newsList);
                }else {

                }
            }
        });

	}
    private void handleMoShi() {
        if (sButton.isSelected()) {
            sButton.setText("�ֶ��л�ģʽ");
            sButton.setSelected(false);
        }
        else{
            sButton.setText("�Զ��л�ģʽ");
            sButton.setSelected(true);
        }
    }

	private void kehu_init()
	{
		
		mButton=(Button) findViewById(R.id.popupmenu_btn); 
		popupMenu = new PopupMenu(this,mButton);
		men = popupMenu.getMenu();

		// ͨ��������Ӳ˵���
		men.add(Menu.NONE, 0, 0, "1���ն�");
		men.add(Menu.NONE, 1, 1, "2���ն�");
		men.add(Menu.NONE, 2, 2, "3���ն�");
		
		// ͨ��XML�ļ���Ӳ˵���
		//MenuInflater menuInflater = getMenuInflater();
		//menuInflater.inflate(R.layout.popupmenu, menu);

		// �����¼�
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case 0:
					ke_fu=1;
					mButton.setText("1���ն�");
					if(flag[0]==1){
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.az));
					}
					else if(flag[0]==2){
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bz));	
					}
					else if(flag[0]==3){
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cz));	
					}
					else {
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					}
					break;
			
				case 1:
					ke_fu=2;
					mButton.setText("2���ն�");
					if(flag[1]==1){
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.az));
						}
						else if(flag[1]==2){
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bz));	
						}
						else if(flag[1]==3){
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cz));	
						}
						else {
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						}
					break;
				case 2:
					ke_fu=3;
					mButton.setText("3���ն�");
					if(flag[2]==1){
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.az));
						}
						else if(flag[2]==2){
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bz));	
						}
						else if(flag[2]==3){
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cz));	
						}
						else {
						imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
						imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
						imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
						}
					break;
				default:
					ke_fu=0;
					mButton.setText("ѡ���ն�");
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					break;
				}
				return true;
			}
		});
	}
	
	
	public void popupmenu(View v) {
		popupMenu.show();
		
	}
	
	private void mbutton_init()
	{
		imagebutton1 = (ImageButton) findViewById(R.id.myImageView1);
		 imagebutton2 = (ImageButton) findViewById(R.id.myImageView2);
		 imagebutton3 = (ImageButton) findViewById(R.id.myImageView3);
		imagebutton1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			
				if(ke_fu==1){
					//String	s = String.valueOf(1);
					//Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show(); 
					flag[0]=1;}
				else if(ke_fu==2) flag[1]=1;
				else if(ke_fu==3) flag[2]=1;
				else {
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					return ;
				}
				
				imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
				imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
				imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.az));
		
				
				//send_str= new String(sendzi);
				send_str="6"+String.valueOf(ke_fu)+1;
				//Toast.makeText(Ble_Activity.this,send_str, Toast.LENGTH_LONG).show(); 
				target_chara.setValue(send_str);
				mBluetoothLeService.writeCharacteristic(target_chara);
			}
			});
		
		imagebutton2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(ke_fu==1){
					//String	s = String.valueOf(1);
					//Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show(); 
					flag[0]=2;}
				else if(ke_fu==2) flag[1]=2;
				else if(ke_fu==3) flag[2]=2;
				else {
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					return ;
				}
				imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
				imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
				imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bz));
		
				
				send_str="6"+String.valueOf(ke_fu)+2;
				//Toast.makeText(Ble_Activity.this,send_str, Toast.LENGTH_LONG).show(); 
				target_chara.setValue(send_str);
				mBluetoothLeService.writeCharacteristic(target_chara);
			}
			});
		
		imagebutton3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			
				if(ke_fu==1){
					//String	s = String.valueOf(1);
					//Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show(); 
					flag[0]=3;}
				else if(ke_fu==2) flag[1]=3;
				else if(ke_fu==3) flag[2]=3;
				else {
					imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
					imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cf));
					imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
					return ;
				}
				imagebutton2.setImageDrawable(getResources().getDrawable(R.drawable.bf));
				imagebutton1.setImageDrawable(getResources().getDrawable(R.drawable.af));
				imagebutton3.setImageDrawable(getResources().getDrawable(R.drawable.cz));
		
				send_str="6"+String.valueOf(ke_fu)+3;
				//Toast.makeText(Ble_Activity.this,send_str, Toast.LENGTH_LONG).show(); 
				target_chara.setValue(send_str);
				mBluetoothLeService.writeCharacteristic(target_chara);
			}
			});
		
	}

	
	
	/* BluetoothLeService�󶨵Ļص����� */
	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service)
		{
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize())
			{
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			// Automatically connects to the device upon successful start-up
			// initialization.
			// ����������ַ�������豸
			mBluetoothLeService.setReConnectAddress(mDeviceAddress);
			mBluetoothLeService.connect(mDeviceAddress);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
		}

	};

	/**
	 * �㲥���������������BluetoothLeService�෢�͵�����
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))//Gatt���ӳɹ�
			{
				//mConnected = true;
				//status = "������";
				 
				//��������״̬
				//updateConnectionState(status);
				System.out.println("BroadcastReceiver :" + "device connected");
				
				//Toast.makeText(Ble_Activity.this,"2", Toast.LENGTH_SHORT).show();

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED//Gatt����ʧ��
					.equals(action))
			{
				mConnected = false;
				status = "δ����";
				//��������״̬
				updateConnectionState(status);
				System.out.println("BroadcastReceiver :"
						+ "device disconnected");
				
				//Toast.makeText(Ble_Activity.this,"4", Toast.LENGTH_SHORT).show();
			
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED//����GATT������
					.equals(action))
			{
				// Show all the supported services and characteristics on the
				// user interface.
				mConnected = true;
				status = "������";
				//��������״̬
				updateConnectionState(status);
				//Toast.makeText(Ble_Activity.this,"3", Toast.LENGTH_SHORT).show();
				//��ȡ�豸��������������
				displayGattServices(mBluetoothLeService
						.getSupportedGattServices());
				System.out.println("BroadcastReceiver :"
						+ "device SERVICES_DISCOVERED");
				target_chara.setValue("600");
				mBluetoothLeService.writeCharacteristic(target_chara);
				
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//��Ч����
			{    
				 //�����͹���������
				displayData(intent.getExtras().getString(
						BluetoothLeService.EXTRA_DATA));//����������������������
				System.out.println("BroadcastReceiver onData:"
						+ intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};

	/* ��������״̬ */
	private void updateConnectionState(String status)
	{
		Message msg = new Message();
		msg.what = 1;
		Bundle b = new Bundle();  //����activity֮���ͨѶ����ͨ��bundle����ʵ��
		b.putString("connect_state", status);
		msg.setData(b);
		//������״̬���µ�UI��textview��
		myHandler.sendMessage(msg);
		System.out.println("connect_state:" + status);

	}


	/* ��ͼ������ */
	private static IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/** 
	* @Title: displayData 
	* @Description: TODO(���յ���������scrollview����ʾ) 
	* @param @param rev_string(���ܵ�����)
	* @return void   
	* @throws 
	*/ 
	private void displayData(String rev_string)
	{
		
		
		Pattern p = Pattern.compile("[0-9]*");  

	     Matcher m = p.matcher(rev_string);  

	     if(m.matches() ){ 
	    	 int[] a = new int[rev_string.length()];
	 		for(int i = 0; i < rev_string.length(); i++)
	 		{
	 		//�����ַ���ת����char,��ת����String,Ȼ��Integer
				//String[] aa = "aaa\\bbb\\bccc".split("\\\\");
				//String string = "abc" ;

				//char [] stringArr = string.toCharArray(); //ע�ⷵ��ֵ��char����
	 		a[i] = Integer.parseInt( String.valueOf(rev_string.charAt(i)));
	 		//Toast.makeText(Ble_Activity.this,String.valueOf(a[i]), Toast.LENGTH_SHORT).show();
	 		
	 		}
	 		
	 
	      if(rev_string.length()>2)
	      {
	    	  if(a[0]==6){
	    		  
			   if(flag[0]!=a[1]) {   
                flag[0]=a[1]; 
			   if(ke_fu==1) biaozhi=1;
	    	  }
			   
			   if(flag[1]!=a[2]) {   
	                flag[1]=a[2]; 
				   if(ke_fu==2) biaozhi=2;
		    	  }
			       
			        flag[2]=0; 
			        
			     
			   }
	    	  
	    	  if (a[0]==1)
	 	     {
	    		  if(a[1]==1)
		    		  Toast.makeText(Ble_Activity.this,"��������", Toast.LENGTH_SHORT).show();  
		    	  else
		    		  Toast.makeText(Ble_Activity.this,"����ɹ�", Toast.LENGTH_SHORT).show();    
	 	     }
	      }
	     
	    } 
		
		
		
	   rev_str = rev_string;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				//rev_tv.setText(rev_str);
				//rev_sv.scrollTo(0, rev_tv.getMeasuredHeight());
				//System.out.println("rev:" + rev_str);
				if(biaozhi>0) {  
				      men.performIdentifierAction(biaozhi-1,0);
				      biaozhi=0;
				     }
			}
		});

	}

	/** 
	* @Title: displayGattServices 
	* @Description: TODO(������������) 
	* @param ��  
	* @return void  
	* @throws 
	*/ 
	private void displayGattServices(List<BluetoothGattService> gattServices)
	{
		
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = "unknown_service";
		String unknownCharaString = "unknown_characteristic";
		
		// ��������,����չ�����б�ĵ�һ������
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();

		// �������ݣ�������ĳһ���������������ֵ���ϣ�
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

		// ���ֲ�Σ���������ֵ����
		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BluetoothGattService gattService : gattServices)
		{

			// ��ȡ�����б�
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString();

			// ������ݸ�uuid��ȡ��Ӧ�ķ������ơ�SampleGattAttributes�������Ҫ�Զ��塣

			gattServiceData.add(currentServiceData);

			System.out.println("Service uuid:" + uuid);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();

			// �ӵ�ǰѭ����ָ��ķ����ж�ȡ����ֵ�б�
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();

			ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

			// Loops through available Characteristics.
			// ���ڵ�ǰѭ����ָ��ķ����е�ÿһ������ֵ
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
			{
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString();

				if (gattCharacteristic.getUuid().toString()
						.equals(HEART_RATE_MEASUREMENT))
				{
					// ���Զ�ȡ��ǰCharacteristic���ݣ��ᴥ��mOnDataAvailable.onCharacteristicRead()
					mhandler.postDelayed(new Runnable()
					{

						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							mBluetoothLeService
									.readCharacteristic(gattCharacteristic);
						}
					}, 200);

					// ����Characteristic��д��֪ͨ,�յ�����ģ������ݺ�ᴥ��mOnDataAvailable.onCharacteristicWrite()
					mBluetoothLeService.setCharacteristicNotification(
							gattCharacteristic, true);
					target_chara = gattCharacteristic;
					// ������������
					// ������ģ��д������
					// mBluetoothLeService.writeCharacteristic(gattCharacteristic);
				}
				List<BluetoothGattDescriptor> descriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor descriptor : descriptors)
				{
					System.out.println("---descriptor UUID:"
							+ descriptor.getUuid());
					// ��ȡ����ֵ������
					mBluetoothLeService.getCharacteristicDescriptor(descriptor);
					// mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,
					// true);
				}

				gattCharacteristicGroupData.add(currentCharaData);
			}
			// ���Ⱥ�˳�򣬷ֲ�η�������ֵ�����У�ֻ������ֵ
			mGattCharacteristics.add(charas);
			// �����ڶ�����չ�б��������������ֵ��
			gattCharacteristicData.add(gattCharacteristicGroupData);

		}
		
		
	}

	/* 
	 * ���Ͱ�������Ӧ�¼�����Ҫ�����ı��������
	 */
	//@Override
	/*public void onClick(View v)
	{
		// TODO Auto-generated method stub
		target_chara.setValue(send_et.getText().toString());
		//�������������д����ֵ����ʵ�ַ�������
		mBluetoothLeService.writeCharacteristic(target_chara);
	}*/
	
	
	// �ж�Menu�˵�
 	public boolean onCreateOptionsMenu(Menu menu)
 	{
 		// TODO Auto-generated method stub	
 		
 		
 		menu.add(0, 1, 1, "����");
 		menu.add(0, 2, 2, "����");
 		menu.add(0, 3, 3, "�˳�");
 		return super.onCreateOptionsMenu(menu);
 	}
 	
 public boolean onOptionsItemSelected(MenuItem item)
 	{
 		// TODO Auto-generated method stub

	     if (item.getItemId() == 1)
 		{
 			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

 			dialog.setTitle("��������").setMessage("�û���������������ͨ���������䵽�ն˽��л���")
 					.show();

 		}
	 
 		if (item.getItemId() == 2)
 		{
 			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

 			dialog.setTitle("����").setMessage("����  ")
 					.show();

 		}
 		if (item.getItemId() == 3)
 		{
 		
 			mBluetoothLeService.close();
 			ActivityCollector.finishAll();
 			
 			
 		}

 		return super.onOptionsItemSelected(item);
 	}

     @Override
     public void onClick(View v) {
	// TODO Auto-generated method stub
	
     }  
	

}
