package com.ddhigh.mylibrary.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 本地图片加载器
 *
 * @author xialeistudio<xialeistudio@gmail.com>
 */
@SuppressWarnings("unused")
public class LocalImageLoader {
    private static LocalImageLoader mInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;

    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池中线程数
     */
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 队列加载策略
     */
    private Type mType = Type.LIFO;

    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 信号量，用来同步mPoolThreadHandler和addTask
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    /**
     * 信号量，用来同步getTask
     */
    private Semaphore mSemaphoneThreadPool;
    /**
     * 后台轮询线程
     * 轮询任务队列是否有图片需要加载
     */
    @SuppressWarnings("FieldCanBeLocal")
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;

    /**
     * UI线程
     * 图片加载成功使用该handler发送消息更新UI
     */
    private Handler mUIHandler;

    /**
     * 加载策略
     */
    public enum Type {
        FIFO,  //first in,first out 先进先出
        LIFO //last in,first out 后进先出
    }

    private LocalImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        //初始化后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //通过线程池取出一个任务执行
                        Runnable r = getTask();
                        if (r != null) {
                            mThreadPool.execute(r);
                            try {
                                //阻塞超过threadCount的任务
                                mSemaphoneThreadPool.acquire();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };
                //释放信号量，addTask执行
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();
        //初始化缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheMemory = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //高度x每行字节数得到图片大小
                return value.getHeight() * value.getRowBytes();
            }
        };
        //初始化线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        //初始化队列
        mTaskQueue = new LinkedList<>();

        mType = type;
        mSemaphoneThreadPool = new Semaphore(threadCount);
    }

    /**
     * 从任务队列取出一个图片加载任务
     *
     * @return
     */
    private Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static LocalImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (LocalImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new LocalImageLoader(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 根据path加载图片并显示
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        imageView.setTag(path);
        if (mUIHandler == null) {
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取得到bitmap，回调imageView设置图片
                    ImageBeanHolder holder = (ImageBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;

                    //将path和imageView存储路径比较，避免混乱
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }

        //根据路径在内存缓存中查找bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            refreshBitmap(path, imageView, bm);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片
                    //压缩
                    //1.获得图片需要显示的大小
                    ImageSize size = getImageSize(imageView);
                    //2.压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, size.width, size.height);
                    //3.把图片加入缓存
                    addBitmapToLruCache(path, bm);
                    //回调
                    refreshBitmap(path, imageView, bm);
                    mSemaphoneThreadPool.release();
                }
            });
        }
    }

    /**
     * 刷新bitmap
     *
     * @param path      路径
     * @param imageView imageView
     * @param bm        图片值
     */
    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        Message m = Message.obtain();
        ImageBeanHolder h = new ImageBeanHolder();
        h.bitmap = bm;
        h.path = path;
        h.imageView = imageView;
        m.obj = h;
        mUIHandler.sendMessage(m);
    }

    /**
     * 将图片加入缓存
     *
     * @param path 图片路径
     * @param bm   图片
     */
    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null && bm != null) {
            mLruCache.put(path, bm);
        }
    }

    /**
     * 根据图片需要显示的宽高压缩指定路径的图片
     *
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

        //获取图片大小并不加载图片到内存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);//获取图片实际宽高
        options.inSampleSize = caculateInSampleSize(options, width, height);
        //使用获取到的InSampleSize在此解析图片
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    /**
     * 根据需要的宽高和图片实际宽高计算sampleSize
     *
     * @param options   实际宽高
     * @param reqWidth  需要宽度
     * @param reqHeight 需要高度
     * @return 压缩比例
     */
    private int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio); //max 为最大压缩比例，内存占用低，图片质量低，min为最小压缩
        }
        return inSampleSize;
    }

    /**
     * 根据imageView获取适当图片压缩宽高
     *
     * @param imageView
     * @return
     */
    private ImageSize getImageSize(ImageView imageView) {

        ImageSize imageSize = new ImageSize();

        DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();

        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        //获取imageView实际宽度
        int width = imageView.getWidth();
        if (width <= 0) {
            width = lp.width;//获取imageView在layout中声明的宽度
        }
        if (width <= 0) {//wrap_content || match_parent
            width = getImageViewFieldValue(imageView,"mMaxWidth");
        }
        if (width <= 0) {//屏幕宽度
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0) {
            height = lp.height;//获取imageView在layout中声明的宽度
        }
        if (height <= 0) {//wrap_content || match_parent
            height = getImageViewFieldValue(imageView,"mMaxHeight");//检查最大高度
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        imageSize.width = width;
        imageSize.height = height;
        return imageSize;
    }

    /**
     * 通过反射获取imageView的某个值
     *
     * @param object
     * @param fieldName
     * @return
     */
    private static int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;
        Field field;
        try {
            field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(object);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 同步操作，防止多个线程请求信号量死锁
     * 添加加载图片任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        mTaskQueue.add(runnable);
        //mPoolThreadHandler只处理添加图片加载任务一种消息，不用判断
        //mPoolThreadHandler有可能为空
        try {
            if (mPoolThreadHandler == null)
                mSemaphorePoolThreadHandler.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(1);
    }

    /**
     * 根据路径在内存查找bitmap
     *
     * @param path
     * @return
     */
    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private class ImageBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

    /**
     * 图片大小
     */
    private class ImageSize {
        int width;
        int height;
    }
}
