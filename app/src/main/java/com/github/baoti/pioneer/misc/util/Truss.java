package com.github.baoti.pioneer.misc.util;

import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;

import java.util.ArrayDeque;
import java.util.Deque;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

/**
 *  A {@link SpannableStringBuilder} wrapper whose API doesn't make me want to stab my eyes out
 * <br>Copy from JakeWharton's u2020
 */
public class Truss {
    private final SpannableStringBuilder builder;
    private final Deque<Span> stack;
    private int replacements;

    public Truss() {
        builder = new SpannableStringBuilder();
        stack = new ArrayDeque<>();
    }

    public Truss append(String string) {
        builder.append(string);
        return this;
    }

    public Truss append(CharSequence charSequence) {
        builder.append(charSequence);
        return this;
    }

    public Truss append(char c) {
        builder.append(c);
        return this;
    }

    public Truss append(int number) {
        builder.append(String.valueOf(number));
        return this;
    }

    /** Starts {@code span} at the current position in the builder. */
    public Truss pushSpan(Object span) {
        if (span instanceof ReplacementSpan) {
            replacements++;
        } else if (replacements > 0 && span instanceof CharacterStyle) {
            span = convert((CharacterStyle) span);
        }
        stack.addLast(new Span(builder.length(), span));
        return this;
    }

    public static MetricAffectingSpan wrap(CharacterStyle style) {
        return new MetricAffectAdapter(style);
    }

    private static MetricAffectingSpan convert(CharacterStyle style) {
        if (style instanceof MetricAffectingSpan) {
            return (MetricAffectingSpan) style;
        } else {
            return new MetricAffectAdapter(style);
        }
    }

    /** End the most recently pushed span at the current position in the builder. */
    public Truss popSpan() {
        Span span = stack.removeLast();
        if (span.span instanceof ReplacementSpan) {
            replacements--;
        }
        builder.setSpan(span.span, span.start, builder.length(), SPAN_INCLUSIVE_EXCLUSIVE);
        return this;
    }

    /** Create the final {@link CharSequence}, popping any remaining spans. */
    public CharSequence build() {
        while (!stack.isEmpty()) {
            popSpan();
        }
        return builder; // TODO make immutable copy?
    }

    private static final class Span {
        final int start;
        final Object span;

        public Span(int start, Object span) {
            this.start = start;
            this.span = span;
        }
    }

    private static final class MetricAffectAdapter extends MetricAffectingSpan {
        private final CharacterStyle style;

        MetricAffectAdapter(CharacterStyle style) {
            this.style = style;
        }

        @Override
        public void updateMeasureState(TextPaint p) {
            if (style instanceof MetricAffectingSpan) {
                ((MetricAffectingSpan) style).updateMeasureState(p);
            }
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            style.updateDrawState(tp);
        }
    }
}