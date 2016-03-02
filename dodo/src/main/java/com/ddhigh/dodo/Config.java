package com.ddhigh.dodo;

/**
 * @project Study
 * @package com.ddhigh.dodo
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class Config {
    public static class ApiCloud {
        public static final String appid = "A6990125149280";
        public static final String appkey = "E448A921-A1A7-FD66-8B47-BDF15CF872CE";
    }

    /**
     * 常量
     */
    public static class Constants {
        public static final int CODE_PICK_IMAGE = 1;//选择图片
        public static final int CODE_PICK_IMAGE_FROM_CAMERA = 2;//相机选择图片
        public static final int CODE_PICK_IMAGE_FROM_PHOTO = 3;//相册选择图片
        public static final int CODE_CROP_IMAGE = 4;//裁剪图片
        public static final String BROADCAST_USER_CHANGED = "com.ddhigh.dodo.action.USER_CHANGED";//用户信息发生改变
        public static final String BROADCAST_USER_UNAUTHORIZED = "com.ddhigh.dodo.action.USER_UNAUTHORIZED";//用过登录失效
    }
}
