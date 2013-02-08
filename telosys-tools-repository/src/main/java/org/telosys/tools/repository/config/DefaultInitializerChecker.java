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
package org.telosys.tools.repository.config;

import java.sql.Types;
import java.util.StringTokenizer;

import org.telosys.tools.commons.jdbctypes.JdbcTypes;
import org.telosys.tools.commons.jdbctypes.JdbcTypesManager;
import org.telosys.tools.repository.model.Column;

/**
 * Default implementation 
 * 
 * @author L.GUERIN
 * 
 */
public class DefaultInitializerChecker implements InitializerChecker
{   
	
	//------------------------------------------------------------------------------
    public DefaultInitializerChecker() {
		super();
	}

	//------------------------------------------------------------------------------
    /**
     * Transform the given token with only the first char in Upper Case  
     * ie : "aBcDeFg" --> "Abcdefg"
     * @param s 
     * @return transformed string
     */
    private String transformToken(String s)
    {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    //------------------------------------------------------------------------------
    /**
     * Transform the given string to "CamelCase", using "_" as word separator  
     * ie : "ORDER_ITEM" --> "OrderItem"
     * @param sName
     * @return transformed string
     */
    private String toCamelCase(String sName)
    {
        if (sName != null)
        {
            StringBuffer sb = new StringBuffer( sName.length() );
            String sToken = null;
            String s = sName.trim(); // to be secure
            StringTokenizer st = new StringTokenizer(s, "_");
            while (st.hasMoreTokens())
            {
                sToken = st.nextToken();
                sb.append( transformToken( sToken ) );
            }
            return sb.toString();
        }
        else
        {
            return null;
        }
    }
	
    //------------------------------------------------------------------------------
    // Classes
    //------------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.telosys.daogen.common.InitializerChecker#getClassNameForVO(java.lang.String)
     */
    public String getJavaBeanClassName(String sTableName)
    {
    	return toCamelCase(sTableName);
    	// First char Upper Case 
        //return (sTableName.substring(0, 1).toUpperCase() + sTableName.substring(1, sTableName.length()).toLowerCase());
        //return getValidTableName(sTableName) + "VO";
    }

    //------------------------------------------------------------------------------
    // Value Object Attribute Name from Database column name and type
    //------------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.telosys.daogen.common.InitializerChecker#getAttributeName(java.lang.String, java.lang.String)
     */
    public String getAttributeName(String sColumnName, String sColumnTypeName, int iColumnTypeCode)
    {
        //--- Colum name converted in "CamelCase"
        String s = toCamelCase(sColumnName);
        
        //--- Java attribute => Force the first char to LowerCase
        return s.substring(0,1).toLowerCase() + s.substring(1,s.length()) ;
    }

    //------------------------------------------------------------------------------
    // Value Object Attribute Type from Database column type
    //------------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.telosys.daogen.common.InitializerChecker#getAttributeType(java.lang.String)
     */
    public String getAttributeType(String sColumnTypeName, int iJdbcTypeCode, boolean bColumnNotNull)
    {
    	//--- Special cases for Date/Time/Timestamp
    	if ( iJdbcTypeCode == Types.DATE  || iJdbcTypeCode == Types.TIME || iJdbcTypeCode == Types.TIMESTAMP ) 
    	{
    		// Force "java.util.Date" : see also getAttributeDateType() for DATE_ONLY, TIME_ONLY, etc...
    		return "java.util.Date";
    	}
    	JdbcTypes types = JdbcTypesManager.getJdbcTypes();
    	String sJavaType = types.getJavaTypeForCode(iJdbcTypeCode, bColumnNotNull);
    	return ( sJavaType != null ? sJavaType : "java.lang.String" );    	
    }

    //------------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see org.telosys.daogen.common.InitializerChecker#getAttributeType(java.lang.String)
     */
    public String getAttributeLongTextFlag (String sColumnType, int iColumnTypeCode, String sJavaType )
    {
    	if ( sJavaType.equals("java.lang.String") )
    	{
    		if (   iColumnTypeCode == Types.LONGVARCHAR
    			|| iColumnTypeCode == Types.CLOB
    			|| iColumnTypeCode == Types.BLOB )
    		{
    			// Considered as a "Long Text"
    			return Column.SPECIAL_LONG_TEXT_TRUE ; 
    		}
    	}
    	return null ;
    }
    
    public String getAttributeDateType(String sColumnType, int iColumnTypeCode, String sJavaType )
    {
    	if ( sJavaType.equals("java.util.Date") )
    	{
        	switch ( iColumnTypeCode )
        	{
        		//--- Type of Date :
        		case Types.DATE : 
        			return Column.SPECIAL_DATE_ONLY ; 
        		case Types.TIME : 
        			return Column.SPECIAL_TIME_ONLY ; 
        		case Types.TIMESTAMP : 
        			return Column.SPECIAL_DATE_AND_TIME ;
        	}
    	}
    	return null ;
    }
    
}