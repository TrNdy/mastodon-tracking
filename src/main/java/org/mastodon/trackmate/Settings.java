package org.mastodon.trackmate;

import java.util.Map;

import org.mastodon.detection.mamut.DoGDetectorMamut;
import org.mastodon.detection.mamut.SpotDetectorOp;
import org.mastodon.linking.LinkingUtils;
import org.mastodon.linking.mamut.SparseLAPLinkerMamut;
import org.mastodon.linking.mamut.SpotLinkerOp;

import bdv.spimdata.SpimDataMinimal;

public class Settings
{

	public final Values values = new Values();

	public Settings detector( final Class< ? extends SpotDetectorOp > detector )
	{
		values.detector = detector;
		return this;
	}

	public Settings linker( final Class< ? extends SpotLinkerOp > linker )
	{
		values.linker = linker;
		return this;
	}

	public Settings linkerSettings( final Map< String, Object > linkerSettings )
	{
		values.linkerSettings = linkerSettings;
		return this;
	}

	public Settings spimData(final SpimDataMinimal spimData)
	{
		values.spimData = spimData;
		return this;
	}

	public static class Values
	{
		private SpimDataMinimal spimData = null;

		private Class< ? extends SpotDetectorOp > detector = DoGDetectorMamut.class;

		private Class< ? extends SpotLinkerOp > linker = SparseLAPLinkerMamut.class;

		private Map< String, Object > linkerSettings = LinkingUtils.getDefaultLAPSettingsMap();

		public Class< ? extends SpotDetectorOp > getDetector()
		{
			return detector;
		}

		public Class< ? extends SpotLinkerOp > getLinker()
		{
			return linker;
		}

		public Map< String, Object > getLinkerSettings()
		{
			return linkerSettings;
		}

		public SpimDataMinimal getSpimData()
		{
			return spimData;
		}
	}
}
