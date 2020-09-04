package org.mastodon.tracking.mamut.detection;

import static org.mastodon.tracking.detection.DetectorKeys.KEY_ADD_BEHAVIOR;

import java.util.List;
import java.util.Map;

import org.mastodon.mamut.model.ModelGraph;
import org.mastodon.mamut.model.Spot;
import org.mastodon.spatial.SpatioTemporalIndex;
import org.mastodon.tracking.detection.DetectionCreatorFactory;
import org.mastodon.tracking.detection.DetectorOp;
import org.mastodon.tracking.mamut.detection.MamutDetectionCreatorFactories.DetectionBehavior;
import org.scijava.ItemIO;
import org.scijava.app.StatusService;
import org.scijava.log.Logger;
import org.scijava.plugin.Parameter;
import org.scijava.thread.ThreadService;

import bdv.viewer.SourceAndConverter;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.algorithm.Benchmark;

public abstract class AbstractSpotDetectorOp extends AbstractUnaryHybridCF< List< SourceAndConverter< ? > >, ModelGraph > implements SpotDetectorOp, Benchmark
{

	@Parameter( type = ItemIO.INPUT )
	private Map< String, Object > settings;

	@Parameter( type = ItemIO.INPUT )
	private SpatioTemporalIndex< Spot > sti;

	@Parameter( type = ItemIO.BOTH, required = false )
	protected DetectionQualityFeature qualityFeature;

	@Parameter( type = ItemIO.OUTPUT )
	protected String errorMessage;

	@Parameter( type = ItemIO.OUTPUT )
	protected boolean ok;

	@Parameter
	private ThreadService threadService;

	@Parameter( required = false )
	private StatusService statusService;

	@Parameter( required = false )
	private Logger log;

	private long processingTime;

	protected DetectorOp detector;

	protected void exec( final List< SourceAndConverter< ? > > sources, final ModelGraph graph, final Class< ? extends DetectorOp > cl )
	{
		ok = false;
		final long start = System.currentTimeMillis();

		if ( null == qualityFeature )
			qualityFeature = new DetectionQualityFeature( graph.vertices().getRefPool() );

		/*
		 * Resolve add detection behavior.
		 */
		final DetectionCreatorFactory detectionCreator;
		if ( null == sti )
		{
			detectionCreator = MamutDetectionCreatorFactories.getAddDetectionCreatorFactory( graph, qualityFeature );
		}
		else
		{
			DetectionBehavior detectionBehavior = DetectionBehavior.ADD;
			final String addBehavior = ( String ) settings.get( KEY_ADD_BEHAVIOR );
			if ( null != addBehavior )
			{
				try
				{
					detectionBehavior = MamutDetectionCreatorFactories.DetectionBehavior.valueOf( addBehavior );
				}
				catch ( final IllegalArgumentException e )
				{}
			}
			detectionCreator = detectionBehavior.getFactory( graph, qualityFeature, sti );
		}

		this.detector = ( DetectorOp ) Inplaces.binary1( ops(), cl,
				detectionCreator, sources, settings );
		detector.setLogger( log );
		detector.setStatusService( statusService );
		try
		{
			detector.mutate1( detectionCreator, sources );
			ok = detector.isSuccessful();
			if ( !ok )
				errorMessage = detector.getErrorMessage();
		}
		catch ( final OutOfMemoryError oome )
		{
			errorMessage = "Not enough memory to process the image.";
			ok = false;
			return;
		}
		finally
		{
			final long end = System.currentTimeMillis();
			processingTime = end - start;
			this.detector = null;
		}
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public ModelGraph createOutput( final List< SourceAndConverter< ? > > input )
	{
		return new ModelGraph();
	}

	@Override
	public DetectionQualityFeature getQualityFeature()
	{
		return qualityFeature;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public boolean isSuccessful()
	{
		return ok;
	}

	@Override
	public void setLogger( final Logger logger )
	{
		this.log = logger;
	}

	@Override
	public void setStatusService( final StatusService statusService )
	{
		this.statusService = statusService;
	}

	// -- Cancelable methods --

	/** Reason for cancelation, or null if not canceled. */
	private String cancelReason;

	@Override
	public boolean isCanceled()
	{
		return cancelReason != null;
	}

	/** Cancels the command execution, with the given reason for doing so. */
	@Override
	public void cancel( final String reason )
	{
		if ( reason != null )
		{
			cancelReason = reason;
			detector.cancel( reason );
		}
		else
		{
			cancelReason = "";
		}
	}

	@Override
	public String getCancelReason()
	{
		return cancelReason;
	}
}