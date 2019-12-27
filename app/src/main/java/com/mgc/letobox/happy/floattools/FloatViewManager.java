package com.mgc.letobox.happy.floattools;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.leto.game.base.event.FloatIconRelocateEvent;
import com.leto.game.base.event.FloatIconVisibilityEvent;
import com.leto.game.base.util.BaseAppUtil;
import com.mgc.letobox.happy.view.FloatBubbleView;
import com.mgc.letobox.happy.view.ShakeShakeView;
import com.mgc.letobox.happy.view.UpgradeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Map;

public class FloatViewManager {
    private static FloatViewManager INST = new FloatViewManager();

    private FloatViewManager() {
        EventBus.getDefault().register(this);
    }

    public static FloatViewManager getInstance() {
        return INST;
    }

    private SparseArray<WeakReference<FloatBubbleView>> bubbleViews = new SparseArray<>();

    public int addBubble(final Activity activity, int count, int x, int y, final View.OnClickListener onBubbleClickListener) {
        final FloatBubbleView bubbleView = new FloatBubbleView(activity);
        bubbleView.setCoinCount(count);

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        decorView.addView(bubbleView, lp);

        bubbleView.setX(x);
        bubbleView.setY(y);

        bubbleViews.put(bubbleView.getBubbleId(), new WeakReference<>(bubbleView));
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBubbleClickListener != null) onBubbleClickListener.onClick(view);
            }
        });
        return bubbleView.getBubbleId();
    }

    private WeakReference<ShakeShakeView> weakShakeView;

    public ShakeShakeView showShakeShake(Activity activity) {
        return showShakeShake(activity, 0, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFloatIconRelocateEvent(FloatIconRelocateEvent e) {
        if(e.viewId == FloatIconRelocateEvent.SHAKE_VIEW) {
            if (weakShakeView != null) {
                ShakeShakeView v = weakShakeView.get();
                if(v != null) {
                    v.relocate(e.x, e.y, e.pinned);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFloatIconVisibilityEvent(FloatIconVisibilityEvent e) {
        if(e.viewId == FloatIconRelocateEvent.SHAKE_VIEW) {
            if (weakShakeView != null) {
                ShakeShakeView v = weakShakeView.get();
                if(v != null) {
                    v.setVisibility(e.visible ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
    }

    public ShakeShakeView showShakeShake(Activity activity, int xDirection, float yRatio) {
        if (weakShakeView == null || weakShakeView.get() == null) {
            ShakeShakeView shakeView = new ShakeShakeView(activity);

            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            decorView.addView(shakeView, lp);

            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            shakeView.measure(w, h);

            int x = 0;
            if (xDirection == 1) {
                x = BaseAppUtil.getDeviceWidth(activity) - shakeView.getMeasuredWidth();
            }
            int y = (int) (yRatio * BaseAppUtil.getDeviceHeight(activity));
            shakeView.setX(x);
            shakeView.setY(y);

            weakShakeView = new WeakReference<>(shakeView);
        } else {
            ShakeShakeView shakeView = weakShakeView.get();
            shakeView.setVisibility(View.VISIBLE);
        }
        return weakShakeView.get();
    }

    public void hideShakeView() {
        if (weakShakeView != null && weakShakeView.get() != null) {
            weakShakeView.get().setVisibility(View.GONE);
        }
    }

    public void removeShakeView(Activity activity) {
        if (weakShakeView != null && weakShakeView.get() != null && activity!= null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ShakeShakeView shakeView = weakShakeView.get();
            if (shakeView.getParent() == decorView) {
                decorView.removeView(shakeView);
            }
            weakShakeView = null;
        }
    }

    public void hideBubbleView(int id) {
        WeakReference<FloatBubbleView> wr = bubbleViews.get(id);
        if (wr != null && wr.get() != null) {
            wr.get().setVisibility(View.GONE);
        }
    }

    public void removeBubbleView(Activity activity, int id) {
        WeakReference<FloatBubbleView> wr = bubbleViews.get(id);
        if (wr != null && wr.get() != null && activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            FloatBubbleView bubbleView = wr.get();
            if (bubbleView.getParent() == decorView) {
                decorView.removeView(bubbleView);
            }
        }
        bubbleViews.remove(id);
    }

    public void showBubbleView(int id) {
        WeakReference<FloatBubbleView> wr = bubbleViews.get(id);
        if (wr != null && wr.get() != null) {
            wr.get().setVisibility(View.VISIBLE);
        }
    }

    public void removeAllBubbleViews(Activity activity) {
        for (int i = 0; i < bubbleViews.size(); i++) {
            removeBubbleView(activity, bubbleViews.keyAt(i));
        }
    }

    public void hideAllBubbleViews() {
        for (int i = 0; i < bubbleViews.size(); i++) {
            hideBubbleView(bubbleViews.keyAt(i));
        }
    }

    public void showAllBubbleViews() {
        for (int i = 0; i < bubbleViews.size(); i++) {
            showBubbleView(bubbleViews.keyAt(i));
        }
    }

    public int getBubbleCount() {
        return bubbleViews.size();
    }


    private WeakReference<UpgradeView> wakeUpgradeView;

    public UpgradeView showUpgradeView(Activity activity, String gameId) {
        return showUpgradeView(activity, gameId, 0, 0);
    }

    public UpgradeView showUpgradeView(Activity activity, String gameId, int xDirection, float yRatio) {
        if (wakeUpgradeView == null || wakeUpgradeView.get() == null) {
            UpgradeView upgradeView = new UpgradeView(activity);
            upgradeView.setGameId(gameId);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            decorView.addView(upgradeView, lp);

            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            upgradeView.measure(w, h);

            int x = 0;
            if (xDirection == 1) {
                x = BaseAppUtil.getDeviceWidth(activity) - upgradeView.getMeasuredWidth();
            }
            int y = (int) (yRatio * BaseAppUtil.getDeviceHeight(activity));
            upgradeView.setX(x);
            upgradeView.setY(y);

            wakeUpgradeView = new WeakReference<>(upgradeView);
        } else {
            UpgradeView upgradeView = wakeUpgradeView.get();
            upgradeView.setVisibility(View.VISIBLE);

            // 已经存在view了, 重新设置一下位置
            int x = 0;
            if (xDirection == 1) {
                x = BaseAppUtil.getDeviceWidth(activity) - upgradeView.getMeasuredWidth();
            }
            int y = (int) (yRatio * BaseAppUtil.getDeviceHeight(activity));
            upgradeView.setX(x);
            upgradeView.setY(y);
        }
        return wakeUpgradeView.get();
    }


    public void hideUpgradeView() {
        if (wakeUpgradeView != null && wakeUpgradeView.get() != null) {
            wakeUpgradeView.get().setVisibility(View.GONE);
        }
    }

    public void removeUpgradeView(Activity activity) {
        if (wakeUpgradeView != null && wakeUpgradeView.get() != null && activity != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            UpgradeView upgradeView = wakeUpgradeView.get();
            //清理handler
            upgradeView.onDestroy();

            if (upgradeView.getParent() == decorView) {
                decorView.removeView(upgradeView);
            }
            wakeUpgradeView = null;
        }
    }

    public void notifyUpgrade(String gameId, Map<String, Integer> gameInfo) {

        if (wakeUpgradeView != null && wakeUpgradeView.get() != null) {

            UpgradeView upgradeView = wakeUpgradeView.get();
            upgradeView.notifyUpdate(gameId, gameInfo);
        }
    }

}