/*
 * Copyright (c) 2014-2017 Sean Liu.
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

package com.github.baoti.pioneer.misc.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * text span 工具类，提供可组合使用的 ReplacementSpan 实例。
 * <br>可用于实现 绘制带圆角矩形背景的文字，甚至是绘制镂空的文字
 * Created by sean on 2017/10/1.
 */
public class Spans {

    public static class SupportSpan extends ReplacementSpan {
        protected final Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
        protected final Rect frame = new Rect();

        @Override
        public final int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            return getSize(frame, paint, text, start, end, fm);
        }

        @Override
        public final void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            draw(frame, canvas, text, start, end, x, top, y, bottom, paint);
        }

        public int getSize(@NonNull Rect outRect, @NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            return getMinSize(outRect, paint, text, start, end, fm);
        }

        public int getMinSize(@NonNull Rect outRect, @NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            if (fm != null) {
                paint.getFontMetricsInt(fm);
            }
            int width = (int) Math.ceil(paint.measureText(text, start, end));
            paint.getFontMetricsInt(fontMetricsInt);
            frame.right = width;
            frame.top = fontMetricsInt.top;
            frame.bottom = fontMetricsInt.bottom;
            return width;
        }

        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        }
    }

    /**
     * 可嵌套使用的 ReplacementSpan
     */
    public static class SpanGroup extends SupportSpan {
        private final List<CharacterStyle> styles;

        public SpanGroup(CharacterStyle... styles) {
            this.styles = new ArrayList<>(styles.length);
            Collections.addAll(this.styles, styles);
        }

        @Override
        public int getSize(@NonNull Rect outRect, @NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            int width = super.getSize(outRect, paint, text, start, end, fm);
            if (styles != null) {
                for (CharacterStyle style : styles) {
                    if (style instanceof SupportSpan) {
                        width = Math.max(width, ((SupportSpan) style).getSize(frame, paint, text, start, end, fm));
                    } else if (style instanceof ReplacementSpan) {
                        width = Math.max(width, ((ReplacementSpan) style).getSize(paint, text, start, end, fm));
                    } else if (paint instanceof TextPaint) {
                        if (style instanceof MetricAffectingSpan) {
                            ((MetricAffectingSpan) style).updateMeasureState((TextPaint) paint);
                        }
                    }
                }
            }
            frame.right = width;
            return width;
        }

        @Override
        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            for (CharacterStyle style : styles) {
                if (style instanceof SupportSpan) {
                    ((SupportSpan) style).draw(frame, canvas, text, start, end, x, top, y, bottom, paint);
                } else if (style instanceof ReplacementSpan) {
                    ((ReplacementSpan) style).draw(canvas, text, start, end, x, top, y, bottom, paint);
                } else if (paint instanceof TextPaint) {
                    style.updateDrawState((TextPaint) paint);
                }
            }
        }
    }

    /**
     * 文本绘制，用于与 ReplacementSpan 组合。
     * <br>因为 TextLine 默认不会为 ReplacementSpan 绘文字
     */
    public static class TextSpan extends SpanGroup {
        private final Paint.Align align;
        private final Paint.Style style;
        private final float strokeWidth;

        public TextSpan(CharacterStyle... styles) {
            this(null, styles);
        }

        public TextSpan(Paint.Align align, CharacterStyle... styles) {
            this(align, null, 0, styles);
        }

        public TextSpan(Paint.Align align, Paint.Style style, float strokeWidth, CharacterStyle... styles) {
            super(styles);
            this.align = align;
            this.strokeWidth = strokeWidth;
            this.style = style;
        }

        @Override
        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            Paint.Align oldAlign = paint.getTextAlign();
            Paint.Style oldStyle = paint.getStyle();
            float oldStrokeWidth = paint.getStrokeWidth();
            if (align != null) {
                paint.setTextAlign(align);
            }
            if (style != null) {
                paint.setStyle(style);
            }
            if (strokeWidth > 0) {
                paint.setStrokeWidth(strokeWidth);
            }
            super.draw(outRect, canvas, text, start, end, x, top, y, bottom, paint);
            switch (paint.getTextAlign()) {
                case CENTER:
                    canvas.drawText(text, start, end, x + (outRect.right - outRect.left) / 2, y, paint);
                    break;
                default:
                    canvas.drawText(text, start, end, x, y, paint);
                    break;
            }
            paint.setTextAlign(oldAlign);
            paint.setStyle(oldStyle);
            paint.setStrokeWidth(oldStrokeWidth);
        }
    }

    /**
     * 镂空绘制样式
     */
    public static class HollowSpan extends SpanGroup {
        private final SpanGroup srcGroup;
        private final PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        private Bitmap bitmap;
        private Canvas bitmapCanvas;

        /**
         * 镂空绘制
         * @param styles 基础样式
         * @param srcStyles 镂空样式
         */
        public HollowSpan(CharacterStyle[] styles, CharacterStyle[] srcStyles) {
            super(styles);
            this.srcGroup = new SpanGroup(srcStyles);
        }

        /**
         * 镂空绘制
         * @param styles 基础样式
         * @param srcGroup 镂空样式
         */
        public HollowSpan(CharacterStyle[] styles, SpanGroup srcGroup) {
            super(styles);
            this.srcGroup = srcGroup;
        }

        @Override
        public int getSize(@NonNull Rect outRect, @NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            int width = super.getSize(outRect, paint, text, start, end, fm);
            srcGroup.getSize(outRect, paint, text, start, end, fm);
            return width;
        }

        @Override
        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(frame.right - frame.left, frame.bottom - frame.top, Bitmap.Config.ARGB_8888);
                bitmapCanvas = new Canvas(bitmap);
            }
            bitmapCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            bitmapCanvas.translate(-x, 0);
            super.draw(outRect, bitmapCanvas, text, start, end, x, top, y, bottom, paint);
            Xfermode oldXfermode = paint.getXfermode();
            paint.setXfermode(this.xfermode);
            srcGroup.draw(outRect, bitmapCanvas, text, start, end, x, top, y, bottom, paint);
            paint.setXfermode(oldXfermode);
            canvas.drawBitmap(bitmap, x, 0, null);
        }
    }

    /**
     * 调试绘制，画 top/bottom/ascent/descent 位置的线
     */
    public static class DebugSpan extends SpanGroup {

        public DebugSpan(CharacterStyle... styles) {
            super(styles);
        }

        @Override
        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            drawDebugLines(outRect, canvas, x, top, y, bottom, paint);

            super.draw(outRect, canvas, text, start, end, x, top, y, bottom, paint);
        }

        protected void drawDebugLines(Rect outRect, @NonNull Canvas canvas, float x, int top, int y, int bottom, @NonNull Paint paint) {
            int width = frame.width();
            int oldColor = paint.getColor();

            // x, y
            canvas.drawLine(x, y, x + width, y, paint);

            // x, top
            paint.setColor(Color.RED);
            canvas.drawLine(x, top, x + width, top, paint);
            canvas.drawLine(x, y + fontMetricsInt.top, x + width, bottom, paint);

            // x, ascent
            paint.setColor(Color.GREEN);
            canvas.drawLine(x, y + paint.ascent(), x + width, bottom, paint);
            canvas.drawLine(x, y + fontMetricsInt.ascent, x + width, y + fontMetricsInt.ascent, paint);

            // x, descent
            paint.setColor(Color.BLUE);
            canvas.drawLine(x, y + paint.descent(), x + width, top, paint);
            canvas.drawLine(x, y + fontMetricsInt.descent, x + width, y + fontMetricsInt.descent, paint);

            // x, bottom
            paint.setColor(Color.YELLOW);
            canvas.drawLine(x, bottom, x + width, bottom, paint);
            canvas.drawLine(x, y + fontMetricsInt.bottom, x + width, top, paint);

            paint.setColor(oldColor);
        }
    }

    /**
     * 图形绘制，可使用 Shape 绘制图形，如：圆角矩形
     */
    public static class ShapeSpan extends SpanGroup {
        private final Shape shape;
        private final Paint.Style style;
        private final int strokeWidth;
        @ColorInt
        private final int color;

        public ShapeSpan(Shape shape, Paint.Style style, int strokeWidth, @ColorInt int color, CharacterStyle... styles) {
            super(styles);
            this.shape = shape;
            this.style = style;
            this.strokeWidth = strokeWidth;
            this.color = color;
        }

        @Override
        public int getSize(@NonNull Rect outRect, @NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            int width = super.getSize(outRect, paint, text, start, end, fm);
//            shape.resize(frame.right - strokeWidth, fontMetricsInt.bottom - fontMetricsInt.top - strokeWidth);
            shape.resize(frame.right - strokeWidth, fontMetricsInt.descent - fontMetricsInt.ascent - strokeWidth);
            return width;
        }

        @Override
        public void draw(@NonNull Rect outRect, @NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            float oldStrokeWidth = paint.getStrokeWidth();
            Paint.Style oldStyle = paint.getStyle();
            int oldColor = paint.getColor();
            paint.setStrokeWidth(strokeWidth);
            paint.setStyle(style);
            paint.setColor(color);
            canvas.save();
            canvas.translate(x + strokeWidth / 2, fontMetricsInt.ascent - fontMetricsInt.top + strokeWidth / 2);
            shape.draw(canvas, paint);
            canvas.restore();
            paint.setColor(oldColor);
            paint.setStyle(oldStyle);
            paint.setStrokeWidth(oldStrokeWidth);

            float oldTextSize = paint.getTextSize();
            paint.setTextSize(oldTextSize * 0.8f);
            super.draw(outRect, canvas, text, start, end, x, top, y, bottom, paint);
            paint.setTextSize(oldTextSize);
        }
    }
}
