package org.jfree.chart.plot;


import org.jfree.chart.event.MarkerChangeEvent;
import java.io.Serializable;

public class IntervalMarkerProduct implements Serializable, Cloneable {
	private double startValue;
	private double endValue;

	public double getStartValue() {
		return startValue;
	}

	public void setStartValue2(double startValue) {
		this.startValue = startValue;
	}

	public double getEndValue() {
		return endValue;
	}

	public void setEndValue2(double endValue) {
		this.endValue = endValue;
	}

	/**
	* Sets the start value for the marker and sends a {@link MarkerChangeEvent}  to all registered listeners.
	* @param value   the value.
	* @since  1.0.3
	*/
	public void setStartValue(double value, IntervalMarker intervalMarker) {
		this.startValue = value;
		intervalMarker.notifyListeners(new MarkerChangeEvent(intervalMarker));
	}

	/**
	* Sets the end value for the marker and sends a {@link MarkerChangeEvent}  to all registered listeners.
	* @param value   the value.
	* @since  1.0.3
	*/
	public void setEndValue(double value, IntervalMarker intervalMarker) {
		this.endValue = value;
		intervalMarker.notifyListeners(new MarkerChangeEvent(intervalMarker));
	}

	public Object clone() throws CloneNotSupportedException {
		return (IntervalMarkerProduct) super.clone();
	}
}