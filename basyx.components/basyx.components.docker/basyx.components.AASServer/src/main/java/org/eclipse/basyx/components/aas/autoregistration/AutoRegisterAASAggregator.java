/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.aas.autoregistration;

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

/**
 * An {@link IAASAggregator} which automatically registers created Shells in the
 * registry
 * 
 * @author fried
 *
 */
public class AutoRegisterAASAggregator implements IAASAggregator {

	private IAASAggregator aggregator;
	private IAASRegistry registry;
	private String endpoint;

	public AutoRegisterAASAggregator(IAASAggregator aggregator, IAASRegistry registry, String endpoint) {
		this.aggregator = aggregator;
		this.registry = registry;
		this.endpoint = endpoint;
	}

	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return aggregator.getAASList();
	}

	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) throws ResourceNotFoundException {
		return aggregator.getAAS(aasId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) throws ResourceNotFoundException {
		return aggregator.getAASProvider(aasId);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		aggregator.createAAS(aas);
		registry.register(new AASDescriptor(aas, getEndpoint(aas)));

	}

	private String getEndpoint(AssetAdministrationShell aas) {
		String harmonized = AASAggregatorAPIHelper.harmonizeURL(endpoint);		
		String aasAccessPath = AASAggregatorAPIHelper.getAASAccessPath(aas.getIdentification());
		return VABPathTools.concatenatePaths(harmonized, aasAccessPath);
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) throws ResourceNotFoundException {
		aggregator.updateAAS(aas);
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		aggregator.deleteAAS(aasId);
		registry.delete(aasId);
	}

}
