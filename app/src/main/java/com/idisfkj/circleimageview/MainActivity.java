package com.idisfkj.circleimageview;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.idisfkj.circleimageview.view.CircleImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView circleImageView;
    private View view;
    private TextView fp;
    private TextView fc;
    private String[] items;

    private static final int START_FP_CODE = 0;
    private static final int START_FC_CODE = 1;
    private static final int RESULT_CODE = 2;
    private static final String IMAGE_NAME = "head.jpg";
    private static final String SAVE_PATH = Environment.getExternalStorageDirectory() + "/circle_image/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new String[]{getResources().getString(R.string.choose_from_picture),
                getResources().getString(R.string.choose_from_camera)};
        init();
        File imageFile = new File(SAVE_PATH + IMAGE_NAME);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(SAVE_PATH + IMAGE_NAME);
            circleImageView.setImageBitmap(bitmap);
        }
    }

    public void init() {
        circleImageView = (CircleImageView) findViewById(R.id.iv);
        view = LayoutInflater.from(this).inflate(R.layout.show_choose, null);
        fp = (TextView) view.findViewById(R.id.fp);
        fc = (TextView) view.findViewById(R.id.fc);
        fp.setOnClickListener(this);
        fc.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
//        circleImageView.setBorderColor(Color.BLACK);
//        circleImageView.setFillColor(Color.RED);
//        circleImageView.setStrokeWidth(2);
    }

    @Override
    public void onClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_picture)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent fp = new Intent();
                                fp.setAction(Intent.ACTION_GET_CONTENT);
                                fp.setType("image/*");
                                startActivityForResult(fp, START_FP_CODE);
                                break;
                            case 1:
                                Intent fc = new Intent();
                                fc.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                                String state = Environment.getExternalStorageState();
                                if (state.equals(Environment.MEDIA_MOUNTED)) {
                                    //获取并初始化系统定义的共享图片的目录
                                    File path = Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES
                                    );
                                    if (!path.exists()) {
                                        path.mkdirs();
                                    }
                                    //构造需要存储的文件图片
                                    File file = new File(path, IMAGE_NAME);
                                    fc.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                                    startActivityForResult(fc, START_FC_CODE);
                                } else {
                                    Toast.makeText(MainActivity.this, "未找到存储卡,请检查存储空间", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case START_FP_CODE:
                startCrop(data.getData());
                break;
            case START_FC_CODE:
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(path, IMAGE_NAME);
                Uri uri = Uri.fromFile(file);
                startCrop(uri);
                break;
            case RESULT_CODE:
                getImagePicture(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startCrop(Uri uri) {
        //跳转到android系统自带的图片裁剪工具
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", true);
        //比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_CODE);
    }


    public void getImagePicture(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Bitmap bitmap = bundle.getParcelable("data");
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                savePictureInfo(bitmap, IMAGE_NAME);
                circleImageView.setImageDrawable(drawable);
            }
        }
    }

    private void savePictureInfo(Bitmap bitmap, String imageName) {
        File file = new File(SAVE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        File mFile = new File(SAVE_PATH, imageName);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mFile));
            //还原图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //更新图库
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(mFile);
        intent.setData(uri);
        MainActivity.this.sendBroadcast(intent);
    }
}
