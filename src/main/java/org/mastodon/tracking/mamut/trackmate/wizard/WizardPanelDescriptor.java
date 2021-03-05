/*-
 * #%L
 * mastodon-tracking
 * %%
 * Copyright (C) 2017 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.tracking.mamut.trackmate.wizard;

import java.awt.Component;

import org.scijava.Cancelable;
import org.scijava.app.StatusService;
import org.scijava.log.Logger;

public class WizardPanelDescriptor
{

	protected Component targetPanel;

	protected String panelIdentifier;

	protected Logger logger;

	protected StatusService statusService;

	public final Component getPanelComponent()
	{
		return targetPanel;
	}

	public final void setPanelComponent( final Component panel )
	{
		targetPanel = panel;
	}

	public final String getPanelDescriptorIdentifier()
	{
		return panelIdentifier;
	}

	public final void setPanelDescriptorIdentifier( final String id )
	{
		panelIdentifier = id;
	}

	public void aboutToHidePanel()
	{}

	public void aboutToDisplayPanel()
	{}

	public void displayingPanel()
	{}

	public Runnable getForwardRunnable()
	{
		return null;
	}

	public Runnable getBackwardRunnable()
	{
		return null;
	}

	public Cancelable getCancelable()
	{
		return null;
	}

	public void setLogger(final Logger logger)
	{
		this.logger = logger;
	}

	public void setStatusService(final StatusService statusService)
	{
		this.statusService = statusService;

	}

}
