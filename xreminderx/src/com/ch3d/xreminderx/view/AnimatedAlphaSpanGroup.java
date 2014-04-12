
package com.ch3d.xreminderx.view;

import java.util.ArrayList;
import java.util.Collections;

public final class AnimatedAlphaSpanGroup
{
    private static final boolean                        DEBUG = false;
    private static final String                         TAG   = "AnimatedAlphaSpanGroup";

    private final float                                 mProgress;
    private final ArrayList<MutableForegroundColorSpan> mSpans;
    private final ArrayList<Integer>                    mSpanIndexes;

    public AnimatedAlphaSpanGroup()
    {
        mProgress = 0;
        mSpans = new ArrayList<MutableForegroundColorSpan>();
        mSpanIndexes = new ArrayList<Integer>();
    }

    public void addSpan(final MutableForegroundColorSpan span)
    {
        span.setAlpha(0);
        mSpanIndexes.add(mSpans.size());
        mSpans.add(span);
    }

    public float getProgress()
    {
        return mProgress;
    }

    public void init()
    {
        Collections.shuffle(mSpans);
    }

    public void setProgress(final float progress)
    {
        final int size = mSpans.size();
        float total = 1.0f * size * progress;

        for (int index = 0; index < size; index++)
        {
            final MutableForegroundColorSpan span = mSpans.get(index);

            if (total >= 1.0f)
            {
                span.setAlpha(255);
                total -= 1.0f;
            }
            else
            {
                span.setAlpha((int) (total * 255));
                total = 0.0f;
            }
        }
    }
}
