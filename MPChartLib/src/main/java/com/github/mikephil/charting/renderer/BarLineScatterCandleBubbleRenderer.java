package com.github.mikephil.charting.renderer;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 09/06/16.
 */
public abstract class BarLineScatterCandleBubbleRenderer extends DataRenderer {

    /**
     * buffer for storing the current minimum and maximum visible x
     */
    protected XBounds mXBounds = new XBounds();

    public BarLineScatterCandleBubbleRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Returns true if the DataSet values should be drawn, false if not.
     *
     * @param set
     * @return
     */
    protected boolean shouldDrawValues(IDataSet set) {
        return set.isVisible() && (set.isDrawValuesEnabled() || set.isDrawIconsEnabled());
    }

    /**
     * Checks if the provided entry object is in bounds for drawing considering the current animation phase.
     *
     * @param e
     * @param set
     * @return
     */
    protected boolean isInBoundsX(Entry e, IBarLineScatterCandleBubbleDataSet set) {

        if (e == null)
            return false;

        float entryIndex = set.getEntryIndex(e);

        if (e == null || entryIndex >= set.getEntryCount() * mAnimator.getPhaseX()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Class representing the bounds of the current viewport in terms of indices in the values array of a DataSet.
     */
    protected class XBounds {

        /**
         * minimum visible entry index
         */
        public int min;

        /**
         * maximum visible entry index
         */
        public int max;

        /**
         * range of visible entry indices
         */
        public int range;

        /**
         * Calculates the minimum and maximum x values as well as the range between them.
         *
         * @param chart
         * @param dataSet
         */
        // TODO: 19/10/24 has to be optimised. Is a source of ANRs
        public void set(BarLineScatterCandleBubbleDataProvider chart, IBarLineScatterCandleBubbleDataSet dataSet) {
            float phaseX = Math.max(0.f, Math.min(1.f, mAnimator.getPhaseX()));

            float low = chart.getLowestVisibleX();
            float high = chart.getHighestVisibleX();

            min = dataSet.getNearestEntryIndex(low);
            if (min == dataSet.getEntryCount()) {
                min = min  - 1;
            }
            max = dataSet.getNearestEntryIndex(high);
            if (max == dataSet.getEntryCount()) {
                max = max - 1;
            }
            range = (int) ((max - min) * phaseX);
        }
    }
}
