/*******************************************************************************
 * Copyright (c) 2009 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.core.parser.tests.scanner;

import junit.framework.TestSuite;

import org.eclipse.cdt.core.parser.IToken;


/**
 * Tests for using the preprocessor on inactive code
 */
public class InactiveCodeTests extends PreprocessorTestsBase {
	
	public static TestSuite suite() {
		return suite(InactiveCodeTests.class);
	}
	
	@Override
	protected void initializeScanner() throws Exception {
		super.initializeScanner();
		fScanner.setProcessInactiveCode(true);
	}
	
	private void validate(char[] activeInactive) throws Exception {
		boolean active= true;
		for (char c : activeInactive) {
			switch(c) {
			case 'a':
				if (!active) {
					validateToken(IToken.tINACTIVE_CODE_END);
					active= true;
				}
				validateIdentifier("a");
				break;
			case 'i':
				validateToken(active ? IToken.tINACTIVE_CODE_START : IToken.tINACTIVE_CODE_SEPARATOR);
				active= false;
				validateIdentifier("i");
				break;
			default:
				fail();
			}
		}
	}


	// #define D
	// #ifdef D
	//   a
	// #elif 1
	//   i
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #ifdef UD
	//   i
	// #elif 1
	//   a
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #ifdef UD
	//   i
	// #elif 0
	//   i
	// #elif 1
	//   a
	// #else
	//   i
	// #endif
	// a
	// #ifdef UD
	//   i
	// #elif 0
	//   i
	// #else
	//   a
	// #endif
	public void testIfDef() throws Exception {
		initializeScanner();
		validate("aiiiaiaiiaiiaiaiia".toCharArray());
		validateEOF();
	}

	// #define D
	// #ifndef UD
	//   a
	// #elif 1
	//   i
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #ifndef D
	//   i
	// #elif 1
	//   a
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #ifndef D
	//   i
	// #elif 0
	//   i
	// #elif 1
	//   a
	// #else
	//   i
	// #endif
	// a
	// #ifndef D
	//   i
	// #elif 0
	//   i
	// #else
	//   a
	// #endif
	public void testIfnDef() throws Exception {
		initializeScanner();
		validate("aiiiaiaiiaiiaiaiia".toCharArray());
		validateEOF();
	}
	
	// #if 1
	//   a
	// #elif 1
	//   i
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #if 0
	//   i
	// #elif 1
	//   a
	// #elif 0
	//   i
	// #else
	//   i
	// #endif
	// a
	// #if 0
	//   i
	// #elif 0
	//   i
	// #elif 1
	//   a
	// #else
	//   i
	// #endif
	// a
	// #if 0
	//   i
	// #elif 0
	//   i
	// #else
	//   a
	// #endif
	public void testIf() throws Exception {
		initializeScanner();
		validate("aiiiaiaiiaiiaiaiia".toCharArray());
		validateEOF();
	}

	// #if 0
	//   i
	//   #if 1
	//     i
	//   #elif 0
	//     i
	//   #else
	//     i
	//   #endif
	//   i
	// #endif
	// a
	// #if 0
	//   i
	//   #if 0
	//     i
	//   #elif 1
	//     i
	//   #else
	//     i
	//   #endif
	//   i
	// #endif
	// a
	// #if 0
	//   i
	//   #if 0
	//     i
	//   #elif 0
	//     i
	//   #else
	//     i
	//   #endif
	//   i
	// #endif
	// a
	public void testNestedInInactive() throws Exception {
		initializeScanner();
		validate("iiiiiaiiiiiaiiiiia".toCharArray());
		validateEOF();
	}
	
	// #if 0
	//    i
	//    #define M
	// #endif
	// a
	// #ifdef M
	//    i
	// #endif
	// a
	public void testInactiveMacroDefinition() throws Exception {
		initializeScanner();
		validate("iaia".toCharArray());
		validateEOF();
		assertNull(fScanner.getMacroDefinitions().get("M"));
	}
	
	//	#ifdef X
	//	# if 0
	//	# endif
	//	#elif defined (Y)
	//	#endif
	public void testDefinedSyntax() throws Exception {
		initializeScanner();
		validateToken(IToken.tINACTIVE_CODE_START);
		fScanner.skipInactiveCode();
		validateEOF();
		validateProblemCount(0);
	}
}
