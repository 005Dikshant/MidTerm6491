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
 * -------------------
 * IntervalMarker.java
 * -------------------
 * (C) Copyright 2002-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 20-Aug-2002 : Added stroke to constructor in Marker class (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 05-Sep-2006 : Added MarkerChangeEvent notification (DG);
 * 18-Dec-2007 : Added new constructor (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.function.Supplier;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.renderer.IntermediateAbstractRenderer;
import org.jfree.data.Range;
import org.jfree.text.TextUtilities;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.ObjectUtilities;

/**
 * Represents an interval to be highlighted in some way.
 */
public class IntervalMarker extends Marker implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -1762344775267627916L;

    /** The start value. */
    private double startValue;

    /** The end value. */
    private double endValue;

    /** The gradient paint transformer (optional). */
    private GradientPaintTransformer gradientPaintTransformer;

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     */
    public IntervalMarker(double start, double end) {
        this(start, end, Color.gray, new BasicStroke(0.5f), Color.gray,
                new BasicStroke(0.5f), 0.8f);
    }

    /**
     * Creates a new interval marker with the specified range and fill paint.
     * The outline paint and stroke default to <code>null</code>.
     *
     * @param start  the lower bound of the interval.
     * @param end  the upper bound of the interval.
     * @param paint  the fill paint (<code>null</code> not permitted).
     *
     * @since 1.0.9
     */
    public IntervalMarker(double start, double end, Paint paint) {
        this(start, end, paint, new BasicStroke(0.5f), null, null, 0.8f);
    }

    /**
     * Constructs an interval marker.
     *
     * @param start  the start of the interval.
     * @param end  the end of the interval.
     * @param paint  the paint (<code>null</code> not permitted).
     * @param stroke  the stroke (<code>null</code> not permitted).
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param alpha  the alpha transparency.
     */
    public IntervalMarker(double start, double end,
                          Paint paint, Stroke stroke,
                          Paint outlinePaint, Stroke outlineStroke,
                          float alpha) {

        super(paint, stroke, outlinePaint, outlineStroke, alpha);
        this.startValue = start;
        this.endValue = end;
        this.gradientPaintTransformer = null;
        setLabelOffsetType(LengthAdjustmentType.CONTRACT);

    }

    /**
     * Returns the start value for the interval.
     *
     * @return The start value.
     */
    public double getStartValue() {
        return this.startValue;
    }

    /**
     * Sets the start value for the marker and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param value  the value.
     *
     * @since 1.0.3
     */
    public void setStartValue(double value) {
        this.startValue = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the end value for the interval.
     *
     * @return The end value.
     */
    public double getEndValue() {
        return this.endValue;
    }

    /**
     * Sets the end value for the marker and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param value  the value.
     *
     * @since 1.0.3
     */
    public void setEndValue(double value) {
        this.endValue = value;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the gradient paint transformer.
     *
     * @return The gradient paint transformer (possibly <code>null</code>).
     */
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    /**
     * Sets the gradient paint transformer and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param transformer  the transformer (<code>null</code> permitted).
     */
    public void setGradientPaintTransformer(
            GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Tests the marker for equality with an arbitrary object.
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
        if (!(obj instanceof IntervalMarker)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        IntervalMarker that = (IntervalMarker) obj;
        if (this.startValue != that.startValue) {
            return false;
        }
        if (this.endValue != that.endValue) {
            return false;
        }
        if (!ObjectUtilities.equal(this.gradientPaintTransformer,
                that.gradientPaintTransformer)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the marker.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException Not thrown by this class, but the
     *         exception is declared for the use of subclasses.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public void getConcreateMarker(ValueAxis axis, Rectangle2D dataArea, Supplier<RectangleEdge> arg0,
			Plot plot, PlotOrientation arg1, PlotOrientation arg2, Graphics2D g2,
			IntermediateAbstractRenderer render) {
		double start = this.getStartValue();
		double end = this.getEndValue();
		Range range = axis.getRange();
		if (!(range.intersects(start, end))) {
			return;
		}
		double start2d = axis.valueToJava2D(start, dataArea, arg0.get());
		double end2d = axis.valueToJava2D(end, dataArea, arg0.get());
		double low = Math.min(start2d, end2d);
		double high = Math.max(start2d, end2d);
		PlotOrientation orientation = plot.getOrientation();
		Rectangle2D rect = null;
		if (orientation == arg1) {
			low = Math.max(low, dataArea.getMinY());
			high = Math.min(high, dataArea.getMaxY());
			rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
		} else if (orientation == arg2) {
			low = Math.max(low, dataArea.getMinX());
			high = Math.min(high, dataArea.getMaxX());
			rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
		}
		final Composite savedComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.getAlpha()));
		Paint p = this.getPaint();
		if (p instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) p;
			GradientPaintTransformer t = this.getGradientPaintTransformer();
			if (t != null) {
				gp = t.transform(gp, rect);
			}
			g2.setPaint(gp);
		} else {
			g2.setPaint(p);
		}
		g2.fill(rect);
		if (this.getOutlinePaint() != null && this.getOutlineStroke() != null) {
			if (orientation == arg2) {
				Line2D line = new Line2D.Double();
				double y0 = dataArea.getMinY();
				double y1 = dataArea.getMaxY();
				g2.setPaint(this.getOutlinePaint());
				g2.setStroke(this.getOutlineStroke());
				if (range.contains(start)) {
					line.setLine(start2d, y0, start2d, y1);
					g2.draw(line);
				}
				if (range.contains(end)) {
					line.setLine(end2d, y0, end2d, y1);
					g2.draw(line);
				}
			} else if (orientation == arg1) {
				Line2D line = new Line2D.Double();
				double x0 = dataArea.getMinX();
				double x1 = dataArea.getMaxX();
				g2.setPaint(this.getOutlinePaint());
				g2.setStroke(this.getOutlineStroke());
				if (range.contains(start)) {
					line.setLine(x0, start2d, x1, start2d);
					g2.draw(line);
				}
				if (range.contains(end)) {
					line.setLine(x0, end2d, x1, end2d);
					g2.draw(line);
				}
			}
		}
		String label = this.getLabel();
		RectangleAnchor anchor = this.getLabelAnchor();
		if (label != null) {
			Font labelFont = this.getLabelFont();
			g2.setFont(labelFont);
			g2.setPaint(this.getLabelPaint());
			Point2D coordinates = render.calculateMarkerTextAnchorPoint(orientation, rect, this.getLabelOffset(),
					this.getLabelOffsetType(), anchor, arg1, arg2);
			TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(),
					this.getLabelTextAnchor());
		}
		g2.setComposite(savedComposite);
	}

}
