<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:tools="http://www.insee.fr/tools"
    office:version="1.2" office:mimetype="application/vnd.oasis.opendocument.spreadsheet"
    exclude-result-prefixes="tools xs office text table "
    version="2.0">

    <xsl:import href="./utils.xsl"/>
    <xsl:output indent="yes"/>
        
    <xsl:variable name="root" select="."/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//office:spreadsheet/table:table[@table:name='operation']"/>
    </xsl:template>
    
    
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='operation']">
        <xsl:variable name="row" select="tools:get-full-row(table:table-row[2])"/>        
        <Campaign>
            <Id><xsl:value-of select="upper-case(tools:getColumn($row,1))"/></Id>
            <Label><xsl:value-of select="tools:getColumn($row,2)"/></Label>
            <xsl:apply-templates select="$root//office:spreadsheet/table:table[@table:name='metadata']"/>
            <xsl:for-each select="tokenize(tools:getColumn($row,3),',')">
                <questionnaireIds><xsl:value-of select="upper-case(.)"/></questionnaireIds>
            </xsl:for-each>
        </Campaign>
    </xsl:template>
    
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='metadata']">
        <xsl:variable name="firstRow" select="tools:get-full-row(table:table-row[1])"/>
        <xsl:variable name="secondRow" select="tools:get-full-row(table:table-row[2])"/>
        <Metadata>
            <MetadataValue>
                <InseeContext><xsl:value-of select="tools:getColumn($secondRow,1)"/></InseeContext>
                <Variables>
                    <xsl:for-each select="$firstRow/table:table-cell">
                    <xsl:if test="normalize-space(.)!='' and position()>1">
                        <Variable>
                            <Name><xsl:value-of select="tools:getColumn($firstRow,position())"/></Name>
                            <Value><xsl:value-of select="tools:getColumn($secondRow,position())"/></Value>
                        </Variable>
                    </xsl:if>
                </xsl:for-each>
            </Variables>
        </MetadataValue>
        </Metadata>
    </xsl:template>
</xsl:stylesheet>