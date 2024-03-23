package com.example.musichub.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUtils {

    public static void downloadFile(final Context context, final String fileUrl, final String fileName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Tạo URL từ đường dẫn tệp
                    URL url = new URL(fileUrl);

                    // Mở kết nối HTTP
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // Kiểm tra phản hồi HTTP
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        // Nếu không thành công, hiển thị thông báo và thoát
                        showToast(context, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                        return;
                    }

                    // Lấy đường dẫn của thư mục lưu trữ external
                    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                    // Tạo một tệp mới trong thư mục lưu trữ với tên được chỉ định
                    File file = new File(storageDir, fileName);

                    // Tạo một luồng đầu vào từ kết nối HTTP
                    InputStream input = connection.getInputStream();
                    // Tạo một luồng đầu ra để ghi dữ liệu vào tệp
                    FileOutputStream output = new FileOutputStream(file);

                    // Đọc dữ liệu từ luồng đầu vào và ghi vào tệp
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    // Đóng các luồng
                    output.close();
                    input.close();

                    // Hiển thị thông báo tải xuống thành công
                    showToast(context, "File downloaded successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                    // Xử lý lỗi và hiển thị thông báo
                    showToast(context, "Error downloading file: " + e.getMessage());
                }
            }
        }).start();
    }

    private static void showToast(final Context context, final String message) {
        // Hiển thị Toast trên luồng giao diện người dùng
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
