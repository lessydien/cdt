/*******************************************************************************
 * Copyright (c) 2008 Symbian Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Andrew Ferguson (Symbian) - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.ui.text.doctools;

/**
 * This interface is a simplified means of obtaining spelling support. 
 * @since 5.0
 */
public interface IDocCommentSimpleDictionary extends IDocCommentDictionary {
	/**
	 * @return an array of words that should be regarded as correct. These
	 * words will be considered in addition to those provided by existing dictionaries.
	 */
	public String[] getAdditionalWords();
}
