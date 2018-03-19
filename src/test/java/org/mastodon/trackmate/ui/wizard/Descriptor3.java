package org.mastodon.trackmate.ui.wizard;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mastodon.trackmate.ui.wizard.WizardPanelDescriptor;

public class Descriptor3 extends WizardPanelDescriptor
{

	private static final String ID = "panel 3";

	public Descriptor3()
	{
		panelIdentifier = ID;
		targetPanel = new Descriptor3Panel();
	}

	private class Descriptor3Panel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public Descriptor3Panel()
		{
			final JLabel jLabel = new JLabel( "goooooood!" );
			add( jLabel );
		}

	}

}