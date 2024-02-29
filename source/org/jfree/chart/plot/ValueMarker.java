/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ----------------
 * ValueMarker.java
 * ----------------
 * (C) Copyright 2004-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 09-Feb-2004 : Version 1 (DG);
 * 16-Feb-2005 : Added new constructor (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 05-Sep-2006 : Added setValue() method (DG);
 * 08-Oct-2007 : Fixed bug 1808376, constructor calling super with incorrect
 *               values (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.renderer.IntermediateAbstractRenderer;
import org.jfree.data.Range;
import org.jfree.text.TextUtilities;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

/**
 * A marker that represents a single value.  Markers can be added to plots to
 * highlight specific values.
 */
public class ValueMarker extends Marker {

    /** The value. */
    private double value;

    /**
     * Creates a new marker.
     *
     * @param value  the value.
     */
    public ValueMarker(double value) {
        super();
        this.value = value;
    }

    /**
     * Creates a new marker.
     *
     * @param value  the value.
     * @param paint  the paint (<code>null</code> not permitted).
     * @param stroke  the stroke (<code>null</code> not permitted).
     */
    public ValueMarker(double value, Paint paint, Stroke stroke) {
        this(value, paint, stroke, paint, stroke, 1.0f);
    }

    /**
     * Creates a new value marker.
     *
     * @param value  the value.
     * @param paint  the paint (<code>null</code> not permitted).
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param outlinePaint  the outline paint (<code>null</code> permitted).
     * @param outlineStroke  the outline stroke (<code>null</code> permitted).
     * @param alpha  the alpha transparency (in the range 0.0f to 1.0f).
     */
    public ValueMarker(double value, Paint paint, Stroke stroke,
                       Paint outlinePaint, Stroke outlineStroke, float alpha) {
        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.value = value;
    }

    /**
     * Returns the value.
     *
     * @return The value.
     *
     * @see #setValue(double)
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the value for the marker and sends a {@link MarkerChangeEvent} to
     * all registered listeners.
     *
     * @param value  the value.
     *
     * @see #getValue()
     *
     * @since 1.0.3
     */
    public void setValue(double value) {
        this.value = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Tests this marker for equality with an arbitrary object.  This method
     * returns <code>true</code> if:
     *
     * <ul>
     * <li><code>obj</code> is not <code>null</code>;</li>
     * <li><code>obj</code> is an instance of <code>ValueMarker</code>;</li>
     * <li><code>obj</code> has the same value as this marker;</li>
     * <li><code>super.equals(obj)</code> returns <code>true</code>.</li>
     * </ul>
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ValueMarker)) {
            return false;
        }
        ValueMarker that = (ValueMarker) obj;
        if (this.value != that.value) {
            return false;
        }
        return true;
    }
    
    public void getConcreateMarker(ValueAxis axis, Rectangle2D dataArea, Supplier<RectangleEdge> arg0,
			Plot plot, PlotOrientation arg1, PlotOrientation arg2, Graphics2D g2,
			IntermediateAbstractRenderer render) {
		double value = this.getValue();
		Range range = axis.getRange();
		if (!range.contains(value)) {
			return;
		}
		double v = axis.valueToJava2D(value, dataArea, arg0.get());
		PlotOrientation orientation = plot.getOrientation();
		Line2D line = null;
		if (orientation == arg1) {
			line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
		} else if (orientation == arg2) {
			line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
		}
		final Composite savedComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getAlpha()));
		g2.setPaint(this.getPaint());
		g2.setStroke(this.getStroke());
		g2.draw(line);
		String label = this.getLabel();
		RectangleAnchor anchor = this.getLabelAnchor();
		if (label != null) {
			Font labelFont = this.getLabelFont();
			g2.setFont(labelFont);
			g2.setPaint(this.getLabelPaint());
			Point2D coordinates = render.calculateMarkerTextAnchorPoint(orientation, line.getBounds2D(),
					this.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor, arg1, arg2);
			TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(),
					this.getLabelTextAnchor());
		}
		g2.setComposite(savedComposite);
	}
}
