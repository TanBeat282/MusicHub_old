package com.example.musichub.helper;

import android.content.Context;
import android.view.View;
import android.widget.MediaController;

public class CustomMediaController extends MediaController {

    public CustomMediaController(Context context) {
        super(context);
    }

    @Override
    public void setAnchorView(View view) {
        // Để ẩn bộ điều khiển mặc định, bạn có thể không gọi phương thức gốc
        // super.setAnchorView(view);
    }

    // Thêm các phương thức tùy chỉnh khác nếu cần thiết
}
