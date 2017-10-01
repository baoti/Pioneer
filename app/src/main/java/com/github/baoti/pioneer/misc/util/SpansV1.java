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
import android.text.style.UpdateAppearance;

/**
 * text span 旧版实现，未定义 SpanGroup，也未采用继承关系
 * Created by sean on 2017/9/30.
 * @see Spans
 */
public class SpansV1 {

    public static class DebugSpan extends ReplacementSpan {
        protected final Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
        protected final Rect frame = new Rect();
//        protected final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        public DebugSpan() {

        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            paint.getFontMetricsInt(fontMetricsInt);
            frame.right = (int) Math.ceil(paint.measureText(text, start, end));
            frame.top = fontMetricsInt.top;
            frame.bottom = fontMetricsInt.bottom;

            if (fm != null) {
                paint.getFontMetricsInt(fm);
//                fm.ascent = -frame.bottom;
//                fm.descent = 0;
//
//                fm.top = fm.ascent;
//                fm.bottom = 0;
            }

            return frame.right;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            canvas.drawText(text, start, end, x, y, paint);

            drawDebugLines(canvas, x, top, y, bottom, paint);
        }

        protected void drawDebugLines(@NonNull Canvas canvas, float x, int top, int y, int bottom, @NonNull Paint paint) {
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

    public static class ShapeSpan extends DebugSpan {
        private final Shape shape;
        @ColorInt
        private final int color;
        private final int strokeWidth = 8;
        private final Paint.Style style;
        private final boolean drawText;

        public ShapeSpan(Shape shape) {
            this(shape, false);
        }

        public ShapeSpan(Shape shape, boolean drawText) {
            this(shape, Color.YELLOW, Paint.Style.STROKE, drawText);
        }

        public ShapeSpan(Shape shape, @ColorInt int color, Paint.Style style) {
            this(shape, color, style, false);
        }

        public ShapeSpan(Shape shape, @ColorInt int color, Paint.Style style, boolean drawText) {
            this.shape = shape;
            this.color = color;
            this.style = style;
            this.drawText = drawText;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            int size = super.getSize(paint, text, start, end, fm);
//            shape.resize(frame.right - strokeWidth, fontMetricsInt.bottom - fontMetricsInt.top - strokeWidth);
            shape.resize(frame.right - strokeWidth, fontMetricsInt.descent - fontMetricsInt.ascent - strokeWidth);
            return size;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            drawDebugLines(canvas, x, top, y, bottom, paint);

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

            int width = frame.width();

            paint.setTextSize(paint.getTextSize() * 0.8f);
            if (drawText) {
                if (false) {
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(text, start, end, frame.centerX(), y, paint);
                } else {
                    canvas.drawText(text, start, end, x + (width * 0.2f) / 2, y, paint);
                }
            }
        }
    }

    public static class HollowSpan extends ReplacementSpan implements UpdateAppearance {
        //        private final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        private final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        private int width;
        private Paint.FontMetricsInt fontMetricsInt = new Paint.FontMetricsInt();
        private Rect frame = new Rect();
        private Bitmap bitmap;
        private Canvas bitmapCanvas;
        private final CharacterStyle[] styles;

        public HollowSpan(CharacterStyle... styles) {
            this.styles = styles;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
            width = 0;
            for (CharacterStyle style : styles) {
                if (style instanceof ReplacementSpan) {
                    width = Math.max(width, ((ReplacementSpan) style).getSize(paint, text, start, end, fm));
                } else if (paint instanceof TextPaint) {
                    if (style instanceof MetricAffectingSpan) {
                        ((MetricAffectingSpan) style).updateMeasureState((TextPaint) paint);
                    }
                }
            }
            if (fm != null) {
                paint.getFontMetricsInt(fm);
            }
            paint.getFontMetricsInt(fontMetricsInt);
            width = Math.max(width, (int) Math.ceil(paint.measureText(text, start, end)));
            frame.right = width;
            frame.top = fontMetricsInt.top;
            frame.bottom = fontMetricsInt.bottom;
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(width, frame.bottom - frame.top, Bitmap.Config.ARGB_8888);
                bitmapCanvas = new Canvas(bitmap);
            }
            return width;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start, @IntRange(from = 0) int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
//            bitmapCanvas.drawColor(Color.GRAY, PorterDuff.Mode.CLEAR);
            int color = /*paint instanceof TextPaint ? ((TextPaint) paint).bgColor :*/ paint.getColor();
            bitmapCanvas.drawColor(color);
            for (CharacterStyle style : styles) {
                if (style instanceof ReplacementSpan) {
                    ((ReplacementSpan) style).draw(bitmapCanvas, text, start, end, 0, top, y, bottom, paint);
                } else if (paint instanceof TextPaint) {
                    style.updateDrawState((TextPaint) paint);
                }
            }
            paint.setXfermode(xfermode);
            bitmapCanvas.drawText(text, start, end, 0, y, paint);
            canvas.drawBitmap(bitmap, x, 0, null);
        }
    }
}
