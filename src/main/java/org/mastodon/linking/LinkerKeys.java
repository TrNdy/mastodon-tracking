package org.mastodon.linking;

import java.util.HashMap;
import java.util.Map;

public class LinkerKeys
{
	/**
	 * Key for the parameter specifying the maximal linking distance. The
	 * expected value must be a Double and should be expressed in physical
	 * units.
	 */
	public static final String KEY_LINKING_MAX_DISTANCE = "LINKING_MAX_DISTANCE";

	/** A default value for the {@value #KEY_LINKING_MAX_DISTANCE} parameter. */
	public static final double DEFAULT_LINKING_MAX_DISTANCE = 15.0;

	/**
	 * Key for the parameter specifying the feature penalties when linking
	 * particles. Expected values should be a
	 * <code>Map&lt;String, Double&gt;</code> where the map keys are spot
	 * feature names.
	 */
	public static final String KEY_LINKING_FEATURE_PENALTIES = "LINKING_FEATURE_PENALTIES";

	/**
	 * A default value for the {@value #KEY_LINKING_FEATURE_PENALTIES}
	 * parameter.
	 */
	public static final Map< String, Double > DEFAULT_LINKING_FEATURE_PENALTIES = new HashMap< String, Double >();

	/**
	 * Key for the parameter specifying whether we allow the detection of
	 * gap-closing events. Expected values are {@link Boolean}s.
	 */
	public static final String KEY_ALLOW_GAP_CLOSING = "ALLOW_GAP_CLOSING";

	/** A default value for the {@value #KEY_ALLOW_GAP_CLOSING} parameter. */
	public static final boolean DEFAULT_ALLOW_GAP_CLOSING = true;

	/**
	 * Key for the parameter that specify the maximal number of frames to bridge
	 * when detecting gap closing. Expected values are {@link Integer}s greater
	 * than 0. A value of 1 means that a detection might be missed in 1 frame,
	 * and the track will not be broken. And so on.
	 */
	public static final String KEY_GAP_CLOSING_MAX_FRAME_GAP = "MAX_FRAME_GAP";

	/**
	 * A default value for the {@value #KEY_GAP_CLOSING_MAX_FRAME_GAP}
	 * parameter.
	 */
	public static final int DEFAULT_GAP_CLOSING_MAX_FRAME_GAP = 2;

	/**
	 * Key for the parameter specifying the max gap-closing distance. Expected
	 * values are {@link Double}s and should be expressed in physical units. If
	 * two spots, candidate for a gap-closing event, are found separated by a
	 * distance larger than this parameter value, gap-closing will not occur.
	 */
	public static final String KEY_GAP_CLOSING_MAX_DISTANCE = "GAP_CLOSING_MAX_DISTANCE";

	/**
	 * A default value for the {@value #KEY_GAP_CLOSING_MAX_DISTANCE} parameter.
	 */
	public static final double DEFAULT_GAP_CLOSING_MAX_DISTANCE = 15.0;

	/**
	 * Key for the parameter specifying the feature penalties when detecting
	 * gap-closing events. Expected values should be a
	 * <code>Map&lt;String, Double&gt;</code> where the map keys are spot
	 * feature names.
	 */
	public static final String KEY_GAP_CLOSING_FEATURE_PENALTIES = "GAP_CLOSING_FEATURE_PENALTIES";

	/**
	 * A default value for the {@value #KEY_GAP_CLOSING_FEATURE_PENALTIES}
	 * parameter.
	 */
	public static final Map< String, Double > DEFAULT_GAP_CLOSING_FEATURE_PENALTIES = new HashMap< String, Double >();

	/**
	 * Key for the parameter specifying whether we allow the detection of
	 * merging events. Expected values are {@link Boolean}s.
	 */
	public static final String KEY_ALLOW_TRACK_MERGING = "ALLOW_TRACK_MERGING";

	/** A default value for the {@value #KEY_ALLOW_TRACK_MERGING} parameter. */
	public static final boolean DEFAULT_ALLOW_TRACK_MERGING = false;

	/**
	 * Key for the parameter specifying the max merging distance. Expected
	 * values are {@link Double}s and should be expressed in physical units. If
	 * two spots, candidate for a merging event, are found separated by a
	 * distance larger than this parameter value, track merging will not occur.
	 */
	public static final String KEY_MERGING_MAX_DISTANCE = "MERGING_MAX_DISTANCE";

	/** A default value for the {@value #KEY_MERGING_MAX_DISTANCE} parameter. */
	public static final double DEFAULT_MERGING_MAX_DISTANCE = 15.0;

	/**
	 * Key for the parameter specifying the feature penalties when dealing with
	 * merging events. Expected values should be a
	 * <code>Map&lt;String, Double&gt;</code> where the map keys are spot
	 * feature names.
	 */
	public static final String KEY_MERGING_FEATURE_PENALTIES = "MERGING_FEATURE_PENALTIES";

	/**
	 * A default value for the {@value #KEY_MERGING_FEATURE_PENALTIES}
	 * parameter.
	 */
	public static final Map< String, Double > DEFAULT_MERGING_FEATURE_PENALTIES = new HashMap< String, Double >();

	/**
	 * Key for the parameter specifying whether we allow the detection of
	 * splitting events. Expected values are {@link Boolean}s.
	 */
	public static final String KEY_ALLOW_TRACK_SPLITTING = "ALLOW_TRACK_SPLITTING";

	/**
	 * A default value for the {@value #KEY_ALLOW_TRACK_SPLITTING} parameter.
	 */
	public static final boolean DEFAULT_ALLOW_TRACK_SPLITTING = false;

	/**
	 * Key for the parameter specifying the max splitting distance. Expected
	 * values are {@link Double}s and should be expressed in physical units. If
	 * two spots, candidate for a merging event, are found separated by a
	 * distance larger than this parameter value, track splitting will not
	 * occur.
	 */
	public static final String KEY_SPLITTING_MAX_DISTANCE = "SPLITTING_MAX_DISTANCE";

	/**
	 * A default value for the {@link #KEY_SPLITTING_MAX_DISTANCE} parameter.
	 */
	public static final double DEFAULT_SPLITTING_MAX_DISTANCE = 15.0;

	/**
	 * Key for the parameter specifying the feature penalties when dealing with
	 * splitting events. Expected values should be a
	 * <code>Map&lt;String, Double&gt;</code> where the map keys are spot
	 * feature names.
	 */
	public static final String KEY_SPLITTING_FEATURE_PENALTIES = "SPLITTING_FEATURE_PENALTIES";

	/**
	 * A default value for the {@value #KEY_SPLITTING_FEATURE_PENALTIES}
	 * parameter.
	 */
	public static final Map< String, Double > DEFAULT_SPLITTING_FEATURE_PENALTIES = new HashMap< String, Double >();

	/**
	 * Key for the parameter specifying the factor used to compute alternative
	 * linking costs. Expected values are {@link Double}s.
	 */
	public static final String KEY_ALTERNATIVE_LINKING_COST_FACTOR = "ALTERNATIVE_LINKING_COST_FACTOR";

	/**
	 * A default value for the {@value #KEY_ALTERNATIVE_LINKING_COST_FACTOR}
	 * parameter.
	 */
	public static final double DEFAULT_ALTERNATIVE_LINKING_COST_FACTOR = 1.05d;

	/**
	 * Key for the cutoff percentile parameter. Expected values are
	 * {@link Double}s.
	 */
	public static final String KEY_CUTOFF_PERCENTILE = "CUTOFF_PERCENTILE";

	/** A default value for the {@value #KEY_CUTOFF_PERCENTILE} parameter. */
	public static final double DEFAULT_CUTOFF_PERCENTILE = 0.9d;

	/**
	 * Key for the parameter that stores the blocking value: cost for
	 * non-physical, forbidden links. Expected values are {@link Double}s, and
	 * are typically very large.
	 */
	public static final String KEY_BLOCKING_VALUE = "BLOCKING_VALUE";

	/** A default value for the {@value #KEY_BLOCKING_VALUE} parameter. */
	public static final double DEFAULT_BLOCKING_VALUE = Double.POSITIVE_INFINITY;

	public static final String KEY_KALMAN_SEARCH_RADIUS = "KALMAN_SEARCH_RADIUS";

	public static final double DEFAULT_MAX_SEARCH_RADIUS = 10.;

	/**
	 * The position accuracy or standard deviation on positions, used to
	 * initialize the Kalman filter.
	 */
	public static final String KEY_POSITION_SIGMA = "POSITION_SIGMA";

	public static final double DEFAULT_POSITION_SIGMA = DEFAULT_MAX_SEARCH_RADIUS / 10.;

	private LinkerKeys()
	{}
}