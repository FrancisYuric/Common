package com.supcon.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.app.annotation.apt.Router;
import com.supcon.IntentRouter;
import com.supcon.common.com_router.util.RouterManager;
import com.supcon.common.view.base.activity.BaseActivity;
import com.supcon.common.view.view.loader.CircularLoaderView;

@Router("main")
public class MainActivity extends BaseActivity {

    CircularLoaderView mCircularLoaderView;

    RouterManager mRouterManager = RouterManager.getInstance();

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.routerText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentRouter.go(context, "routerTest");
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onLoading("正在加载1123");
                onLoadFailed("2343");
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onLoadFailed("2343");
//                        }
//                    }, 5000);
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoading("正在加载...");
                onLoadSuccess();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                            onLoadSuccess();
//                    }
//                }, 5000);
            }
        });

        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoading("正在加载...");

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoading("正在解析2000");
//                    }
//                }, 2000);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoading("正在解析4000");
//                    }
//                }, 4000);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoading("正在解析6000");
//                    }
//                }, 6000);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoading("正在解析8000");
//                    }
//                }, 8000);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        onLoadSuccess("解析成功！");
//                    }
//                }, 10000);
                onLoading("正在解析2000");
                onLoading("正在解析4000");
                onLoading("正在解析6000");
                onLoading("正在解析8000");
                onLoading("正在解析10000");
                onLoading("正在解析12000");
                onLoadSuccess("解析成功！");
            }
        });

        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoading("正在加载...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            mLoaderController.closeLoader();
                    }
                }, 5000);
            }
        });

        mCircularLoaderView = findViewById(R.id.btn2);

        mCircularLoaderView.setDoneColor(ContextCompat.getColor(context, R.color.bapThemeBlue));
        mCircularLoaderView.setInitialHeight(100);
        mCircularLoaderView.setSpinningBarColor(ContextCompat.getColor(context, R.color.bapThemeOrange));

        mCircularLoaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonAndRevert((CircularLoaderView) v, ContextCompat.getColor(context, R.color.bapThemeBlue),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_success2));
            }
        });






    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

//        onLoading("正在加载。。。");
    }

    private void animateButtonAndRevert(final CircularLoaderView circular, final int fillColor, final Bitmap bitmap) {
        Handler handler = new  Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                circular.doneLoadingAnimation(fillColor, bitmap);
            }
        };

        Runnable runnableRevert = new Runnable() {
            @Override
            public void run() {
                circular.revertAnimation();
                circular.setSpinningBarColor(Color.MAGENTA);
            }
        };


        circular.revertAnimation();

        circular.startAnimation();
        handler.postDelayed(runnable, 3000);
        handler.postDelayed(runnableRevert, 4000);
        handler.postDelayed(runnableRevert, 4100);
    }

}