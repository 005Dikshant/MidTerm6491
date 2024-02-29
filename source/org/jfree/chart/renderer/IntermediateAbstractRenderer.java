package org.jfree.chart.renderer;


import java.awt.geom.Point2D;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.axis.ValueAxis;
import java.util.function.Supplier;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.plot.Plot;
import java.awt.Graphics2D;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.Range;
import java.awt.geom.Line2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Font;
import org.jfree.text.TextUtilities;
import org.jfree.chart.plot.IntervalMarker;
import java.awt.Paint;
import java.awt.GradientPaint;
import org.jfree.ui.GradientPaintTransformer;

public abstract class IntermediateAbstractRenderer extends AbstractRenderer {
	protected IntermediateAbstractRenderer() {
		super();
	}

	protected Point2D calculateMarkerTextAnchorPoint(PlotOrientation orientation, Rectangle2D markerArea,
			RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor,
			PlotOrientation arg0, PlotOrientation arg1) {
		Rectangle2D anchorRect = null;
		if (orientation == arg0) {
			anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT,
					labelOffsetType);
		} else if (orientation == arg1) {
			anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType,
					LengthAdjustmentType.CONTRACT);
		}
		return RectangleAnchor.coordinates(anchorRect, anchor);
	}

	protected void drawMarkerExtracted(Marker marker, ValueAxis axis, Rectangle2D dataArea,
			Supplier<RectangleEdge> arg0, Plot plot, PlotOrientation arg1, PlotOrientation arg2, Graphics2D g2) {
		if (marker instanceof ValueMarker) {
			ValueMarker vm = (ValueMarker) marker;
			double value = vm.getValue();
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
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
			g2.setPaint(marker.getPaint());
			g2.setStroke(marker.getStroke());
			g2.draw(line);
			String label = marker.getLabel();
			RectangleAnchor anchor = marker.getLabelAnchor();
			if (label != null) {
				Font labelFont = marker.getLabelFont();
				g2.setFont(labelFont);
				g2.setPaint(marker.getLabelPaint());
				Point2D coordinates = calculateMarkerTextAnchorPoint(orientation, line.getBounds2D(),
						marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor, arg1, arg2);
				TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(),
						marker.getLabelTextAnchor());
			}
			g2.setComposite(savedComposite);
		} else if (marker instanceof IntervalMarker) {
			IntervalMarker im = (IntervalMarker) marker;
			double start = im.getStartValue();
			double end = im.getEndValue();
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
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
			Paint p = marker.getPaint();
			if (p instanceof GradientPaint) {
				GradientPaint gp = (GradientPaint) p;
				GradientPaintTransformer t = im.getGradientPaintTransformer();
				if (t != null) {
					gp = t.transform(gp, rect);
				}
				g2.setPaint(gp);
			} else {
				g2.setPaint(p);
			}
			g2.fill(rect);
			if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
				if (orientation == arg2) {
					Line2D line = new Line2D.Double();
					double y0 = dataArea.getMinY();
					double y1 = dataArea.getMaxY();
					g2.setPaint(im.getOutlinePaint());
					g2.setStroke(im.getOutlineStroke());
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
					g2.setPaint(im.getOutlinePaint());
					g2.setStroke(im.getOutlineStroke());
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
			String label = marker.getLabel();
			RectangleAnchor anchor = marker.getLabelAnchor();
			if (label != null) {
				Font labelFont = marker.getLabelFont();
				g2.setFont(labelFont);
				g2.setPaint(marker.getLabelPaint());
				Point2D coordinates = calculateMarkerTextAnchorPoint(orientation, rect, marker.getLabelOffset(),
						marker.getLabelOffsetType(), anchor, arg1, arg2);
				TextUtilities.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(),
						marker.getLabelTextAnchor());
			}
			g2.setComposite(savedComposite);
		}
		return;
	}
}