/**
 *  Copyright (C) 2008-2013  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.repository.persistence.commande;

import java.util.List;

import org.telosys.tools.repository.model.InverseJoinColumns;
import org.telosys.tools.repository.model.JoinColumn;
import org.telosys.tools.repository.persistence.util.CommandException;
import org.telosys.tools.repository.persistence.util.ProcessContext;
import org.telosys.tools.repository.persistence.util.RepositoryConst;
import org.w3c.dom.Element;

public class CommandElementInverseJoinColumns extends AbstractCommand implements ICommandContext 
{

	public ProcessContext runProcess(final ProcessContext processContext, final ICommandManager iCommandManager) throws CommandException {
		final Element elem = processContext.getElement();

		// Transform element
		//final JoinFK joinFK = RepositoryConst.JOIN_FK_WRAPPER.getJoinFK(elem);
		final InverseJoinColumns inverseJoinColumns = RepositoryConst.INVERSE_JOIN_COLUMNS_WRAPPER.getObject(elem);

		final ProcessContext processContextNext = genericChildProcess(iCommandManager, elem);

		List<?> list = processContextNext.getList();
		for ( Object obj : list ) {
			if (obj instanceof JoinColumn) {
				inverseJoinColumns.add((JoinColumn) obj);
			} else {
				throw new CommandException("Unsupported child on JoinFK : " + obj.getClass());
			}
		}
//		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//			Object obj = iterator.next();
//			if (obj instanceof JoinColumn) {
//				inverseJoinColumns.add((JoinColumn) obj);
//			} else {
//				throw new CommandException("Unsupported child on JoinFK : " + obj.getClass());
//			}
//		}
		
		// Return result
		final ProcessContext context = new ProcessContext(inverseJoinColumns);
		return context;
	}

	public boolean accept(final ProcessContext processContext) {
		return genericAccept(processContext, RepositoryConst.INVERSE_JOIN_COLUMNS_ELEMENT);
	}

}
