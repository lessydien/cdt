/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.c;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.c.CASTVisitor;
import org.eclipse.cdt.core.parser.util.ArrayUtil;
import org.eclipse.cdt.internal.core.dom.parser.IASTAmbiguityParent;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPVisitor;

public abstract class CASTAmbiguity extends CASTNode  {

    protected static class CASTNameCollector extends CASTVisitor
    {
        private IASTName[] names = new IASTName[ 2 ];

        {
            shouldVisitNames = true;
        }
        
        public int visit(IASTName name) {
            names = (IASTName[]) ArrayUtil.append( IASTName.class, names, name );
            return PROCESS_CONTINUE;
        }
        
        public IASTName [] getNames()
        {
            return (IASTName[]) ArrayUtil.removeNulls( IASTName.class, names );
        }
    }
    
    
    /**
     * @param names
     * @param j
     * @throws DOMException
     */
    protected void clearCache(IASTName[] names, int j) throws DOMException {
        IScope scope = CPPVisitor.getContainingScope( names[j] );
        scope.flushCache();
    }

    /**
     * @param visitor
     * @param nodes
     * @param bestIndex
     * @return
     */
    protected boolean reduceAmbiguity(ASTVisitor visitor, IASTNode[] nodes, int bestIndex) {
        IASTAmbiguityParent owner = (IASTAmbiguityParent) getParent();
        IASTNode s = nodes[bestIndex ];
        owner.replace( this, nodes[bestIndex] );
        if( !s.accept( visitor ) ) return false;
        return true;
    }
    
    protected abstract IASTNode [] getNodes();
    
    public boolean accept(ASTVisitor visitor) {
        IASTNode [] nodez = getNodes();
        int [] issues = new int[ nodez.length ];
        Arrays.fill( issues, 0 );
        for( int i = 0; i < nodez.length; ++i )
        {
            IASTNode s = nodez[i];
            CASTNameCollector resolver = new CASTNameCollector();
            s.accept( resolver );
            IASTName [] names  = resolver.getNames();
            for( int j = 0; j < names.length; ++j )
            {
                try
                {
                    IBinding b = names[j].resolveBinding();
                    if( b == null || b instanceof IProblemBinding )
                        ++issues[i];
                    clearCache(names, j);
                }
                catch( Throwable t )
                {
                    ++issues[i];
                }
            }
        }
        int bestIndex = 0;
        int bestValue = issues[0];
        for( int i = 1; i < issues.length; ++i )
        {
            if( issues[i] < bestValue )
            {
                bestIndex = i;
                bestValue = issues[i];
            }
        }

        return reduceAmbiguity(visitor, nodez, bestIndex);
    }

}
