package org.jfree.chart.renderer;


import java.awt.geom.Point2D;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import java.util.function.Supplier;
import org.jfree.ui.RectangleEdge;
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

	public Point2D calculateMarkerTextAnchorPoint(PlotOrientation orientation, Rectangle2D markerArea,
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

	protected void drawMarkerExtracted(Marker marker, ValueAxis axis, Plot plot, Rectangle2D dataArea,
			Supplier<RectangleEdge> arg0, PlotOrientation arg1, PlotOrientation arg2, Graphics2D g2) {
		
		marker.getConcreateMarker(axis, plot, dataArea, arg0, arg1, arg2, g2,this);
		
	}

	
}