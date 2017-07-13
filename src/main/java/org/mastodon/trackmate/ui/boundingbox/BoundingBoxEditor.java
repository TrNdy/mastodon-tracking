package org.mastodon.trackmate.ui.boundingbox;

import org.scijava.ui.behaviour.DragBehaviour;

import bdv.tools.boundingbox.BoxSelectionPanel;
import bdv.util.ModifiableInterval;
import bdv.viewer.ViewerPanel;
import net.imglib2.FinalInterval;

public class BoundingBoxEditor implements DragBehaviour
{

	private final BoundingBoxOverlay boxOverlay;

	private boolean moving = false;

	private ViewerPanel viewerPanel;

	private BoxSelectionPanel boxSelectionPanel;

	private ModifiableInterval interval;

	public BoundingBoxEditor( final BoundingBoxOverlay boxOverlay, final ViewerPanel viewerPanel, final BoxSelectionPanel boxSelectionPanel, final ModifiableInterval interval )
	{
		this.boxOverlay = boxOverlay;
		this.viewerPanel = viewerPanel;
		this.boxSelectionPanel = boxSelectionPanel;
		this.interval = interval;
	}

	@Override
	public void init( final int x, final int y )
	{
		if ( boxOverlay.cornerId < 0 )
			return;

		moving = true;
	}

	@Override
	public void drag( final int x, final int y )
	{
		if ( !moving )
			return;

		final double[] corner = boxOverlay.renderBoxHelper.corners[ boxOverlay.cornerId ];
		final double[] lPos = new double[] { x, y, corner[ 2 ] };
		final double[] gPos = new double[ 3 ];
		boxOverlay.transform.applyInverse( gPos, lPos );

		final long[] max = new long[ 3 ];
		final long[] min = new long[ 3 ];
		interval.max( max );
		interval.min( min );

		// Z.
		if ( boxOverlay.cornerId < 4 )
			min[ 2 ] = Math.round( gPos[ 2 ] );
		else
			max[ 2 ] = Math.round( gPos[ 2 ] );

		// Y.
		if ( ( boxOverlay.cornerId / 2 ) % 2 == 0 )
			min[ 1 ] = Math.round( gPos[ 1 ] );
		else
			max[ 1 ] = Math.round( gPos[ 1 ] );

		// X.
		if ( boxOverlay.cornerId % 2 == 0 )
			min[ 0 ] = Math.round( gPos[ 0 ] );
		else
			max[ 0 ] = Math.round( gPos[ 0 ] );

		interval.set( new FinalInterval( min, max ) );
		boxSelectionPanel.updateSliders( interval );
		viewerPanel.requestRepaint();
	}

	@Override
	public void end( final int x, final int y )
	{
		moving = false;
		viewerPanel.requestRepaint();
	}
}
