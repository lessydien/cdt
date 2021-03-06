/*******************************************************************************
 * Copyright (c) 2017 Nathan Ridge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.IASTCompletionContext;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeSelector;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.internal.core.dom.parser.IASTInactiveCompletionName;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPVisitor;

public class CPPASTInactiveCompletionName extends CPPASTName implements IASTInactiveCompletionName {
	private IASTTranslationUnit fAst;
	
	public CPPASTInactiveCompletionName(char[] name, IASTTranslationUnit ast) {
		super(name);
		fAst = ast;
	}
	
	@Override
	public IASTCompletionContext getCompletionContext() {
		return this;
	}

	@Override
	public IBinding[] findBindings(IASTName name, boolean isPrefix) {
		// 'name' (which is the same as 'this') is not hooked up to the AST, but it
		// does have a location (offset and length) which we use to compute the 
		// containing scope.
		IASTNodeSelector sel = fAst.getNodeSelector(null);
		IASTNode node = sel.findEnclosingNode(getOffset(), getLength());
		IScope lookupScope = CPPVisitor.getContainingScope(node);
		if (lookupScope == null) {
			lookupScope = fAst.getScope();
		}
		return CPPSemantics.findBindingsForContentAssist(name.getLookupKey(), isPrefix, lookupScope, name);
	}
}
