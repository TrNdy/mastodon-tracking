package org.mastodon.trackmate.ui;

import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.util.Intervals;

import org.mastodon.project.MamutProject;
import org.mastodon.project.MamutProjectIO;
import org.mastodon.revised.bdv.ViewerFrameMamut;
import org.mastodon.revised.bdv.ViewerPanelMamut;
import org.mastodon.revised.mamut.MamutViewBdv;
import org.mastodon.revised.mamut.ProjectManager;
import org.mastodon.revised.mamut.WindowManager;
import org.mastodon.revised.util.ToggleDialogAction;
import org.scijava.Context;
import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Actions;

import bdv.tools.boundingbox.BoxSelectionOptions;
import bdv.tools.boundingbox.TransformedBoxSelectionDialog;
import bdv.viewer.Source;
import bdv.viewer.state.ViewerState;

/**
 * Example of how to make a bounding box dialog.
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 */
public class BoundingBoxDialog
{
	static final String TOGGLE_BOUNDING_BOX = "toggle bounding-box";

	static final String[] TOGGLE_BOUNDING_BOX_KEYS = new String[] { "V" };

	public static void main( final String[] args ) throws Exception
	{
		Locale.setDefault( Locale.US );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		System.setProperty( "apple.laf.useScreenMenuBar", "true" );

		/*
		 * Load a Mastodon project.
		 */
		final Context context = new Context();
		final WindowManager windowManager = new WindowManager( context );
		final ProjectManager projectManager = windowManager.getProjectManager();
		final MamutProject project = new MamutProjectIO().load( "/Users/pietzsch/workspace/Mastodon/TrackMate3/samples/mamutproject" );
		projectManager.open( project );
		final MamutViewBdv[] bdv = new MamutViewBdv[ 1 ];
		SwingUtilities.invokeAndWait( () -> {
			bdv[ 0 ] = windowManager.createBigDataViewer();
		} );
		final ViewerFrameMamut viewerFrame = ( ViewerFrameMamut ) bdv[ 0 ].getFrame();
		final ViewerPanelMamut viewer = viewerFrame.getViewerPanel();
		final InputTriggerConfig keyconf = windowManager.getAppModel().getKeymap().getConfig();

		/*
		 * Compute an initial interval from the specified setup id.
		 */
		final int setupID = 0;
		final ViewerState state = viewer.getState();
		final Source< ? > source = state.getSources().get( setupID ).getSpimSource();
		final int numTimepoints = state.getNumTimepoints();
		int tp = 0;
		Interval interval = null;
		final AffineTransform3D sourceTransform = new AffineTransform3D();
		while ( tp++ < numTimepoints )
		{
			if ( source.isPresent( tp ) )
			{
				final RandomAccessibleInterval< ? > intervalPix = source.getSource( tp, 0 );
				source.getSourceTransform( tp, 0, sourceTransform );
				interval = intervalPix;
				break;
			}
		}
		if ( null == interval )
			interval = Intervals.createMinMax( 0, 0, 0, 1, 1, 1 );

		final JDialog dialog = new TransformedBoxSelectionDialog(
				viewer,
				windowManager.getAppModel().getSharedBdvData().getSetupAssignments(),
				keyconf,
				viewerFrame.getTriggerbindings(),
				sourceTransform,
				interval,
				interval,
				BoxSelectionOptions.options().title( "Test Bounding-Box" ) );

		/*
		 * Install a action to toggle the dialog
		 */
		final Actions actions = new Actions( keyconf, "bbtest" );
		actions.install( viewerFrame.getKeybindings(), "bbtest" );
		actions.namedAction( new ToggleDialogAction( TOGGLE_BOUNDING_BOX, dialog ), TOGGLE_BOUNDING_BOX_KEYS );
	}
}
