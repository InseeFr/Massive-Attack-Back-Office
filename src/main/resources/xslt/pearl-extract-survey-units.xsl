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
    
    <xsl:variable name="personsSheet" select="//office:spreadsheet/table:table[@table:name='persons']"/>
    
    <xsl:variable name="statesSheet" select="//office:spreadsheet/table:table[@table:name='states']"/>

    <xsl:variable name="contactAttemptsSheet" select="//office:spreadsheet/table:table[@table:name='contactAttempts']"/>
   
    <xsl:variable name="identificationsSheet" select="//office:spreadsheet/table:table[@table:name='identification']"/>

    <xsl:variable name="campaign">
        <xsl:variable name="temp" select="//office:spreadsheet/table:table[@table:name='campaign']"/>
        <xsl:value-of select="upper-case(tools:getColumn(tools:get-full-row($temp/table:table-row[2]),1))"/>
    </xsl:variable>
    
    <xsl:function name="tools:get-persons">
        <xsl:param name="idSurveyUnit"/>
        <xsl:copy-of select="$personsSheet/table:table-row[normalize-space(table:table-cell[1])=$idSurveyUnit]"></xsl:copy-of>
    </xsl:function>

    <xsl:function name="tools:get-states">
        <xsl:param name="idSurveyUnit"/>
        <xsl:copy-of select="$statesSheet/table:table-row[normalize-space(table:table-cell[1])=$idSurveyUnit]"></xsl:copy-of>
    </xsl:function>
    
    <xsl:function name="tools:get-contactAttempts">
        <xsl:param name="idSurveyUnit"/>
        <xsl:copy-of select="$contactAttemptsSheet/table:table-row[normalize-space(table:table-cell[1])=$idSurveyUnit]"></xsl:copy-of>
    </xsl:function>
    
    <xsl:function name="tools:get-identification">
        <xsl:param name="idSurveyUnit"/>
        <xsl:copy-of select="$identificationsSheet/table:table-row[normalize-space(table:table-cell[1])=$idSurveyUnit]"></xsl:copy-of>
    </xsl:function>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//office:spreadsheet/table:table[@table:name='surveyUnits']"/>        
    </xsl:template>
    <xsl:template match="office:body/office:spreadsheet/table:table[@table:name='surveyUnits']">
        <SurveyUnits>
            <xsl:for-each select="table:table-row[position()>1]">
                <xsl:variable name="row" select="tools:get-full-row(.)"/>
                <xsl:if test="normalize-space($row)!=''">
                    <SurveyUnit>
                        <Id><xsl:copy-of select="tools:getColumn($row,1)"/></Id>
                        <Persons>
                            <xsl:for-each select="tools:get-persons(tools:getColumn($row,1))">
                                <xsl:variable name="personRow" select="tools:get-full-row(.)"/>
                                <Person>
                                    <FirstName><xsl:value-of select="tools:getColumn($personRow,2)"/></FirstName>
                                    <LastName><xsl:value-of select="tools:getColumn($personRow,3)"/></LastName>
                                    <Title><xsl:value-of select="tools:getColumn($personRow,4)"/></Title>
                                    <Email><xsl:value-of select="tools:getColumn($personRow,5)"/></Email>
                                    <Privileged><xsl:value-of select="tools:getColumn($personRow,6)"/></Privileged>
                                    <FavoriteEmail><xsl:value-of select="tools:getColumn($personRow,7)"/></FavoriteEmail>
                                    <BirthDate><xsl:value-of select="tools:getColumn($personRow,8)"/></BirthDate>
                                    <PhoneNumbers>
                                        <xsl:for-each select="$personRow/table:table-cell[position()>=9 and ((position()-9) mod 3 = 0)]">
                                            <xsl:variable name="realPosition" select="9 + (position()-1)*3"/>
                                            <xsl:if test="normalize-space(.)!=''">
                                                <PhoneNumber>
                                                    <Source><xsl:value-of select="tools:getColumn($personRow,$realPosition)"/></Source>
                                                    <Favorite><xsl:value-of select="tools:getColumn($personRow,$realPosition+1)"/></Favorite>
                                                    <Number><xsl:value-of select="tools:getColumn($personRow,$realPosition+2)"/></Number>
                                                </PhoneNumber>
                                            </xsl:if>
                                        </xsl:for-each>
                                    </PhoneNumbers>
                                </Person>
                            </xsl:for-each>
                        </Persons>
                        <Priority><xsl:value-of select="tools:getColumn($row,2)"/></Priority>
                        <Campaign><xsl:value-of select="$campaign"/></Campaign>
                        <OrganizationUnitId><xsl:value-of select="tools:getColumn($row,3)"/></OrganizationUnitId>
                        <Move><xsl:value-of select="tools:getColumn($row,4)"/></Move>
                        <Address>
                            <L1><xsl:value-of select="tools:getColumn($row,5)"/></L1>
                            <L2><xsl:value-of select="tools:getColumn($row,6)"/></L2>
                            <L3><xsl:value-of select="tools:getColumn($row,7)"/></L3>
                            <L4><xsl:value-of select="tools:getColumn($row,8)"/></L4>
                            <L5><xsl:value-of select="tools:getColumn($row,9)"/></L5>
                            <L6><xsl:value-of select="tools:getColumn($row,10)"/></L6>
                            <L7><xsl:value-of select="tools:getColumn($row,11)"/></L7>
                            <Elevator><xsl:value-of select="tools:getColumn($row,27)"/></Elevator>
                            <Building><xsl:value-of select="tools:getColumn($row,28)"/></Building>
                            <Floor><xsl:value-of select="tools:getColumn($row,29)"/></Floor>
                            <Door><xsl:value-of select="tools:getColumn($row,30)"/></Door>
                            <Staircase><xsl:value-of select="tools:getColumn($row,31)"/></Staircase>
                            <CityPriorityDistrict><xsl:value-of select="tools:getColumn($row,32)"/></CityPriorityDistrict>
                        </Address>
                        
                        <SampleIdentifiers>
                            <Bs><xsl:value-of select="tools:getColumn($row,12)"/></Bs>
                            <Ec><xsl:value-of select="tools:getColumn($row,13)"/></Ec>
                            <Le><xsl:value-of select="tools:getColumn($row,14)"/></Le>
                            <Noi><xsl:value-of select="tools:getColumn($row,15)"/></Noi>
                            <Numfa><xsl:value-of select="tools:getColumn($row,16)"/></Numfa>
                            <Rges><xsl:value-of select="tools:getColumn($row,17)"/></Rges>
                            <Ssech><xsl:value-of select="tools:getColumn($row,18)"/></Ssech>
                            <Nolog><xsl:value-of select="tools:getColumn($row,19)"/></Nolog>
                            <Nole><xsl:value-of select="tools:getColumn($row,20)"/></Nole>
                            <Autre><xsl:value-of select="tools:getColumn($row,21)"/></Autre>
                            <Nograp><xsl:value-of select="tools:getColumn($row,22)"/></Nograp>
                        </SampleIdentifiers>
                        <xsl:if test="normalize-space(tools:getColumn($row,23))!=''">
                            <Comment><xsl:value-of select="tools:getColumn($row,23)"/></Comment>
                        </xsl:if>
                        <ContactAttempts>
                            <xsl:for-each select="tools:get-contactAttempts(tools:getColumn($row,1))">
                                <xsl:variable name="contactAttemptRow" select="tools:get-full-row(.)"/>
                                <xsl:if test="normalize-space($contactAttemptRow)!=''">
                                    <ContactAttempt>
                                        <Value><xsl:value-of select="tools:getColumn($contactAttemptRow,2)"/></Value>
                                        <Date><xsl:value-of select="tools:getColumn($contactAttemptRow,3)"/></Date>
                                        <Medium><xsl:value-of select="tools:getColumn($contactAttemptRow,4)"/></Medium>
                                    </ContactAttempt>
                                </xsl:if>
                            </xsl:for-each>
                        </ContactAttempts>
                        <xsl:if test="normalize-space(tools:getColumn($row,24))!=''">
                            <ContactOutcome>
                                <Value><xsl:value-of select="tools:getColumn($row,24)"/></Value>
                                <AttemptsNumber><xsl:value-of select="tools:getColumn($row,25)"/></AttemptsNumber>
                                <Date><xsl:value-of select="tools:getColumn($row,26)"/></Date>
                            </ContactOutcome>
                        </xsl:if>
                        <States>
                            <xsl:for-each select="tools:get-states(tools:getColumn($row,1))">
                                <xsl:variable name="stateRow" select="tools:get-full-row(.)"/>
                                <xsl:if test="normalize-space($stateRow)!=''">
                                    <State>
                                        <Value><xsl:value-of select="tools:getColumn($stateRow,2)"/></Value>
                                        <Date><xsl:value-of select="tools:getColumn($stateRow,3)"/></Date>
                                    </State>
                                </xsl:if>
                            </xsl:for-each>
                        </States>
                        <SurveyUnitIdentification>
                            <xsl:for-each select="tools:get-identification(tools:getColumn($row,1))">
                                <xsl:variable name="identificationRow" select="tools:get-full-row(.)"/>
                                    <Identification><xsl:value-of select="tools:getColumn($identificationRow,2)"/></Identification>
                                    <Access><xsl:value-of select="tools:getColumn($identificationRow,3)"/></Access>
                                    <Situation><xsl:value-of select="tools:getColumn($identificationRow,4)"/></Situation>
                                    <Category><xsl:value-of select="tools:getColumn($identificationRow,5)"/></Category>
                                    <Occupant><xsl:value-of select="tools:getColumn($identificationRow,6)"/></Occupant>
                            </xsl:for-each>
                        </SurveyUnitIdentification>
                    </SurveyUnit>
                </xsl:if>
            </xsl:for-each>
        </SurveyUnits>
    </xsl:template>
</xsl:stylesheet>