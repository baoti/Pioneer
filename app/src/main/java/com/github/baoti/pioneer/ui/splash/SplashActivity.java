/*
 * Copyright (c) 2014-2016 Sean Liu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.baoti.pioneer.ui.splash;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.baoti.android.presenter.ActivityView;
import com.github.baoti.pioneer.AppMain;
import com.github.baoti.pioneer.R;
import com.github.baoti.pioneer.misc.util.Spans;
import com.github.baoti.pioneer.misc.util.Truss;
import com.github.baoti.pioneer.ui.Navigator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import dagger.Lazy;
import timber.log.Timber;

/**
 * Created by liuyedong on 14-12-18.
 */
public class SplashActivity extends ActivityView<ISplashView, SplashPresenter> implements ISplashView {

    @BindView(R.id.tv_status)
    TextView statusText;

    @BindView(R.id.tv_styled_text)
    TextView styledText;

    @BindView(R.id.btn_retain_in_bundle)
    Button retainInBundle;

    @BindView(R.id.btn_retain_in_presenter)
    Button retainInPresenter;

    @Inject
    Lazy<SplashPresenter> presenterLazy;

    @Override
    protected SplashPresenter createPresenter(ISplashView view) {
        return presenterLazy.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        AppMain.globalGraph().plus(new SplashModule()).inject(this);

        showStyledText();
    }

    @OnCheckedChanged(R.id.cb_retain)
    public void onRetainChanged(boolean checked) {
        Timber.i("retain turn %s", checked ? "on" : "off");
        setRetainPresenter(checked);
    }

    @Override
    public void showStatus(String status) {
        statusText.setText(status);
    }

    void showStyledText() {
        // 外部矩形弧度
        float[] outerR = new float[] { 24, 24, 24, 24, 8, 8, 8, 8 };

        Truss truss = new Truss();
        truss.append("H");
        truss.pushSpan(new BackgroundColorSpan(Color.RED));
        truss.append("e");

        // 绿色字
        truss.pushSpan(Truss.wrap(new ForegroundColorSpan(Color.GREEN)));
        Spans.TextSpan lloText = new Spans.TextSpan(Paint.Align.CENTER, Paint.Style.FILL_AND_STROKE, 4);
        // 黄色圆角空心矩形边框，上面绘字
        Spans.ShapeSpan lloStrokeShape = new Spans.ShapeSpan(
                new RoundRectShape(outerR, null, null), Paint.Style.STROKE, 8, Color.YELLOW, lloText);
        truss.append(new Truss()
                .pushSpan(lloStrokeShape)
                .append("l l o")
                .build());
        truss.append(", ");
        truss.popSpan();

        // 灰色圆角实心矩形边框，上面绘字
        Spans.TextSpan woTextSpan = new Spans.TextSpan(Paint.Align.CENTER, Paint.Style.FILL_AND_STROKE, 4);
        Spans.ShapeSpan woFillShape = new Spans.ShapeSpan(
                new RoundRectShape(outerR, null, null), Paint.Style.FILL_AND_STROKE, 18, Color.LTGRAY, woTextSpan);
        truss.append(new Truss()
                .pushSpan(woFillShape)
                .append("我'")
                .build());
        truss.append("s ");

        // 灰色圆角实心矩形边框，上面镂空绘字
        Spans.ShapeSpan swoFillShape = new Spans.ShapeSpan(
                new RoundRectShape(outerR, null, null), Paint.Style.FILL_AND_STROKE, 18, Color.LTGRAY);
        Spans.TextSpan swoTextSpan = new Spans.TextSpan(Paint.Align.CENTER, Paint.Style.FILL_AND_STROKE, 4);
        CharacterStyle[] bg = new CharacterStyle[]{swoFillShape};
        truss.append(new Truss()
                .pushSpan(new Spans.HollowSpan(bg, swoTextSpan))
                .append("Wo")
                .build());

        truss.append("rl");
        truss.popSpan();
        truss.append("d!");
        styledText.setTextColor(Color.YELLOW);
        styledText.setText(truss.build());
    }

    @OnClick(R.id.btn_go_to_main)
    void onGoToMainClicked() {
        getPresenter().onGoToMainClicked();
    }

    @Override
    public void navigateToMain(boolean byUser) {
        Navigator.launchMain(SplashActivity.this);
    }

    @Override
    public void hideRetainInPresenter() {
        retainInPresenter.setVisibility(View.GONE);
    }

    @Override
    public void hideRetainInBundle() {
        retainInBundle.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_retain_in_presenter)
    void onHideRetainInPresenterClicked() {
        getPresenter().onRetainInPresenterClicked();
    }

    @OnClick(R.id.btn_retain_in_bundle)
    void onHideRetainInBundleClicked() {
        getPresenter().onRetainInBundleClicked();
    }
}
