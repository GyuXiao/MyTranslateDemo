package com.example.gitranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout beforeLay;
    private NiceSpinner spLanguage;
    private LinearLayout afterLay;
    private TextView tvFrom;
    private TextView tvTo;

    private EditText edContext;
    private ImageView ivClear;
    private TextView tvTranslation;

    private LinearLayout resultLay;
    private TextView tvResult;
    private ImageView ivCopy;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    private String fromLanguage = "auto";
    private String toLanguage = "auto";

    private String appId = "20201204000636944";
    private String key = "ef2ZKUAD31GlSI_mBdYC";

    private List<String> data = new LinkedList<>(Arrays.asList(
            "自动检测语言",
            "中文 → 英文",
            "英文 → 中文",
            "中文 → 繁体中文",
            "中文 → 粤语",
            "中文 → 日语",
            "中文 → 韩语",
            "中文 → 法语",
            "中文 → 俄语",
            "中文 → 阿拉伯语",
            "中文 → 西班牙语 ",
            "中文 → 意大利语"
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();

    }

    // 初始化控件
    private void initview() {
        //设置亮色状态栏模式 systemUiVisibility在Android11中弃用了，可以尝试一下。
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // 控件初始化
        beforeLay = findViewById(R.id.before_lay);
        spLanguage = findViewById(R.id.sp_language);
        afterLay = findViewById(R.id.after_lay);
        tvFrom = findViewById(R.id.tv_from);
        tvTo = findViewById(R.id.tv_to);
        edContext = findViewById(R.id.ed_context);
        ivClear = findViewById(R.id.iv_clear);
        tvTranslation = findViewById(R.id.tv_translation);
        resultLay = findViewById(R.id.result_lay);
        tvResult = findViewById(R.id.tv_result);
        ivCopy = findViewById(R.id.iv_copy);

        // 点击时间
        ivClear.setOnClickListener(this);
        ivCopy.setOnClickListener(this);
        tvTranslation.setOnClickListener(this);

        // 设置下拉数据
        spLanguage.attachDataSource(data);
        editTextListener();
        spinnerListener();
        myClipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
    }

    // 输入监听函数
    private void editTextListener() {
        edContext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ivClear.setVisibility(View.VISIBLE);// 显示清除按钮
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(View.VISIBLE);// 显示清除按钮
            }

            @Override
            public void afterTextChanged(Editable s) {
                ivClear.setVisibility(View.VISIBLE); // 显示清除按钮
                String context = edContext.getText().toString().trim();
                if(context.isEmpty()){
                    resultLay.setVisibility(View.GONE);
                    tvTranslation.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.VISIBLE);
                    afterLay.setVisibility(View.GONE);
                    ivClear.setVisibility(View.GONE);
                }
            }
        });
    }

    // 语言类型选择
    private void spinnerListener() {
        spLanguage.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                switch (position){
                    case 0://自动检测
                        fromLanguage = "auto";
                        toLanguage = fromLanguage;
                        break;
                    case 1://中文 → 英文
                        fromLanguage = "zh";
                        toLanguage = "en";
                        break;
                    case 2://英文 → 中文
                        fromLanguage = "en";
                        toLanguage = "zh";
                        break;
                    case 3://中文 → 繁体中文
                        fromLanguage = "zh";
                        toLanguage = "cht";
                        break;
                    case 4://中文 → 粤语
                        fromLanguage = "zh";
                        toLanguage = "yue";
                        break;
                    case 5://中文 → 日语
                        fromLanguage = "zh";
                        toLanguage = "jp";
                        break;
                    case 6://中文 → 韩语
                        fromLanguage = "zh";
                        toLanguage = "kor";
                        break;
                    case 7://中文 → 法语
                        fromLanguage = "zh";
                        toLanguage = "fra";
                        break;
                    case 8://中文 → 俄语
                        fromLanguage = "zh";
                        toLanguage = "ru";
                        break;
                    case 9://中文 → 阿拉伯语
                        fromLanguage = "zh";
                        toLanguage = "ara";
                        break;
                    case 10://中文 → 西班牙语
                        fromLanguage = "zh";
                        toLanguage = "spa";
                        break;
                    case 11://中文 → 意大利语
                        fromLanguage = "zh";
                        toLanguage = "it";
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.iv_clear:
                edContext.setText("");
                ivClear.setVisibility(View.GONE);
                break;
            case R.id.iv_copy:
                String inviteCode = tvResult.getText().toString();
                myClip = ClipData.newPlainText("text", inviteCode);
                myClipboard.setPrimaryClip(myClip);
                showMsg("已复制");
                break;
            case R.id.tv_translation:
                translation();
                break;
            default:
                break;
        }
    }

    private void translation(){
        String inputTx = edContext.getText().toString().trim();
        if(!inputTx.isEmpty() || "".equals(inputTx)){
            tvTranslation.setText("翻译ing");
            tvTranslation.setEnabled(false);
            String salt = getNum(1);
            String spliceStr = appId + inputTx + salt + key;// 百度文档要求
            String sign = stringToMD5(spliceStr);
            asynGet(inputTx, fromLanguage, toLanguage, salt, sign);
        }
        else{
            showMsg("请输入要翻译的内容");
        }
    }

    // 异步请求
    private void asynGet(String context, String fromType, String toType, String salt, String sign){
        String httpStr = "http://api.fanyi.baidu.com/api/trans/vip/translate";
        String httpsStr = "https://fanyi-api.baidu.com/api/trans/vip/translate";

        String url = httpsStr + "?appid=" + appId + "&q=" + context + "&from=" + fromType
                + "&to=" + toType + "&salt=" + salt + "&sign=" + sign;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                goToUiThread(e.toString(), 0);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                goToUiThread(response.body().string(), 1);
            }
        });
    }


    private void goToUiThread(final Object object, final int key) {
        // 切换到主线程处理数据
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTranslation.setText("翻译");
                tvTranslation.setEnabled(true);

                if(key == 0){
                    showMsg("异常信息：" + object.toString());
                    Log.e("MainActivity", object.toString());
                } else{
                    final TranslateResult result = new Gson().fromJson(object.toString(), TranslateResult.class);
                    tvTranslation.setVisibility(View.GONE);

                    if(result.getTrans_result().get(0).getDst() == null){
                        showMsg("数据为空");
                    }

                    tvResult.setText(result.getTrans_result().get(0).getDst());
                    resultLay.setVisibility(View.VISIBLE);
                    beforeLay.setVisibility(View.GONE);
                    afterLay.setVisibility(View.VISIBLE);

                    // 翻译成功后的语言判断
                    initAfter(result.getFrom(), result.getTo());
                }
            }
        });
    }

    private void initAfter(String from, String to){
        if (("zh").equals(from)) {
            tvFrom.setText("中文");
        } else if (("en").equals(from)) {
            tvFrom.setText("英文");
        } else if (("yue").equals(from)) {
            tvFrom.setText("粤语");
        } else if (("cht").equals(from)) {
            tvFrom.setText("繁体中文");
        } else if (("jp").equals(from)) {
            tvFrom.setText("日语");
        } else if (("kor").equals(from)) {
            tvFrom.setText("韩语");
        } else if (("fra").equals(from)) {
            tvFrom.setText("法语");
        } else if (("ru").equals(from)) {
            tvFrom.setText("俄语");
        } else if (("ara").equals(from)) {
            tvFrom.setText("阿拉伯语");
        } else if (("spa").equals(from)) {
            tvFrom.setText("西班牙语");
        } else if (("it").equals(from)) {
            tvFrom.setText("意大利语");
        }
        if (("zh").equals(to)) {
            tvTo.setText("中文");
        } else if (("en").equals(to)) {
            tvTo.setText("英文");
        } else if (("yue").equals(to)) {
            tvTo.setText("粤语");
        } else if (("cht").equals(to)) {
            tvTo.setText("繁体中文");
        } else if (("jp").equals(to)) {
            tvTo.setText("日语");
        } else if (("kor").equals(to)) {
            tvTo.setText("韩语");
        } else if (("fra").equals(to)) {
            tvTo.setText("法语");
        } else if (("ru").equals(to)) {
            tvTo.setText("俄语");
        } else if (("ara").equals(to)) {
            tvTo.setText("阿拉伯语");
        } else if (("spa").equals(to)) {
            tvTo.setText("西班牙语");
        } else if (("it").equals(to)) {
            tvTo.setText("意大利语");
        }
    }


    // 将字符串转成MD5值
    public static String stringToMD5(String s) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    // 获取一个随机数
    public static String getNum(int a){
        Random r = new Random(a);
        int ran1 = 0;
        for(int i = 0; i < 5; i++){
            ran1 = r.nextInt(100);
            System.out.println(ran1);
        }
        return String.valueOf(ran1);
    }

    private void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}