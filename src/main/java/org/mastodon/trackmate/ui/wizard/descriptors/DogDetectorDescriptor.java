package org.mastodon.trackmate.ui.wizard.descriptors;

import static org.mastodon.detection.DetectorKeys.KEY_MAX_TIMEPOINT;
import static org.mastodon.detection.DetectorKeys.KEY_MIN_TIMEPOINT;
import static org.mastodon.detection.DetectorKeys.KEY_RADIUS;
import static org.mastodon.detection.DetectorKeys.KEY_SETUP_ID;
import static org.mastodon.detection.DetectorKeys.KEY_THRESHOLD;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.detection.DetectionUtil;
import org.mastodon.detection.DetectorKeys;
import org.mastodon.detection.DogDetectorOp;
import org.mastodon.detection.mamut.DoGDetectorMamut;
import org.mastodon.detection.mamut.LoGDetectorMamut;
import org.mastodon.detection.mamut.SpotDetectorOp;
import org.mastodon.properties.DoublePropertyMap;
import org.mastodon.revised.bdv.ViewerFrameMamut;
import org.mastodon.revised.mamut.WindowManager;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.ModelGraph;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.spatial.SpatialIndex;
import org.mastodon.trackmate.Settings;
import org.mastodon.trackmate.TrackMate;
import org.mastodon.trackmate.ui.wizard.Wizard;
import org.mastodon.trackmate.ui.wizard.util.HistogramUtil;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import bdv.spimdata.SequenceDescriptionMinimal;
import bdv.spimdata.SpimDataMinimal;
import bdv.util.Affine3DHelpers;
import mpicbg.spim.data.generic.sequence.BasicMultiResolutionImgLoader;
import mpicbg.spim.data.generic.sequence.BasicViewSetup;
import net.imagej.ops.OpService;
import net.imagej.ops.special.hybrid.Hybrids;
import net.imglib2.realtransform.AffineTransform3D;

@Plugin( type = SpotDetectorDescriptor.class, name = "DoG detector configuration descriptor" )
public class DogDetectorDescriptor extends SpotDetectorDescriptor
{

	private static final NumberFormat FORMAT = new DecimalFormat( "0.0" );

	public static final String IDENTIFIER = "Configure DoG detector";

	private static final Icon PREVIEW_ICON = new ImageIcon( Wizard.class.getResource( "led-icon-eye-green.png" ) );

	@Parameter
	private LogService log;

	@Parameter
	private OpService ops;

	private Settings settings;

	private Model model;

	private WindowManager windowManager;

	private ViewerFrameMamut viewFrame;

	private ChartPanel chartPanel;

	public DogDetectorDescriptor()
	{
		this.panelIdentifier = IDENTIFIER;
		this.targetPanel = new DogDetectorPanel();
	}

	@Override
	public void aboutToHidePanel()
	{
		grabSettings();

		final Integer setupID = ( Integer ) settings.values.getDetectorSettings().get( DetectorKeys.KEY_SETUP_ID );
		final String units = ( null != setupID && null != settings.values.getSpimData() )
				? settings.values.getSpimData().getSequenceDescription()
						.getViewSetups().get( setupID ).getVoxelSize().unit()
				: "pixels";

		final SpimDataMinimal spimData = settings.values.getSpimData();
		final double radius = ( double ) settings.values.getDetectorSettings().get( KEY_RADIUS );
		final double minSizePixel = DogDetectorOp.MIN_SPOT_PIXEL_SIZE / 2.;
		final int timepoint = ( int ) settings.values.getDetectorSettings().get( KEY_MIN_TIMEPOINT );
		final int level = DetectionUtil.determineOptimalResolutionLevel( spimData, radius, minSizePixel, timepoint, setupID );
		final AffineTransform3D mipmapTransform = DetectionUtil.getMipmapTransform( spimData, timepoint, setupID, level );
		final AffineTransform3D transform = DetectionUtil.getTransform( spimData, timepoint, setupID, level );

		final double sx = Affine3DHelpers.extractScale( mipmapTransform, 0 );
		final double sy = Affine3DHelpers.extractScale( mipmapTransform, 1 );
		final double sz = Affine3DHelpers.extractScale( mipmapTransform, 2 );

		final double px = Affine3DHelpers.extractScale( transform, 0 );
		final double py = Affine3DHelpers.extractScale( transform, 1 );
		final double pz = Affine3DHelpers.extractScale( transform, 2 );

		final double rx = radius / px;
		final double ry = radius / py;
		final double rz = radius / pz;

		log.info( "Configured detector with parameters:\n" );
		log.info( String.format( "  - spot radius: %.1f %s\n", radius, units ) );
		log.info( String.format( "  - quality threshold: %.1f\n", ( double ) settings.values.getDetectorSettings().get( KEY_THRESHOLD ) ) );
		final SequenceDescriptionMinimal seq = spimData.getSequenceDescription();
		if ( seq.getImgLoader() instanceof BasicMultiResolutionImgLoader )
		{
			log.info( String.format( "  - will operate on resolution level %d (%.0f x %.0f x %.0f)\n", level, sx, sy, sz ) );
			log.info( String.format( "  - at this level, radius = %.1f %s corresponds to:\n", radius, units ) );
		}
		else
		{
			log.info( String.format( "  - equivalent radius = %.1f %s in pixels:\n", radius, units ) );
		}
		log.info( String.format( "      - %.1f pixels in X.\n", rx ) );
		log.info( String.format( "      - %.1f pixels in Y.\n", ry ) );
		log.info( String.format( "      - %.1f pixels in Z.\n", rz ) );

	}

	private void preview()
	{
		grabSettings();
		final ModelGraph graph = model.getGraph();
		final SpimDataMinimal spimData = settings.values.getSpimData();
		if ( null == spimData )
		{
			log.error( "Cannot start detection: SpimData obect is null." );
			return;
		}

		/*
		 * Get or show a viewer.
		 */

		final int currentTimepoint;
		if ( null != windowManager )
		{
			if ( viewFrame == null || !viewFrame.isShowing() )
			{
				// Is there a BDV open?
				if ( viewFrame == null || !viewFrame.isShowing() )
					windowManager.forEachBdvView( ( view ) -> {
						viewFrame = ( ViewerFrameMamut ) view.getFrame();
					} );

				// Create one
				if ( viewFrame == null )
					viewFrame = ( ViewerFrameMamut ) windowManager.createBigDataViewer().getFrame();

				viewFrame.toFront();
			}
			currentTimepoint = viewFrame.getViewerPanel().getState().getCurrentTimepoint();
		}
		else
		{
			currentTimepoint = spimData.getSequenceDescription().getTimePoints().getTimePointsOrdered().get( 0 ).getId();
		}

		final DogDetectorPanel panel = ( DogDetectorPanel ) targetPanel;
		panel.preview.setEnabled( false );
		new Thread( "DogDetectorPanel preview thread" )
		{
			@Override
			public void run()
			{
				try
				{
					/*
					 * Delete spots from current time-point if we have some.
					 * FIXME We should not do that if we want to add spots to
					 * existing model. But I don't know how to preview then.
					 * Maybe with another Model?
					 */

					SpatialIndex< Spot > spatialIndex;
					model.getSpatioTemporalIndex().readLock().lock();
					final RefList< Spot > toRemove = RefCollections.createRefList( graph.vertices() );
					try
					{
						spatialIndex = model.getSpatioTemporalIndex().getSpatialIndex( currentTimepoint );
						for ( final Spot spot : spatialIndex )
							toRemove.add( spot );
					}
					finally
					{
						model.getSpatioTemporalIndex().readLock().unlock();
					}

					// FIXME should lock the graph for writing.
					for ( final Spot spot : toRemove )
						graph.remove( spot );

					/*
					 * Tune settings for preview.
					 */

					final Class< ? extends SpotDetectorOp > cl = settings.values.getDetector();
					// Copy settings.
					final Map< String, Object > detectorSettings = new HashMap<>( settings.values.getDetectorSettings() );
					detectorSettings.put( KEY_MIN_TIMEPOINT, currentTimepoint );
					detectorSettings.put( KEY_MAX_TIMEPOINT, currentTimepoint );

					/*
					 * Execute preview.
					 */

					final SpotDetectorOp detector = ( SpotDetectorOp ) Hybrids.unaryCF( ops, cl,
							graph, spimData,
							detectorSettings );
					panel.lblInfo.setText( "Previewing..." );
					detector.compute( spimData, graph );

					if ( !detector.isSuccessful() )
					{
						log.error( "Detection failed:\n" + detector.getErrorMessage() );
						return;
					}

					model.getFeatureModel().declareFeature( detector.getQualityFeature() );
					graph.notifyGraphChanged();

					int nSpots = 0;
					for ( @SuppressWarnings( "unused" )
					final Spot spot : spatialIndex )
						nSpots++;

					panel.lblInfo.setText( "Found " + nSpots + " spots in time-point " + currentTimepoint );
					plotQualityHistogram( detector.getQualityFeature().getPropertyMap() );
				}
				finally
				{
					panel.preview.setEnabled( true );
				}
			};
		}.start();

	}

	private void plotQualityHistogram( final DoublePropertyMap< Spot > qualities )
	{
		final DogDetectorPanel panel = ( DogDetectorPanel ) targetPanel;
		if ( null != chartPanel )
		{
			panel.remove( chartPanel );
			panel.repaint();
		}

		final double[] values = qualities.getMap().values();
		if ( values.length == 0 )
			return;

		this.chartPanel = HistogramUtil.createHistogramPlot( values, false );
		chartPanel.getChart().setTitle( new TextTitle( "Quality histogram", chartPanel.getFont() ) );

		// Disable zoom.
		for ( final MouseListener ml : chartPanel.getMouseListeners() )
			chartPanel.removeMouseListener( ml );

		// Re enable the X axis.
		final XYPlot plot = chartPanel.getChart().getXYPlot();
		plot.getDomainAxis().setVisible( true );
		final TickUnitSource source = new NumberTickUnitSource( false, FORMAT );
		plot.getDomainAxis().setStandardTickUnits( source );
		final Font smallFont = chartPanel.getFont().deriveFont( chartPanel.getFont().getSize2D() - 2f );
		plot.getDomainAxis().setTickLabelFont( smallFont );

		// Transparent background.
		chartPanel.getChart().setBackgroundPaint( null );

		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 5;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.BASELINE_LEADING;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		panel.add( chartPanel, gbc );
		panel.revalidate();
	}

	/**
	 * Update the settings field of this descriptor with the values set on the
	 * GUI.
	 */
	private void grabSettings()
	{
		if ( null == settings )
			return;

		final DogDetectorPanel panel = ( DogDetectorPanel ) targetPanel;
		final Map< String, Object > detectorSettings = settings.values.getDetectorSettings();
		detectorSettings.put( KEY_RADIUS, ( ( Number ) panel.diameter.getValue() ).doubleValue() / 2. );
		detectorSettings.put( KEY_THRESHOLD, ( ( Number ) panel.threshold.getValue() ).doubleValue() );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public Collection< Class< ? extends SpotDetectorOp > > getTargetClasses()
	{
		final Collection b = Collections.unmodifiableCollection( Arrays.asList( new Class[] {
				DoGDetectorMamut.class,
				LoGDetectorMamut.class
		} ) );
		final Collection< Class< ? extends SpotDetectorOp > > a = b;
		return a;
	}

	@Override
	public Map< String, Object > getDefaultSettings()
	{
		return DetectionUtil.getDefaultDetectorSettingsMap();
	}

	@Override
	public void setTrackMate( final TrackMate trackmate )
	{
		final DogDetectorPanel panel = ( DogDetectorPanel ) targetPanel;

		this.settings = trackmate.getSettings();
		this.model = trackmate.getModel();
		panel.preview.setEnabled( model != null );
		if ( null == settings )
			return;

		final Map< String, Object > detectorSettings = settings.values.getDetectorSettings();

		final double diameter;
		final Object objRadius = detectorSettings.get( KEY_RADIUS );
		if ( null == objRadius )
			diameter = 2. * DetectorKeys.DEFAULT_RADIUS;
		else
			diameter = 2. * ( double ) objRadius;

		final double threshold;
		final Object objThreshold = detectorSettings.get( KEY_THRESHOLD );
		if ( null == objThreshold )
			threshold = DetectorKeys.DEFAULT_THRESHOLD;
		else
			threshold = ( double ) objThreshold;

		final int setupID = ( int ) settings.values.getDetectorSettings().get( KEY_SETUP_ID );
		final BasicViewSetup setup = settings.values.getSpimData().getSequenceDescription().getViewSetups().get( setupID );

		panel.diameter.setValue( diameter );
		panel.threshold.setValue( threshold );
		panel.lblDiameterUnit.setText( setup.getVoxelSize().unit() );
	}

	@Override
	public void setWindowManager( final WindowManager windowManager )
	{
		this.windowManager = windowManager;
	}

	private class DogDetectorPanel extends JPanel
	{

		private static final long serialVersionUID = 1L;

		private final JFormattedTextField diameter;

		private final JFormattedTextField threshold;

		private final JLabel lblDiameterUnit;

		private final JButton preview;

		private final JLabel lblInfo;

		public DogDetectorPanel()
		{
			final GridBagLayout layout = new GridBagLayout();
			layout.columnWidths = new int[] { 80, 80, 40 };
			layout.columnWeights = new double[] { 0.2, 0.7, 0.1 };
			layout.rowHeights = new int[] { 26, 0, 0, 0, 26, 26 };
			layout.rowWeights = new double[] { 1., 0., 0., 0., 0., 1. };
			setLayout( layout );

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			gbc.anchor = GridBagConstraints.BASELINE_LEADING;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets( 5, 5, 5, 5 );

			final JLabel title = new JLabel( "Configure detector." );
			title.setFont( getFont().deriveFont( Font.BOLD ) );
			add( title, gbc );

			// Diameter.

			final JLabel lblDiameter = new JLabel( "Estimated diameter:", JLabel.RIGHT );
			gbc.gridy++;
			gbc.gridwidth = 1;
			gbc.anchor = GridBagConstraints.BASELINE_TRAILING;
			add( lblDiameter, gbc );

			this.diameter = new JFormattedTextField( FORMAT );
			diameter.setHorizontalAlignment( JLabel.RIGHT );
			diameter.addFocusListener( new SelectAllOnFocus( diameter ) );
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.CENTER;
			add( diameter, gbc );

			lblDiameterUnit = new JLabel();
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.LINE_END;
			add( lblDiameterUnit, gbc );

			// Threshold.

			final JLabel lblThreshold = new JLabel( "Quality threshold:", JLabel.RIGHT );
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.anchor = GridBagConstraints.BASELINE_TRAILING;
			add( lblThreshold, gbc );

			this.threshold = new JFormattedTextField( FORMAT );
			threshold.setHorizontalAlignment( JLabel.RIGHT );
			threshold.addFocusListener( new SelectAllOnFocus( threshold ) );
			gbc.gridx++;
			gbc.anchor = GridBagConstraints.CENTER;
			add( threshold, gbc );

			// Preview button.
			preview = new JButton( "Preview", PREVIEW_ICON );
			preview.addActionListener( ( e ) -> preview() );
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.fill = GridBagConstraints.NONE;
			add( preview, gbc );

			// Info text.
			this.lblInfo = new JLabel( "", JLabel.RIGHT );
			lblInfo.setFont( getFont().deriveFont( getFont().getSize2D() - 2f ) );
			gbc.gridwidth = 3;
			gbc.gridy++;
			gbc.gridx = 0;
			add( lblInfo, gbc );

			// Quality histogram place holder.
		}
	}

	private static class SelectAllOnFocus extends FocusAdapter
	{
		private final JFormattedTextField textField;

		public SelectAllOnFocus( final JFormattedTextField textField )
		{
			this.textField = textField;
		}

		@Override
		public void focusGained( final FocusEvent e )
		{
			SwingUtilities.invokeLater( () -> textField.selectAll() );
		}
	}
}
