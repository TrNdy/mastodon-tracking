package org.mastodon.detection;

import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.DoublePropertyMap;
import org.mastodon.revised.model.feature.Feature;
import org.scijava.Cancelable;

import bdv.spimdata.SpimDataMinimal;
import net.imagej.ops.special.inplace.BinaryInplace1OnlyOp;

public interface DetectorOp< V extends Vertex< ? > > extends BinaryInplace1OnlyOp< Graph<V,?>, SpimDataMinimal >, Cancelable
{

	/**
	 * Returns the quality feature calculated by this detector.
	 * <p>
	 * The quality feature is defined for all vertices created by the last call
	 * to the detector and only them. By convention, quality values are real
	 * positive <code>double</code>s, with large values indicating the
	 * confidence in the detection result.
	 *
	 * @return the spot quality feature.
	 */
	public Feature< V, Double, DoublePropertyMap< V > > getQualityFeature();

	/**
	 * Returns <code>true</code> if the particle-linking process completed
	 * successfully. If not, a meaningful error message can be obtained with
	 * {@link #getErrorMessage()}.
	 *
	 * @return <code>true</code> if the particle-linking process completed
	 *         successfully.
	 * @see #getErrorMessage()
	 */
	public boolean wasSuccessful();

	/**
	 * Returns a meaningful error message after the particle-linking process
	 * failed to complete.
	 *
	 * @return an error message.
	 */
	public String getErrorMessage();
}
