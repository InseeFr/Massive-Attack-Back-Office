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
        
    <xsl:template match="/">
        <Campaign>
            <xsl:apply-templates select="//office:spreadsheet/table:table[@table:name='campaign']"/>
            <xsl:apply-templates select="//office:spreadsheet/table:table[@table:name='organisationalUnits']"/>
            <xsl:apply-templates select="//office:spreadsheet/table:table[@table:name='referent']"/>
        </Campaign>
    </xsl:template>
    
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='campaign']">
        <xsl:variable name="row" select="tools:get-full-row(table:table-row[2])"/>
        <Campaign><xsl:value-of select="upper-case(tools:getColumn($row,1))"/></Campaign>
        <CampaignLabel><xsl:value-of select="tools:getColumn($row,2)"/></CampaignLabel>
        <Email><xsl:value-of select="tools:getColumn($row,3)"/></Email>
        <IdentificationConfiguration><xsl:value-of select="tools:getColumn($row,4)"/></IdentificationConfiguration>
        <ContactOutcomeConfiguration><xsl:value-of select="tools:getColumn($row,5)"/></ContactOutcomeConfiguration>
        <ContactAttemptConfiguration><xsl:value-of select="tools:getColumn($row,6)"/></ContactAttemptConfiguration>
    </xsl:template>
    
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='organisationalUnits']">
        <Visibilities>
            <xsl:for-each select="table:table-row[position()>1]">
                <xsl:variable name="row" select="tools:get-full-row(.)"/>
                <xsl:if test="normalize-space($row)!=''">
                    <Visibility>
                        <OrganisationalUnit><xsl:value-of select="tools:getColumn($row,1)"/></OrganisationalUnit>
                        <CollectionStartDate><xsl:value-of select="tools:getColumn($row,2)"/></CollectionStartDate>
                        <CollectionEndDate><xsl:value-of select="tools:getColumn($row,3)"/></CollectionEndDate>
                        <IdentificationPhaseStartDate><xsl:value-of select="tools:getColumn($row,4)"/></IdentificationPhaseStartDate>
                        <InterviewerStartDate><xsl:value-of select="tools:getColumn($row,5)"/></InterviewerStartDate>
                        <ManagementStartDate><xsl:value-of select="tools:getColumn($row,6)"/></ManagementStartDate>
                        <EndDate><xsl:value-of select="tools:getColumn($row,7)"/></EndDate>
                    </Visibility>
                </xsl:if>                
            </xsl:for-each>
        </Visibilities>
    </xsl:template>
    
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='referent']">
        <Referents>
            <xsl:for-each select="table:table-row[position()>1]">
                <xsl:variable name="row" select="tools:get-full-row(.)"/>
                <xsl:if test="normalize-space($row)!=''">
                    <Referent>
                        <FirstName><xsl:value-of select="tools:getColumn($row,1)"/></FirstName>
                        <LastName><xsl:value-of select="tools:getColumn($row,2)"/></LastName>
                        <Role><xsl:value-of select="tools:getColumn($row,3)"/></Role>
                        <PhoneNumber><xsl:value-of select="tools:getColumn($row,4)"/></PhoneNumber>
                    </Referent>
                </xsl:if>                
            </xsl:for-each>
        </Referents>
    </xsl:template>
</xsl:stylesheet>