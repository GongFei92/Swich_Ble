package com.huicheng.ui;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gong on 2018-07-14.
 */

public class OkPostUtils {
    public OkHttpClient mOkHttpClient;
    public static OkPostUtils instance;
    public static String LOGIN_URL = "http://gongfei123.ticp.net:53047/Test1/ServletForPost";
    private Handler mhandler;
    private Context mcontext;
    private OkPostUtils(Context context) {
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        mhandler= new Handler();
        this.mcontext=context;

    }
    public static OkPostUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (OkPostUtils.class) {
                if (instance == null) {
                    //配置了网络请求的超时时间

                    instance=new OkPostUtils(context);
                }
            }
        }
        return instance;
    }
    public   void performPost(Map<Object, Object> map) {


        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("name", "android")
                .add("password", "50")
                .build();

        final Request request = new Request.Builder()
                .url(LOGIN_URL)//请求的url
                .post(formBody)
                .build();


        //创建/Call
        Call call = mOkHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    final String str=response.body().string();
                    System.out.println(str);
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext,str,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public   void performPostgson(List<Book> bookList) {

        /*List<Book> bookList=new ArrayList();
        Book book = new Book(15,"asdf","123456");
        Book book1 = new Book(16,"sdfg","789456");
        Book book2 = new Book(16,"sdfg","789456");
        bookList.add(book);
        bookList.add(book1);
        bookList.add(book2);*/
       /* Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(book);*/
       /* JSONObject user = new JSONObject();
        try {

            user.put("name", "qwer");
            user.put("password", "1234567");
        }  catch (Exception e) {

         }*/
        //MediaType  设置Content-Type 标头中包含的媒体类型值
        String ajson=EntityToString(bookList);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , ajson);
        Request request = new Request.Builder()
                .url(LOGIN_URL)//请求的url
                .post(requestBody)
                .build();


        //创建/Call
        Call call = mOkHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    String str=response.body().string();
                    ArrayList<Book> listBook=StringToEntityList(str,Book.class);
                    Book book0=listBook.get(listBook.size()-1);
                    final String strr=book0.getPassword();
                    System.out.println(listBook);
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext,strr,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    /**
     * 实体类转换为字符串
     *
     */
    public static <T> String EntityToString(T data) {
        String str = JSON.toJSONString(data);
        return str;
    }
    public static <T> T StringToEntity(String str, Class<T> classT) {
        T t = JSON.parseObject(str, classT);
        return t;
    }
    /**
     * 字符串转换为实体集合
     *
     */
    public static <T> ArrayList<T> StringToEntityList(String str, Class<T> classT) {
        List<T> lst = JSON.parseArray(str, classT);
        return (ArrayList<T>) lst;
    }
}
