/*
 * Planet CRS Registry - The coordinates reference system registry for solar bodies
 * Copyright (C) 2025 - CNES (for PDSSP)
 *
 * This file is part of CRS Service.
 *
 * CRS Service is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License v3  as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CRS Service is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License v3  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License v3
 * along with CRS Service.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.geomatys.crsservice.client;

import com.geomatys.crsservice.service.DefaultCrsOperationService;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.util.FactoryException;

/**
 * A {@link CoordinateOperationFactory coordinate operation factory} that provides transform logic in a requested scripting language.
 */
public interface ScriptingCoordinateOperationFactory extends CoordinateOperationFactory {

    @Override
    default CoordinateOperation createOperation(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2) throws FactoryException {
        return createOperation(crs1, crs2, DefaultCrsOperationService.FORMAT_JAVASCRIPT);
    }

    CoordinateOperation createOperation(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, final String format) throws FactoryException;
}
